package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.Constants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A tiny expression layer for animation-controller transitions.
 *
 * <p>Deliberately <em>not</em> Molang. It is a small, documented subset, parsed once when the
 * resource pack loads and evaluated as a compiled tree afterwards — nothing is parsed per frame.
 * Anything outside the grammar fails at load time with a message naming the offending text, so a typo
 * surfaces as a log error rather than a silently dead transition.
 *
 * <pre>
 * expression := or
 * or         := and ( "||" and )*
 * and        := comparison ( "&amp;&amp;" comparison )*
 * comparison := unary ( ("=="|"!="|"&gt;="|"&lt;="|"&gt;"|"&lt;") unary )?
 * unary      := "!" unary | primary
 * primary    := number | 'string' | "true" | "false" | "query." name | "(" expression ")"
 * </pre>
 *
 * <p>Values are numbers or strings; a number is truthy when it is non-zero, matching Molang.
 */
public sealed interface AnimationExpression {
    Codec<AnimationExpression> CODEC = Codec.STRING.comapFlatMap(
        text -> {
            try {
                return DataResult.success(parse(text));
            } catch (ParseException e) {
                return DataResult.error(() -> "invalid animation expression \"" + text + "\": " + e.getMessage());
            }
        },
        AnimationExpression::source);

    AnimationExpression ALWAYS = new Literal(1.0, null, "true");

    boolean test(AnimationQueryContext context);

    double value(AnimationQueryContext context);

    String source();

    static AnimationExpression parse(String text) throws ParseException {
        Parser parser = new Parser(tokenize(text), text);
        AnimationExpression expression = parser.parseExpression();
        parser.expectEnd();
        return new Wrapper(expression, text);
    }

    static AnimationExpression parseOrAlways(String text, String where) {
        try {
            return parse(text);
        } catch (ParseException e) {
            Constants.LOG.error("Ignoring animation condition in {}: \"{}\" - {}", where, text, e.getMessage());
            return ALWAYS;
        }
    }

    record Wrapper(AnimationExpression delegate, String source) implements AnimationExpression {
        @Override
        public boolean test(AnimationQueryContext context) {
            return this.delegate.test(context);
        }

        @Override
        public double value(AnimationQueryContext context) {
            return this.delegate.value(context);
        }
    }

    record Literal(double number, String text, String source) implements AnimationExpression {
        @Override
        public boolean test(AnimationQueryContext context) {
            return this.text != null ? !this.text.isEmpty() : this.number != 0.0;
        }

        @Override
        public double value(AnimationQueryContext context) {
            return this.number;
        }
    }

    record Query(String name, String source) implements AnimationExpression {
        @Override
        public boolean test(AnimationQueryContext context) {
            double number = context.number(this.name);
            if (!Double.isNaN(number)) {
                return number != 0.0;
            }
            String text = context.string(this.name);
            return text != null && !text.isEmpty() && !"none".equals(text);
        }

        @Override
        public double value(AnimationQueryContext context) {
            double number = context.number(this.name);
            return Double.isNaN(number) ? 0.0 : number;
        }
    }

    record Not(AnimationExpression operand, String source) implements AnimationExpression {
        @Override
        public boolean test(AnimationQueryContext context) {
            return !this.operand.test(context);
        }

        @Override
        public double value(AnimationQueryContext context) {
            return this.test(context) ? 1.0 : 0.0;
        }
    }

    record And(AnimationExpression left, AnimationExpression right, String source) implements AnimationExpression {
        @Override
        public boolean test(AnimationQueryContext context) {
            return this.left.test(context) && this.right.test(context);
        }

        @Override
        public double value(AnimationQueryContext context) {
            return this.test(context) ? 1.0 : 0.0;
        }
    }

    record Or(AnimationExpression left, AnimationExpression right, String source) implements AnimationExpression {
        @Override
        public boolean test(AnimationQueryContext context) {
            return this.left.test(context) || this.right.test(context);
        }

        @Override
        public double value(AnimationQueryContext context) {
            return this.test(context) ? 1.0 : 0.0;
        }
    }

    record Comparison(AnimationExpression left, String operator, AnimationExpression right, String source)
        implements AnimationExpression {

        @Override
        public boolean test(AnimationQueryContext context) {
            String leftText = textOf(this.left, context);
            String rightText = textOf(this.right, context);

            // String comparison only makes sense for equality, e.g. query.action == 'graze'.
            if (leftText != null || rightText != null) {
                boolean equal = java.util.Objects.equals(
                    leftText != null ? leftText : Double.toString(this.left.value(context)),
                    rightText != null ? rightText : Double.toString(this.right.value(context)));
                return switch (this.operator) {
                    case "==" -> equal;
                    case "!=" -> !equal;
                    default -> false;
                };
            }

            double a = this.left.value(context);
            double b = this.right.value(context);
            return switch (this.operator) {
                case "==" -> a == b;
                case "!=" -> a != b;
                case ">" -> a > b;
                case "<" -> a < b;
                case ">=" -> a >= b;
                case "<=" -> a <= b;
                default -> false;
            };
        }

        private static String textOf(AnimationExpression expression, AnimationQueryContext context) {
            if (expression instanceof Wrapper wrapper) {
                return textOf(wrapper.delegate(), context);
            }
            if (expression instanceof Literal literal) {
                return literal.text();
            }
            if (expression instanceof Query query) {
                return context.string(query.name());
            }
            return null;
        }

        @Override
        public double value(AnimationQueryContext context) {
            return this.test(context) ? 1.0 : 0.0;
        }
    }

    final class ParseException extends Exception {
        ParseException(String message) {
            super(message);
        }
    }

    record Token(Kind kind, String text) {
        enum Kind { NUMBER, STRING, IDENTIFIER, OPERATOR, LPAREN, RPAREN }
    }

    private static List<Token> tokenize(String text) throws ParseException {
        List<Token> tokens = new ArrayList<>();
        int index = 0;

        while (index < text.length()) {
            char c = text.charAt(index);

            if (Character.isWhitespace(c)) {
                index++;
            } else if (c == '(') {
                tokens.add(new Token(Token.Kind.LPAREN, "("));
                index++;
            } else if (c == ')') {
                tokens.add(new Token(Token.Kind.RPAREN, ")"));
                index++;
            } else if (c == '\'' || c == '"') {
                int end = text.indexOf(c, index + 1);
                if (end < 0) {
                    throw new ParseException("unterminated string starting at position " + index);
                }
                tokens.add(new Token(Token.Kind.STRING, text.substring(index + 1, end)));
                index = end + 1;
            } else if (Character.isDigit(c) || (c == '.' && index + 1 < text.length() && Character.isDigit(text.charAt(index + 1)))) {
                int end = index;
                while (end < text.length() && (Character.isDigit(text.charAt(end)) || text.charAt(end) == '.')) {
                    end++;
                }
                tokens.add(new Token(Token.Kind.NUMBER, text.substring(index, end)));
                index = end;
            } else if (Character.isLetter(c) || c == '_') {
                int end = index;
                while (end < text.length()
                    && (Character.isLetterOrDigit(text.charAt(end)) || text.charAt(end) == '_' || text.charAt(end) == '.')) {
                    end++;
                }
                tokens.add(new Token(Token.Kind.IDENTIFIER, text.substring(index, end)));
                index = end;
            } else {
                String two = index + 1 < text.length() ? text.substring(index, index + 2) : "";
                if (two.equals("&&") || two.equals("||") || two.equals("==") || two.equals("!=")
                    || two.equals(">=") || two.equals("<=")) {
                    tokens.add(new Token(Token.Kind.OPERATOR, two));
                    index += 2;
                } else if (c == '!' || c == '>' || c == '<') {
                    tokens.add(new Token(Token.Kind.OPERATOR, String.valueOf(c)));
                    index++;
                } else {
                    throw new ParseException("unexpected character '" + c + "' at position " + index);
                }
            }
        }
        return tokens;
    }

    final class Parser {
        private final List<Token> tokens;
        private final String source;
        private int position;

        Parser(List<Token> tokens, String source) {
            this.tokens = tokens;
            this.source = source;
        }

        AnimationExpression parseExpression() throws ParseException {
            return this.parseOr();
        }

        void expectEnd() throws ParseException {
            if (this.position < this.tokens.size()) {
                throw new ParseException("unexpected trailing text at token " + this.position);
            }
        }

        private AnimationExpression parseOr() throws ParseException {
            AnimationExpression left = this.parseAnd();
            while (this.matchOperator("||")) {
                left = new Or(left, this.parseAnd(), this.source);
            }
            return left;
        }

        private AnimationExpression parseAnd() throws ParseException {
            AnimationExpression left = this.parseComparison();
            while (this.matchOperator("&&")) {
                left = new And(left, this.parseComparison(), this.source);
            }
            return left;
        }

        private AnimationExpression parseComparison() throws ParseException {
            AnimationExpression left = this.parseUnary();
            for (String operator : new String[]{"==", "!=", ">=", "<=", ">", "<"}) {
                if (this.matchOperator(operator)) {
                    return new Comparison(left, operator, this.parseUnary(), this.source);
                }
            }
            return left;
        }

        private AnimationExpression parseUnary() throws ParseException {
            if (this.matchOperator("!")) {
                return new Not(this.parseUnary(), this.source);
            }
            return this.parsePrimary();
        }

        private AnimationExpression parsePrimary() throws ParseException {
            if (this.position >= this.tokens.size()) {
                throw new ParseException("expression ended unexpectedly");
            }

            Token token = this.tokens.get(this.position++);
            return switch (token.kind()) {
                case NUMBER -> new Literal(Double.parseDouble(token.text()), null, token.text());
                case STRING -> new Literal(0.0, token.text(), "'" + token.text() + "'");
                case LPAREN -> {
                    AnimationExpression inner = this.parseOr();
                    if (this.position >= this.tokens.size() || this.tokens.get(this.position).kind() != Token.Kind.RPAREN) {
                        throw new ParseException("missing ')'");
                    }
                    this.position++;
                    yield inner;
                }
                case IDENTIFIER -> this.identifier(token.text());
                default -> throw new ParseException("unexpected '" + token.text() + "'");
            };
        }

        private AnimationExpression identifier(String name) throws ParseException {
            String lower = name.toLowerCase(Locale.ROOT);
            if (lower.equals("true")) {
                return new Literal(1.0, null, "true");
            }
            if (lower.equals("false")) {
                return new Literal(0.0, null, "false");
            }
            if (lower.startsWith("query.") || lower.startsWith("q.")) {
                String query = lower.substring(lower.indexOf('.') + 1);
                if (query.isEmpty()) {
                    throw new ParseException("'query.' must be followed by a name");
                }
                return new Query(query, name);
            }
            throw new ParseException("unknown identifier '" + name + "'; only 'true', 'false' and 'query.<name>' are supported");
        }

        private boolean matchOperator(String operator) {
            if (this.position < this.tokens.size()) {
                Token token = this.tokens.get(this.position);
                if (token.kind() == Token.Kind.OPERATOR && token.text().equals(operator)) {
                    this.position++;
                    return true;
                }
            }
            return false;
        }
    }
}

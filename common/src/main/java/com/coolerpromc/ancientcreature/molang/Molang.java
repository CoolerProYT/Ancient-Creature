package com.coolerpromc.ancientcreature.molang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Compiles Molang, the expression language Bedrock uses in animations, animation controllers, render
 * controllers and client entity scripts.
 *
 * <p>Supported: numbers, {@code 'strings'}, {@code query.}/{@code q.}, {@code variable.}/{@code v.},
 * {@code temp.}/{@code t.}, {@code context.}/{@code c.}, {@code math.*}, {@code this},
 * {@code Array.}/{@code Geometry.}/{@code Texture.}/{@code Material.} references, unary {@code ! -},
 * {@code * / + -}, comparisons, {@code == !=}, {@code && ||}, {@code a ? b : c}, {@code a ? b},
 * {@code ??}, assignment, {@code ;}-separated statements with {@code return}, {@code {}} blocks,
 * {@code loop}, {@code for_each}, {@code break} and {@code continue}. Everything is case-insensitive.
 *
 * <p>The {@code ->} entity-reference operator parses but evaluates to 0, because there are no entity
 * references outside Bedrock.
 */
public final class Molang {
    static final Logger LOG = LoggerFactory.getLogger("AncientCreature/Molang");

    public static MolangExpression compile(String source) throws MolangException {
        String trimmed = source == null ? "" : source.trim();
        if (trimmed.isEmpty()) {
            return MolangExpression.ZERO;
        }
        Parser parser = new Parser(Lexer.tokenize(trimmed), trimmed);
        return parser.parseProgram();
    }

    /** Compiles, or logs the problem and returns {@code fallback}. {@code where} names the file and field. */
    public static MolangExpression compileOr(String source, MolangExpression fallback, String where) {
        try {
            return compile(source);
        } catch (MolangException e) {
            LOG.error("Invalid Molang in {}: \"{}\" - {}", where, source, e.getMessage());
            return fallback;
        }
    }

    /** Compiles a number or a Molang string from JSON-ish input. */
    public static MolangExpression of(Object numberOrString, String where) {
        if (numberOrString instanceof Number number) {
            return MolangExpression.constant(number.doubleValue());
        }
        if (numberOrString instanceof Boolean flag) {
            return flag ? MolangExpression.ONE : MolangExpression.ZERO;
        }
        return compileOr(String.valueOf(numberOrString), MolangExpression.ZERO, where);
    }

    /** {@code -(expression)}, folded when constant. */
    public static MolangExpression negate(MolangExpression expression) {
        if (expression.isConstant()) {
            return MolangExpression.constant(-expression.constantValue());
        }
        return compileOr("-(" + expression.source() + ")", MolangExpression.ZERO, "negation of \"" + expression.source() + "\"");
    }

    private Molang() {
    }

    // ===================================================================== lexer

    enum Kind { NUMBER, STRING, NAME, OP, END }

    record Token(Kind kind, String text, double number, int position) {
        boolean is(String op) {
            return (this.kind == Kind.OP || this.kind == Kind.NAME) && this.text.equals(op);
        }
    }

    static final class Lexer {
        private static final String[] OPERATORS = {
            "&&", "||", "==", "!=", "<=", ">=", "??", "->",
            "<", ">", "+", "-", "*", "/", "!", "?", ":", "=", "(", ")", "[", "]", "{", "}", ",", ";"
        };

        static List<Token> tokenize(String source) throws MolangException {
            List<Token> tokens = new ArrayList<>();
            int i = 0;
            int n = source.length();
            while (i < n) {
                char c = source.charAt(i);
                if (Character.isWhitespace(c)) {
                    i++;
                    continue;
                }
                if (Character.isDigit(c) || (c == '.' && i + 1 < n && Character.isDigit(source.charAt(i + 1)))) {
                    int start = i;
                    while (i < n && (Character.isDigit(source.charAt(i)) || source.charAt(i) == '.')) {
                        i++;
                    }
                    if (i < n && (source.charAt(i) == 'e' || source.charAt(i) == 'E')
                        && i + 1 < n && (Character.isDigit(source.charAt(i + 1)) || ((source.charAt(i + 1) == '-' || source.charAt(i + 1) == '+') && i + 2 < n && Character.isDigit(source.charAt(i + 2))))) {
                        i += 2;
                        while (i < n && Character.isDigit(source.charAt(i))) {
                            i++;
                        }
                    }
                    String text = source.substring(start, i);
                    // Tolerate a trailing float suffix ("0.5f"), which some exporters emit.
                    if (i < n && (source.charAt(i) == 'f' || source.charAt(i) == 'F')
                        && (i + 1 >= n || !Character.isLetterOrDigit(source.charAt(i + 1)) && source.charAt(i + 1) != '_')) {
                        i++;
                    }
                    try {
                        tokens.add(new Token(Kind.NUMBER, text, Double.parseDouble(text), start));
                    } catch (NumberFormatException e) {
                        throw new MolangException("malformed number '" + text + "' at " + start, source);
                    }
                    continue;
                }
                if (c == '\'') {
                    int end = source.indexOf('\'', i + 1);
                    if (end < 0) {
                        throw new MolangException("unterminated string starting at " + i, source);
                    }
                    tokens.add(new Token(Kind.STRING, source.substring(i + 1, end), 0, i));
                    i = end + 1;
                    continue;
                }
                if (Character.isLetter(c) || c == '_') {
                    int start = i;
                    while (i < n) {
                        char d = source.charAt(i);
                        if (Character.isLetterOrDigit(d) || d == '_') {
                            i++;
                        } else if (d == '.' && i + 1 < n && (Character.isLetter(source.charAt(i + 1)) || source.charAt(i + 1) == '_')) {
                            i++;
                        } else {
                            break;
                        }
                    }
                    tokens.add(new Token(Kind.NAME, source.substring(start, i).toLowerCase(Locale.ROOT), 0, start));
                    continue;
                }
                boolean matched = false;
                for (String op : OPERATORS) {
                    if (source.startsWith(op, i)) {
                        tokens.add(new Token(Kind.OP, op, 0, i));
                        i += op.length();
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    throw new MolangException("unexpected character '" + c + "' at " + i, source);
                }
            }
            tokens.add(new Token(Kind.END, "", 0, n));
            return tokens;
        }
    }

    // ===================================================================== parser

    static final class Parser {
        private final List<Token> tokens;
        private final String source;
        private int position;
        private boolean complex;

        Parser(List<Token> tokens, String source) {
            this.tokens = tokens;
            this.source = source;
        }

        private Token peek() {
            return this.tokens.get(this.position);
        }

        private Token next() {
            return this.tokens.get(this.position++);
        }

        private boolean accept(String op) {
            if (this.peek().is(op)) {
                this.position++;
                return true;
            }
            return false;
        }

        private void expect(String op) throws MolangException {
            if (!this.accept(op)) {
                throw this.error("expected '" + op + "' but found " + describe(this.peek()));
            }
        }

        private MolangException error(String message) {
            return new MolangException(message, this.source);
        }

        private static String describe(Token token) {
            return token.kind() == Kind.END ? "end of expression" : "'" + token.text() + "' at " + token.position();
        }

        MolangExpression parseProgram() throws MolangException {
            List<MolangNode> statements = this.parseStatements(false);
            if (this.peek().kind() != Kind.END) {
                throw this.error("unexpected " + describe(this.peek()));
            }
            if (!this.complex && statements.size() == 1) {
                MolangNode only = statements.getFirst();
                if (only.isConstant() && !only.mayBeText()) {
                    only = new MolangNode.Constant(only.number(new MolangContext()));
                }
                return new MolangExpression(only, false, this.source);
            }
            return new MolangExpression(new MolangNode.Block(statements.toArray(MolangNode[]::new)), true, this.source);
        }

        private List<MolangNode> parseStatements(boolean inBraces) throws MolangException {
            List<MolangNode> statements = new ArrayList<>();
            while (true) {
                Token token = this.peek();
                if (token.kind() == Kind.END || (inBraces && token.is("}"))) {
                    break;
                }
                if (this.accept(";")) {
                    this.complex = true;
                    continue;
                }
                statements.add(this.parseStatement());
                if (this.accept(";")) {
                    this.complex = true;
                } else {
                    Token after = this.peek();
                    if (!(after.kind() == Kind.END || (inBraces && after.is("}")))) {
                        throw this.error("expected ';' before " + describe(after));
                    }
                }
            }
            return statements;
        }

        private MolangNode parseStatement() throws MolangException {
            Token token = this.peek();
            if (token.kind() == Kind.NAME) {
                switch (token.text()) {
                    case "return" -> {
                        this.position++;
                        this.complex = true;
                        return new MolangNode.Return(this.parseExpression());
                    }
                    case "break" -> {
                        this.position++;
                        return new MolangNode.Break();
                    }
                    case "continue" -> {
                        this.position++;
                        return new MolangNode.Continue();
                    }
                    default -> {
                    }
                }
            }
            return this.parseExpression();
        }

        MolangNode parseExpression() throws MolangException {
            return this.parseAssignment();
        }

        private MolangNode parseAssignment() throws MolangException {
            MolangNode left = this.parseCoalesce();
            if (this.peek().is("=")) {
                this.position++;
                MolangNode value = this.parseAssignment();
                if (left instanceof MolangNode.Variable variable) {
                    return new MolangNode.Assign(variable.store, variable.name, value);
                }
                throw this.error("only variable.*, temp.* and context.* can be assigned to");
            }
            return left;
        }

        private MolangNode parseCoalesce() throws MolangException {
            MolangNode left = this.parseConditional();
            while (this.accept("??")) {
                left = new MolangNode.Coalesce(left, this.parseConditional());
            }
            return left;
        }

        private MolangNode parseConditional() throws MolangException {
            MolangNode condition = this.parseOr();
            if (this.accept("?")) {
                MolangNode whenTrue = this.parseConditional();
                MolangNode whenFalse = this.accept(":") ? this.parseConditional() : new MolangNode.Constant(0.0);
                return fold(new MolangNode.Ternary(condition, whenTrue, whenFalse));
            }
            return condition;
        }

        private MolangNode parseOr() throws MolangException {
            MolangNode left = this.parseAnd();
            while (this.accept("||")) {
                left = fold(new MolangNode.Or(left, this.parseAnd()));
            }
            return left;
        }

        private MolangNode parseAnd() throws MolangException {
            MolangNode left = this.parseEquality();
            while (this.accept("&&")) {
                left = fold(new MolangNode.And(left, this.parseEquality()));
            }
            return left;
        }

        private MolangNode parseEquality() throws MolangException {
            MolangNode left = this.parseComparison();
            while (true) {
                if (this.accept("==")) {
                    left = fold(new MolangNode.Equality(left, this.parseComparison(), false));
                } else if (this.accept("!=")) {
                    left = fold(new MolangNode.Equality(left, this.parseComparison(), true));
                } else {
                    return left;
                }
            }
        }

        private MolangNode parseComparison() throws MolangException {
            MolangNode left = this.parseAdditive();
            while (true) {
                if (this.accept("<=")) {
                    left = fold(new MolangNode.Arithmetic(left, this.parseAdditive(), (a, b) -> a <= b ? 1 : 0));
                } else if (this.accept(">=")) {
                    left = fold(new MolangNode.Arithmetic(left, this.parseAdditive(), (a, b) -> a >= b ? 1 : 0));
                } else if (this.accept("<")) {
                    left = fold(new MolangNode.Arithmetic(left, this.parseAdditive(), (a, b) -> a < b ? 1 : 0));
                } else if (this.accept(">")) {
                    left = fold(new MolangNode.Arithmetic(left, this.parseAdditive(), (a, b) -> a > b ? 1 : 0));
                } else {
                    return left;
                }
            }
        }

        private MolangNode parseAdditive() throws MolangException {
            MolangNode left = this.parseMultiplicative();
            while (true) {
                if (this.accept("+")) {
                    left = fold(new MolangNode.Arithmetic(left, this.parseMultiplicative(), Double::sum));
                } else if (this.peek().is("-")) {
                    this.position++;
                    left = fold(new MolangNode.Arithmetic(left, this.parseMultiplicative(), (a, b) -> a - b));
                } else {
                    return left;
                }
            }
        }

        private MolangNode parseMultiplicative() throws MolangException {
            MolangNode left = this.parseUnary();
            while (true) {
                if (this.accept("*")) {
                    left = fold(new MolangNode.Arithmetic(left, this.parseUnary(), (a, b) -> a * b));
                } else if (this.accept("/")) {
                    left = fold(new MolangNode.Arithmetic(left, this.parseUnary(), MolangNode::divide));
                } else {
                    return left;
                }
            }
        }

        private MolangNode parseUnary() throws MolangException {
            if (this.accept("!")) {
                return fold(new MolangNode.Not(this.parseUnary()));
            }
            if (this.accept("-")) {
                return fold(new MolangNode.Negate(this.parseUnary()));
            }
            if (this.accept("+")) {
                return this.parseUnary();
            }
            return this.parseArrow();
        }

        private MolangNode parseArrow() throws MolangException {
            MolangNode left = this.parsePrimary();
            while (this.accept("->")) {
                this.parsePrimary();
                left = new MolangNode.Unsupported();
                LOG.warn("Molang '->' entity references are not supported outside Bedrock and evaluate to 0: \"{}\"", this.source);
            }
            return left;
        }

        private MolangNode parsePrimary() throws MolangException {
            Token token = this.next();
            switch (token.kind()) {
                case NUMBER -> {
                    return new MolangNode.Constant(token.number());
                }
                case STRING -> {
                    return new MolangNode.Text(token.text());
                }
                case END -> throw this.error("unexpected end of expression");
                case OP -> {
                    if (token.is("(")) {
                        MolangNode inner = this.parseExpression();
                        this.expect(")");
                        return inner;
                    }
                    if (token.is("{")) {
                        this.complex = true;
                        List<MolangNode> statements = this.parseStatements(true);
                        this.expect("}");
                        return new MolangNode.Block(statements.toArray(MolangNode[]::new));
                    }
                    throw this.error("unexpected " + describe(token));
                }
                case NAME -> {
                    return this.parseName(token);
                }
            }
            throw this.error("unexpected " + describe(token));
        }

        private MolangNode parseName(Token token) throws MolangException {
            String name = token.text();
            switch (name) {
                case "true" -> {
                    return new MolangNode.Constant(1.0);
                }
                case "false" -> {
                    return new MolangNode.Constant(0.0);
                }
                case "this" -> {
                    return new MolangNode.This();
                }
                case "break" -> {
                    return new MolangNode.Break();
                }
                case "continue" -> {
                    return new MolangNode.Continue();
                }
                case "loop" -> {
                    this.complex = true;
                    this.expect("(");
                    MolangNode count = this.parseExpression();
                    this.expect(",");
                    MolangNode body = this.parseExpression();
                    this.expect(")");
                    return new MolangNode.Loop(count, body);
                }
                case "for_each" -> {
                    this.complex = true;
                    this.expect("(");
                    MolangNode target = this.parseExpression();
                    if (!(target instanceof MolangNode.Variable variable)) {
                        throw this.error("for_each needs a variable.* or temp.* as its first argument");
                    }
                    this.expect(",");
                    Token arrayToken = this.next();
                    String array = stripPrefix(arrayToken.text(), "array.");
                    if (arrayToken.kind() != Kind.NAME || array == null) {
                        throw this.error("for_each can only iterate an array.* in this runtime");
                    }
                    this.expect(",");
                    MolangNode body = this.parseExpression();
                    this.expect(")");
                    return new MolangNode.ForEach(variable.store, variable.name, array, body);
                }
                default -> {
                }
            }

            int dot = name.indexOf('.');
            if (dot < 0) {
                throw this.error("unknown identifier '" + name + "' at " + token.position());
            }
            String prefix = name.substring(0, dot);
            String rest = name.substring(dot + 1);

            switch (prefix) {
                case "q", "query" -> {
                    MolangNode[] args = this.peek().is("(") ? this.parseArgs() : new MolangNode[0];
                    return new MolangNode.Query(rest, args);
                }
                case "v", "variable" -> {
                    return new MolangNode.Variable(MolangNode.Store.VARIABLE, rest);
                }
                case "t", "temp" -> {
                    return new MolangNode.Variable(MolangNode.Store.TEMP, rest);
                }
                case "c", "context" -> {
                    return new MolangNode.Variable(MolangNode.Store.CONTEXT, rest);
                }
                case "math" -> {
                    MolangMath.Function function = MolangMath.get(rest);
                    if (function == null) {
                        throw this.error("unknown function 'math." + rest + "'");
                    }
                    MolangNode[] args;
                    if (this.peek().is("(")) {
                        args = this.parseArgs();
                    } else if (MolangMath.CONSTANTS.contains(rest)) {
                        args = new MolangNode[0];
                    } else {
                        throw this.error("'math." + rest + "' must be called with parentheses");
                    }
                    if (args.length < function.minArgs()) {
                        throw this.error("'math." + rest + "' needs " + function.minArgs() + " argument(s), got " + args.length);
                    }
                    return fold(new MolangNode.MathCall(function, args));
                }
                case "array" -> {
                    this.expect("[");
                    MolangNode index = this.parseExpression();
                    this.expect("]");
                    return new MolangNode.ArrayAccess(rest, index);
                }
                case "geometry", "texture", "material" -> {
                    return new MolangNode.Resource(prefix, rest);
                }
                default -> throw this.error("unknown identifier '" + name + "' at " + token.position());
            }
        }

        private MolangNode[] parseArgs() throws MolangException {
            this.expect("(");
            List<MolangNode> args = new ArrayList<>();
            if (!this.accept(")")) {
                do {
                    args.add(this.parseExpression());
                } while (this.accept(","));
                this.expect(")");
            }
            return args.toArray(MolangNode[]::new);
        }

        private static String stripPrefix(String text, String prefix) {
            return text.startsWith(prefix) ? text.substring(prefix.length()) : null;
        }

        private static MolangNode fold(MolangNode node) {
            if (node.isConstant() && !node.mayBeText()) {
                return new MolangNode.Constant(node.number(new MolangContext()));
            }
            return node;
        }
    }
}

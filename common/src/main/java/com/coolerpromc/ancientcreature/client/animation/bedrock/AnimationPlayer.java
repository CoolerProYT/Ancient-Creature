package com.coolerpromc.ancientcreature.client.animation.bedrock;

import com.coolerpromc.ancientcreature.molang.MolangContext;
import com.coolerpromc.ancientcreature.molang.MolangExpression;

/**
 * Playback state of one animation on one entity: its own {@code anim_time}, start and loop delays,
 * and which timeline/sound/particle events have already fired.
 */
public final class AnimationPlayer {
    /** Receives the events an animation reaches while playing. */
    public interface EventSink {
        void sound(BedrockAnimation.TimedEffect effect);

        void particle(BedrockAnimation.TimedEffect effect);
    }

    private final BedrockAnimation animation;
    private float animTime;
    private float delay;
    private boolean finished;
    private boolean fresh = true;

    public AnimationPlayer(BedrockAnimation animation) {
        this.animation = animation;
    }

    public BedrockAnimation animation() {
        return this.animation;
    }

    public float animTime() {
        return this.animTime;
    }

    /** A {@code loop: false} animation that has run past its length: no longer posed. */
    public boolean isFinished() {
        return this.finished;
    }

    /** Whether this animation should currently contribute to the pose. */
    public boolean isPosing() {
        return this.delay <= 0.0F && !(this.finished && this.animation.loop() == BedrockAnimation.LoopMode.ONCE);
    }

    /** Restarts from the beginning, re-evaluating {@code start_delay}. */
    public void restart(MolangContext ctx) {
        this.animTime = 0.0F;
        this.finished = false;
        this.fresh = true;
        this.delay = Math.max(0.0F, (float) this.animation.startDelay().evaluate(ctx));
    }

    /**
     * Advances by {@code delta} seconds. {@code clock} lets the caller expose {@code q.anim_time} and
     * {@code q.delta_time} to {@code anim_time_update}.
     */
    public void advance(float delta, MolangContext ctx, AnimClock clock, EventSink sink) {
        if (this.finished) {
            return;
        }
        if (this.delay > 0.0F) {
            this.delay -= delta;
            if (this.delay > 0.0F) {
                return;
            }
            delta = -this.delay;
            this.delay = 0.0F;
        }

        float previous = this.fresh ? -1.0E-4F : this.animTime;
        this.fresh = false;

        float next;
        if (this.animation.animTimeUpdate().isPresent()) {
            clock.set(this.animTime, delta);
            next = (float) this.animation.animTimeUpdate().get().evaluate(ctx);
        } else {
            next = this.animTime + delta;
        }

        float length = this.animation.length();
        if (length > 0.0F && next >= length) {
            switch (this.animation.loop()) {
                case LOOP -> {
                    this.fire(previous, length, ctx, sink);
                    next -= length;
                    if (next >= length) {
                        next %= length;
                    }
                    float loopDelay = Math.max(0.0F, (float) this.animation.loopDelay().evaluate(ctx));
                    if (loopDelay > 0.0F) {
                        this.animTime = 0.0F;
                        this.delay = loopDelay;
                        this.fresh = true;
                        return;
                    }
                    this.fire(-1.0E-4F, next, ctx, sink);
                    this.animTime = next;
                }
                case ONCE, HOLD_ON_LAST_FRAME -> {
                    this.fire(previous, length, ctx, sink);
                    this.animTime = length;
                    this.finished = true;
                }
            }
            return;
        }

        this.fire(previous, next, ctx, sink);
        this.animTime = Math.max(0.0F, next);
    }

    /** Fires every event with {@code from < time <= to}. */
    private void fire(float from, float to, MolangContext ctx, EventSink sink) {
        if (to <= from) {
            return;
        }
        for (BedrockAnimation.TimedScript script : this.animation.timeline()) {
            if (script.time() > from && script.time() <= to) {
                for (MolangExpression expression : script.scripts()) {
                    expression.execute(ctx);
                }
            }
        }
        for (BedrockAnimation.TimedEffect effect : this.animation.soundEffects()) {
            if (effect.time() > from && effect.time() <= to) {
                effect.preEffectScript().ifPresent(script -> script.execute(ctx));
                sink.sound(effect);
            }
        }
        for (BedrockAnimation.TimedEffect effect : this.animation.particleEffects()) {
            if (effect.time() > from && effect.time() <= to) {
                effect.preEffectScript().ifPresent(script -> script.execute(ctx));
                sink.particle(effect);
            }
        }
    }

    /** Where {@code q.anim_time}/{@code q.delta_time} come from while {@code anim_time_update} runs. */
    public interface AnimClock {
        void set(float animTime, float deltaTime);
    }
}

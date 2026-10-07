package com.coolerpromc.ancientcreature.data.component.custom;

import com.coolerpromc.ancientcreature.entity.Species;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * The genome a creature hatches from, carried by a completed cartridge, the fertilized egg and the
 * capsule, and finally by the creature itself.
 *
 * <p>{@code fidelity} (0-1) is how faithfully the genome was reconstructed. When the embryo is made, it
 * decides the creature's traits: a high-fidelity genome gives a healthy, fertile animal close to its
 * species' norm; a poor one gives more extreme sizes, weaker constitutions, sterility and defects.
 * Bred offspring inherit the average of their parents with a little variation.
 */
public record CreatureGenome(Species species, float fidelity, float size, float vitality, float vigor, Temperament temperament, boolean fertile, boolean frail) implements TooltipProvider {
    public enum Temperament implements StringRepresentable {
        CALM, STEADY, FIERCE;

        public static final Codec<Temperament> CODEC = StringRepresentable.fromEnum(Temperament::values);

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public static final Codec<CreatureGenome> CODEC = RecordCodecBuilder.create(i -> i.group(
        Species.CODEC.fieldOf("species").forGetter(CreatureGenome::species),
        Codec.floatRange(0, 1).fieldOf("fidelity").forGetter(CreatureGenome::fidelity),
        Codec.floatRange(0.5F, 1.5F).optionalFieldOf("size", 1.0F).forGetter(CreatureGenome::size),
        Codec.floatRange(0.3F, 2.0F).optionalFieldOf("vitality", 1.0F).forGetter(CreatureGenome::vitality),
        Codec.floatRange(0.5F, 1.5F).optionalFieldOf("vigor", 1.0F).forGetter(CreatureGenome::vigor),
        Temperament.CODEC.optionalFieldOf("temperament", Temperament.STEADY).forGetter(CreatureGenome::temperament),
        Codec.BOOL.optionalFieldOf("fertile", true).forGetter(CreatureGenome::fertile),
        Codec.BOOL.optionalFieldOf("frail", false).forGetter(CreatureGenome::frail)
    ).apply(i, CreatureGenome::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CreatureGenome> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    /** What a creature from before genetics existed is treated as: an unremarkable, fertile animal. */
    public static CreatureGenome neutral(Species species) {
        return new CreatureGenome(species, GenomeData.LEGACY_FIDELITY, 1.0F, 1.0F, 1.0F, Temperament.STEADY, true, false);
    }

    /**
     * Rolls traits for an embryo. {@code precision} is the number of precision modules in the chamber;
     * each narrows the spread and cuts the chance of sterility and defects.
     */
    public static CreatureGenome roll(Species species, float fidelity, int precision, RandomSource random) {
        float f = Math.max(0.0F, Math.min(1.0F, fidelity + 0.08F * precision));
        float spread = 0.12F * (1.0F - f) + 0.03F;
        float size = clamp(1.0F + (float) random.nextGaussian() * spread, 0.8F, 1.2F);
        float vitality = clamp(0.8F + 0.3F * f + (float) random.nextGaussian() * spread, 0.6F, 1.3F);
        float vigor = clamp(0.92F + 0.12F * f + (float) random.nextGaussian() * spread * 0.6F, 0.8F, 1.15F);
        float t = random.nextFloat();
        Temperament temperament = t < 0.2F ? Temperament.CALM : t > 0.8F ? Temperament.FIERCE : Temperament.STEADY;
        boolean fertile = random.nextFloat() >= (1.0F - f) * 0.4F;
        boolean frail = random.nextFloat() < (1.0F - f) * (1.0F - f) * 0.5F;
        return new CreatureGenome(species, f, size, frail ? vitality * 0.7F : vitality, vigor, temperament, fertile, frail);
    }

    /** Offspring of two parents: the average, with a little variation, and the parents' fidelity. */
    public static CreatureGenome inherit(CreatureGenome a, CreatureGenome b, RandomSource random) {
        float f = (a.fidelity + b.fidelity) * 0.5F;
        float jitter = 0.04F;
        Temperament temperament = random.nextBoolean() ? a.temperament : b.temperament;
        boolean fertile = random.nextFloat() >= 0.05F;
        boolean frail = random.nextFloat() < (a.frail || b.frail ? 0.15F : 0.02F);
        return new CreatureGenome(a.species, f,
            clamp((a.size + b.size) * 0.5F + (float) random.nextGaussian() * jitter, 0.8F, 1.2F),
            clamp((a.vitality + b.vitality) * 0.5F + (float) random.nextGaussian() * jitter, 0.6F, 1.3F) * (frail ? 0.7F : 1.0F),
            clamp((a.vigor + b.vigor) * 0.5F + (float) random.nextGaussian() * jitter * 0.6F, 0.8F, 1.15F),
            temperament, fertile, frail);
    }

    /** The same genome, unable to breed: what every spliced hybrid is. */
    public CreatureGenome sterile() {
        return new CreatureGenome(this.species, this.fidelity, this.size, this.vitality, this.vigor, this.temperament, false, this.frail);
    }

    /** The same genome with its frailty, and the constitution it cost, removed. */
    public CreatureGenome withoutFrailty() {
        if (!this.frail) {
            return this;
        }
        return new CreatureGenome(this.species, this.fidelity, this.size, Math.min(2.0F, this.vitality / 0.7F), this.vigor, this.temperament, this.fertile, false);
    }

    private static float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components) {
        consumer.accept(Component.translatable("tooltip.ancientcreature.genome_fidelity", Math.round(this.fidelity * 100)).withStyle(ChatFormatting.GRAY));
        consumer.accept(Component.translatable("tooltip.ancientcreature.genome_traits",
            Math.round(this.size * 100), Math.round(this.vitality * 100), Math.round(this.vigor * 100),
            Component.translatable("temperament.ancientcreature." + this.temperament.getSerializedName())).withStyle(ChatFormatting.DARK_GRAY));
        if (!this.fertile) {
            consumer.accept(Component.translatable("tooltip.ancientcreature.genome_sterile").withStyle(ChatFormatting.RED));
        }
        if (this.frail) {
            consumer.accept(Component.translatable("tooltip.ancientcreature.genome_frail").withStyle(ChatFormatting.RED));
        }
    }
}

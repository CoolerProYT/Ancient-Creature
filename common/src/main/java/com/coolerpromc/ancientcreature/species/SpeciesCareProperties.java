package com.coolerpromc.ancientcreature.species;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public record SpeciesCareProperties(Climate climate, Social social, int groupMin, int groupMax, int space) {
    public static final SpeciesCareProperties DEFAULT = new SpeciesCareProperties(Climate.ANY, Social.SOLITARY, 1, 1, 0);

    public enum Climate implements StringRepresentable {
        ANY, COLD, TEMPERATE, WARM;

        public static final Codec<Climate> CODEC = StringRepresentable.fromEnum(Climate::values);

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public enum Social implements StringRepresentable {
        SOLITARY, PAIR, HERD;

        public static final Codec<Social> CODEC = StringRepresentable.fromEnum(Social::values);

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public static final Codec<SpeciesCareProperties> CODEC = RecordCodecBuilder.<SpeciesCareProperties>create(i -> i.group(
        Climate.CODEC.optionalFieldOf("climate", DEFAULT.climate()).forGetter(SpeciesCareProperties::climate),
        Social.CODEC.optionalFieldOf("social", DEFAULT.social()).forGetter(SpeciesCareProperties::social),
        Codec.intRange(1, 64).optionalFieldOf("group_min", 1).forGetter(SpeciesCareProperties::groupMin),
        Codec.intRange(1, 64).optionalFieldOf("group_max", 1).forGetter(SpeciesCareProperties::groupMax),
        Codec.intRange(0, 8192).optionalFieldOf("space", 0).forGetter(SpeciesCareProperties::space)
    ).apply(i, SpeciesCareProperties::new)).validate(care -> care.groupMax() < care.groupMin()
        ? DataResult.error(() -> "care.group_max (" + care.groupMax() + ") is below care.group_min (" + care.groupMin() + ")")
        : DataResult.success(care));

    public int spaceFor(float width) {
        if (this.space > 0) {
            return this.space;
        }
        return Math.max(24, Math.round(width * width * 36.0F));
    }
}

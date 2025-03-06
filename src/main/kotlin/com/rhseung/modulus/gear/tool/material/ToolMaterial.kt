package com.rhseung.modulus.gear.tool.material

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.rhseung.blueprint.color.Palette
import com.rhseung.blueprint.lang.Translatable
import com.rhseung.modulus.Modulus
import com.rhseung.modulus.util.Palettes
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs

data class ToolMaterial(
    val name: String,
    val type: ToolMaterialType,
    val palette: Palette,
    val stat: Stat
): Translatable {

    override val translationKey: String = Modulus.id("tool.material.$name").toTranslationKey();

    override fun toString(): String {
        return "ToolMaterial.${name.uppercase()}";
    }

    init {
        entries.add(this);
    }

    data class Stat(
        val attackDamage: Float,
        val attackSpeed: Float,
        val durability: Int,
        val enchantability: Int,
    ) {
        data class Builder(
            var attackDamage: Float = 0.0f,
            var attackSpeed: Float = 0.0f,
            var durability: Int = 0,
            var enchantability: Int = 0,
        ) {
            fun build(): Stat {
                return Stat(
                    attackDamage,
                    attackSpeed,
                    durability,
                    enchantability,
                );
            }
        }

        companion object {
            val CODEC: Codec<Stat> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.FLOAT.fieldOf("attack_damage").forGetter(Stat::attackDamage),
                    Codec.FLOAT.fieldOf("attack_speed").forGetter(Stat::attackSpeed),
                    Codec.INT.fieldOf("durability").forGetter(Stat::durability),
                    Codec.INT.fieldOf("enchantability").forGetter(Stat::enchantability)
                ).apply(instance, ::Stat);
            };

            val PACKET_CODEC: PacketCodec<ByteBuf, Stat> = PacketCodec.tuple(
                PacketCodecs.FLOAT, Stat::attackDamage,
                PacketCodecs.FLOAT, Stat::attackSpeed,
                PacketCodecs.INTEGER, Stat::durability,
                PacketCodecs.INTEGER, Stat::enchantability,
                ::Stat
            );
        }
    }

    companion object {
        val entries = mutableListOf<ToolMaterial>();
        val entriesNotDefault: List<ToolMaterial>
            get() = entries.filter { it != DEFAULT };

        val CODEC: Codec<ToolMaterial> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("name").forGetter(ToolMaterial::name),
                ToolMaterialType.CODEC.fieldOf("type").forGetter(ToolMaterial::type),
                Palette.CODEC.fieldOf("palette").forGetter(ToolMaterial::palette),
                Stat.CODEC.fieldOf("stat").forGetter(ToolMaterial::stat)
            ).apply(instance, ::ToolMaterial);
        };

        val PACKET_CODEC: PacketCodec<ByteBuf, ToolMaterial> = PacketCodec.tuple(
            PacketCodecs.STRING, ToolMaterial::name,
            ToolMaterialType.PACKET_CODEC, ToolMaterial::type,
            Palette.PACKET_CODEC, ToolMaterial::palette,
            Stat.PACKET_CODEC, ToolMaterial::stat,
            ::ToolMaterial
        );

        fun builder(name: String, type: ToolMaterialType, palette: Palette, stat: Stat.Builder.() -> Unit): ToolMaterial {
            return ToolMaterial(name, type, palette, Stat.Builder().apply(stat).build());
        }

        val DEFAULT = ToolMaterial.builder("default", ToolMaterialType.ALL, Palette.DEFAULT) {
            attackDamage = 0.0f;
            attackSpeed = 0.0f;
            durability = 10;
            enchantability = 10;
        };

        val WOOD = ToolMaterial.builder("wood", ToolMaterialType.CRUDE, Palettes.WOOD) {
            attackDamage = 2.0f;
            attackSpeed = 0.0f;
            durability = 59;
            enchantability = 15;
        };

        val STONE = ToolMaterial.builder("stone", ToolMaterialType.CRUDE, Palettes.STONE) {
            attackDamage = 3.0f;
            attackSpeed = 0.0f;
            durability = 131;
            enchantability = 5;
        };

        val IRON = ToolMaterial.builder("iron", ToolMaterialType.METAL, Palettes.IRON) {
            attackDamage = 4.0f;
            attackSpeed = 0.0f;
            durability = 250;
            enchantability = 14;
        }

        val GOLD = ToolMaterial.builder("gold", ToolMaterialType.METAL, Palettes.GOLD) {
            attackDamage = 2.0f;
            attackSpeed = 0.0f;
            durability = 32;
            enchantability = 22;
        };

        val DIAMOND = ToolMaterial.builder("diamond", ToolMaterialType.METAL, Palettes.DIAMOND) {
            attackDamage = 5.0f;
            attackSpeed = 0.0f;
            durability = 1561;
            enchantability = 10;
        };

        val NETHERITE = ToolMaterial.builder("netherite", ToolMaterialType.METAL, Palettes.NETHERITE) {
            attackDamage = 6.0f;
            attackSpeed = 0.0f;
            durability = 2031;
            enchantability = 15;
        };
    }
}
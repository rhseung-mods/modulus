package com.rhseung.modulus.init

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.rhseung.modulus.tool.*
import com.rhseung.modulus.tool.component.ToolPartsComponent
import com.rhseung.modulus.util.ARGBColor
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

object ModCodecs : IModInit {
    val TOOL_TIER: Codec<ToolTier> = Codec.INT.xmap({ it -> ToolTier.entries[it] }, ToolTier::ordinal);

    val TOOL_MATERIAL: Codec<ToolMaterial> = RecordCodecBuilder.create { instance ->
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(ToolMaterial::name),
            ARGBColor.Companion.CODEC.fieldOf("color").forGetter(ToolMaterial::color),
            TOOL_TIER.fieldOf("tier").forGetter(ToolMaterial::tier),
            Codec.INT.fieldOf("durability").forGetter(ToolMaterial::durability),
            Codec.INT.fieldOf("enchantment_value").forGetter(ToolMaterial::enchantmentValue),
            Codec.FLOAT.fieldOf("bonus_damage").forGetter(ToolMaterial::bonusDamage),
            Codec.FLOAT.fieldOf("mining_speed").forGetter(ToolMaterial::miningSpeed),
            TagKey.codec(RegistryKeys.ITEM).fieldOf("repair_tag").forGetter(ToolMaterial::repairTag)
        ).apply(instance, ::ToolMaterial)
    };

    val TOOL_POSITION: Codec<ToolPosition> = Codec.STRING.xmap(ToolPosition::valueOf, ToolPosition::name);

    val TOOL_PRIMITIVE_TYPE: Codec<ToolPrimitiveType> = Codec.STRING.xmap(ToolPrimitiveType::valueOf, ToolPrimitiveType::name);

    val TOOL_PART_TYPE: Codec<ToolPartType> = RecordCodecBuilder.create { instance ->
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(ToolPartType::name),
            TOOL_POSITION.fieldOf("position").forGetter(ToolPartType::position),
        ).apply(instance, ::ToolPartType);
    };

    val TOOL_PART: Codec<ToolPart> = RecordCodecBuilder.create { instance ->
        instance.group(
            TOOL_PART_TYPE.fieldOf("partType").forGetter(ToolPart::partType),
            TOOL_MATERIAL.fieldOf("material").forGetter(ToolPart::toolMaterial)
        ).apply(instance, ::ToolPart)
    };

    val TOOL_PARTS_COMPONENT: Codec<ToolPartsComponent> = RecordCodecBuilder.create { instance ->
        instance.group(
            Codec.list(TOOL_PART).fieldOf("toolParts").forGetter(ToolPartsComponent::toolParts)
        ).apply(instance, ::ToolPartsComponent)
    };
}
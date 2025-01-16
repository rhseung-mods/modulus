package com.rhseung.modulus.init

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.rhseung.modulus.tool.*
import com.rhseung.modulus.tool.component.ToolPartsComponent
import com.rhseung.modulus.util.ARGBColor
import com.rhseung.modulus.util.Ingredient
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

object ModCodecs : IModInit {
    val TOOL_TIER: Codec<ToolTier> = Codec.INT.xmap({ it -> ToolTier.entries[it] }, ToolTier::ordinal);

    val TOOL_MATERIAL_TYPE: Codec<ToolMaterialType> = Codec.STRING.xmap(ToolMaterialType::valueOf, ToolMaterialType::name);

    val ITEM_LIST: Codec<Ingredient> = RecordCodecBuilder.create { instance ->
        instance.group(
            Codec.list(TagKey.codec(RegistryKeys.ITEM)).fieldOf("tags").forGetter(Ingredient::tags),
            Codec.list(Item.ENTRY_CODEC).fieldOf("items").forGetter(Ingredient::items)
        ).apply(instance, ::Ingredient)
    };

    val TOOL_MATERIAL: Codec<ToolMaterial> = RecordCodecBuilder.create { instance ->
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(ToolMaterial::name),
            ARGBColor.Companion.CODEC.fieldOf("color").forGetter(ToolMaterial::color),
            TOOL_TIER.fieldOf("tier").forGetter(ToolMaterial::tier),
            TOOL_MATERIAL_TYPE.fieldOf("type").forGetter(ToolMaterial::type),
            Codec.INT.fieldOf("durability").forGetter(ToolMaterial::durability),
            Codec.INT.fieldOf("enchantment_value").forGetter(ToolMaterial::enchantmentValue),
            Codec.FLOAT.fieldOf("bonus_attack_damage").forGetter(ToolMaterial::bonusAttackDamage),
            Codec.FLOAT.fieldOf("bonus_attack_speed").forGetter(ToolMaterial::bonusAttackSpeed),
            Codec.FLOAT.fieldOf("mining_speed").forGetter(ToolMaterial::miningSpeed),
            ITEM_LIST.fieldOf("repairable").forGetter(ToolMaterial::repairable)
        ).apply(instance, ::ToolMaterial)
    };

    val TOOL_POSITION: Codec<ToolPosition> = Codec.STRING.xmap(ToolPosition::valueOf, ToolPosition::name);

    val TOOL_PART_TYPE: Codec<ToolPartType> = RecordCodecBuilder.create { instance ->
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(ToolPartType::name),
            TOOL_POSITION.fieldOf("position").forGetter(ToolPartType::position),
            Codec.list(TOOL_MATERIAL_TYPE).fieldOf("appliable_material_types").forGetter(ToolPartType::appliableMaterialTypes),
            Codec.FLOAT.fieldOf("base_attack_damage").forGetter(ToolPartType::baseAttackDamage),
            Codec.FLOAT.fieldOf("base_attack_speed").forGetter(ToolPartType::baseAttackSpeed),
            Codec.list(TagKey.codec(RegistryKeys.BLOCK)).fieldOf("mineable_block_tags").forGetter(ToolPartType::mineableBlockTags)
        ).apply(instance, ::ToolPartType);
    };

    val TOOL_PART: Codec<ToolPart> = RecordCodecBuilder.create { instance ->
        instance.group(
            TOOL_PART_TYPE.fieldOf("part_type").forGetter(ToolPart::partType),
            TOOL_MATERIAL.fieldOf("material").forGetter(ToolPart::toolMaterial)
        ).apply(instance, ::ToolPart)
    };

    val TOOL_TYPE: Codec<ToolType> = Codec.STRING.xmap(ToolType::valueOf, ToolType::name);

    val TOOL_PARTS_COMPONENT: Codec<ToolPartsComponent> = RecordCodecBuilder.create { instance ->
        instance.group(
            TOOL_TYPE.fieldOf("tool_type").forGetter(ToolPartsComponent::toolType),
            Codec.unboundedMap(TOOL_POSITION, TOOL_PART)
                .fieldOf("tool_part_by_position")
                .forGetter(ToolPartsComponent::toolPartByPosition)
        ).apply(instance, ::ToolPartsComponent)
    };
}
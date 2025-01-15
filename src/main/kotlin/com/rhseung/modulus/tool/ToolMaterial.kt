package com.rhseung.modulus.tool

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.util.ARGBColor
import com.rhseung.modulus.util.RGBColor
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.ItemTags
import net.minecraft.registry.tag.TagKey

data class ToolMaterial(
    val name: String,
    val color: ARGBColor,
    val tier: ToolTier,
    val durability: Int,
    val enchantmentValue: Int,
    val bonusAttackDamage: Float,
    val bonusAttackSpeed: Float,
    val miningSpeed: Float,
    val repairTag: TagKey<Item> = TagKey.of(RegistryKeys.ITEM, Modulus.id(name))
) {

    init {
        VALUES_WITH_EMPTY.add(this);
        if (this.name != "empty" && this.name != "unknown") {
            VALUES.add(this);
        }
    }

    override fun toString(): String {
        return name.uppercase();
    }

    companion object {
        fun DEFAULT(toolType: ToolType, partType: ToolPartType): ToolMaterial {
            return if (toolType.isOptionalPart(partType)) EMPTY else UNKNOWN;
        }

        val VALUES_WITH_EMPTY = mutableListOf<ToolMaterial>();
        val VALUES = mutableListOf<ToolMaterial>();

        val EMPTY = ToolMaterial(
            "empty",
            RGBColor.WHITE.zeroAlpha(),
            ToolTier.WOOD,
            0,
            0,
            0f,
            0f,
            0f,
        );

        val UNKNOWN = ToolMaterial(
            "unknown",
            RGBColor.WHITE.fullAlpha(),
            ToolTier.WOOD,
            10,
            10,
            0f,
            0f,
            2f,
        );

        val WOOD = ToolMaterial(
            "wood",
            RGBColor.WOOD.fullAlpha(),
            ToolTier.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            ItemTags.WOODEN_TOOL_MATERIALS
        );

        val STONE = ToolMaterial(
            "stone",
            RGBColor.STONE.fullAlpha(),
            ToolTier.STONE,
            35,
            5,
            1f,
            0f,
            4f,
            ItemTags.STONE_TOOL_MATERIALS
        );

        val IRON = ToolMaterial(
            "iron",
            RGBColor.IRON.fullAlpha(),
            ToolTier.IRON,
            65,
            5,
            2f,
            0f,
            6f,
            ItemTags.IRON_TOOL_MATERIALS
        );

        val DIAMOND = ToolMaterial(
            "diamond",
            RGBColor.DIAMOND.fullAlpha(),
            ToolTier.DIAMOND,
            400,
            3,
            3f,
            0f,
            8f,
            ItemTags.DIAMOND_TOOL_MATERIALS
        );

        val GOLD = ToolMaterial(
            "gold",
            RGBColor.GOLD.fullAlpha(),
            ToolTier.WOOD,
            10,
            7,
            0f,
            0f,
            12f,
            ItemTags.GOLD_TOOL_MATERIALS
        );

        val NETHERITE = ToolMaterial(
            "netherite",
            RGBColor.NETHERITE.fullAlpha(),
            ToolTier.NETHERITE,
            510,
            4,
            4f,
            0f,
            9f,
            ItemTags.NETHERITE_TOOL_MATERIALS
        );
    }
}
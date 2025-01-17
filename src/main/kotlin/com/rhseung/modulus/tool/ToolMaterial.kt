package com.rhseung.modulus.tool

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.util.ARGBColor
import com.rhseung.modulus.util.Colors
import com.rhseung.modulus.util.Ingredient
import net.minecraft.item.Items
import net.minecraft.registry.tag.ItemTags

data class ToolMaterial(
    val name: String,
    val color: ARGBColor,
    val tier: ToolTier,
    val type: ToolMaterialType,
    val durability: Int,
    val enchantmentValue: Int,
    val bonusAttackDamage: Float,
    val bonusAttackSpeed: Float,
    val miningSpeed: Float,
    val repairable: Ingredient = Ingredient.EMPTY
): Translatable {

    init {
        VALUES_WITH_EMPTY.add(this);
        if (!this.name.startsWith("default"))
            VALUES.add(this);
    }

    override fun toString(): String {
        return name.uppercase();
    }

    override fun equals(other: Any?): Boolean {
        return other is ToolMaterial &&
            other.name == name &&
            other.color == color &&
            other.tier == tier &&
            other.type == type &&
            other.durability == durability &&
            other.enchantmentValue == enchantmentValue &&
            other.bonusAttackDamage == bonusAttackDamage &&
            other.bonusAttackSpeed == bonusAttackSpeed &&
            other.miningSpeed == miningSpeed &&
            other.repairable == repairable;
    }

    override fun hashCode(): Int {
        var result = durability
        result = 31 * result + enchantmentValue
        result = 31 * result + bonusAttackDamage.hashCode()
        result = 31 * result + bonusAttackSpeed.hashCode()
        result = 31 * result + miningSpeed.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + tier.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + repairable.hashCode()
        result = 31 * result + translationKey.hashCode()
        return result
    }

    override val translationKey: String = Modulus.id("material.${name}").toTranslationKey();

    companion object {
        val VALUES_WITH_EMPTY = mutableListOf<ToolMaterial>();
        val VALUES = mutableListOf<ToolMaterial>();

        val DEFAULT = ToolMaterial(
            "default",
            Colors.TRANSPARENT,
            ToolTier.WOOD,
            ToolMaterialType.ALL,
            10,
            10,
            0f,
            0f,
            10f,
        );

        val WOOD = ToolMaterial(
            "wood",
            Colors.WOOD,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(ItemTags.WOODEN_TOOL_MATERIALS)
        );

        val OAK_WOOD = ToolMaterial(
            "oak_wood",
            Colors.OAK_WOOD,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.OAK_PLANKS)
        );

        val SPRUCE_WOOD = ToolMaterial(
            "spruce_wood",
            Colors.SPRUCE_WOOD,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.SPRUCE_PLANKS)
        );

        val BIRCH_WOOD = ToolMaterial(
            "birch_wood",
            Colors.BIRCH_WOOD,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.BIRCH_PLANKS)
        );

        val JUNGLE_WOOD = ToolMaterial(
            "jungle_wood",
            Colors.JUNGLE_WOOD,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.JUNGLE_PLANKS)
        );

        val ACACIA_WOOD = ToolMaterial(
            "acacia_wood",
            Colors.ACACIA_WOOD,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.ACACIA_PLANKS)
        );

        val DARK_OAK_WOOD = ToolMaterial(
            "dark_oak_wood",
            Colors.DARK_OAK_WOOD,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.DARK_OAK_PLANKS)
        );

        val BAMBOO_WOOD = ToolMaterial(
            "bamboo_wood",
            Colors.BAMBOO_WOOD,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.BAMBOO_PLANKS)
        );

        val CHERRY_WOOD = ToolMaterial(
            "cherry_wood",
            Colors.CHERRY_WOOD,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.CHERRY_PLANKS)
        );

        val MANGROVE_WOOD = ToolMaterial(
            "mangrove_wood",
            Colors.MANGROVE_WOOD,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.MANGROVE_PLANKS)
        );

        val PALE_OAK_WOOD = ToolMaterial(
            "pale_oak_wood",
            Colors.PALE_OAK_WOOD,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.PALE_OAK_PLANKS)
        );

        val CRIMSON_HYPHAE = ToolMaterial(
            "crimson_hyphae",
            Colors.CRIMSON_HYPHAE,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.CRIMSON_PLANKS)
        );

        val WARPED_HYPHAE = ToolMaterial(
            "warped_hyphae",
            Colors.WARPED_HYPHAE,
            ToolTier.WOOD,
            ToolMaterialType.WOOD,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.WARPED_PLANKS)
        );

        val STONE = ToolMaterial(
            "stone",
            Colors.STONE,
            ToolTier.STONE,
            ToolMaterialType.STONE,
            35,
            5,
            1f,
            0f,
            4f,
            Ingredient(ItemTags.STONE_TOOL_MATERIALS)
        );

        val COBBLESTONE = ToolMaterial(
            "cobblestone",
            Colors.COBBLESTONE,
            ToolTier.STONE,
            ToolMaterialType.STONE,
            35,
            5,
            1f,
            0f,
            4f,
            Ingredient(Items.COBBLESTONE)
        );

        val DEEPSLATE = ToolMaterial(
            "deepslate",
            Colors.DEEPSLATE,
            ToolTier.STONE,
            ToolMaterialType.STONE,
            35,
            5,
            1f,
            0f,
            4f,
            Ingredient(Items.DEEPSLATE)
        );

        val COBBLED_DEEPSLATE = ToolMaterial(
            "cobbled_deepslate",
            Colors.COBBLED_DEEPSLATE,
            ToolTier.STONE,
            ToolMaterialType.STONE,
            35,
            5,
            1f,
            0f,
            4f,
            Ingredient(Items.COBBLED_DEEPSLATE)
        );

        val FLINT = ToolMaterial(
            "flint",
            Colors.FLINT,
            ToolTier.STONE,
            ToolMaterialType.STONE,
            35,
            5,
            1f,
            0f,
            4f,
            Ingredient(Items.FLINT)
        );

        val GRANITE = ToolMaterial(
            "granite",
            Colors.GRANITE,
            ToolTier.STONE,
            ToolMaterialType.STONE,
            35,
            5,
            1f,
            0f,
            4f,
            Ingredient(Items.GRANITE)
        );

        val DIORITE = ToolMaterial(
            "diorite",
            Colors.DIORITE,
            ToolTier.STONE,
            ToolMaterialType.STONE,
            35,
            5,
            1f,
            0f,
            4f,
            Ingredient(Items.DIORITE)
        );

        val ANDESITE = ToolMaterial(
            "andesite",
            Colors.ANDESITE,
            ToolTier.STONE,
            ToolMaterialType.STONE,
            35,
            5,
            1f,
            0f,
            4f,
            Ingredient(Items.ANDESITE)
        );

        val BLACKSTONE = ToolMaterial(
            "blackstone",
            Colors.BLACKSTONE,
            ToolTier.STONE,
            ToolMaterialType.STONE,
            35,
            5,
            1f,
            0f,
            4f,
            Ingredient(Items.BLACKSTONE)
        );

        val BASALT = ToolMaterial(
            "basalt",
            Colors.BASALT,
            ToolTier.STONE,
            ToolMaterialType.STONE,
            35,
            5,
            1f,
            0f,
            4f,
            Ingredient(Items.BASALT)
        );

        val OBSIDIAN = ToolMaterial(
            "obsidian",
            Colors.OBSIDIAN,
            ToolTier.STONE,
            ToolMaterialType.STONE,
            35,
            5,
            1f,
            0f,
            4f,
            Ingredient(Items.OBSIDIAN)
        );

        val GOLD = ToolMaterial(
            "gold",
            Colors.GOLD,
            ToolTier.WOOD,
            ToolMaterialType.METAL,
            10,
            7,
            0f,
            0f,
            12f,
            Ingredient(ItemTags.GOLD_TOOL_MATERIALS)
        );

        val COPPER = ToolMaterial(
            "copper",
            Colors.COPPER,
            ToolTier.STONE,
            ToolMaterialType.METAL,
            45,
            5,
            1f,
            0f,
            4f,
            Ingredient(Items.COPPER_INGOT)
        );

        val IRON = ToolMaterial(
            "iron",
            Colors.IRON,
            ToolTier.IRON,
            ToolMaterialType.METAL,
            65,
            5,
            2f,
            0f,
            6f,
            Ingredient(ItemTags.IRON_TOOL_MATERIALS)
        );

        val DIAMOND = ToolMaterial(
            "diamond",
            Colors.DIAMOND,
            ToolTier.DIAMOND,
            ToolMaterialType.METAL,
            400,
            3,
            3f,
            0f,
            8f,
            Ingredient(ItemTags.DIAMOND_TOOL_MATERIALS)
        );

        val NETHERITE = ToolMaterial(
            "netherite",
            Colors.NETHERITE,
            ToolTier.NETHERITE,
            ToolMaterialType.METAL,
            510,
            4,
            4f,
            0f,
            9f,
            Ingredient(ItemTags.NETHERITE_TOOL_MATERIALS)
        );

        val STRING = ToolMaterial(
            "string",
            Colors.STRING,
            ToolTier.WOOD,
            ToolMaterialType.FIBER,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.STRING)
        );

        val LEATHER = ToolMaterial(
            "leather",
            Colors.LEATHER,
            ToolTier.WOOD,
            ToolMaterialType.FIBER,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.LEATHER)
        );

        val RABBIT_HIDE = ToolMaterial(
            "rabbit_hide",
            Colors.RABBIT_HIDE,
            ToolTier.WOOD,
            ToolMaterialType.FIBER,
            15,
            5,
            0f,
            0f,
            2f,
            Ingredient(Items.RABBIT_HIDE)
        );

        // todo: wool
        // todo: netherite면 fireproof 인 것처럼 특성 부여
    }
}
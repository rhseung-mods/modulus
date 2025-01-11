package com.rhseung.modulus.tool

import com.rhseung.modulus.Modulus
import net.minecraft.block.Block
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey

enum class ToolTier(incorrectBlockTag: TagKey<Block>? = null, newMineableBlockTag: TagKey<Block>? = null) {
    WOOD(BlockTags.INCORRECT_FOR_WOODEN_TOOL),
    STONE(BlockTags.INCORRECT_FOR_STONE_TOOL, BlockTags.NEEDS_STONE_TOOL),
    IRON(BlockTags.INCORRECT_FOR_IRON_TOOL, BlockTags.NEEDS_IRON_TOOL),
    DIAMOND(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, BlockTags.NEEDS_DIAMOND_TOOL),
    NETHERITE(BlockTags.INCORRECT_FOR_NETHERITE_TOOL);

    val incorrectBlockTag: TagKey<Block> =
        incorrectBlockTag ?: TagKey.of(RegistryKeys.BLOCK, Modulus.id("incorrect_for_${name.lowercase()}_tool"));

    val newMineableBlockTag: TagKey<Block> =
        newMineableBlockTag ?: TagKey.of(RegistryKeys.BLOCK, Modulus.id("needs_${name.lowercase()}_tool"));

    val level = ordinal;
}
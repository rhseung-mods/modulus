package com.rhseung.modulus.tool

import net.minecraft.block.Block
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey

open class ToolPartType(
    val name: String,
    val position: ToolPosition,
    val baseAttackDamage: Float,
    val baseAttackSpeed: Float,
    val mineableBlockTags: List<TagKey<Block>> = emptyList()
) {
    override fun toString(): String {
        return name.uppercase();
    }

    override fun equals(other: Any?): Boolean {
        return other is ToolPartType &&
            other.name == name &&
            other.position == position &&
            other.baseAttackDamage == baseAttackDamage &&
            other.baseAttackSpeed == baseAttackSpeed &&
            other.mineableBlockTags == mineableBlockTags;
    }

    override fun hashCode(): Int {
        var result = name.hashCode();
        result = 31 * result + position.hashCode();
        return result;
    }

    operator fun component1() = name;

    operator fun component2() = position;

    fun withMaterial(material: ToolMaterial): ToolPart {
        return ToolPart(this, material);
    }

    init {
        VALUES.add(this);
    }

    companion object {
        val VALUES = mutableListOf<ToolPartType>();

        val PICKAXE_LEFT_HEAD = ToolPartType(
            "pickaxe_left_head",
            ToolPosition.LEFT_HEAD,
            0.5f,
            -0.9f,
            listOf(BlockTags.PICKAXE_MINEABLE)
        );

        val PICKAXE_RIGHT_HEAD = ToolPartType(
            "pickaxe_right_head",
            ToolPosition.RIGHT_HEAD,
            0.5f,
            -0.9f,
            listOf(BlockTags.PICKAXE_MINEABLE)
        );

        val AXE_HEAD = ToolPartType(
            "axe_head",
            ToolPosition.LEFT_HEAD,
            5f,
            -3f,
            listOf(BlockTags.AXE_MINEABLE)
        );

        val SHOVEL_HEAD = ToolPartType(
            "shovel_head",
            ToolPosition.HEAD,
            1.5f,
            -2f,
            listOf(BlockTags.SHOVEL_MINEABLE)
        );

        val HOE_HEAD = ToolPartType(
            "hoe_head",
            ToolPosition.LEFT_HEAD,
            0f,
            -2f,
            listOf(BlockTags.HOE_MINEABLE)
        );

        val BUTT_HEAD = ToolPartType(
            "butt_head",
            ToolPosition.RIGHT_HEAD,
            0f,
            1f,
        );

        val HANDLE = ToolPartType(
            "handle",
            ToolPosition.HANDLE,
            0f,
            2f
        );

        val BINDING = ToolPartType(
            "binding",
            ToolPosition.BINDING,
            0f,
            1f
        );

        val GRIP = ToolPartType(
            "grip",
            ToolPosition.GRIP,
            0f,
            0f
        );

        val EXTRA = ToolPartType(
            "extra",
            ToolPosition.EXTRA,
            0f,
            0f
        );
    }
}
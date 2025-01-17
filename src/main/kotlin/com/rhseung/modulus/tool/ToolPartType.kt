package com.rhseung.modulus.tool

import com.rhseung.modulus.Modulus
import net.minecraft.block.Block
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey

open class ToolPartType(
    val name: String,
    val position: ToolPosition,
    appliableMaterialTypes: List<ToolMaterialType>,
    val baseAttackDamage: Float,
    val baseAttackSpeed: Float,
    val mineableBlockTags: List<TagKey<Block>> = emptyList(),
    val actions: List<ToolAction> = emptyList()
): Translatable {

    constructor(
        name: String,
        position: ToolPosition,
        baseAttackDamage: Float,
        baseAttackSpeed: Float,
        mineableBlockTags: List<TagKey<Block>> = emptyList(),
        actions: List<ToolAction> = emptyList()
    ) : this(name, position, ToolMaterialType.entries, baseAttackDamage, baseAttackSpeed, mineableBlockTags, actions);

    val appliableMaterialTypes: List<ToolMaterialType> = (appliableMaterialTypes + ToolMaterialType.ALL).distinct();

    override val translationKey: String = Modulus.id("part.$name").toTranslationKey();

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
        VALUES_WITH_EMPTY.add(this);
        if (!name.startsWith("default"))
            VALUES.add(this);
    }

    companion object {
        val VALUES_WITH_EMPTY = mutableListOf<ToolPartType>();
        val VALUES = mutableListOf<ToolPartType>();

        val DEFAULT = ToolPosition.entries.associateWith { position ->
            ToolPartType(
                "default_${position.name.lowercase()}",
                position,
                1f,
                1f
            )
        }

        val PICKAXE_LEFT_HEAD = ToolPartType(
            "pickaxe_left_head",
            ToolPosition.LEFT_HEAD,
            listOf(
                ToolMaterialType.WOOD,
                ToolMaterialType.STONE,
                ToolMaterialType.METAL,
            ),
            0.5f,
            -0.9f,
            listOf(BlockTags.PICKAXE_MINEABLE),
        );

        val PICKAXE_RIGHT_HEAD = ToolPartType(
            "pickaxe_right_head",
            ToolPosition.RIGHT_HEAD,
            listOf(
                ToolMaterialType.WOOD,
                ToolMaterialType.STONE,
                ToolMaterialType.METAL,
            ),
            0.5f,
            -0.9f,
            listOf(BlockTags.PICKAXE_MINEABLE)
        );

        val AXE_LEFT_HEAD = ToolPartType(
            "axe_left_head",
            ToolPosition.LEFT_HEAD,
            listOf(
                ToolMaterialType.WOOD,
                ToolMaterialType.STONE,
                ToolMaterialType.METAL,
            ),
            5f,
            -1.5f,
            listOf(BlockTags.AXE_MINEABLE),
            ToolAction.AXE_ACTIONS
        );

        val AXE_RIGHT_HEAD = ToolPartType(
            "axe_right_head",
            ToolPosition.RIGHT_HEAD,
            listOf(
                ToolMaterialType.WOOD,
                ToolMaterialType.STONE,
                ToolMaterialType.METAL,
            ),
            5f,
            -1.5f,
            listOf(BlockTags.AXE_MINEABLE),
            ToolAction.AXE_ACTIONS
        );

        val SHOVEL_HEAD = ToolPartType(
            "shovel_head",
            ToolPosition.HEAD,
            listOf(
                ToolMaterialType.WOOD,
                ToolMaterialType.STONE,
                ToolMaterialType.METAL,
            ),
            1.5f,
            -3f,
            listOf(BlockTags.SHOVEL_MINEABLE),
            ToolAction.SHOVEL_ACTIONS
        );

        val HOE_HEAD = ToolPartType(
            "hoe_head",
            ToolPosition.LEFT_HEAD,
            listOf(
                ToolMaterialType.WOOD,
                ToolMaterialType.STONE,
                ToolMaterialType.METAL,
            ),
            0f,
            -1.5f,
            listOf(BlockTags.HOE_MINEABLE),
            ToolAction.HOE_ACTIONS
        );

        val BUTT_HEAD = ToolPartType(
            "butt_head",
            ToolPosition.RIGHT_HEAD,
            listOf(
                ToolMaterialType.WOOD,
                ToolMaterialType.STONE,
                ToolMaterialType.METAL,
            ),
            0f,
            1.5f,
        );

        val HANDLE = ToolPartType(
            "handle",
            ToolPosition.HANDLE,
            listOf(
                ToolMaterialType.WOOD,
                ToolMaterialType.STONE,
                ToolMaterialType.METAL,
            ),
            0f,
            4f
        );

        val BINDING = ToolPartType(
            "binding",
            ToolPosition.BINDING,
            listOf(
                ToolMaterialType.FIBER,
            ),
            0f,
            1f
        );

        val GRIP = ToolPartType(
            "grip",
            ToolPosition.GRIP,
            listOf(
                ToolMaterialType.FIBER,
            ),
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
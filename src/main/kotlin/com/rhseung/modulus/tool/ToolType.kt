package com.rhseung.modulus.tool

import com.rhseung.modulus.tool.ToolPartType.Companion.main
import net.minecraft.block.Block
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey

data class ToolType(
    val name: String,
    val primitiveType: ToolPrimitiveType,
    val baseAttackDamage: Float,
    val baseAttackSpeed: Float,
    val mineableBlockTags: List<TagKey<Block>>,
    val partTypes: List<ToolPartType>,
    val optionalPartTypes: List<ToolPartType>
) {
    val necessaryPartTypes: List<ToolPartType> get() = partTypes;
    val everyPartTypes = necessaryPartTypes + optionalPartTypes;
    val mainPartType = partTypes.first { it.isMain };

    val positions = everyPartTypes.associate { it.position to it };

    fun isOptionalPart(partType: ToolPartType) = partType in optionalPartTypes;

    fun isOptionalPart(part: ToolPart) = part.partType in optionalPartTypes;

    fun isNecessaryPart(partType: ToolPartType) = partType in partTypes;

    fun isNecessaryPart(part: ToolPart) = part.partType in partTypes;

    fun withMaterials(vararg materials: Pair<ToolPartType, ToolMaterial>): List<ToolPart> {
        return materials.map { it.first.withMaterial(it.second) };
    }

    fun withSameMaterial(material: ToolMaterial): List<ToolPart> {
        return withMaterials(*everyPartTypes.map { it to material }.toTypedArray());
    }

    fun withSameMaterialNecessary(material: ToolMaterial): List<ToolPart> {
        return withMaterials(*necessaryPartTypes.map { it to material }.toTypedArray());
    }

    override fun toString(): String {
        return name.uppercase();
    }

    init {
        require(mineableBlockTags.isNotEmpty()) { "ToolType $name has no mineable block tags" };
        require(partTypes.count { it.isMain } == 1) { "ToolType $name has no or multiple main parts" };
        require(optionalPartTypes.all { !it.isMain }) { "ToolType $name has optional main parts" };

        for ((pos1, _) in positions) {
            val subPositions = pos1.subPositions;
            if (subPositions.isEmpty())
                continue;

            for ((pos2, _) in positions) {
                if (pos1 == pos2)
                    continue;

                if (pos2 in subPositions)
                    throw IllegalArgumentException("Position $pos2 is a subposition of $pos1, positions=$positions");
            }
        }

        VALUES.add(this);
    }

    class Builder(val name: String, val primitiveType: ToolPrimitiveType) {
        private val mineableBlockTags: MutableList<TagKey<Block>> = mutableListOf();
        private var baseAttackDamage: Float = 0f;
        private var baseAttackSpeed: Float = 0f;
        private var partTypes: List<ToolPartType> = emptyList();
        private var optionalPartTypes: List<ToolPartType> = emptyList();

        fun addMineable(mineableBlockTag: TagKey<Block>) = apply { this.mineableBlockTags.add(mineableBlockTag) };
        fun setBaseAttackDamage(baseAttackDamage: Float) = apply { this.baseAttackDamage = baseAttackDamage };
        fun setBaseAttackSpeed(baseAttackSpeed: Float) = apply { this.baseAttackSpeed = baseAttackSpeed };
        fun setPartTypes(vararg partTypes: ToolPartType) = apply { this.partTypes = partTypes.toList() };
        fun setOptionalPartTypes(vararg optionalPartTypes: ToolPartType) = apply { this.optionalPartTypes = optionalPartTypes.toList() };

        fun build(): ToolType {
            return ToolType(
                name,
                primitiveType,
                baseAttackDamage,
                baseAttackSpeed,
                mineableBlockTags,
                partTypes,
                optionalPartTypes
            );
        };
    }

    companion object {
        val VALUES = mutableListOf<ToolType>();

        val PICKAXE = ToolType.Builder("pickaxe", ToolPrimitiveType.MINING)
            .addMineable(BlockTags.PICKAXE_MINEABLE)
            .setBaseAttackDamage(1f)
            .setBaseAttackSpeed(-2.8f)
            .setPartTypes(
                ToolPartType.HANDLE,
                ToolPartType.PICKAXE_RIGHT_HEAD,
                main(ToolPartType.PICKAXE_LEFT_HEAD),
                ToolPartType.BINDING,
            )
            .setOptionalPartTypes(
                ToolPartType.GRIP,
                ToolPartType.EXTRA
            )
            .build();

        val AXE = ToolType.Builder("axe", ToolPrimitiveType.MINING)
            .addMineable(BlockTags.AXE_MINEABLE)
            .setBaseAttackDamage(5f)
            .setBaseAttackSpeed(-3f)
            .setPartTypes(
                ToolPartType.HANDLE,
                main(ToolPartType.AXE_HEAD),
                ToolPartType.BINDING,
            )
            .setOptionalPartTypes(
                ToolPartType.GRIP,
                ToolPartType.EXTRA
            )
            .build();

        val ADZE = ToolType.Builder("adze", ToolPrimitiveType.MINING)
            .addMineable(BlockTags.PICKAXE_MINEABLE)
            .addMineable(BlockTags.AXE_MINEABLE)
            .setBaseAttackDamage(3f)
            .setBaseAttackSpeed(-3f)
            .setPartTypes(
                ToolPartType.HANDLE,
                ToolPartType.PICKAXE_RIGHT_HEAD,
                main(ToolPartType.AXE_HEAD),
                ToolPartType.BINDING,
            )
            .setOptionalPartTypes(
                ToolPartType.GRIP,
                ToolPartType.EXTRA
            )
            .build();

        val SHOVEL = ToolType.Builder("shovel", ToolPrimitiveType.MINING)
            .addMineable(BlockTags.SHOVEL_MINEABLE)
            .setBaseAttackDamage(1.5f)
            .setBaseAttackSpeed(-3f)
            .setPartTypes(
                ToolPartType.HANDLE,
                main(ToolPartType.SHOVEL_HEAD),
                ToolPartType.BINDING,
            )
            .setOptionalPartTypes(
                ToolPartType.GRIP,
                ToolPartType.EXTRA
            )
            .build();

        val HOE = ToolType.Builder("hoe", ToolPrimitiveType.MINING)
            .addMineable(BlockTags.HOE_MINEABLE)
            .setBaseAttackDamage(0f)
            .setBaseAttackSpeed(0f)
            .setPartTypes(
                ToolPartType.HANDLE,
                main(ToolPartType.HOE_HEAD),
                ToolPartType.BINDING,
            )
            .setOptionalPartTypes(
                ToolPartType.GRIP,
                ToolPartType.EXTRA
            )
            .build();
    }
}
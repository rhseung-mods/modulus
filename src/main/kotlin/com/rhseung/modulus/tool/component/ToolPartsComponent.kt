package com.rhseung.modulus.tool.component

import com.rhseung.modulus.tool.ToolMaterial
import com.rhseung.modulus.tool.ToolPart
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.tool.ToolPosition
import com.rhseung.modulus.tool.ToolTier
import com.rhseung.modulus.tool.ToolType
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.registry.tag.TagKey

class ToolPartsComponent(val toolType: ToolType, val toolPartByPosition: Map<ToolPosition, ToolPart>) {
    init {
        val necessaryPartPositions = toolType.necessaryPartPositions;
        val optionalPartPositions = toolType.optionalPartPositions;

        val missingNecessaryPositions = necessaryPartPositions.filter { it !in toolPartByPosition.keys };
        if (missingNecessaryPositions.isNotEmpty())
            throw IllegalArgumentException("Missing necessary positions: $missingNecessaryPositions for $toolType");

        val extraPositions = toolPartByPosition.keys - toolType.everyPartPositions;
        if (extraPositions.isNotEmpty())
            throw IllegalArgumentException("Extra positions: $extraPositions for $toolType");
    }

    val toolPositions: List<ToolPosition> = toolPartByPosition.keys.toList();
    val toolParts: List<ToolPart> = toolPartByPosition.values.toList();
    val toolPartTypes: List<ToolPartType> = toolParts.map { it.partType };
    val toolMaterials: List<ToolMaterial> = toolParts.map { it.toolMaterial };

    /**
     * enchantability = sum of enchantability of all materials
     */
    val enchantmentValue: Int = toolMaterials.sumOf { it.enchantmentValue };

    /**
     * durability = sum of durability of all materials
     */
    val durability: Int = toolMaterials.sumOf { it.durability };

    /**
     * repairTags = list of repair tags of all materials
     */
    val repairTags: List<TagKey<Item>> = toolMaterials.map { it.repairTag };

    /**
     * attackDamage = sum of base attack damage to all parts + sum of bonus attack damage to all materials
     */
    val bonusAttackDamage: Double = toolMaterials.sumOf { it.bonusAttackDamage.toDouble() };
    val baseAttackDamage: Double = toolPartTypes.sumOf { it.baseAttackDamage.toDouble() };
    val attackDamage: Double = baseAttackDamage + bonusAttackDamage;

    /**
     * attackSpeed = sum of base attack speed to all parts + sum of bonus attack speed to all materials
     */
    val bonusAttackSpeed: Double = toolMaterials.sumOf { it.bonusAttackSpeed.toDouble() };
    val baseAttackSpeed: Double = toolPartTypes.sumOf { it.baseAttackSpeed.toDouble() };
    val attackSpeed: Double = baseAttackSpeed + bonusAttackSpeed;

    /**
     * miningSpeed = sum of mining speed of all materials
     */
    val miningSpeed: Double = toolMaterials.sumOf { it.miningSpeed.toDouble() };

    /**
     * miningLevel = max mining level of all materials
     */
    val maxTier: ToolTier = toolMaterials.maxBy { it.tier.level }.tier;

    /**
     * mineableBlockTags = list of mineable block tags of all materials
     */
    val mineableBlockTags: List<TagKey<Block>> = toolPartTypes.flatMap { it.mineableBlockTags };

    operator fun get(position: ToolPosition): ToolPart? = toolPartByPosition[position];

    operator fun get(partType: ToolPartType): ToolPart? = toolParts.firstOrNull { it.partType == partType };

    operator fun get(idx: Int): ToolPart? = toolParts.getOrNull(idx);

    operator fun contains(position: ToolPosition) = toolPartByPosition.contains(position);

    fun forEach(action: (ToolPosition, ToolPart) -> Unit) {
        toolPartByPosition.forEach(action);
    }

    fun forEachIndexed(action: (Int, ToolPosition, ToolPart) -> Unit) {
        toolPartByPosition.entries.forEachIndexed { i, (position, part) -> action(i, position, part) };
    }

    override fun toString(): String {
        return "ToolPartsComponent(toolType=$toolType, $toolPartByPosition)";
    }
}
package com.rhseung.modulus.tool.component

import com.rhseung.modulus.tool.ToolPart
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.tool.ToolType

class ToolPartsComponent(val toolParts: List<ToolPart>) {
    val toolPartTypes = toolParts.map { it.partType };

    val enchantmentValue = toolParts.sumOf { it.toolMaterial.enchantmentValue };
    val durability = toolParts.sumOf { it.toolMaterial.durability };
    val repairTags = toolParts.map { it.toolMaterial.repairTag };
    val bonusAttackDamage = toolParts.sumOf { it.toolMaterial.bonusDamage.toDouble() };
    val miningSpeed = toolParts.sumOf { it.toolMaterial.miningSpeed.toDouble() };
    val maxTier = toolParts.maxBy { it.toolMaterial.tier.level }.toolMaterial.tier;

    operator fun get(partType: ToolPartType): ToolPart? = toolParts.firstOrNull { it.partType == partType };

    operator fun get(idx: Int): ToolPart? = toolParts.getOrNull(idx);

    fun toList() = toolParts;

    override fun toString(): String {
        return "ToolPartsComponent($toolParts)";
    }

    companion object {
        val DEFAULT = { toolType: ToolType -> ToolPartsComponent(toolType.withMaterials()) };
    }
}
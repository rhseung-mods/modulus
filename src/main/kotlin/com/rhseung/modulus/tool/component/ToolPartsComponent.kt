package com.rhseung.modulus.tool.component

import com.rhseung.modulus.tool.ToolMaterial
import com.rhseung.modulus.tool.ToolPart
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.tool.ToolPosition
import com.rhseung.modulus.tool.ToolTier
import com.rhseung.modulus.tool.ToolType
import com.rhseung.modulus.util.ARGBColor
import net.minecraft.block.Block
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.Item
import net.minecraft.registry.tag.TagKey

class ToolPartsComponent(val toolType: ToolType, val toolPartByPosition: Map<ToolPosition, ToolPart>) {

    val toolPositions: List<ToolPosition> = toolType.everyPartPositions.filter { toolPartByPosition.containsKey(it) };
    val toolParts: List<ToolPart> = toolPositions.map { toolPartByPosition[it]!! };
    val toolPartTypes: List<ToolPartType> = toolParts.map { it.partType };
    val toolMaterials: List<ToolMaterial> = toolParts.map { it.toolMaterial };
    val size: Int = toolPartByPosition.size;

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
    val attackSpeed: Double = baseAttackSpeed + bonusAttackSpeed - EntityAttributes.ATTACK_SPEED.value().defaultValue;   // attackSpeed - 4F

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

    init {
        val necessaryPartPositions = toolType.necessaryPartPositions;

        val missingNecessaryPositions = necessaryPartPositions.filter { it !in toolPartByPosition.keys };
        if (missingNecessaryPositions.isNotEmpty())
            throw IllegalArgumentException("Missing necessary positions: $missingNecessaryPositions for $toolType");

        val extraPositions = toolPartByPosition.keys - toolType.everyPartPositions;
        if (extraPositions.isNotEmpty())
            throw IllegalArgumentException("Extra positions: $extraPositions for $toolType");

        if (durability <= 0)
            throw IllegalArgumentException("Durability must be positive: $durability for $toolPartByPosition");

        if (attackSpeed <= -EntityAttributes.ATTACK_SPEED.value().defaultValue)     // -4f
            throw IllegalArgumentException("Attack speed must be positive: $attackSpeed for $toolPartByPosition");
    }

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

    fun getColor(tintIndex: Int): ARGBColor {
        return get(toolType.everyPartPositions[tintIndex])!!.toolMaterial.color;
    }

    override fun toString(): String {
        return "ToolPartsComponent(toolType=$toolType, $toolPartByPosition)";
    }
}
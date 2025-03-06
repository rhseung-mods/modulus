package com.rhseung.modulus.gear.tool.synergy

import com.rhseung.blueprint.lang.Translatable
import com.rhseung.modulus.Modulus
import com.rhseung.modulus.gear.tool.part.ToolPartPosition
import com.rhseung.modulus.gear.tool.part.ToolPartType
import com.rhseung.modulus.gear.tool.type.ToolType

data class ToolSynergy(
    val name: String,
    val type: ToolType,
    val necessaryPartTypes: Map<ToolPartPosition, ToolPartType>
): Translatable {

    override val translationKey: String = Modulus.id("tool.synergy.$name").toTranslationKey();

    override fun toString(): String {
        return "ToolSynergy.${name.uppercase()}";
    }

    init {
        entries.add(this);
    }

    companion object {
        val entries = mutableListOf<ToolSynergy>();

        val PICKAXE = ToolSynergy("pickaxe", ToolType.DOUBLE, mapOf(
            ToolPartPosition.HANDLE to ToolPartType.HANDLE,
            ToolPartPosition.RIGHT_HEAD to ToolPartType.PICKAXE_RIGHT_HEAD,
            ToolPartPosition.LEFT_HEAD to ToolPartType.PICKAXE_LEFT_HEAD
        ));

        val AXE = ToolSynergy("axe", ToolType.DOUBLE, mapOf(
            ToolPartPosition.HANDLE to ToolPartType.HANDLE,
            ToolPartPosition.RIGHT_HEAD to ToolPartType.BUTT_HEAD,
            ToolPartPosition.LEFT_HEAD to ToolPartType.AXE_LEFT_HEAD
        ));

        val GREATAXE = ToolSynergy("greataxe", ToolType.DOUBLE, mapOf(
            ToolPartPosition.HANDLE to ToolPartType.HANDLE,
            ToolPartPosition.RIGHT_HEAD to ToolPartType.AXE_RIGHT_HEAD,
            ToolPartPosition.LEFT_HEAD to ToolPartType.AXE_LEFT_HEAD
        ));

        val ADZE = ToolSynergy("adze", ToolType.DOUBLE, mapOf(
            ToolPartPosition.HANDLE to ToolPartType.HANDLE,
            ToolPartPosition.RIGHT_HEAD to ToolPartType.PICKAXE_RIGHT_HEAD,
            ToolPartPosition.LEFT_HEAD to ToolPartType.AXE_LEFT_HEAD
        ));

        val SHOVEL = ToolSynergy("shovel", ToolType.SINGLE, mapOf(
            ToolPartPosition.HANDLE to ToolPartType.HANDLE,
            ToolPartPosition.HEAD to ToolPartType.SHOVEL_HEAD
        ));

        val HOE = ToolSynergy("hoe", ToolType.DOUBLE, mapOf(
            ToolPartPosition.HANDLE to ToolPartType.HANDLE,
            ToolPartPosition.RIGHT_HEAD to ToolPartType.BUTT_HEAD,
            ToolPartPosition.LEFT_HEAD to ToolPartType.HOE_HEAD
        ));
    }
}
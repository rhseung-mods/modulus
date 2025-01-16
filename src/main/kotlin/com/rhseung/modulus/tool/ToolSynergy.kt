package com.rhseung.modulus.tool

import com.rhseung.modulus.Modulus

enum class ToolSynergy(
    val toolType: ToolType,
    val partTypes: List<ToolPartType>
): Translatable {
    PICKAXE(
        ToolType.DOUBLE, listOf(
            ToolPartType.PICKAXE_LEFT_HEAD,
            ToolPartType.PICKAXE_RIGHT_HEAD,
            ToolPartType.HANDLE,
        )
    ),

    AXE(
        ToolType.DOUBLE, listOf(
            ToolPartType.AXE_LEFT_HEAD,
            ToolPartType.BUTT_HEAD,
            ToolPartType.HANDLE,
        )
    ),

    GREATAXE(
        ToolType.DOUBLE, listOf(
            ToolPartType.AXE_LEFT_HEAD,
            ToolPartType.AXE_RIGHT_HEAD,
            ToolPartType.HANDLE,
        )
    ),

    DOLABRA(
        ToolType.DOUBLE, listOf(
            ToolPartType.AXE_LEFT_HEAD,
            ToolPartType.PICKAXE_RIGHT_HEAD,
            ToolPartType.HANDLE,
        )
    ),

    SHOVEL(
        ToolType.SINGLE, listOf(
            ToolPartType.SHOVEL_HEAD,
            ToolPartType.HANDLE,
        )
    ),

    HOE(
        ToolType.DOUBLE, listOf(
            ToolPartType.HOE_HEAD,
            ToolPartType.BUTT_HEAD,
            ToolPartType.HANDLE,
        )
    ),

//    SWORD(),
//    SPEAR(),
    ;

    override val translationKey: String = Modulus.id("synergy.${name.lowercase()}").toTranslationKey();

    val partTypeByPosition: Map<ToolPosition, ToolPartType> =
        toolType.withNecessaryPartTypes(*partTypes.toTypedArray());

    fun withParts(vararg parts: ToolPart): Map<ToolPosition, ToolPart> {
        return toolType.withNecessaryParts(*parts);
    }

    fun withSameMaterial(material: ToolMaterial): Map<ToolPosition, ToolPart> {
        return withParts(*partTypeByPosition.values.map { it.withMaterial(material) }.toTypedArray());
    }
}
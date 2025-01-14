package com.rhseung.modulus.tool

enum class ToolPreType(
    val toolType: ToolType,
    partTypes: (ToolType) -> Map<ToolPosition, ToolPartType>
) {
    PICKAXE(ToolType.DOUBLE, {
        it.withNecessaryPartTypes(
            ToolPartType.PICKAXE_LEFT_HEAD,
            ToolPartType.PICKAXE_RIGHT_HEAD,
            ToolPartType.HANDLE,
            ToolPartType.BINDING
        )
    }),

    AXE(ToolType.DOUBLE, {
        it.withNecessaryPartTypes(
            ToolPartType.AXE_HEAD,
            ToolPartType.BUTT_HEAD,
            ToolPartType.HANDLE,
            ToolPartType.BINDING
        )
    }),

    SHOVEL(ToolType.SINGLE, {
        it.withNecessaryPartTypes(
            ToolPartType.SHOVEL_HEAD,
            ToolPartType.HANDLE,
            ToolPartType.BINDING
        )
    }),

    HOE(ToolType.DOUBLE, {
        it.withNecessaryPartTypes(
            ToolPartType.HOE_HEAD,
            ToolPartType.BUTT_HEAD,
            ToolPartType.HANDLE,
            ToolPartType.BINDING
        )
    })
    ;

    val partTypeByPosition: Map<ToolPosition, ToolPartType> = partTypes(toolType);

    fun withParts(vararg parts: ToolPart): Map<ToolPosition, ToolPart> {
        return toolType.withNecessaryParts(*parts);
    }

    fun withSameMaterial(material: ToolMaterial): Map<ToolPosition, ToolPart> {
        return withParts(*partTypeByPosition.values.map { it.withMaterial(material) }.toTypedArray());
    }
}
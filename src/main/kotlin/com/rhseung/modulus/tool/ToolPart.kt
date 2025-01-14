package com.rhseung.modulus.tool

data class ToolPart(val partType: ToolPartType, val toolMaterial: ToolMaterial) {
    val isEmpty = toolMaterial == ToolMaterial.EMPTY;

    override fun toString(): String {
        return "$toolMaterial/$partType";
    }
};
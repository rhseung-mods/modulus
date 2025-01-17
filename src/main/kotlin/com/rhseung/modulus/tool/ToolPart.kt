package com.rhseung.modulus.tool

import com.rhseung.modulus.util.Utils.plus
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.MutableText

data class ToolPart(val partType: ToolPartType, val toolMaterial: ToolMaterial) {
    init {
        if (toolMaterial.type !in partType.appliableMaterialTypes)
            throw IllegalArgumentException("Material $toolMaterial is not applicable to part $partType");
    }

    override fun toString(): String {
        return "$toolMaterial/$partType";
    }

    override fun equals(other: Any?): Boolean {
        return other is ToolPart && other.partType == partType && other.toolMaterial == toolMaterial;
    }

    override fun hashCode(): Int {
        var result = partType.hashCode()
        result = 31 * result + toolMaterial.hashCode()
        return result
    }

    fun getName(): MutableText {
        return toolMaterial.getName() + ScreenTexts.space() + partType.getName();
    }
};
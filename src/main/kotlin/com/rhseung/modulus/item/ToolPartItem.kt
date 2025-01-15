package com.rhseung.modulus.item

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.init.ModItemGroups
import com.rhseung.modulus.tool.ToolPart
import com.rhseung.modulus.tool.ToolPartType
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.util.Identifier

class ToolPartItem(
    val toolPart: ToolPart
) : InitializableItem(
    "part/${toolPart.toolMaterial.name}_${toolPart.partType.name}",
    ModItemGroups.PARTS,
    Settings()
) {
    val partType = toolPart.partType;
    val toolMaterial = toolPart.toolMaterial;

    override fun clientInit() {
        ColorProviderRegistry.ITEM.register({ stack, _ ->
            return@register toolPart.toolMaterial.color.toInt();
        }, this);
    }

    companion object {
        fun getModelId(partType: ToolPartType): Identifier {
            return Modulus.id("part/${partType.name}");
        }
    }
}
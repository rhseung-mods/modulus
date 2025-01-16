package com.rhseung.modulus.item

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.datagen.LanguageProvider
import com.rhseung.modulus.datagen.LanguageProvider.Words
import com.rhseung.modulus.init.ModItemGroups
import com.rhseung.modulus.tool.ToolPart
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.util.Utils
import com.rhseung.modulus.util.Utils.plus
import com.rhseung.modulus.util.Utils.titlecase
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
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

    override fun getName(stack: ItemStack): Text {
        return toolPart.getName() + ScreenTexts.space() + Words.PART.getName();
    }

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
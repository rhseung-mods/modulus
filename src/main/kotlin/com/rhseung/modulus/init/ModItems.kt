package com.rhseung.modulus.init

import com.rhseung.modulus.item.InitializableItem
import com.rhseung.modulus.item.ToolItem
import com.rhseung.modulus.item.ToolPartItem
import com.rhseung.modulus.tool.ToolMaterial
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.tool.ToolPreType

object ModItems : IModInit {
    override fun initialize() {
        (TOOLS.values + PARTS.values + DIAMOND_PICKAXE).forEach(InitializableItem::commonInit);
    }

    fun initializeClient() {
        (TOOLS.values + PARTS.values + DIAMOND_PICKAXE).forEach(InitializableItem::clientInit);
    }

    val DIAMOND_PICKAXE = ToolItem.of("example_diamond_pickaxe", ToolPreType.PICKAXE.toolType,
        ToolPreType.PICKAXE.withParts(
            ToolPartType.PICKAXE_LEFT_HEAD.withMaterial(ToolMaterial.DIAMOND),
            ToolPartType.PICKAXE_RIGHT_HEAD.withMaterial(ToolMaterial.DIAMOND),
            ToolPartType.HANDLE.withMaterial(ToolMaterial.WOOD),
            ToolPartType.BINDING.withMaterial(ToolMaterial.IRON)
        )
    );

    val TOOLS = ToolMaterial.VALUES.flatMap { material ->
        ToolPreType.entries.map { preToolType -> Pair(
            (material to preToolType),
            ToolItem.of(
                "${material.name}_${preToolType.name.lowercase()}",    // todo: name mapping (pickaxe, axe, adze etc)
                preToolType.toolType,
                preToolType.withSameMaterial(material)
            )
        )}
    }.toMap();

    val PARTS = ToolMaterial.VALUES.flatMap { material ->
        ToolPartType.VALUES.map { partType -> Pair(
            (material to partType),
            ToolPartItem(partType.withMaterial(material))
        )}
    }.toMap();
}
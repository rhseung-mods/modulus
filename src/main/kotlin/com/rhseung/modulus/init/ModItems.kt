package com.rhseung.modulus.init

import com.rhseung.modulus.item.ModularToolItem
import com.rhseung.modulus.tool.ToolMaterial
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.tool.ToolType

object ModItems : IModInit {
    val DIAMOND_PICKAXE = ModularToolItem.of("example_diamond_pickaxe", ToolType.PICKAXE) { it.withMaterials(
        ToolPartType.HANDLE to ToolMaterial.WOOD,
        ToolPartType.BINDING to ToolMaterial.IRON,
        ToolPartType.PICKAXE_LEFT_HEAD to ToolMaterial.DIAMOND,
        ToolPartType.PICKAXE_RIGHT_HEAD to ToolMaterial.DIAMOND
    ) };

    val TOOLS = ToolMaterial.VALUES.flatMap { material ->
        ToolType.VALUES.map { type ->
            (material to type) to ModularToolItem.of(
                "${material.name}_${type.name}",
                type
            ) { it.withSameMaterialNecessary(material) }
        }
    }.toMap();
}
package com.rhseung.modulus.init

import com.rhseung.modulus.item.InitializableItem
import com.rhseung.modulus.item.ToolItem
import com.rhseung.modulus.item.ToolPartItem
import com.rhseung.modulus.tool.ToolMaterial
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.tool.ToolSynergy

object ModItems : IModInit {
    override fun initialize() {
        (TOOLS.values + PARTS.values + DIAMOND_PICKAXE).forEach(InitializableItem::commonInit);
    }

    fun initializeClient() {
        (TOOLS.values + PARTS.values + DIAMOND_PICKAXE).forEach(InitializableItem::clientInit);
    }

    val DIAMOND_PICKAXE = ToolItem.of(
        "example_diamond_pickaxe", ToolSynergy.PICKAXE.toolType,
        ToolSynergy.PICKAXE.withParts(
            ToolPartType.PICKAXE_LEFT_HEAD.withMaterial(ToolMaterial.DIAMOND),
            ToolPartType.PICKAXE_RIGHT_HEAD.withMaterial(ToolMaterial.DIAMOND),
            ToolPartType.HANDLE.withMaterial(ToolMaterial.WOOD),
            ToolPartType.BINDING.withMaterial(ToolMaterial.LEATHER)
        )
    );

    val TOOLS = ToolMaterial.VALUES.flatMap { material ->
        ToolSynergy.entries.mapNotNull { synergy ->
            val appliableMaterialTypes = synergy.partTypes.map { it.appliableMaterialTypes.toSet() };
            val intersect = appliableMaterialTypes.reduce { acc, set -> acc.intersect(set) };

            if (material.type in intersect) {
                Pair(
                    (material to synergy),
                    ToolItem.of(
                        "${material.name}_${synergy.name.lowercase()}",
                        synergy.toolType,
                        synergy.withSameMaterial(material)
                    )
                )
            } else null
        }
    }.toMap();

    val PARTS = ToolMaterial.VALUES.flatMap { material ->
        ToolPartType.VALUES.mapNotNull { partType ->
            if (material.type in partType.appliableMaterialTypes) {
                Pair(
                    (material to partType),
                    ToolPartItem(partType.withMaterial(material))
                )
            } else null
        }
    }.toMap();
}
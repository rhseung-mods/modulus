package com.rhseung.modulus.init

import com.rhseung.modulus.item.InitializableItem
import com.rhseung.modulus.item.ToolItem
import com.rhseung.modulus.item.ToolPartItem
import com.rhseung.modulus.tool.ToolMaterial
import com.rhseung.modulus.tool.ToolPart
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.tool.ToolSynergy
import com.rhseung.modulus.tool.ToolType

object ModItems : IModInit {
    override fun initialize() {
        (TOOLS.values + PARTS.values + TEMPORALS.values).forEach(InitializableItem::commonInit);
    }

    fun initializeClient() {
        (TOOLS.values + PARTS.values + TEMPORALS.values).forEach(InitializableItem::clientInit);
    }

    val TEMPORALS = ToolType.entries.associateWith { toolType ->
        ToolItem.of(
            toolType.name.lowercase(),
            null,
            toolType,
            toolType.withParts(
                toolType.necessaryPartPositions.map { 
                    ToolPartType.DEFAULT[it]!!.withMaterial(ToolMaterial.DEFAULT)
                }
            )
        );
    }

    val TOOLS = ToolSynergy.entries.flatMap { synergy ->
        ToolMaterial.VALUES.mapNotNull { material ->
            val appliableMaterialTypes = synergy.partTypes.map { it.appliableMaterialTypes.toSet() };
            val intersect = appliableMaterialTypes.reduce { acc, set -> acc.intersect(set) };

            if (material.type in intersect) {
                Pair(material, synergy) to
                ToolItem.of(
                    "${material.name}_${synergy.name.lowercase()}",
                    ModItemGroups.TOOLS,
                    synergy.toolType,
                    synergy.withSameMaterial(material)
                )
            }
            else null
        }
    }.toMap();

    val PARTS = ToolMaterial.VALUES.flatMap { material ->
        ToolPartType.VALUES.mapNotNull { partType ->
            if (material.type in partType.appliableMaterialTypes) {
                ToolPart(partType, material) to
                ToolPartItem(partType.withMaterial(material))
            }
            else null
        }
    }.toMap();
}
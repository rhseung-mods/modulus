package com.rhseung.modulus.init

import com.rhseung.blueprint.registration.IModInit
import com.rhseung.modulus.gear.ModulusTool
import com.rhseung.modulus.gear.ModulusToolPart
import com.rhseung.modulus.gear.tool.material.ToolMaterial
import com.rhseung.modulus.gear.tool.part.ToolPart
import com.rhseung.modulus.gear.tool.part.ToolPartType
import com.rhseung.modulus.gear.tool.type.ToolType

object ModulusItems : IModInit {
    override fun initialize() {
        (PARTS.values + TOOLS.values).forEach { it.init() };
    }

    override fun initializeClient() {
        (PARTS.values + TOOLS.values).forEach { it.initClient() };
    }

    val PARTS: Map<ToolPart, ModulusToolPart> = ToolMaterial.entriesNotDefault.flatMap { material ->
        ToolPartType.entriesNotDefault.map { type ->
            val toolPart = ToolPart(material, type);
            toolPart to ModulusToolPart(toolPart);
        }
    }.toMap();

    val TOOLS: Map<ToolType, ModulusTool> = ToolType.entries.associateWith { type ->
        ModulusTool.of(type, type.positions.map { ToolPart(ToolMaterial.DEFAULT, ToolPartType.DEFAULT[it]!!) });
    };
}
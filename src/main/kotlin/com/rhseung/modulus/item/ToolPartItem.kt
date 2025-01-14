package com.rhseung.modulus.item

import com.rhseung.modulus.init.ModItemGroups
import com.rhseung.modulus.tool.ToolPart

class ToolPartItem(
    val toolPart: ToolPart
) : InitializableItem(
    "part/${toolPart.toolMaterial.name}_${toolPart.partType.name}",
    ModItemGroups.PARTS,
    Settings()
) {
}
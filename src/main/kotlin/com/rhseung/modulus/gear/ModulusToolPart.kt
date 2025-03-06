package com.rhseung.modulus.gear

import com.rhseung.blueprint.color.ColorARGB
import com.rhseung.blueprint.color.Colors
import com.rhseung.blueprint.color.Palette
import com.rhseung.blueprint.color.PaletteTintSource
import com.rhseung.blueprint.datagen.BlueprintTextureProvider
import com.rhseung.blueprint.registration.DynamicTintItem
import com.rhseung.blueprint.render.TextureImage
import com.rhseung.blueprint.util.CollectionUtils.toTextureMap
import com.rhseung.modulus.Modulus
import com.rhseung.modulus.gear.tool.material.ToolMaterial
import com.rhseung.modulus.gear.tool.part.ToolPart
import com.rhseung.modulus.gear.tool.part.ToolPartPosition
import com.rhseung.modulus.gear.tool.part.ToolPartType
import com.rhseung.modulus.init.ModulusItemGroups
import net.minecraft.client.data.ItemModelGenerator
import net.minecraft.client.data.ItemModels
import net.minecraft.client.data.Model
import net.minecraft.client.data.TextureKey
import net.minecraft.client.render.item.model.BasicItemModel
import net.minecraft.data.DataOutput
import net.minecraft.util.Identifier
import java.util.Optional

class ModulusToolPart(
    val part: ToolPart
) : DynamicTintItem(
    Modulus.id("part/${part.material.name}_${part.type.name}"),
    Modulus.id("item/part/${part.type.name}"),
    ModulusItemGroups.PARTS.registryKey,
    Settings(),
    part.material.palette
) {
    val material: ToolMaterial
        get() = part.material;

    val type: ToolPartType
        get() = part.type;

    val position: ToolPartPosition
        get() = part.position;
}
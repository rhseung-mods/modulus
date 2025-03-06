package com.rhseung.modulus.datagen

import com.rhseung.blueprint.color.Palette
import com.rhseung.blueprint.color.PaletteTintSource
import com.rhseung.blueprint.util.CollectionUtils.toTextureMap
import com.rhseung.modulus.Modulus
import com.rhseung.modulus.gear.tool.model.ToolModel
import com.rhseung.modulus.init.ModulusItems
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.*
import net.minecraft.client.render.item.model.BasicItemModel
import net.minecraft.util.Identifier
import java.util.*

class ModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockModel: BlockStateModelGenerator) {
    }

    override fun generateItemModels(itemModel: ItemModelGenerator) {
        val parts = mutableMapOf<String, BasicItemModel.Unbaked>();
        val map: Map<String, BasicItemModel.Unbaked> = ModulusItems.PARTS.values.mapNotNull {
            if (parts.containsKey(it.part.type.name))
                null;
            else {
                parts[it.part.type.name] = it.generateModels("generated", itemModel);
                it.part.type.name to parts[it.part.type.name]!!;
            }
        }.toMap();

        ModulusItems.TOOLS.values.forEach {
            item("handheld").upload(it, TextureMap(), itemModel.modelCollector);
            itemModel.output.accept(it, ToolModel.Unbaked(map));
        };
    }

    companion object {
        fun item(parent: String, vararg textureKey: TextureKey): Model {
            return Model(Optional.of(Identifier.ofVanilla("item/$parent")), Optional.empty(), *textureKey);
        }
    }
}
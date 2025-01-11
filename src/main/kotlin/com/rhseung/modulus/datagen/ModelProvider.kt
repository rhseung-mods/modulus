package com.rhseung.modulus.datagen

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.init.ModItems
import com.rhseung.modulus.item.ModularToolItem
import com.rhseung.modulus.tool.ToolPartType
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.*
import net.minecraft.util.Identifier
import java.util.*

class ModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockModel: BlockStateModelGenerator) {}

    fun generateToolModel(tool: ModularToolItem, itemModel: ItemModelGenerator) {
        val partTypes: List<ToolPartType> = tool.toolType.partTypes + tool.toolType.optionalPartTypes;
        val textureKeys: Array<TextureKey> = partTypes.indices.map { TextureKey.of("layer$it") }.toTypedArray();
        val textureMap: Map<TextureKey, Identifier> = partTypes.zip(textureKeys).associate { (partType, textureKey) ->
            val textureName = partType.name;
            val textureId = Modulus.id(textureName).withPrefixedPath("item/");
            return@associate textureKey to textureId;
        }
        val model = item("handheld", *textureKeys);

        model.upload(
            ModelIds.getItemModelId(tool),
            textureMap.entries.fold(TextureMap()) { map, entry -> map.put(entry.key, entry.value) },
            itemModel.writer
        );
    }

    override fun generateItemModels(itemModel: ItemModelGenerator) {
        ModItems.TOOLS.values.forEach { tool -> generateToolModel(tool, itemModel) };
        generateToolModel(ModItems.DIAMOND_PICKAXE, itemModel);
    }

    companion object {
        fun item(parent: String, vararg requiredTextureKeys: TextureKey): Model {
            return Model(
                Optional.of(ModelIds.getMinecraftNamespacedItem(parent)),
                Optional.empty(),
                *requiredTextureKeys
            );
        }

        fun block(parent: String, vararg requiredTextureKeys: TextureKey): Model {
            return Model(
                Optional.of(ModelIds.getMinecraftNamespacedBlock(parent)),
                Optional.empty(),
                *requiredTextureKeys
            );
        }

        fun block(parent: String, variant: String, vararg requiredTextureKeys: TextureKey): Model {
            return Model(
                Optional.of(ModelIds.getMinecraftNamespacedBlock(parent)),
                Optional.of(variant),
                *requiredTextureKeys
            );
        }
    }
}
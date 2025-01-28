package com.rhseung.modulus.datagen

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.item.ToolItem
import com.rhseung.modulus.item.ToolPartItem
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.tool.ToolType
import com.rhseung.modulus.util.ColorPalette
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.minecraft.data.client.*
import java.util.*

class ModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockModel: BlockStateModelGenerator) {}

//    fun generateToolModel(tool: ToolItem, itemModel: ItemModelGenerator) {
//        val partTypes: List<ToolPartType> = tool.toolType.partTypes + tool.toolType.optionalPartTypes;
//        val textureKeys: Array<TextureKey> = partTypes.indices.map { TextureKey.of("layer$it") }.toTypedArray();
//        val textureMap: Map<TextureKey, Identifier> = partTypes.zip(textureKeys).associate { (partType, textureKey) ->
//            val textureName = partType.name;
//            val textureId = Modulus.id(textureName).withPrefixedPath("item/");
//            return@associate textureKey to textureId;
//        }
//        val model = item("handheld", *textureKeys);
//
//        model.upload(
//            ModelIds.getItemModelId(tool),
//            textureMap.entries.fold(TextureMap()) { map, entry -> map.put(entry.key, entry.value) },
//            itemModel.writer
//        );
//    }

    override fun generateItemModels(itemModel: ItemModelGenerator) {
        // only parent tool model
        ToolType.entries.forEach { toolType ->
            val model = item("handheld");
            val id = ToolItem.getModelId(toolType).withPrefixedPath("item/");

            model.upload(id, TextureMap(), itemModel.writer);
        };

        // part item model
        ToolPartType.VALUES.forEach { partType ->
            val id = ToolPartItem.getModelId(partType).withPrefixedPath("item/");
            val textureKeys: Array<TextureKey> = (0..<ColorPalette.SIZE).map { TextureKey.of("layer$it") }.toTypedArray();
            val model = item("generated", *textureKeys);

            model.upload(id, TextureMap().apply {
                textureKeys.forEachIndexed { index, key -> put(key, id.withSuffixedPath("/$index")) }
            }, itemModel.writer);
        }

        // tool part layer model
        ToolType.entries.forEach { toolType ->
            ToolPartType.VALUES.forEach { partType ->
                if (partType.position in toolType.everyPartPositions) {
                    val id = ToolItem.getPartModelId(toolType, partType).withPrefixedPath("item/");
                    val textureKeys: Array<TextureKey> = (0..<ColorPalette.SIZE).map { TextureKey.of("layer$it") }.toTypedArray();
                    val model = item("handheld", *textureKeys);

                    model.upload(id, TextureMap().apply {
                        textureKeys.forEachIndexed { index, key -> put(key, id.withSuffixedPath("/$index")) }
                    }, itemModel.writer);
                }
            };
        }
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
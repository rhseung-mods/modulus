package com.rhseung.modulus.gear.tool.model

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.rhseung.modulus.gear.ModulusTool
import com.rhseung.modulus.gear.tool.part.ToolPart
import com.rhseung.modulus.gear.tool.part.ToolPartType
import com.rhseung.modulus.init.ModulusItems
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.item.ItemModelManager
import net.minecraft.client.render.item.ItemRenderState
import net.minecraft.client.render.item.model.BasicItemModel
import net.minecraft.client.render.item.model.ItemModel
import net.minecraft.client.render.model.ResolvableModel
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ModelTransformationMode

data class ToolModel(private val map: Map<ToolPartType, BasicItemModel>) : ItemModel {
    override fun update(
        state: ItemRenderState,
        stack: ItemStack,
        resolver: ItemModelManager,
        transformationMode: ModelTransformationMode,
        world: ClientWorld?,
        user: LivingEntity?,
        seed: Int
    ) {
        val toolParts: List<ToolPart> = ModulusTool.toolParts(stack);

        state.addLayers(toolParts.size);
        toolParts.forEach { part ->
            val partStack = ItemStack(ModulusItems.PARTS[part]!!);
            this.map[part.type]?.update(state, partStack, resolver, transformationMode, world, user, seed);
        };
    }

    @Environment(EnvType.CLIENT)
    class Unbaked(private val map: Map<String, BasicItemModel.Unbaked>) : ItemModel.Unbaked {
        companion object {
            val CODEC: MapCodec<Unbaked> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Codec.unboundedMap(Codec.STRING, BasicItemModel.Unbaked.CODEC.codec())
                        .fieldOf("map").forGetter(Unbaked::map)
                ).apply(instance, ::Unbaked)
            };
        }

        override fun getCodec(): MapCodec<ToolModel.Unbaked> {
            return CODEC;
        }

        override fun bake(context: ItemModel.BakeContext): ItemModel {
            return ToolModel(map.map {
                Pair(ToolPartType[it.key]!!, it.value.bake(context) as BasicItemModel)
            }.toMap());
        }

        override fun resolve(resolver: ResolvableModel.Resolver) {
            this.map.values.forEach { it.resolve(resolver) };
        }
    }
}
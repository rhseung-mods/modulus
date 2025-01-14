package com.rhseung.modulus.tool

import net.minecraft.block.BlockState
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.render.model.ItemModel
import net.minecraft.client.render.model.WrapperBakedModel
import net.minecraft.client.render.model.json.ModelOverrideList
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.texture.Sprite
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random

//class ToolModel(id: Identifier) : ItemModel(id) {
//    class BakedToolModel(wrapped: BakedModel, val overrides: ModelOverrideList) : BakedModel {
//        override fun getQuads(
//            state: BlockState?,
//            face: Direction?,
//            random: Random
//        ): List<BakedQuad> {
//            TODO("Not yet implemented")
//        }
//
//        override fun useAmbientOcclusion(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun hasDepth(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun isSideLit(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun isBuiltin(): Boolean {
//            TODO("Not yet implemented")
//        }
//
//        override fun getParticleSprite(): Sprite? {
//            TODO("Not yet implemented")
//        }
//
//        override fun getTransformation(): ModelTransformation? {
//            TODO("Not yet implemented")
//        }
//    }
//}
package com.rhseung.modulus.tool

import com.rhseung.modulus.init.ModItems
import com.rhseung.modulus.item.ToolItem
import com.rhseung.modulus.item.ToolPartItem
import net.minecraft.block.BlockState
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.item.ItemModels
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.texture.Sprite
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.ModelTransformationMode
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random

/**
 * @param toolTypeModel textures 항목 없이 parent 항목만 존재해 transformation 기능만 존재하는 json 파일로부터 생성된 BakedModel.
 */
class ToolModel(val stack: ItemStack, val toolTypeModel: BakedModel, val models: ItemModels) : BakedModel {
    val tool = stack.item as? ToolItem ?: throw IllegalArgumentException("Not a tool item: $stack");
    val toolType = tool.toolType;
    val toolPartsComponent = ToolItem.getToolPartsComponent(stack);
    val toolPositions = toolType.everyPartPositions;
    val quads = mutableListOf<BakedQuad>();
    val facedQuads = mutableMapOf<Direction, MutableList<BakedQuad>>();

    init {
        val random = Random.create();
        val seed = 42L;

        toolPositions.forEachIndexed { layerN, position ->
            if (position in toolPartsComponent) {
                val toolPart = toolPartsComponent[position]!!;
                val toolPartModel = models.getModel(ToolItem.getPartModelId(tool.toolType, toolPart.partType));

                Direction.entries.forEach { direction ->
                    random.setSeed(seed);
                    val partQuads = toolPartModel.getQuads(null, direction, random).map { it.withColorIndex(layerN) };
                    facedQuads.computeIfAbsent(direction) { mutableListOf() }.addAll(partQuads);
                }

                random.setSeed(seed);
                val partQuads = toolPartModel.getQuads(null, null, random).map { it.withColorIndex(layerN) };
                quads.addAll(partQuads);
            }
        }
    }

    /**
     * @see net.minecraft.client.render.item.ItemRenderer.renderItem(net.minecraft.item.ItemStack, net.minecraft.item.ModelTransformationMode, boolean, net.minecraft.client.util.math.MatrixStack, net.minecraft.client.render.VertexConsumerProvider, int, int, net.minecraft.client.render.model.BakedModel, boolean, float)
     */
    fun render(
        transformationMode: ModelTransformationMode,
        leftHanded: Boolean,
        matrices: MatrixStack,
        vertices: VertexConsumer,
        light: Int,
        overlay: Int
    ) {
        matrices.push();
        toolTypeModel.transformation.getTransformation(transformationMode).apply(leftHanded, matrices);
        matrices.translate(-0.5F, -0.5F, -0.5F);
        facedQuads.values.plusElement(quads).forEach { renderBakedQuads(matrices, vertices, it, light, overlay) }
        matrices.pop();
    }

    /**
     * @see net.minecraft.client.render.item.ItemRenderer.renderBakedItemQuads
     */
    private fun renderBakedQuads(matrices: MatrixStack, vertices: VertexConsumer, quads: List<BakedQuad>, light: Int, overlay: Int) {
        val entry = matrices.peek();

        // todo: [net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource] 처럼 색상을 단순 틴트가 아니라 매퍼로 처리

        quads.forEach { quad ->
            val color = toolPartsComponent.getColor(quad.colorIndex);
            vertices.quad(entry, quad, color.r, color.g, color.b, color.a, light, overlay);
        }
    }

    override fun getQuads(
        state: BlockState?,
        face: Direction?,
        random: Random
    ): List<BakedQuad> {
        return if (face != null) facedQuads[face] ?: emptyList() else quads;
    }

    override fun useAmbientOcclusion(): Boolean {
        return toolTypeModel.useAmbientOcclusion();
    }

    override fun hasDepth(): Boolean {
        return toolTypeModel.hasDepth();
    }

    override fun isSideLit(): Boolean {
        return toolTypeModel.isSideLit;
    }

    override fun isBuiltin(): Boolean {
        return toolTypeModel.isBuiltin;
    }

    override fun getParticleSprite(): Sprite {
        val mainPart = toolPartsComponent[toolType.mainPartPosition]
            ?: throw IllegalArgumentException("Missing main part: ${toolType.mainPartPosition} in $toolPartsComponent");
        val mainPartItem = ModItems.PARTS[mainPart]
            ?: throw IllegalArgumentException("Missing main part item: $mainPart in ${ModItems.PARTS}");

        return models.getModel(ToolPartItem.getModelId(mainPartItem.partType)).particleSprite;
    }

    override fun getTransformation(): ModelTransformation {
        return toolTypeModel.transformation;
    }

    companion object {
        private fun BakedQuad.withColorIndex(colorIndex: Int): BakedQuad {
            return BakedQuad(this.vertexData, colorIndex, this.face, this.sprite, this.hasShade(), this.lightEmission);
        }
    }
}
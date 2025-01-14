package com.rhseung.modulus.tool

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.item.ToolItem
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.item.ItemModels
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.ModelTransformationMode
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random

class ToolModel(val stack: ItemStack, models: ItemModels) {
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
                val toolPartTexture = Modulus.id("tool/${toolPart.partType.name}");
                val toolPartModel = models.getModel(toolPartTexture);

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
        toolModel: BakedModel,
        transformationMode: ModelTransformationMode,
        leftHanded: Boolean,
        matrices: MatrixStack,
        vertices: VertexConsumer,
        light: Int,
        overlay: Int
    ) {
        matrices.push();
        toolModel.transformation.getTransformation(transformationMode).apply(leftHanded, matrices);
        matrices.translate(-0.5F, -0.5F, -0.5F);
        facedQuads.values.plusElement(quads).forEach { renderBakedQuads(matrices, vertices, it, light, overlay) }
        matrices.pop();
    }

    /**
     * @see net.minecraft.client.render.item.ItemRenderer.renderBakedItemQuads
     */
    private fun renderBakedQuads(matrices: MatrixStack, vertices: VertexConsumer, quads: List<BakedQuad>, light: Int, overlay: Int) {
        val entry = matrices.peek();

        // todo: net.minecraft.client.texture.atlas.PalettedPermutationsAtlasSource 처럼 색상을 단순 틴트가 아니라 매퍼로 처리

        quads.forEach { quad ->
            val toolPart = toolPartsComponent[toolPositions[quad.colorIndex]]!!;
            val color = toolPart.toolMaterial.color;
            vertices.quad(entry, quad, color.r, color.g, color.b, color.a, light, overlay);
        }
    }

    companion object {
        private fun BakedQuad.withColorIndex(colorIndex: Int): BakedQuad {
            return BakedQuad(this.vertexData, colorIndex, this.face, this.sprite, this.hasShade(), this.lightEmission);
        }
    }
}
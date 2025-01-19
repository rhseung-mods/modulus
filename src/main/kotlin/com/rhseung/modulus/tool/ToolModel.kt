package com.rhseung.modulus.tool

import com.rhseung.modulus.init.ModItems
import com.rhseung.modulus.item.ToolItem
import com.rhseung.modulus.item.ToolPartItem
import com.rhseung.modulus.util.ARGBColor
import com.rhseung.modulus.util.ColorPalette
import com.rhseung.modulus.util.RGBColor
import com.rhseung.modulus.util.Utils.getProperty
import net.minecraft.block.BlockState
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.item.ItemModels
import net.minecraft.client.render.model.BakedModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteContents
import net.minecraft.client.texture.SpriteDimensions
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import net.minecraft.item.ModelTransformationMode
import net.minecraft.resource.metadata.ResourceMetadata
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random

/**
 * @param toolTypeModel textures 항목 없이 parent 항목만 존재해 transformation 기능만 존재하는 json 파일로부터 생성된 BakedModel.
 */
class ToolModel(stack: ItemStack, val toolTypeModel: BakedModel, val models: ItemModels) : BakedModel {
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
                val toolPart: ToolPart = toolPartsComponent[position]!!;
                val toolPartModel: BakedModel = models.getModel(ToolItem.getPartModelId(tool.toolType, toolPart.partType));

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

    private fun renderBakedQuadsMapper(matrices: MatrixStack, vertices: VertexConsumer, quads: List<BakedQuad>, light: Int, overlay: Int) {
        val entry = matrices.peek();

        quads.forEach { quad ->
            val part = toolPartsComponent[quad.colorIndex]!!;
            val mapper = ColorPalette.toMapper(ColorPalette.DEFAULT, part.toolMaterial.colorPalette);

            val sprite = quad.sprite;
            val spriteContents = sprite.contents;
            val nativeImage: NativeImage = spriteContents.getProperty("image");

            val newNativeImage = nativeImage.applyToCopy { mapper(ARGBColor(it)).toInt() }
            val newSpriteContents = SpriteContents(
                spriteContents.id,
                SpriteDimensions(newNativeImage.width, newNativeImage.height),
                nativeImage,
                ResourceMetadata.NONE
            );

            val constructor = Sprite::class.java.getConstructor(
                Identifier::class.java, SpriteContents::class.java, Int::class.java, Int::class.java, Int::class.java, Int::class.java
            );

            if (constructor.trySetAccessible()) {
                val atlasWidth = (1f / sprite.minU * sprite.x).toInt();
                val atlasHeight = (1f / sprite.minV * sprite.y).toInt();

                val newSprite = constructor.newInstance(sprite.atlasId, newSpriteContents, atlasWidth, atlasHeight, sprite.x, sprite.y);
                val newQuad = quad.withSprite(newSprite);

                // todo: 픽셀 단위 팔레트 컬러링
                //  - ArmorTrim, ToolTrim 모두 PalettedPermutationsAtlasSource라는 AtlasSource를 사용하고
                //  - 모델 json 파일을 보면 override로 모든 material에 대해 ModelPredicateProvider를 사용하고 있다.
                //  - 즉, 똑같이 PalettedToolAtlasSource를 구현하고 register한 다음, models/item/double, single, part 폴더의 모든 json 파일에 override를 ToolMaterial.VALUES로 전부 추가, 이후 ModelPredicateProvider를 구현하면 될 것 같다.

                val color = RGBColor.WHITE.fullAlpha();
                vertices.quad(entry, newQuad, color.r, color.g, color.b, color.a, light, overlay);
            } else {
                throw IllegalStateException("Failed to access Sprite constructor");
            }
        }
    }
    
    /**
     * @see net.minecraft.client.render.item.ItemRenderer.renderBakedItemQuads
     */
    private fun renderBakedQuads(matrices: MatrixStack, vertices: VertexConsumer, quads: List<BakedQuad>, light: Int, overlay: Int) {
        val entry = matrices.peek();

//        val rainbow = listOf(// quad가 어디 위치에 있는지 알기 위해 색상을 다르게 설정
//            RGBColor.RED,
//            RGBColor.ORANGE,
//            RGBColor.YELLOW,
//            RGBColor.GREEN,
//            RGBColor.BLUE,
//            RGBColor.PURPLE
//        ).map(RGBColor::fullAlpha);

        quads.forEachIndexed { i, quad ->
            val part = toolPartsComponent[quad.colorIndex]!!;
            val color = part.toolMaterial.colorPalette.mainColor;
//            val color = rainbow[i % rainbow.size];
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

        private fun BakedQuad.withSprite(sprite: Sprite): BakedQuad {
            return BakedQuad(this.vertexData, this.colorIndex, this.face, sprite, this.hasShade(), this.lightEmission);
        }
    }
}
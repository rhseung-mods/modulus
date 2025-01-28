package com.rhseung.modulus.tool

import com.rhseung.modulus.init.ModItems
import com.rhseung.modulus.item.ToolItem
import com.rhseung.modulus.item.ToolPartItem
import com.rhseung.modulus.tool.component.ToolPartsComponent
import com.rhseung.modulus.util.ARGBColor
import com.rhseung.modulus.util.ColorPalette
import com.rhseung.modulus.util.RGBColor
import com.rhseung.modulus.util.Utils.forEach
import com.rhseung.modulus.util.Utils.getProperty
import net.minecraft.block.BlockState
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexFormats
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
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.Direction
import net.minecraft.util.math.random.Random
import org.joml.Vector3f
import org.lwjgl.system.MemoryStack

/**
 * @param toolTypeModel textures 항목 없이 parent 항목만 존재해 transformation 기능만 존재하는 json 파일로부터 생성된 BakedModel.
 */
class ToolModel(val stack: ItemStack, val toolTypeModel: BakedModel, val models: ItemModels) : BakedModel {
    val tool: ToolItem = stack.item as? ToolItem ?: throw IllegalArgumentException("Not a tool item: $stack");
    val toolType: ToolType = tool.toolType;
    val toolPartsComponent: ToolPartsComponent = ToolItem.getToolPartsComponent(stack);
    val toolPositions: List<ToolPosition> = toolType.everyPartPositions;
    val quads: MutableList<BakedQuad> = mutableListOf<BakedQuad>();
    val facedQuads: MutableMap<Direction, MutableList<BakedQuad>> = mutableMapOf<Direction, MutableList<BakedQuad>>();

    init {
        val random = Random.create();
        val seed = 42L;

        toolPositions.forEachIndexed { layerN, position ->
            if (position in toolPartsComponent) {
                val toolPart: ToolPart = toolPartsComponent[position]!!;
                val toolPartModelId: Identifier = ToolItem.getPartModelId(toolType, toolPart.partType);
                val toolPartModel: BakedModel = models.getModel(toolPartModelId);

                Direction.entries.forEach { direction ->
                    random.setSeed(seed);
                    val partQuads = toolPartModel.getQuads(null, direction, random).map { it.withColorIndex(layerN * ColorPalette.SIZE + it.colorIndex) };
                    facedQuads.computeIfAbsent(direction) { mutableListOf() }.addAll(partQuads);
                }

                random.setSeed(seed);
                val partQuads = toolPartModel.getQuads(null, null, random).map { it.withColorIndex(layerN * ColorPalette.SIZE + it.colorIndex) };
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
            val palette = part.toolMaterial.colorPalette;
            val newQuad = newQuad(part, palette, quad);
//            val newQuad = quad;
            val color = RGBColor.WHITE.fullAlpha();

            vertices.quad(entry, newQuad, color.r, color.g, color.b, color.a, light, overlay);
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
            val colorIndex = quad.colorIndex;
            val layerN = colorIndex / ColorPalette.SIZE;
            val tintIndex = colorIndex % ColorPalette.SIZE;

            val part = toolPartsComponent[layerN]!!;
            val color = part.toolMaterial.colorPalette[tintIndex].fullAlpha();
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

        fun newQuad(part: ToolPart, palette: ColorPalette, quad: BakedQuad): BakedQuad {
            val sprite = quad.sprite;
            val spriteContents = sprite.contents;
            val nativeImage: NativeImage = spriteContents.getProperty("image");

            val newNativeImage = NativeImage(nativeImage.width, nativeImage.height, false);
            newNativeImage.copyFrom(nativeImage);
            newNativeImage.forEach { img, x, y ->
                val pixelColor: ARGBColor = ARGBColor(img.getColorArgb(x, y));
                if (pixelColor.A != 0) {
                    val idx = ColorPalette.DEFAULT.colors.indexOf(pixelColor.toRGB()).takeIf { it != -1 }
                        ?: error("Invalid color: $pixelColor in $part");
                    val newPixelColor = palette[idx];

                    img.setColorArgb(x, y, newPixelColor.withAlpha(pixelColor.A).toInt());
                }
            };

            // fixme: debug
//            if (part.toolMaterial.colorPalette != ColorPalette.DEFAULT) {
//                val path = Path("C:\\Users\\rhseung\\Desktop\\native_image\\${part.toString().replace('/', '_')}.png");
//                if (path.notExists()) {
//                    newNativeImage.writeTo(path);
//                    println("Saved to $path");
//                }
//            }

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
                println(sprite.atlasId);

                val newQuad = quad.withSprite(newSprite);

                return newQuad;
            } else {
                throw IllegalStateException("Failed to access Sprite constructor");
            }
        }
    }
}
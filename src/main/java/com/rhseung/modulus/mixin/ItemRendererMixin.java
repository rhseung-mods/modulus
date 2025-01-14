package com.rhseung.modulus.mixin;

import com.rhseung.modulus.Modulus;
import com.rhseung.modulus.item.ToolItem;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    protected abstract BakedModel getModelOrOverride(BakedModel model, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed);

    @Shadow
    @Final
    private ItemModels models;

    @Shadow
    private static boolean usesDynamicDisplay(ItemStack stack) {
        return false;
    }

    @Shadow
    public static VertexConsumer getDynamicDisplayGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry) {
        return null;
    }

    @Shadow
    public static VertexConsumer getItemGlintConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint) {
        return null;
    }

    @Shadow
    @Final
    private BuiltinModelItemRenderer builtinModelItemRenderer;

    @Shadow
    @Final
    private ItemColors colors;

    /**
     * {@link ItemRenderer#renderItem(ItemStack, ModelTransformationMode, boolean, MatrixStack, VertexConsumerProvider, int, int, BakedModel, boolean, float)}
     */
    @Unique
    protected void renderItemMixin(ItemStack stack, ModelTransformationMode transformationMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel toolPartModel, boolean useInventoryModel, float z, BakedModel baseModel) {
        matrices.push();
        baseModel.getTransformation().getTransformation(transformationMode).apply(leftHanded, matrices);
        matrices.translate(-0.5F, -0.5F, z);
        renderItemMixin(stack, transformationMode, matrices, vertexConsumers, light, overlay, toolPartModel, useInventoryModel);
        matrices.pop();
    }

    @Unique
    private VertexConsumer providerToConsumer(VertexConsumerProvider vertexConsumers, ItemStack stack, MatrixStack matrices, ModelTransformationMode transformationMode) {
        RenderLayer renderLayer = RenderLayers.getItemLayer(stack);

        if (usesDynamicDisplay(stack) && stack.hasGlint()) {
            MatrixStack.Entry entry = matrices.peek().copy();
            if (transformationMode == ModelTransformationMode.GUI) {
                MatrixUtil.scale(entry.getPositionMatrix(), 0.5F);
            } else if (transformationMode.isFirstPerson()) {
                MatrixUtil.scale(entry.getPositionMatrix(), 0.75F);
            }

            return getDynamicDisplayGlintConsumer(vertexConsumers, renderLayer, entry);
        } else {
            return getItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
        }
    }

    /**
     * {@link ItemRenderer#renderItem(ItemStack, ModelTransformationMode, MatrixStack, VertexConsumerProvider, int, int, BakedModel, boolean)}
     */
    @Unique
    private void renderItemMixin(ItemStack stack, ModelTransformationMode transformationMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel toolPartModel, boolean useInventoryModel) {
        if (!toolPartModel.isBuiltin() && (!stack.isOf(Items.TRIDENT) || useInventoryModel)) {
            renderBakedItemModelMixin(toolPartModel, stack, light, overlay, matrices, providerToConsumer(vertexConsumers, stack, matrices, transformationMode));
        } else {
            this.builtinModelItemRenderer.render(stack, transformationMode, matrices, vertexConsumers, light, overlay);
        }
    }

    /**
     * {@link ItemRenderer#renderBakedItemModel(BakedModel, ItemStack, int, int, MatrixStack, VertexConsumer)}
     */
    @Unique
    private void renderBakedItemModelMixin(BakedModel toolPartModel, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices) {
        Random random = Random.create();
        long seed = 42L;

        for (Direction direction : Direction.values()) {
            random.setSeed(seed);
            renderBakedItemQuadsMixin(matrices, vertices, toolPartModel.getQuads(null, direction, random), stack, light, overlay);
        }

        random.setSeed(seed);
        renderBakedItemQuadsMixin(matrices, vertices, toolPartModel.getQuads(null, null, random), stack, light, overlay);
    }

    /**
     * {@link ItemRenderer#renderBakedItemQuads(MatrixStack, VertexConsumer, List, ItemStack, int, int)}
     */
    @Unique
    private void renderBakedItemQuadsMixin(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay) {
        boolean bl = !stack.isEmpty();
        MatrixStack.Entry entry = matrices.peek();

        for (BakedQuad quad : quads) {
            int i = Colors.WHITE;
            if (bl && quad.hasColor()) {
                i = this.colors.getColor(stack, quad.getColorIndex());
            }

            float a = (float) ColorHelper.getAlpha(i) / 255.0F;
            float r = (float) ColorHelper.getRed(i) / 255.0F;
            float g = (float) ColorHelper.getGreen(i) / 255.0F;
            float b = (float) ColorHelper.getBlue(i) / 255.0F;
            vertices.quad(entry, quad, r, g, b, a, light, overlay);
        }
    }

    @Unique
    void renderTool(ItemStack stack, ModelTransformationMode transformationMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel baseModel) {
        var tool = (ToolItem) stack.getItem();
        var toolType = tool.getToolType();
        var toolPartsComponent = ToolItem.Companion.getToolPartsComponent(stack);
        var positions = toolType.getEveryPartPositions();

        ArrayList<BakedQuad> quads = new java.util.ArrayList<>();
        Map<Direction, ArrayList<BakedQuad>> faceQuads = new java.util.HashMap<>();
        long seed = 42L;
        Random random = Random.create();

        for (int layerN = 0; layerN < positions.size(); layerN++) {
            int finalLayerN = layerN;
            var position = positions.get(layerN);

            if (toolPartsComponent.contains(position)) {
                var toolPart = Objects.requireNonNull(toolPartsComponent.get(position));
                var toolPartTexture = Modulus.INSTANCE.id("tool/" + toolPart.getPartType().getName());
                var toolPartModel = this.models.getModel(toolPartTexture);

                for (Direction direction : Direction.values()) {
                    random.setSeed(seed);
                    if (!faceQuads.containsKey(direction))
                        faceQuads.put(direction, new ArrayList<>());
                    faceQuads.get(direction).addAll(toolPartModel.getQuads(null, direction, random).stream().map(quad -> withColorIndex(quad, finalLayerN)).toList());
                }
                random.setSeed(seed);
                quads.addAll(toolPartModel.getQuads(null, null, random).stream().map(quad -> withColorIndex(quad, finalLayerN)).toList());

//                renderItemMixin(stack, transformationMode, leftHanded, matrices, vertexConsumers, light, overlay, toolPartModel, false, -0.5F + 1.0f * layerN, model);
            }
        }

        matrices.push();
        baseModel.getTransformation().getTransformation(transformationMode).apply(leftHanded, matrices);
        matrices.translate(-0.5F, -0.5F, -0.5F);
        VertexConsumer vertices = providerToConsumer(vertexConsumers, stack, matrices, transformationMode);
        for (Direction direction : Direction.values()) {
            renderBakedItemQuadsMixin(matrices, vertices, faceQuads.get(direction), stack, light, overlay);
        }
        renderBakedItemQuadsMixin(matrices, vertices, quads, stack, light, overlay);
        matrices.pop();
    }

    @Unique
    private BakedQuad withColorIndex(BakedQuad original, int colorIndex) {
        return new BakedQuad(original.getVertexData(), colorIndex, original.getFace(), original.getSprite(), original.hasShade(), original.getLightEmission());
    }

    @Inject(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;ZF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderItemMixin(ItemStack stack, ModelTransformationMode transformationMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, boolean useInventoryModel, float z, CallbackInfo ci) {
        if (stack.getItem() instanceof ToolItem) {
            this.renderTool(stack, transformationMode, leftHanded, matrices, vertexConsumers, light, overlay, model);
            ci.cancel();
        }
    }
}

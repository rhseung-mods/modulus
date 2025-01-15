package com.rhseung.modulus.mixin;

import com.rhseung.modulus.item.ToolItem;
import com.rhseung.modulus.item.ToolPartItem;
import com.rhseung.modulus.tool.ToolModel;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
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

    @Inject(
        method = "getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void getModelMixin(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        BakedModel bakedModel;

        if (stack.getItem() instanceof ToolItem toolItem) {
            bakedModel = this.models.getModel(ToolItem.Companion.getModelId(toolItem.getToolType()));
            cir.setReturnValue(new ToolModel(stack, bakedModel, this.models));
        }
        else if (stack.getItem() instanceof ToolPartItem partItem) {
            bakedModel = this.models.getModel(ToolPartItem.Companion.getModelId(partItem.getPartType()));
            cir.setReturnValue(bakedModel);
        }
    }

    @Inject(
        method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;ZF)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void renderItemMixin(ItemStack stack, ModelTransformationMode transformationMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, boolean useInventoryModel, float z, CallbackInfo ci) {
        if (model instanceof ToolModel toolModel) {
            var vertices = providerToConsumer(vertexConsumers, stack, matrices, transformationMode);
            toolModel.render(transformationMode, leftHanded, matrices, vertices, light, overlay);
            ci.cancel();
        }
    }
}

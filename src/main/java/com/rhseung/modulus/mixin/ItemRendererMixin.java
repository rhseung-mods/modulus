package com.rhseung.modulus.mixin;

import com.rhseung.modulus.Modulus;
import com.rhseung.modulus.item.ToolItem;
import com.rhseung.modulus.tool.ToolModel;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
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
        method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;ZF)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void renderItemMixin(ItemStack stack, ModelTransformationMode transformationMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, boolean useInventoryModel, float z, CallbackInfo ci) {
        if (stack.getItem() instanceof ToolItem) {
            var toolModel = new ToolModel(stack, this.models);
            var vertices = providerToConsumer(vertexConsumers, stack, matrices, transformationMode);
            toolModel.render(model, transformationMode, leftHanded, matrices, vertices, light, overlay);
            ci.cancel();
        }
    }
}

package com.rhseung.modulus.mixin;

import com.rhseung.modulus.item.ToolItem;
import kotlin.random.Random;
import net.minecraft.client.particle.CrackParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrackParticle.class)
public class CrackParticleMixin {
    @Inject(method = "<init>(Lnet/minecraft/client/world/ClientWorld;DDDLnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private void initMixin(ClientWorld world, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
        var instance = (CrackParticle) (Object) this;

        if (stack.getItem() instanceof ToolItem) {
            var toolPartsComponent = ToolItem.Companion.getToolPartsComponent(stack);
            var tintIndex = Random.Default.nextInt(toolPartsComponent.getSize());
            var color = toolPartsComponent.getColorPalette(tintIndex).getMainColor();

            instance.setColor(color.r(), color.g(), color.b());
        }
    }
}

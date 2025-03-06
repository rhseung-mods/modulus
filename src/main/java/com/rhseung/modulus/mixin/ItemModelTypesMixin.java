package com.rhseung.modulus.mixin;

import com.mojang.serialization.MapCodec;
import com.rhseung.modulus.Modulus;
import com.rhseung.modulus.gear.tool.model.ToolModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelTypes.class)
public class ItemModelTypesMixin {
    @Shadow @Final public static Codecs.IdMapper<Identifier, MapCodec<? extends ItemModel.Unbaked>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void bootstrapMixin(CallbackInfo ci) {
        ID_MAPPER.put(Modulus.INSTANCE.id("tool"), ToolModel.Unbaked.Companion.getCODEC());
    }
}

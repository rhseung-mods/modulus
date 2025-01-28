package com.rhseung.modulus.mixin;

import com.rhseung.modulus.Modulus;
import com.rhseung.modulus.tool.ToolMaterial;
import com.rhseung.modulus.tool.ToolModel;
import com.rhseung.modulus.tool.ToolPartType;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JsonUnbakedModel.class)
public class JsonUnbakedModelMixin {
//    @Inject(method = "createQuad", at = @At("HEAD"), cancellable = true)
//    private static void createQuadMixin(ModelElement element, ModelElementFace elementFace, Sprite sprite, Direction side, ModelBakeSettings settings, CallbackInfoReturnable<BakedQuad> cir) {
//        var id = sprite.getContents().getId();
//
//        if (id.getNamespace().equals(Modulus.MOD_ID)) {
//            // todo: 추가 조건 필요, ex. needTint 속성 추가
//            var partNames = id.getPath().split("/");
//            var partName = partNames[partNames.length - 1];
//            var partType = ToolPartType.Companion.getVALUES().stream().filter(p -> p.getName().equals(partName)).findFirst();
//
//            if (partType.isPresent()) {
//                var t = partType.get();
//                var material = ToolMaterial.Companion.getDIAMOND();
//                cir.setReturnValue(ToolModel.Companion.newQuad(t.withMaterial(material), material.getColorPalette(), cir.getReturnValue()));
//            }
//        }
//    }
}

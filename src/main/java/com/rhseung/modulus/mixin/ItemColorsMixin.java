package com.rhseung.modulus.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.rhseung.modulus.init.ModItems;
import com.rhseung.modulus.item.ToolItem;
import com.rhseung.modulus.util.ARGBColor;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemColors.class)
public class ItemColorsMixin {

    @ModifyReturnValue(method = "create", at = @At("RETURN"))
    private static ItemColors createMixin(ItemColors original) {
        ModItems.INSTANCE.getPARTS().values().forEach(part -> {
            original.register((stack, tintIndex) -> part.getToolPart().getToolMaterial().getColor().toInt(), part);
        });

        var list = new java.util.ArrayList<>(List.of(ModItems.INSTANCE.getDIAMOND_PICKAXE()));
        list.addAll(ModItems.INSTANCE.getTOOLS().values());
        list.forEach(tool -> {
            // todo: 색깔이 왜케 까맣게 나옴? 그리고 DIAMOND_PICKAXE의 색이 버그임
            // todo: 도구를 들었을 떄 모델이 제대로 나오지 않음
            original.register((stack, tintIndex) -> {
                var position = tool.getToolType().getEveryPartPositions().get(tintIndex);
                var component = ToolItem.Companion.getToolPartsComponent(stack);
                var part = component.get(position);

                if (part == null)
                    return ARGBColor.Companion.getEMPTY().toInt();
                else
                    return part.getToolMaterial().getColor().toInt();
            }, tool);
        });

        return original;
    }
}

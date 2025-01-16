package com.rhseung.modulus.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.rhseung.modulus.item.ToolItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Inject(method = "canRepairWith", at = @At("HEAD"), cancellable = true)
	private void canRepairWithMixin(ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
		ItemStack itemStack = (ItemStack) (Object) this;

		if (itemStack.getItem() instanceof ToolItem) {
			var toolParts = ToolItem.Companion.getToolPartsComponent(itemStack);
			cir.setReturnValue(toolParts.getRepairables().contains(ingredient));

//			var durabilities = ModularToolItem.Companion.getStructuredDurabilityComponent(itemStack);
//			var repairTags = durabilities.getBrokenParts().stream().map(part -> part.getToolMaterial().getRepairTag());
//			cir.setReturnValue(repairTags.anyMatch(ingredient::isIn));
		}
	}

	@ModifyReturnValue(method = "isDamageable", at = @At("RETURN"))
	private boolean isDamageableMixin(boolean original) {
		ItemStack itemStack = (ItemStack) (Object) this;

		if (itemStack.getItem() instanceof ToolItem) {
			return true;
		} else {
			return original;
		}
	}

//	@ModifyReturnValue(method = "isDamaged", at = @At("RETURN"))
//	private boolean isDamagedMixin(boolean original) {
//		ItemStack itemStack = (ItemStack) (Object) this;
//
//		if (itemStack.getItem() instanceof ModularToolItem) {
//			return ModularToolItem.Companion.getStructuredDurabilityComponent(itemStack).isDamaged();
//		} else {
//			return original;
//		}
//	}
}
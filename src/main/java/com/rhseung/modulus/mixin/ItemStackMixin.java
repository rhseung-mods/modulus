package com.rhseung.modulus.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.rhseung.modulus.init.ModComponents;
import com.rhseung.modulus.item.ModularToolItem;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Inject(method = "canRepairWith", at = @At("HEAD"), cancellable = true)
	private void canRepairWithMixin(ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
		ItemStack itemStack = (ItemStack) (Object) this;

		if (itemStack.getItem() instanceof ModularToolItem) {
			var toolParts = ModularToolItem.Companion.getToolPartsComponent(itemStack);
			cir.setReturnValue(toolParts.getRepairTags().stream().anyMatch(ingredient::isIn));

//			var durabilities = ModularToolItem.Companion.getStructuredDurabilityComponent(itemStack);
//			var repairTags = durabilities.getBrokenParts().stream().map(part -> part.getToolMaterial().getRepairTag());
//			cir.setReturnValue(repairTags.anyMatch(ingredient::isIn));
		}
	}

	@ModifyReturnValue(method = "isDamageable", at = @At("RETURN"))
	private boolean isDamageableMixin(boolean original) {
		ItemStack itemStack = (ItemStack) (Object) this;

		if (itemStack.getItem() instanceof ModularToolItem) {
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
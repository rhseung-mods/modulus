package com.rhseung.modulus.gear

import com.rhseung.blueprint.color.Palette
import com.rhseung.blueprint.registration.InitializeItem
import com.rhseung.modulus.Modulus
import com.rhseung.modulus.gear.tool.component.ToolComponent
import com.rhseung.modulus.gear.tool.part.ToolPart
import com.rhseung.modulus.gear.tool.type.ToolType
import com.rhseung.modulus.init.ModulusComponents
import com.rhseung.modulus.init.ModulusItemGroups
import com.rhseung.modulus.init.ModulusItemGroups.ModulusItemGroup
import net.minecraft.client.data.TextureKey
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.AttributeModifierSlot
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.component.type.EnchantableComponent
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryWrapper
import net.minecraft.util.Identifier

class ModulusTool(
    name: String,
    itemGroup: RegistryKey<ItemGroup>?,
    settings: Settings
) : InitializeItem(
    Modulus.id(name),
    itemGroup,
    settings
) {
    fun toolComponent(stack: ItemStack): ToolComponent {
        return stack[ModulusComponents.TOOL]
            ?: throw IllegalArgumentException("ItemStack does not have a ToolComponent");
    }

    fun toolType(stack: ItemStack): ToolType {
        return toolComponent(stack).toolType;
    }

    fun toolParts(stack: ItemStack): List<ToolPart> {
        return toolComponent(stack).parts;
    }

    companion object {
        fun toolComponent(stack: ItemStack): ToolComponent {
            return stack[ModulusComponents.TOOL]
                ?: throw IllegalArgumentException("ItemStack does not have a ToolComponent");
        }

        fun toolType(stack: ItemStack): ToolType {
            return toolComponent(stack).toolType;
        }

        fun toolParts(stack: ItemStack): List<ToolPart> {
            return toolComponent(stack).parts;
        }

        fun update(stack: ItemStack, toolType: ToolType, parts: List<ToolPart>, registries: RegistryWrapper.WrapperLookup) {
            val toolComponent = ToolComponent(toolType, parts);

            stack.set(ModulusComponents.TOOL, toolComponent);
            stack.set(DataComponentTypes.DAMAGE, stack.damage.coerceAtMost(toolComponent.durability));
            stack.set(DataComponentTypes.MAX_DAMAGE, toolComponent.durability);
            stack.set(DataComponentTypes.ENCHANTABLE, EnchantableComponent(toolComponent.enchantability));
            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, getAttributeModifiersComponent(toolComponent.attackDamage, toolComponent.attackSpeed));
        }

        fun of(toolType: ToolType, parts: List<ToolPart>, itemGroup: RegistryKey<ItemGroup>? = null): ModulusTool {
            val toolComponent = ToolComponent(toolType, parts);
            val settings = Settings();

            settings.component(ModulusComponents.TOOL, toolComponent);
            settings.maxDamage(toolComponent.durability);
            settings.enchantable(toolComponent.enchantability);
            settings.attributeModifiers(getAttributeModifiersComponent(toolComponent.attackDamage, toolComponent.attackSpeed));

            return ModulusTool(toolType.name, itemGroup, settings);
        }

        private fun getAttributeModifiersComponent(attackDamage: Float, attackSpeed: Float): AttributeModifiersComponent {
            return AttributeModifiersComponent.builder()
                .add(EntityAttributes.ATTACK_DAMAGE, EntityAttributeModifier(
                    Item.BASE_ATTACK_DAMAGE_MODIFIER_ID,
                    attackDamage.toDouble(),
                    EntityAttributeModifier.Operation.ADD_VALUE
                ), AttributeModifierSlot.MAINHAND)
                .add(EntityAttributes.ATTACK_SPEED, EntityAttributeModifier(
                    Item.BASE_ATTACK_SPEED_MODIFIER_ID,
                    attackSpeed.toDouble(),
                    EntityAttributeModifier.Operation.ADD_VALUE
                ), AttributeModifierSlot.MAINHAND)
                .build();
        }
    }
}
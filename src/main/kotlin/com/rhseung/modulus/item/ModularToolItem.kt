package com.rhseung.modulus.item

import com.rhseung.modulus.init.ModComponents
import com.rhseung.modulus.init.ModItemGroups
import com.rhseung.modulus.tool.ToolPart
import com.rhseung.modulus.tool.ToolType
import com.rhseung.modulus.tool.component.ToolPartsComponent
import com.rhseung.modulus.util.ARGBColor
import com.rhseung.modulus.util.Utils.plus
import com.rhseung.modulus.util.Utils.titlecase
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.client.gui.screen.Screen
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.*
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class ModularToolItem private constructor(
    name: String,
    val toolType: ToolType,
    settings: Settings
) : AutoRegistryItem(name, ModItemGroups.TOOLS, settings) {

    // todo: 내구도 분리
    // todo: crack particle tint

    override fun getName(stack: ItemStack): Text {
        val toolParts = getToolPartsComponent(stack);
        val mainPartType = toolType.mainPartType;
        val mainPart = toolParts[mainPartType]!!;

        return Text.of("${mainPart.toolMaterial.name} ${toolType.name}".titlecase());
    }

    override fun appendTooltip(stack: ItemStack, context: TooltipContext, tooltip: MutableList<Text>, type: TooltipType) {
        super.appendTooltip(stack, context, tooltip, type);

        val toolParts = getToolPartsComponent(stack);

        if (Screen.hasShiftDown()) {
            tooltip.add(Text.literal("Tier: ${toolParts.maxTier.name.lowercase()}").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Durability: ${toolParts.durability}").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Mining Speed: ${toolParts.miningSpeed}").formatted(Formatting.GRAY));
            tooltip.add(Text.literal("Enchantability: ${toolParts.enchantmentValue}").formatted(Formatting.GRAY));
            tooltip.add(ScreenTexts.EMPTY);
        }

        toolParts.toList().forEachIndexed { i, part ->
            val toolMaterial = part.toolMaterial;
            val partType = part.partType;

            tooltip.add(
                Text.literal("> ").formatted(Formatting.DARK_GRAY) +
                Text.literal("${toolMaterial.name} ${partType.name}".titlecase()).withColor(toolMaterial.color.toInt())
            );
        }
    }

    companion object {
        fun of(
            name: String,
            toolType: ToolType,
            materialFactory: (ToolType) -> List<ToolPart> = { it.withMaterials() }
        ): ModularToolItem {
            val itemRegistryLookup = Registries.createEntryLookup(Registries.ITEM);
            val blockRegistryLookup = Registries.createEntryLookup(Registries.BLOCK);

            var settings = Settings();

            /**
             * @see ToolPartsComponent
             */
            val toolPartsComponent = ToolPartsComponent(materialFactory(toolType));
            if (!toolPartsComponent.toolPartTypes.containsAll(toolType.necessaryPartTypes)) {
                val missingPartTypes = toolType.necessaryPartTypes - toolPartsComponent.toolPartTypes;
                throw IllegalArgumentException("$name is missing necessary part types $missingPartTypes");
            }
            settings = settings.component(
                ModComponents.TOOL_PARTS,
                toolPartsComponent
            );

            /**
             * maxDamage is durability of the tool
             */
            val durability = toolPartsComponent.durability;
            settings = settings.maxDamage(durability);

//            /**
//             * @see StructuredDurabilityComponent
//             */
//            val durabilityComponent = StructuredDurabilityComponent.DEFAULT(toolType, toolPartsComponent);
//            settings = settings.component(
//                ModComponents.STRUCTURED_DURABILITY,
//                durabilityComponent
//            ).maxCount(1);

            /**
             * @see EnchantableComponent
             */
            val enchantmentValue = toolPartsComponent.enchantmentValue;
            settings = settings.enchantable(enchantmentValue);

            /**
             * [com.rhseung.modulus.mixin.ItemStackMixin] 에서 구현됨
             * @see RepairableComponent
             */
            val repairTags = toolPartsComponent.repairTags;

            /**
             * @see AttributeModifiersComponent
             */
            val attackDamage = toolPartsComponent.bonusAttackDamage + toolType.baseAttackDamage.toDouble();
            val attackSpeed = toolType.baseAttackSpeed.toDouble();
            val attributeModifiers = AttributeModifiersComponent.builder()
                .add(
                    EntityAttributes.ATTACK_DAMAGE,
                    EntityAttributeModifier(
                        Item.BASE_ATTACK_DAMAGE_MODIFIER_ID,
                        attackDamage,
                        Operation.ADD_VALUE
                    ),
                    AttributeModifierSlot.MAINHAND
                ).add(
                    EntityAttributes.ATTACK_SPEED,
                    EntityAttributeModifier(
                        Item.BASE_ATTACK_SPEED_MODIFIER_ID,
                        attackSpeed,
                        Operation.ADD_VALUE
                    ),
                    AttributeModifierSlot.MAINHAND
                )
            settings = settings.attributeModifiers(attributeModifiers.build());

            /**
             * @see ToolComponent
             */
            val miningSpeed = toolPartsComponent.miningSpeed;
            val maxTier = toolPartsComponent.maxTier;
            val neverDroppingRule = ToolComponent.Rule.ofNeverDropping(blockRegistryLookup.getOrThrow(maxTier.incorrectBlockTag));
            val alwaysDroppingRules = toolType.mineableBlockTags.map {
                ToolComponent.Rule.ofAlwaysDropping(blockRegistryLookup.getOrThrow(it), miningSpeed.toFloat())
            }.toTypedArray();
            settings = settings.component(
                DataComponentTypes.TOOL,
                ToolComponent(listOf(neverDroppingRule, *alwaysDroppingRules), 1f, 1)
            );

            return ModularToolItem(name, toolType, settings);
        }

        fun of(toolType: ToolType, materialFactory: (ToolType) -> List<ToolPart> = { it.withMaterials() }): ModularToolItem {
            return of(toolType.name, toolType, materialFactory);
        }

        fun getToolPartsComponent(itemStack: ItemStack): ToolPartsComponent {
            require(itemStack.item is ModularToolItem) { "ItemStack($itemStack) is not a ModularToolItem" };
            return itemStack.get(ModComponents.TOOL_PARTS)!!;
        }

//        fun getStructuredDurabilityComponent(itemStack: ItemStack): StructuredDurabilityComponent {
//            require(itemStack.item is ModularToolItem) { "ItemStack($itemStack) is not a ModularToolItem" };
//            return itemStack.get(ModComponents.STRUCTURED_DURABILITY)!!;
//        }

        fun onClient(item: ModularToolItem) {
            ColorProviderRegistry.ITEM.register({ stack, tintIndex ->
                val toolType = (stack.item as ModularToolItem).toolType;
                val toolParts = getToolPartsComponent(stack);
                val toolPartType = toolType.everyPartTypes[tintIndex];
                val toolPart = toolParts[toolPartType];

                return@register (toolPart?.toolMaterial?.color ?: ARGBColor.EMPTY).toInt();
            }, item);
        }
    }
}
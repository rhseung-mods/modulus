package com.rhseung.modulus.item

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.init.ModComponents
import com.rhseung.modulus.init.ModItemGroups
import com.rhseung.modulus.tool.ToolPart
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.tool.ToolPosition
import com.rhseung.modulus.tool.ToolType
import com.rhseung.modulus.tool.component.ToolPartsComponent
import com.rhseung.modulus.util.RGBColor
import com.rhseung.modulus.util.Utils.colored
import com.rhseung.modulus.util.Utils.plus
import com.rhseung.modulus.util.Utils.titlecase
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
import net.minecraft.util.Identifier

class ToolItem private constructor(
    name: String,
    val toolType: ToolType,
    settings: Settings
) : InitializableItem(name, ModItemGroups.TOOLS, settings) {

    // todo: 내구도 분리
    // todo: crack particle tint
    // todo: name map

    override fun getName(stack: ItemStack): Text {
        val toolPartsComponent = getToolPartsComponent(stack);
        val mainPart = toolPartsComponent[toolType.mainPartPosition]!!;

        return Text.of("${mainPart.toolMaterial.name} ${toolType.name.lowercase()}".titlecase());
    }

    override fun appendTooltip(stack: ItemStack, context: TooltipContext, tooltip: MutableList<Text>, type: TooltipType) {
        super.appendTooltip(stack, context, tooltip, type);

        val toolPartsComponent = getToolPartsComponent(stack);

        if (Screen.hasShiftDown()) {
            tooltip.add("Tier: ${toolPartsComponent.maxTier.name.lowercase()}" colored RGBColor.GRAY);
            tooltip.add("Durability: ${toolPartsComponent.durability}" colored RGBColor.GRAY);
            tooltip.add("Mining Speed: ${toolPartsComponent.miningSpeed}" colored RGBColor.GRAY);
            tooltip.add("Enchantability: ${toolPartsComponent.enchantmentValue}" colored RGBColor.GRAY);
            tooltip.add(ScreenTexts.EMPTY);
        }

        toolPartsComponent.forEachIndexed { i, _, part ->
            val toolMaterial = part.toolMaterial;
            val partType = part.partType;

            tooltip.add(
                ("» " colored RGBColor.DARK_GRAY) +
                ("${toolMaterial.name} ${partType.name}".titlecase() colored toolMaterial.color)
            );
        }
    }

    companion object {
        fun of(
            name: String,
            toolType: ToolType,
            toolParts: Map<ToolPosition, ToolPart>
        ): ToolItem {
            val itemRegistryLookup = Registries.createEntryLookup(Registries.ITEM);
            val blockRegistryLookup = Registries.createEntryLookup(Registries.BLOCK);

            var settings = Settings();

            /**
             * @see ToolPartsComponent
             */
            val toolPartsComponent = ToolPartsComponent(toolType, toolParts);
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
            val attackDamage = toolPartsComponent.attackDamage;
            val attackSpeed = toolPartsComponent.attackSpeed;   // todo: reach도 구현
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
            val alwaysDroppingRules = toolPartsComponent.mineableBlockTags.map {
                ToolComponent.Rule.ofAlwaysDropping(blockRegistryLookup.getOrThrow(it), miningSpeed.toFloat())
            }.toTypedArray();
            settings = settings.component(
                DataComponentTypes.TOOL,
                ToolComponent(listOf(neverDroppingRule, *alwaysDroppingRules), 1f, 1)
            );

            return ToolItem(name, toolType, settings);
        }

        fun getModelId(toolType: ToolType): Identifier {
            return Modulus.id(toolType.name);
        }

        fun getPartModelId(toolType: ToolType, partType: ToolPartType): Identifier {
            require(partType.position in toolType.everyPartPositions) { "PartType($partType) is not in ToolType($toolType)" };
            return Modulus.id("${toolType.name}/${partType.name}");
        }

        fun getToolPartsComponent(itemStack: ItemStack): ToolPartsComponent {
            require(itemStack.item is ToolItem) { "ItemStack($itemStack) is not a ModularToolItem" };
            return itemStack.get(ModComponents.TOOL_PARTS)!!;
        }

//        fun getStructuredDurabilityComponent(itemStack: ItemStack): StructuredDurabilityComponent {
//            require(itemStack.item is ModularToolItem) { "ItemStack($itemStack) is not a ModularToolItem" };
//            return itemStack.get(ModComponents.STRUCTURED_DURABILITY)!!;
//        }
    }
}
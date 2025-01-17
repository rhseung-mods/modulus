package com.rhseung.modulus.item

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.datagen.LanguageProvider.Words
import com.rhseung.modulus.init.ModComponents
import com.rhseung.modulus.tool.*
import com.rhseung.modulus.tool.component.ToolPartsComponent
import com.rhseung.modulus.util.RGBColor
import com.rhseung.modulus.util.Utils.colored
import com.rhseung.modulus.util.Utils.plus
import net.minecraft.client.gui.screen.Screen
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.*
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier

class ToolItem private constructor(
    name: String,
    itemGroup: RegistryKey<ItemGroup>?,
    val toolType: ToolType,
    settings: Settings
) : InitializableItem(name, itemGroup, settings) {

    // todo: structured durability

    override fun getName(stack: ItemStack): Text {
        val toolPartsComponent = getToolPartsComponent(stack);
        val mainPart = toolPartsComponent.mainPart ?: return super.getName(stack);
        val synergy = ToolSynergy.entries.find { toolPartsComponent.toolPartTypes.containsAll(it.partTypes) };

        return if (synergy != null)
            mainPart.toolMaterial.getName() + ScreenTexts.space() + synergy.getName();
        else
            mainPart.toolMaterial.getName() + ScreenTexts.space() + Words.TOOL.getName();
    }

    override fun appendTooltip(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Text>,
        type: TooltipType
    ) {
        super.appendTooltip(stack, context, tooltip, type);

        val toolPartsComponent = getToolPartsComponent(stack);

        if (Screen.hasShiftDown()) {
            val tierTitle = (Words.TIER.getName() + Text.literal(": ")) colored RGBColor.DARK_GRAY;
            val tierContent = toolPartsComponent.maxTier.getName() colored RGBColor.GRAY;
            tooltip.add(RGBColor.DARK_AQUA(" > ") + tierTitle + tierContent);

            val durabilityTitle = (Words.DURABILITY.getName() + Text.literal(": ")) colored RGBColor.DARK_GRAY;
            val durabilityContent = RGBColor.GRAY((stack.maxDamage - stack.damage).toString()) +
                    RGBColor.DARK_GRAY("/") +
                    RGBColor.GRAY(toolPartsComponent.durability.toString());
            tooltip.add(RGBColor.DARK_AQUA(" > ") + durabilityTitle + durabilityContent);

            val miningSpeedTitle = (Words.MINING_SPEED.getName() + Text.literal(": ")) colored RGBColor.DARK_GRAY;
            val miningSpeedContent = toolPartsComponent.miningSpeed.toString() colored RGBColor.GRAY;
            tooltip.add(RGBColor.DARK_AQUA(" > ") + miningSpeedTitle + miningSpeedContent);

            val enchantabilityTitle = (Words.ENCHANTABILITY.getName() + Text.literal(": ")) colored RGBColor.DARK_GRAY;
            val enchantabilityContent = toolPartsComponent.enchantmentValue.toString() colored RGBColor.GRAY;
            tooltip.add(RGBColor.DARK_AQUA(" > ") + enchantabilityTitle + enchantabilityContent);

            if (toolPartsComponent.actions.isNotEmpty()) {
                val actionTitle = (Words.ACTION.getName() + Text.literal(": ")) colored RGBColor.DARK_GRAY;
                val actionContents = toolPartsComponent.actions.map { (Text.literal("  - ") + it.getName()) colored RGBColor.DARK_GRAY };
                tooltip.add(RGBColor.DARK_AQUA(" > ") + actionTitle);
                actionContents.forEach(tooltip::add);
            }

            tooltip.add(ScreenTexts.EMPTY);
        }

        toolPartsComponent.forEachIndexed { i, _, part ->
            tooltip.add(("» " colored RGBColor.DARK_GRAY) + (part.getName() colored part.toolMaterial.color));
        }
    }

    fun useOnBlock(stack: ItemStack, context: ItemUsageContext): ActionResult {
        val toolPartsComponent = getToolPartsComponent(stack);

        toolPartsComponent.actions.forEach { action ->
            val ret = action(context);
            if (ret is ActionResult.Success)
                return ret;
        }

        return ActionResult.PASS;
    }

// todo: ToolAction 다양화
//    override fun useOnEntity(
//        stack: ItemStack?,
//        user: PlayerEntity?,
//        entity: LivingEntity?,
//        hand: Hand?
//    ): ActionResult? {
//        return super.useOnEntity(stack, user, entity, hand)
//    }

    companion object {
        fun update(
            stack: ItemStack,
            toolType: ToolType,
            toolParts: Map<ToolPosition, ToolPart>,
            registries: RegistryWrapper.WrapperLookup
        ) {
            val blockRegistryLookup = registries.getOrThrow(RegistryKeys.BLOCK);

            val toolPartsComponent = ToolPartsComponent(toolType, toolParts);
            stack.set(ModComponents.TOOL_PARTS, toolPartsComponent);

            val durability = toolPartsComponent.durability;
            stack.set(DataComponentTypes.DAMAGE, stack.damage.coerceAtMost(durability));
            stack.set(DataComponentTypes.MAX_DAMAGE, durability);

            val enchantmentValue = toolPartsComponent.enchantmentValue;
            stack.set(DataComponentTypes.ENCHANTABLE, EnchantableComponent(enchantmentValue));

            val builder = AttributeModifiersComponent.builder()
                .add(
                    EntityAttributes.ATTACK_DAMAGE,
                    EntityAttributeModifier(
                        Item.BASE_ATTACK_DAMAGE_MODIFIER_ID,
                        toolPartsComponent.attackDamage,
                        Operation.ADD_VALUE
                    ),
                    AttributeModifierSlot.MAINHAND
                ).add(
                    EntityAttributes.ATTACK_SPEED,
                    EntityAttributeModifier(
                        Item.BASE_ATTACK_SPEED_MODIFIER_ID,
                        toolPartsComponent.attackSpeed,
                        Operation.ADD_VALUE
                    ),
                    AttributeModifierSlot.MAINHAND
                );
            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, builder.build());

            val miningSpeed = toolPartsComponent.miningSpeed;
            val maxTier = toolPartsComponent.maxTier;
            val neverDroppingRule =
                ToolComponent.Rule.ofNeverDropping(blockRegistryLookup.getOrThrow(maxTier.incorrectBlockTag));
            val alwaysDroppingRules = toolPartsComponent.mineableBlockTags.map {
                ToolComponent.Rule.ofAlwaysDropping(blockRegistryLookup.getOrThrow(it), miningSpeed.toFloat())
            }.toTypedArray();
            stack.set(DataComponentTypes.TOOL, ToolComponent(listOf(neverDroppingRule, *alwaysDroppingRules), 1f, 1));
        }

        fun of(
            name: String,
            itemGroup: RegistryKey<ItemGroup>?,
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
            val repairTags = toolPartsComponent.repairables;

            /**
             * @see AttributeModifiersComponent
             */
            val attackDamage = toolPartsComponent.attackDamage;
            val attackSpeed = toolPartsComponent.attackSpeed;
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
            val neverDroppingRule =
                ToolComponent.Rule.ofNeverDropping(blockRegistryLookup.getOrThrow(maxTier.incorrectBlockTag));
            val alwaysDroppingRules = toolPartsComponent.mineableBlockTags.map {
                ToolComponent.Rule.ofAlwaysDropping(blockRegistryLookup.getOrThrow(it), miningSpeed.toFloat())
            }.toTypedArray();
            settings = settings.component(
                DataComponentTypes.TOOL,
                ToolComponent(listOf(neverDroppingRule, *alwaysDroppingRules), 1f, 1)
            );

            return ToolItem(name, itemGroup, toolType, settings);
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
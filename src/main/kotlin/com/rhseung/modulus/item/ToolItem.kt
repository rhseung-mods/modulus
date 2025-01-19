package com.rhseung.modulus.item

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.datagen.LanguageProvider.Words
import com.rhseung.modulus.init.ModComponents
import com.rhseung.modulus.tool.*
import com.rhseung.modulus.tool.component.ToolPartsComponent
import com.rhseung.modulus.util.RGBColor
import com.rhseung.modulus.util.Utils.colored
import com.rhseung.modulus.util.Utils.plus
import net.minecraft.block.Block
import net.minecraft.client.gui.screen.Screen
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.*
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryEntryLookup
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.world.World

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
            tooltip.add(("» " colored RGBColor.DARK_GRAY) + (part.getName() colored part.toolMaterial.colorPalette.mainColor));
        }
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val stack = context.stack;
        if (stack.item !is ToolItem)
            return ActionResult.PASS;

        val toolPartsComponent = getToolPartsComponent(stack);
        return if (toolPartsComponent.actions.any { it(context) is ActionResult.Success })
            ActionResult.SUCCESS;
        else
            ActionResult.PASS;
    }

    override fun useOnEntity(
        stack: ItemStack,
        user: PlayerEntity,
        entity: LivingEntity,
        hand: Hand
    ): ActionResult {
        if (stack.item !is ToolItem)
            return ActionResult.PASS;

        val toolPartsComponent = getToolPartsComponent(stack);
        return if (toolPartsComponent.actions.any { it(stack, user, entity, hand) is ActionResult.Success })
            ActionResult.SUCCESS;
        else
            ActionResult.PASS;
    }

    override fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        val stack = user.getStackInHand(hand);
        if (stack.item !is ToolItem)
            return ActionResult.PASS;

        val toolPartsComponent = getToolPartsComponent(stack);
        return if (toolPartsComponent.actions.any { it(world, user, hand) is ActionResult.Success })
            ActionResult.SUCCESS;
        else
            ActionResult.PASS;
    }

    companion object {
        private fun getEnchantableComponent(toolPartsComponent: ToolPartsComponent): EnchantableComponent {
            return EnchantableComponent(toolPartsComponent.enchantmentValue);
        }

        private fun getAttributeModifiersComponent(toolPartsComponent: ToolPartsComponent): AttributeModifiersComponent {
            return AttributeModifiersComponent.builder()
                .add(
                    EntityAttributes.ATTACK_DAMAGE,
                    EntityAttributeModifier(
                        BASE_ATTACK_DAMAGE_MODIFIER_ID,
                        toolPartsComponent.attackDamage,
                        Operation.ADD_VALUE
                    ),
                    AttributeModifierSlot.MAINHAND
                ).add(
                    EntityAttributes.ATTACK_SPEED,
                    EntityAttributeModifier(
                        BASE_ATTACK_SPEED_MODIFIER_ID,
                        toolPartsComponent.attackSpeed,
                        Operation.ADD_VALUE
                    ),
                    AttributeModifierSlot.MAINHAND
                ).build();
        }

        private fun getToolComponent(toolPartsComponent: ToolPartsComponent, blockRegistryLookup: RegistryEntryLookup<Block>): ToolComponent {
            val miningSpeed = toolPartsComponent.miningSpeed;
            val maxTier = toolPartsComponent.maxTier;

            val neverDroppingRule =
                ToolComponent.Rule.ofNeverDropping(blockRegistryLookup.getOrThrow(maxTier.incorrectBlockTag));
            val alwaysDroppingRules = toolPartsComponent.mineableBlockTags.map { tag ->
                ToolComponent.Rule.ofAlwaysDropping(blockRegistryLookup.getOrThrow(tag), miningSpeed.toFloat())
            };

            return ToolComponent(listOf(neverDroppingRule, *alwaysDroppingRules.toTypedArray()), 1f, 1);
        }

        fun update(
            stack: ItemStack,
            toolType: ToolType,
            toolParts: Map<ToolPosition, ToolPart>,
            registries: RegistryWrapper.WrapperLookup
        ) {
            val blockRegistryLookup: RegistryWrapper.Impl<Block> = registries.getOrThrow(RegistryKeys.BLOCK);
            val toolPartsComponent = ToolPartsComponent(toolType, toolParts);

            stack.set(ModComponents.TOOL_PARTS, toolPartsComponent);
            stack.set(DataComponentTypes.DAMAGE, stack.damage.coerceAtMost(toolPartsComponent.durability));
            stack.set(DataComponentTypes.MAX_DAMAGE, toolPartsComponent.durability);
            stack.set(DataComponentTypes.ENCHANTABLE, getEnchantableComponent(toolPartsComponent));
            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, getAttributeModifiersComponent(toolPartsComponent));
            stack.set(DataComponentTypes.TOOL, getToolComponent(toolPartsComponent, blockRegistryLookup));
        }

        fun of(
            name: String,
            itemGroup: RegistryKey<ItemGroup>?,
            toolType: ToolType,
            toolParts: Map<ToolPosition, ToolPart>
        ): ToolItem {
            val blockRegistryLookup: RegistryEntryLookup<Block> = Registries.createEntryLookup(Registries.BLOCK);
            val toolPartsComponent = ToolPartsComponent(toolType, toolParts);

//            /**
//             * @see StructuredDurabilityComponent
//             */
//            val durabilityComponent = StructuredDurabilityComponent.DEFAULT(toolType, toolPartsComponent);
//            settings = settings.component(
//                ModComponents.STRUCTURED_DURABILITY,
//                durabilityComponent
//            ).maxCount(1);

            val settings = Settings();
            settings.component(ModComponents.TOOL_PARTS, toolPartsComponent);
            settings.maxDamage(toolPartsComponent.durability);
            settings.component(DataComponentTypes.ENCHANTABLE, getEnchantableComponent(toolPartsComponent));
            settings.component(DataComponentTypes.ATTRIBUTE_MODIFIERS, getAttributeModifiersComponent(toolPartsComponent));
            settings.component(DataComponentTypes.TOOL, getToolComponent(toolPartsComponent, blockRegistryLookup));

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
package com.rhseung.modulus.tool

import com.rhseung.modulus.Modulus
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.CampfireBlock
import net.minecraft.block.Oxidizable
import net.minecraft.block.PillarBlock
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.HoeItem
import net.minecraft.item.HoneycombItem
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import net.minecraft.world.WorldEvents
import net.minecraft.world.event.GameEvent
import java.util.Optional
import java.util.function.Consumer

data class ToolAction(
    val name: String,
    val action: OnAction
) : Translatable {

    interface OnAction {
        fun use(world: World, user: PlayerEntity, hand: Hand): ActionResult = ActionResult.PASS;

        fun useOnEntity(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand): ActionResult = ActionResult.PASS;

        fun useOnBlock(context: ItemUsageContext): ActionResult = ActionResult.PASS;
    }

    init {
        if (name in VALUES_MAP)
            throw IllegalArgumentException("Duplicate tool action name: $name");

        VALUES_MAP[name] = this;
        VALUES.add(this);
    }

    override val translationKey: String = Modulus.id("action.$name").toTranslationKey();

    override fun toString(): String {
        return name.uppercase();
    }

    override fun equals(other: Any?): Boolean {
        return other is ToolAction && other.name == name;
    }

    override fun hashCode(): Int {
        var result = name.hashCode();
        return result;
    }

    operator fun invoke(context: ItemUsageContext): ActionResult {
        return action.useOnBlock(context);
    }

    operator fun invoke(world: World, user: PlayerEntity, hand: Hand): ActionResult {
        return action.use(world, user, hand);
    }

    operator fun invoke(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, hand: Hand): ActionResult {
        return action.useOnEntity(stack, user, entity, hand);
    }

    companion object {
        private val VALUES_MAP = mutableMapOf<String, ToolAction>();
        val VALUES: MutableList<ToolAction> = mutableListOf();

        fun fromName(name: String): ToolAction {
            return VALUES_MAP[name] ?: throw IllegalArgumentException("Unknown tool action name: $name");
        }

        private fun shouldCancelAttempt(player: PlayerEntity?, hand: Hand): Boolean {
            // todo: modular shield support
            return if (player == null) true
            else hand == Hand.MAIN_HAND &&
                    player.offHandStack.isOf(Items.SHIELD) &&
                    player.shouldCancelInteraction().not();
        }

        val STRIP = ToolAction("strip", object : OnAction {
            private val STRIPPED_MAP = mapOf<Block, Block>(
                Blocks.OAK_WOOD to Blocks.STRIPPED_OAK_WOOD,
                Blocks.OAK_LOG to Blocks.STRIPPED_OAK_LOG,
                Blocks.DARK_OAK_WOOD to Blocks.STRIPPED_DARK_OAK_WOOD,
                Blocks.DARK_OAK_LOG to Blocks.STRIPPED_DARK_OAK_LOG,
                Blocks.PALE_OAK_WOOD to Blocks.STRIPPED_PALE_OAK_WOOD,
                Blocks.PALE_OAK_LOG to Blocks.STRIPPED_PALE_OAK_LOG,
                Blocks.ACACIA_WOOD to Blocks.STRIPPED_ACACIA_WOOD,
                Blocks.ACACIA_LOG to Blocks.STRIPPED_ACACIA_LOG,
                Blocks.CHERRY_WOOD to Blocks.STRIPPED_CHERRY_WOOD,
                Blocks.CHERRY_LOG to Blocks.STRIPPED_CHERRY_LOG,
                Blocks.BIRCH_WOOD to Blocks.STRIPPED_BIRCH_WOOD,
                Blocks.BIRCH_LOG to Blocks.STRIPPED_BIRCH_LOG,
                Blocks.JUNGLE_WOOD to Blocks.STRIPPED_JUNGLE_WOOD,
                Blocks.JUNGLE_LOG to Blocks.STRIPPED_JUNGLE_LOG,
                Blocks.SPRUCE_WOOD to Blocks.STRIPPED_SPRUCE_WOOD,
                Blocks.SPRUCE_LOG to Blocks.STRIPPED_SPRUCE_LOG,
                Blocks.WARPED_STEM to Blocks.STRIPPED_WARPED_STEM,
                Blocks.WARPED_HYPHAE to Blocks.STRIPPED_WARPED_HYPHAE,
                Blocks.CRIMSON_STEM to Blocks.STRIPPED_CRIMSON_STEM,
                Blocks.CRIMSON_HYPHAE to Blocks.STRIPPED_CRIMSON_HYPHAE,
                Blocks.MANGROVE_WOOD to Blocks.STRIPPED_MANGROVE_WOOD,
                Blocks.MANGROVE_LOG to Blocks.STRIPPED_MANGROVE_LOG,
                Blocks.BAMBOO_BLOCK to Blocks.STRIPPED_BAMBOO_BLOCK,
                // todo: custom wood support
            );

            private fun getStrippedState(state: BlockState): BlockState? {
                return STRIPPED_MAP[state.block]?.defaultState?.with(PillarBlock.AXIS, state.get(PillarBlock.AXIS));
            }

            private fun tryStrip(world: World, blockPos: BlockPos, player: PlayerEntity?, state: BlockState): BlockState? {
                val strippedState: BlockState? = getStrippedState(state);

                if (strippedState != null) {
                    world.playSound(player, blockPos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1f, 1f);
                    return strippedState;
                }
                else
                    return null;
            }

            override fun useOnBlock(context: ItemUsageContext): ActionResult {
                val world = context.world;
                val blockPos = context.blockPos;
                val player = context.player;
                val hand = context.hand;

                val changedState = tryStrip(world, blockPos, player, world.getBlockState(blockPos));

                if (shouldCancelAttempt(player, hand)) {
                    return ActionResult.PASS;
                }
                else if (changedState == null) {
                    return ActionResult.PASS;
                }
                else {
                    val itemStack = context.stack;

                    if (player is ServerPlayerEntity)
                        Criteria.ITEM_USED_ON_BLOCK.trigger(player, blockPos, itemStack);

                    world.setBlockState(blockPos, changedState, Block.NOTIFY_ALL_AND_REDRAW);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(player, changedState));

                    if (player != null)
                        itemStack.damage(1, player, LivingEntity.getSlotForHand(hand));

                    return ActionResult.SUCCESS;
                }
            }
        });

        val DECREASE_OXIDATION = ToolAction("decrease_oxidation", object : OnAction {
            private fun tryDecreaseOxidation(world: World, blockPos: BlockPos, player: PlayerEntity?, state: BlockState): BlockState? {
                val decreasedState: Optional<BlockState> = Oxidizable.getDecreasedOxidationState(state);

                if (decreasedState.isPresent) {
                    world.playSound(player, blockPos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1f, 1f);
                    world.syncWorldEvent(player, WorldEvents.BLOCK_SCRAPED, blockPos, 0);
                    return decreasedState.get();
                }
                else
                    return null;
            }

            override fun useOnBlock(context: ItemUsageContext): ActionResult {
                val world = context.world;
                val blockPos = context.blockPos;
                val player = context.player;
                val hand = context.hand;

                val changedState = tryDecreaseOxidation(world, blockPos, player, world.getBlockState(blockPos));

                if (shouldCancelAttempt(player, hand)) {
                    return ActionResult.PASS;
                }
                else if (changedState == null) {
                    return ActionResult.PASS;
                }
                else {
                    val itemStack = context.stack;

                    if (player is ServerPlayerEntity)
                        Criteria.ITEM_USED_ON_BLOCK.trigger(player, blockPos, itemStack);

                    world.setBlockState(blockPos, changedState, Block.NOTIFY_ALL_AND_REDRAW);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(player, changedState));

                    if (player != null)
                        itemStack.damage(1, player, LivingEntity.getSlotForHand(hand));

                    return ActionResult.SUCCESS;
                }
            }
        });

        val REMOVE_WAX = ToolAction("remove_wax", object : OnAction {
            private fun tryRemoveWax(world: World, blockPos: BlockPos, player: PlayerEntity?, state: BlockState): BlockState? {
                val waxRemovedState: BlockState? = HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get()[state.block]?.getStateWithProperties(state);

                if (waxRemovedState != null) {
                    world.playSound(player, blockPos, SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1f, 1f);
                    world.syncWorldEvent(player, WorldEvents.WAX_REMOVED, blockPos, 0);
                    return waxRemovedState;
                }
                else
                    return null;
            }

            override fun useOnBlock(context: ItemUsageContext): ActionResult {
                val world = context.world;
                val blockPos = context.blockPos;
                val player = context.player;
                val hand = context.hand;

                val changedState = tryRemoveWax(world, blockPos, player, world.getBlockState(blockPos));

                if (shouldCancelAttempt(player, hand)) {
                    return ActionResult.PASS;
                }
                else if (changedState == null) {
                    return ActionResult.PASS;
                }
                else {
                    val itemStack = context.stack;

                    if (player is ServerPlayerEntity)
                        Criteria.ITEM_USED_ON_BLOCK.trigger(player, blockPos, itemStack);

                    world.setBlockState(blockPos, changedState, Block.NOTIFY_ALL_AND_REDRAW);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(player, changedState));

                    if (player != null)
                        itemStack.damage(1, player, LivingEntity.getSlotForHand(hand));

                    return ActionResult.SUCCESS;
                }
            }
        });

        val TILL = ToolAction("till", object : OnAction {
            private val TILLING_MAP = mapOf<Block, Pair<(ItemUsageContext) -> Boolean, Consumer<ItemUsageContext>>>(
                Blocks.GRASS_BLOCK to Pair(HoeItem::canTillFarmland, HoeItem.createTillAction(Blocks.FARMLAND.defaultState)),
                Blocks.DIRT to Pair(HoeItem::canTillFarmland, HoeItem.createTillAction(Blocks.FARMLAND.defaultState)),
                Blocks.DIRT_PATH to Pair(HoeItem::canTillFarmland, HoeItem.createTillAction(Blocks.FARMLAND.defaultState)),
                Blocks.COARSE_DIRT to Pair(HoeItem::canTillFarmland, HoeItem.createTillAction(Blocks.DIRT.defaultState)),
                Blocks.ROOTED_DIRT to Pair({ true }, HoeItem.createTillAndDropAction(Blocks.DIRT.defaultState, Items.HANGING_ROOTS)),
                // todo: custom tilling support
            );

            override fun useOnBlock(context: ItemUsageContext): ActionResult {
                val world = context.world;
                val blockPos = context.blockPos;
                val state = world.getBlockState(blockPos);
                val (condition, consumer) = TILLING_MAP[state.block] ?: return ActionResult.PASS;

                if (condition(context)) {
                    val player = context.player;
                    world.playSound(player, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1f, 1f);

                    if (!world.isClient) {
                        consumer.accept(context);

                        val itemStack = context.stack;
                        val hand = context.hand;
                        if (player != null)
                            itemStack.damage(1, player, LivingEntity.getSlotForHand(hand));
                    }

                    return ActionResult.SUCCESS;
                }
                else
                    return ActionResult.PASS;
            }
        });

        val PATH = ToolAction("path", object : OnAction {
            private val PATH_MAP = mapOf<Block, BlockState>(
                Blocks.GRASS_BLOCK to Blocks.DIRT_PATH.defaultState,
                Blocks.DIRT to Blocks.DIRT_PATH.defaultState,
                Blocks.PODZOL to Blocks.DIRT_PATH.defaultState,
                Blocks.COARSE_DIRT to Blocks.DIRT_PATH.defaultState,
                Blocks.MYCELIUM to Blocks.DIRT_PATH.defaultState,
                Blocks.ROOTED_DIRT to Blocks.DIRT_PATH.defaultState,
                // todo: custom path support
            );

            override fun useOnBlock(context: ItemUsageContext): ActionResult {
                val world = context.world;
                val blockPos = context.blockPos;
                val state = world.getBlockState(blockPos);
                val player = context.player;

                if (context.side == Direction.DOWN) {
                    return ActionResult.PASS;
                }
                else {
                    val itemStack = context.stack;
                    val pathState = PATH_MAP[state.block];

                    if (pathState != null && world.getBlockState(blockPos.up()).isAir) {
                        world.playSound(player, blockPos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1f, 1f);
                    }

                    if (pathState != null) {
                        if (!world.isClient) {
                            world.setBlockState(blockPos, pathState, Block.NOTIFY_ALL_AND_REDRAW);
                            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(player, pathState));

                            if (player != null)
                                itemStack.damage(1, player, LivingEntity.getSlotForHand(context.hand));
                        }

                        return ActionResult.SUCCESS;
                    }
                    else
                        return ActionResult.PASS;
                }
            }
        });

        val CAMPFIRE_EXTINGUISH = ToolAction("campfire_extinguish", object : OnAction {
            override fun useOnBlock(context: ItemUsageContext): ActionResult {
                val world = context.world;
                val blockPos = context.blockPos;
                val state = world.getBlockState(blockPos);
                val player = context.player;

                if (context.side == Direction.DOWN) {
                    return ActionResult.PASS;
                }
                else {
                    val itemStack = context.stack;

                    if (state.block is CampfireBlock && state.get(CampfireBlock.LIT)) {
                        if (!world.isClient)
                            world.syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, blockPos, 0);

                        CampfireBlock.extinguish(player, world, blockPos, state);
                        val changedState = state.with(CampfireBlock.LIT, false);

                        if (!world.isClient) {
                            world.setBlockState(blockPos, changedState, Block.NOTIFY_ALL_AND_REDRAW);
                            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(player, changedState));

                            if (player != null)
                                itemStack.damage(1, player, LivingEntity.getSlotForHand(context.hand));
                        }

                        return ActionResult.SUCCESS;
                    }
                    else
                        return ActionResult.PASS;
                }
            }
        });

        val AXE_ACTIONS = listOf(STRIP, DECREASE_OXIDATION, REMOVE_WAX);
        val HOE_ACTIONS = listOf(TILL);
        val SHOVEL_ACTIONS = listOf(PATH, CAMPFIRE_EXTINGUISH);
    }
}
package com.rhseung.modulus.gear.tool.action

import com.rhseung.modulus.gear.tool.action.ToolAction.OnAction
import net.minecraft.advancement.criterion.Criteria
import net.minecraft.block.*
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.HoeItem
import net.minecraft.item.HoneycombItem
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
import java.util.*
import java.util.function.Consumer

object ToolActions {
    val EMPTY = listOf<ToolAction>();
    val AXE_ACTIONS = listOf(ToolAction.STRIP, ToolAction.DECREASE_OXIDATION, ToolAction.REMOVE_WAX);
    val HOE_ACTIONS = listOf(ToolAction.TILL);
    val SHOVEL_ACTIONS = listOf(ToolAction.PATH, ToolAction.CAMPFIRE_EXTINGUISH);
}
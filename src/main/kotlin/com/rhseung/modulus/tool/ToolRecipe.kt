package com.rhseung.modulus.tool

import com.rhseung.modulus.init.ModItems
import com.rhseung.modulus.init.ModSerializers
import com.rhseung.modulus.item.ToolItem
import com.rhseung.modulus.item.ToolPartItem
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.recipe.input.CraftingRecipeInput
import net.minecraft.registry.RegistryWrapper
import net.minecraft.world.World

class ToolRecipe(craftingRecipeCategory: CraftingRecipeCategory) : SpecialCraftingRecipe(craftingRecipeCategory) {
    override fun getSerializer(): RecipeSerializer<ToolRecipe> {
        return ModSerializers.TOOL_RECIPE;
    }

    override fun matches(input: CraftingRecipeInput, world: World): Boolean {
        val inputs = input.stacks;
        val partItems = inputs.mapNotNull { it.item as? ToolPartItem };
        val positions = partItems.map { it.partType.position };

        return ToolType.entries.any { toolType ->
            toolType.everyPartPositions.containsAll(positions) &&
            positions.containsAll(toolType.necessaryPartPositions)
        };
    }

    override fun craft(input: CraftingRecipeInput, registries: RegistryWrapper.WrapperLookup): ItemStack {
        val inputs = input.stacks;
        val partItems = inputs.mapNotNull { it.item as? ToolPartItem };
        val parts = partItems.map { it.toolPart };
        val positions = parts.map { it.partType.position };

        val toolType = ToolType.entries.find { toolType ->
            toolType.everyPartPositions.containsAll(positions) &&
            positions.containsAll(toolType.necessaryPartPositions)
        }!!;

        val toolStack = ItemStack(ModItems.TEMPORALS[toolType]!!);
        ToolItem.update(toolStack, toolType, parts.associateBy { it.partType.position }, registries);

        return toolStack;
    }
}
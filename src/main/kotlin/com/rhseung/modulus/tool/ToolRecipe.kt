package com.rhseung.modulus.tool

import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.recipe.input.CraftingRecipeInput
import net.minecraft.registry.RegistryWrapper
import net.minecraft.world.World

class ToolRecipe : SpecialCraftingRecipe(CraftingRecipeCategory.EQUIPMENT) {
    override fun getSerializer(): RecipeSerializer<ToolRecipe> {
        TODO("Not yet implemented")
    }

    override fun matches(input: CraftingRecipeInput, world: World): Boolean {
        TODO("Not yet implemented")
    }

    override fun craft(input: CraftingRecipeInput, registries: RegistryWrapper.WrapperLookup): ItemStack {
        TODO("Not yet implemented")
    }
}
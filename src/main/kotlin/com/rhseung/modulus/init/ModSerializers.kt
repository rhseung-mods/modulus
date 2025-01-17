package com.rhseung.modulus.init

import com.rhseung.modulus.tool.ToolRecipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.SpecialCraftingRecipe.SpecialRecipeSerializer

object ModSerializers : IModInit {
    val TOOL_RECIPE: SpecialRecipeSerializer<ToolRecipe> =
        RecipeSerializer.register("tool_recipe", SpecialRecipeSerializer(::ToolRecipe));
}
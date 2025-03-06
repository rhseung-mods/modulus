package com.rhseung.modulus.init

import com.rhseung.blueprint.registration.IModInit
import com.rhseung.modulus.gear.tool.recipe.ToolStructionRecipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.SpecialCraftingRecipe.SpecialRecipeSerializer

object ModulusRecipeSerializers : IModInit {
    override fun initialize() {
    }

    override fun initializeClient() {
    }

    val TOOL_STRUCTION: SpecialRecipeSerializer<ToolStructionRecipe> =
        RecipeSerializer.register("tool_struction", SpecialRecipeSerializer(::ToolStructionRecipe));
}
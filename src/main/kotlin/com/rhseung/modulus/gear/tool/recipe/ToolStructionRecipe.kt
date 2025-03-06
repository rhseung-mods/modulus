package com.rhseung.modulus.gear.tool.recipe

import com.rhseung.modulus.gear.ModulusTool
import com.rhseung.modulus.gear.ModulusToolPart
import com.rhseung.modulus.gear.tool.type.ToolType
import com.rhseung.modulus.init.ModulusItems
import com.rhseung.modulus.init.ModulusRecipeSerializers
import net.minecraft.item.ItemStack
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.SpecialCraftingRecipe
import net.minecraft.recipe.book.CraftingRecipeCategory
import net.minecraft.recipe.input.CraftingRecipeInput
import net.minecraft.registry.RegistryWrapper
import net.minecraft.world.World

class ToolStructionRecipe(category: CraftingRecipeCategory) : SpecialCraftingRecipe(category) {
    override fun getSerializer(): RecipeSerializer<ToolStructionRecipe> {
        return ModulusRecipeSerializers.TOOL_STRUCTION;
    }

    override fun matches(
        input: CraftingRecipeInput,
        world: World
    ): Boolean {
        val inputStacks = input.stacks;
        if (!inputStacks.all { it.item is ModulusToolPart })
            return false;

        val parts = inputStacks.map { it.item as ModulusToolPart };
        if (parts.distinctBy { it.position }.size != parts.size)
            return false;

        return ToolType.entries.any {
            it.positions.toSet() == parts.map { part -> part.position }.toSet()
        };
    }

    override fun craft(
        input: CraftingRecipeInput,
        registries: RegistryWrapper.WrapperLookup
    ): ItemStack {
        val inputStacks = input.stacks;
        val parts: List<ModulusToolPart> = inputStacks.map { it.item as ModulusToolPart };
        val toolType: ToolType = ToolType.entries.find {
            it.positions.toSet() == parts.map { part -> part.position }.toSet()
        }!!;

        val toolStack = ItemStack(ModulusItems.TOOLS[toolType]!!);
        ModulusTool.update(toolStack, toolType, parts.map { it.part }, registries);

        return toolStack;
    }
}
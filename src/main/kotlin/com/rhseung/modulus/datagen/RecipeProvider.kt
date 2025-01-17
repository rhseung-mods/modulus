package com.rhseung.modulus.datagen

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.tool.ToolRecipe
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder
import net.minecraft.data.server.recipe.RecipeExporter
import net.minecraft.data.server.recipe.RecipeGenerator
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class RecipeProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>,
) : FabricRecipeProvider(output, registriesFuture) {

    override fun getRecipeGenerator(
        registryLookup: RegistryWrapper.WrapperLookup,
        exporter: RecipeExporter
    ): RecipeGenerator {
        return object : RecipeGenerator(registryLookup, exporter) {
            override fun generate() {
                ComplexRecipeJsonBuilder.create(::ToolRecipe)
                    .offerTo(exporter, Modulus.id("tool_recipe").toString());
            }
        }
    }

    override fun getName(): String {
        return "Modulus Recipe Provider";
    }
}
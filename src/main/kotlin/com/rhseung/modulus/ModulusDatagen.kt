package com.rhseung.modulus

import com.rhseung.modulus.datagen.LanguageProvider
import com.rhseung.modulus.datagen.ModelProvider
import com.rhseung.modulus.datagen.RecipeProvider
import com.rhseung.modulus.datagen.TextureProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

object ModulusDatagen : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack();

		pack.addProvider(::LanguageProvider);
		pack.addProvider(::ModelProvider);
		pack.addProvider(::RecipeProvider);
		pack.addProvider(::TextureProvider);
	}
}
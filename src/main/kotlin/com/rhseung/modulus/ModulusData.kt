package com.rhseung.modulus

import com.rhseung.modulus.datagen.BlockTagProvider
import com.rhseung.modulus.datagen.ItemTagProvider
import com.rhseung.modulus.datagen.LanguageProvider
import com.rhseung.modulus.datagen.ModelProvider
import com.rhseung.modulus.datagen.RecipeProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

object ModulusData : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack();

		pack.addProvider(::ModelProvider);
		pack.addProvider(::ItemTagProvider);
		pack.addProvider(::BlockTagProvider);
		pack.addProvider(::LanguageProvider);
		pack.addProvider(::RecipeProvider);
	}
}
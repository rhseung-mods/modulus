package com.rhseung.modulus.datagen

import com.rhseung.modulus.init.ModItemGroups
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class LanguageProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricLanguageProvider(output, "en_us", registriesFuture) {

    override fun generateTranslations(lookUp: RegistryWrapper.WrapperLookup, translationBuilder: TranslationBuilder) {
        translationBuilder.add(ModItemGroups.TOOLS_NAME, "Modulus Tools");
    }
}
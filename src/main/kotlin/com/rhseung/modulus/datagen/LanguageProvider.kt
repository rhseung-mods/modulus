package com.rhseung.modulus.datagen

import com.rhseung.modulus.init.ModItemGroups
import com.rhseung.modulus.init.ModItems
import com.rhseung.modulus.util.Utils.titlecase
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
        translationBuilder.add(ModItemGroups.PARTS_NAME, "Modulus Parts");

        ModItems.PARTS.values.forEach { part ->
            translationBuilder.add(part, "${part.toolPart.toolMaterial.name.lowercase()} ${part.toolPart.partType.name.titlecase()} Part".titlecase());
        }
    }
}
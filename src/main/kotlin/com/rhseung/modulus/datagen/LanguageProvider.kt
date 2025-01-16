package com.rhseung.modulus.datagen

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.init.ModItemGroups
import com.rhseung.modulus.init.ModItems
import com.rhseung.modulus.tool.ToolMaterial
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.tool.ToolSynergy
import com.rhseung.modulus.tool.Translatable
import com.rhseung.modulus.util.Utils.titlecase
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import java.util.concurrent.CompletableFuture

class LanguageProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricLanguageProvider(output, "en_us", registriesFuture) {

    object Words {
        val VALUES = mutableListOf<Word>();

        data class Word(val name: String): Translatable {
            override val translationKey: String = Modulus.id("word.$name").toTranslationKey();

            init { VALUES.add(this); }
        }

        val PART = Word("part");
        val TOOL = Word("tool");
    }

    override fun generateTranslations(lookUp: RegistryWrapper.WrapperLookup, translationBuilder: TranslationBuilder) {
        translationBuilder.add(ModItemGroups.TOOLS_NAME, "Modulus Tools");
        translationBuilder.add(ModItemGroups.PARTS_NAME, "Modulus Parts");

        ToolMaterial.VALUES.forEach { material ->
            translationBuilder.add(material.translationKey, material.name.titlecase());
        }

        ToolPartType.VALUES.forEach { partType ->
            translationBuilder.add(partType.translationKey, partType.name.titlecase());
        }

        ToolSynergy.entries.forEach { synergy ->
            translationBuilder.add(synergy.translationKey, synergy.name.titlecase());
        }

        Words.VALUES.forEach { word ->
            translationBuilder.add(word.translationKey, word.name.titlecase());
        }
    }
}
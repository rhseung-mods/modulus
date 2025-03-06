package com.rhseung.modulus.datagen

import com.rhseung.blueprint.util.StringUtils.titlecase
import com.rhseung.modulus.gear.tool.action.ToolAction
import com.rhseung.modulus.gear.tool.material.ToolMaterial
import com.rhseung.modulus.gear.tool.part.ToolPartType
import com.rhseung.modulus.gear.tool.synergy.ToolSynergy
import com.rhseung.modulus.gear.tool.type.ToolType
import com.rhseung.modulus.init.ModulusItemGroups
import com.rhseung.modulus.init.ModulusItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class LanguageProvider(
    output: FabricDataOutput,
    registryLookup: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricLanguageProvider(output, registryLookup) {

    override fun generateTranslations(registry: RegistryWrapper.WrapperLookup, translationBuilder: TranslationBuilder) {
        translationBuilder.add(ModulusItemGroups.TOOLS.registryKey, "Modulus Tools");
        translationBuilder.add(ModulusItemGroups.PARTS.registryKey, "Modulus Parts");

        ToolAction.entries.forEach {
            translationBuilder.add(it.translationKey, it.name.titlecase());
        };

        ToolMaterial.entriesNotDefault.forEach {
            translationBuilder.add(it.translationKey, it.name.titlecase());
        };

        ToolPartType.entriesNotDefault.forEach {
            translationBuilder.add(it.translationKey, it.name.titlecase());
        };

        ToolSynergy.entries.forEach {
            translationBuilder.add(it.translationKey, it.name.titlecase());
        };

        ToolType.entries.forEach {
            translationBuilder.add(it.translationKey, it.name.titlecase());
        };

        ModulusItems.TOOLS.values.forEach {
            translationBuilder.add(it, it.id.path.titlecase());
        };

        ModulusItems.PARTS.values.forEach {
            translationBuilder.add(it, it.id.path.titlecase());
        };
    }
}
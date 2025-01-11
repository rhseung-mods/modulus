package com.rhseung.modulus.datagen

import com.rhseung.modulus.init.ModItems
import com.rhseung.modulus.tool.ToolPrimitiveType
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.registry.RegistryWrapper
import net.minecraft.registry.tag.ItemTags
import java.util.concurrent.CompletableFuture

class ItemTagProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider.ItemTagProvider(output, registriesFuture) {

    override fun configure(lookUp: RegistryWrapper.WrapperLookup) {
        (ModItems.TOOLS.values + ModItems.DIAMOND_PICKAXE).forEach { tool ->
            when (tool.toolType.primitiveType) {
                ToolPrimitiveType.WEAPON -> {
                    getOrCreateTagBuilder(ItemTags.SWORD_ENCHANTABLE).add(tool);
                }
                ToolPrimitiveType.BOW -> {
                    getOrCreateTagBuilder(ItemTags.BOW_ENCHANTABLE).add(tool);
                }
                ToolPrimitiveType.CROSSBOW -> {
                    getOrCreateTagBuilder(ItemTags.CROSSBOW_ENCHANTABLE).add(tool);
                }
                ToolPrimitiveType.SPEAR -> {
                    getOrCreateTagBuilder(ItemTags.TRIDENT_ENCHANTABLE).add(tool);
                }
                ToolPrimitiveType.MACE -> {
                    getOrCreateTagBuilder(ItemTags.MACE_ENCHANTABLE).add(tool);
                }
                ToolPrimitiveType.MINING -> {
                    getOrCreateTagBuilder(ItemTags.MINING_ENCHANTABLE).add(tool);
                    getOrCreateTagBuilder(ItemTags.MINING_LOOT_ENCHANTABLE).add(tool);
                }
            }

            getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE).add(tool);
        }
    }
}
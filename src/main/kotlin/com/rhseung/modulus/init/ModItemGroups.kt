package com.rhseung.modulus.init

import com.rhseung.modulus.Modulus
import com.rhseung.modulus.Modulus.MOD_ID
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text

object ModItemGroups : IModInit {
    fun of(name: String) = RegistryKey.of(RegistryKeys.ITEM_GROUP, Modulus.id(name));

    const val TOOLS_NAME = "itemGroup.$MOD_ID.tools";
    val TOOLS = of("tools");

    const val PARTS_NAME = "itemGroup.$MOD_ID.parts";
    val PARTS = of("parts");

    override fun initialize() {
        Registry.register(Registries.ITEM_GROUP, TOOLS.value, FabricItemGroup.builder()
            .displayName(Text.translatable(TOOLS_NAME))
            .icon { ItemStack(ModItems.TOOLS.values.first()) }
            .build()
        );

        Registry.register(Registries.ITEM_GROUP, PARTS.value, FabricItemGroup.builder()
            .displayName(Text.translatable(PARTS_NAME))
            .icon { ItemStack(ModItems.PARTS.values.first()) }
            .build()
        );
    }
}
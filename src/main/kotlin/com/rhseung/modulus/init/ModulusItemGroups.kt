package com.rhseung.modulus.init

import com.rhseung.blueprint.lang.Translatable
import com.rhseung.blueprint.registration.IModInit
import com.rhseung.modulus.Modulus
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys

object ModulusItemGroups : IModInit {
    override fun initialize() {}

    override fun initializeClient() {}

    data class ModulusItemGroup(val registryKey: RegistryKey<ItemGroup>): Translatable {
        override val translationKey: String = Modulus.id("itemGroup.${registryKey.value.path}").toTranslationKey();
    }

    fun itemGroup(path: String, icon: ItemConvertible): ModulusItemGroup {
        val itemGroup = ModulusItemGroup(RegistryKey.of(RegistryKeys.ITEM_GROUP, Modulus.id(path)));

        Registry.register(Registries.ITEM_GROUP, itemGroup.registryKey, FabricItemGroup.builder()
            .displayName(itemGroup.getText())
            .icon { ItemStack(icon) }
            .build()
        );

        return itemGroup;
    }

    val TOOLS = itemGroup("tools", Items.DIAMOND_PICKAXE);

    val PARTS = itemGroup("parts", Items.IRON_INGOT);
}
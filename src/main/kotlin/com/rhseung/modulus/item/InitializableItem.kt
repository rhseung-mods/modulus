package com.rhseung.modulus.item

import com.rhseung.modulus.Modulus
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

abstract class InitializableItem(
    val name: String,
    val itemGroup: RegistryKey<ItemGroup>?,
    open val id: Identifier,
    open val registryKey: RegistryKey<Item>,
    settings: Settings
) : Item(settings.registryKey(registryKey)) {

    constructor(name: String, itemGroup: RegistryKey<ItemGroup>?, settings: Settings) : this(
        name,
        itemGroup,
        Modulus.id(name),
        RegistryKey.of(RegistryKeys.ITEM, Modulus.id(name)),
        settings
    );

    constructor(name: String, settings: Settings) : this(
        name,
        null,
        Modulus.id(name),
        RegistryKey.of(RegistryKeys.ITEM, Modulus.id(name)),
        settings
    );

    open fun commonInit() {}

    @Environment(EnvType.CLIENT)
    open fun clientInit() {}

    init {
        Registry.register(Registries.ITEM, id, this);
        if (itemGroup != null)
            ItemGroupEvents.modifyEntriesEvent(itemGroup).register { it.add(this) }
    }
}
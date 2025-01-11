package com.rhseung.modulus

import com.rhseung.modulus.init.ModItems
import com.rhseung.modulus.item.ModularToolItem
import net.fabricmc.api.ClientModInitializer

object ModulusClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModItems.TOOLS.values.forEach(ModularToolItem::onClient);
        ModularToolItem.onClient(ModItems.DIAMOND_PICKAXE);
    }
}
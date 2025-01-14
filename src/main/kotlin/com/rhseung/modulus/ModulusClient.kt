package com.rhseung.modulus

import com.rhseung.modulus.init.ModItems
import com.rhseung.modulus.item.ToolItem
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin

object ModulusClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModItems.initializeClient();
//        ModelLoadingPlugin.register()
    }
}
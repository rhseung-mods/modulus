package com.rhseung.modulus

import com.rhseung.modulus.init.ModulusComponents
import com.rhseung.modulus.init.ModulusItemGroups
import com.rhseung.modulus.init.ModulusItems
import com.rhseung.modulus.init.ModulusRecipeSerializers
import net.fabricmc.api.ClientModInitializer

object ModulusClient : ClientModInitializer {
    override fun onInitializeClient() {
        ModulusComponents.initializeClient();
        ModulusItemGroups.initializeClient();
        ModulusItems.initializeClient();
        ModulusRecipeSerializers.initializeClient();
    }
}
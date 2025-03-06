package com.rhseung.modulus

import com.rhseung.modulus.gear.tool.action.ToolActions
import com.rhseung.modulus.init.ModulusComponents
import com.rhseung.modulus.init.ModulusItemGroups
import com.rhseung.modulus.init.ModulusItems
import com.rhseung.modulus.init.ModulusRecipeSerializers
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Modulus : ModInitializer {
	const val MOD_ID = "modulus";
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID);

	fun id(path: String): Identifier = Identifier.of(MOD_ID, path.lowercase());

	override fun onInitialize() {
		ModulusComponents.initialize();
		ModulusItemGroups.initialize();
		ModulusItems.initialize();
		ModulusRecipeSerializers.initialize();
	}
}
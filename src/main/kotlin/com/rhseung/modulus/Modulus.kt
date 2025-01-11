package com.rhseung.modulus

import com.rhseung.modulus.init.ModCodecs
import com.rhseung.modulus.init.ModComponents
import com.rhseung.modulus.init.ModItemGroups
import com.rhseung.modulus.init.ModItems
import com.rhseung.modulus.init.ModPacketCodecs
import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.slf4j.LoggerFactory

object Modulus : ModInitializer {
	const val MOD_ID = "modulus";
    val LOGGER = LoggerFactory.getLogger(MOD_ID);

	fun id(path: String): Identifier {
		return Identifier.of(MOD_ID, path);
	}

	override fun onInitialize() {
		ModCodecs.initialize();
		ModComponents.initialize();
		ModItemGroups.initialize();
		ModItems.initialize();
		ModPacketCodecs.initialize();
	}
}
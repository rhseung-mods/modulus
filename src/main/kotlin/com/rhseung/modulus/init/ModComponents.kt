package com.rhseung.modulus.init

import com.rhseung.modulus.Modulus
import net.minecraft.component.ComponentType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

typealias Unary<T> = (T) -> T;

object ModComponents : IModInit {
    fun <T> register(name: String, builder: Unary<ComponentType.Builder<T>>): ComponentType<T> {
        return Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Modulus.id(name),
            builder(ComponentType.builder<T>()).build()
        );
    }

    val TOOL_PARTS = register("tool_parts") {
        it.codec(ModCodecs.TOOL_PARTS_COMPONENT).packetCodec(ModPacketCodecs.TOOL_PARTS_COMPONENT).cache()
    };

//    val STRUCTURED_DURABILITY = register("structured_durability") {
//        it.codec(ModCodecs.STRUCTURED_DURABILITY_COMPONENT).packetCodec(ModPacketCodecs.STRUCTURED_DURABILITY_COMPONENT).cache()
//    }
}
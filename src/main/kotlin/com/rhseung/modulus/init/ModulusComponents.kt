package com.rhseung.modulus.init

import com.mojang.serialization.Codec
import com.rhseung.blueprint.registration.IModInit
import com.rhseung.modulus.Modulus
import com.rhseung.modulus.gear.tool.component.ToolComponent
import net.minecraft.component.ComponentType
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object ModulusComponents : IModInit {
    override fun initialize() {}

    override fun initializeClient() {}

    fun <V> component(name: String, codec: Codec<V>, packetCodec: PacketCodec<in RegistryByteBuf, V>): ComponentType<V> {
        return Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Modulus.id(name),
            ComponentType.builder<V>()
                .codec(codec)
                .packetCodec(packetCodec)
                .cache()
                .build()
        );
    }

    val TOOL = component("tool", ToolComponent.CODEC, ToolComponent.PACKET_CODEC);
}
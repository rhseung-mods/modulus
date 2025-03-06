package com.rhseung.modulus.gear.tool.material

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.util.Identifier

enum class ToolMaterialType {
    ALL,
    CRUDE,
    METAL;

    override fun toString(): String {
        return "ToolMaterialType.${name.uppercase()}";
    }

    fun texture(from: Identifier): Identifier {
        return from.withSuffixedPath("_${name.lowercase()}");
    }

    companion object {
        val CODEC: Codec<ToolMaterialType> = Codec.STRING.xmap(::valueOf, ToolMaterialType::name);
        val PACKET_CODEC: PacketCodec<ByteBuf, ToolMaterialType> = PacketCodecs.STRING.xmap(::valueOf, ToolMaterialType::name);
    }
}
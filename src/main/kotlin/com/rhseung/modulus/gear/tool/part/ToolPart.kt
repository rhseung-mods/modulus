package com.rhseung.modulus.gear.tool.part

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.rhseung.modulus.gear.tool.material.ToolMaterial
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec

data class ToolPart(val material: ToolMaterial, val type: ToolPartType) {
    val position: ToolPartPosition
        get() = type.position;

    override fun toString(): String {
        return "${material.name.uppercase()}/${type.name.uppercase()}";
    }

    companion object {
        val CODEC: Codec<ToolPart> = RecordCodecBuilder.create { instance ->
            instance.group(
                ToolMaterial.CODEC.fieldOf("material").forGetter(ToolPart::material),
                ToolPartType.CODEC.fieldOf("type").forGetter(ToolPart::type)
            ).apply(instance, ::ToolPart);
        };

        val PACKET_CODEC: PacketCodec<ByteBuf, ToolPart> = PacketCodec.tuple(
            ToolMaterial.PACKET_CODEC, ToolPart::material,
            ToolPartType.PACKET_CODEC, ToolPart::type,
            ::ToolPart
        );
    }
}
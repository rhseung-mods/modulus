package com.rhseung.modulus.gear.tool.part

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs

enum class ToolPartPosition(vararg subPositions: ToolPartPosition) {
    LEFT_HEAD,
    RIGHT_HEAD,
    HEAD(LEFT_HEAD, RIGHT_HEAD),
    HANDLE;

    val subPositions: List<ToolPartPosition> = subPositions.toList();

    init {
        for (subPos in subPositions) {
            if (subPos.subPositions.isNotEmpty())
                throw IllegalArgumentException("Sub-positions($subPos) cannot have sub-positions(${subPos.subPositions})");
        }
    }

    override fun toString(): String {
        return "ToolPartPosition.${name.uppercase()}";
    }

    companion object {
        val CODEC: Codec<ToolPartPosition> = Codec.STRING.xmap(::valueOf, ToolPartPosition::name);
        val PACKET_CODEC: PacketCodec<ByteBuf, ToolPartPosition> = PacketCodecs.STRING.xmap(::valueOf, ToolPartPosition::name);
    }
}
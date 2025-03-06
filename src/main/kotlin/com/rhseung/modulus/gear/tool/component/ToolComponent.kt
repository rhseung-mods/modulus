package com.rhseung.modulus.gear.tool.component

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.rhseung.modulus.gear.tool.material.ToolMaterial
import com.rhseung.modulus.gear.tool.part.ToolPart
import com.rhseung.modulus.gear.tool.part.ToolPartPosition
import com.rhseung.modulus.gear.tool.part.ToolPartType
import com.rhseung.modulus.gear.tool.type.ToolType
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs

class ToolComponent(val toolType: ToolType, parts: List<ToolPart>) {
    val positions: List<ToolPartPosition> = toolType.positions;
    val parts: List<ToolPart> = parts.sortedBy { positions.indexOf(it.position) };

    val size: Int
        get() = parts.size;

    val partTypes: List<ToolPartType>
        get() = parts.map { it.type };

    val materials: List<ToolMaterial>
        get() = parts.map { it.material };

    val attackDamage: Float
        get() = materials.map { it.stat.attackDamage }.sum();

    val attackSpeed: Float
        get() = materials.map { it.stat.attackSpeed }.sum();

    val durability: Int
        get() = materials.sumOf { it.stat.durability };

    val enchantability: Int
        get() = materials.sumOf { it.stat.enchantability };

    operator fun get(position: ToolPartPosition): ToolPart? {
        return parts.find { it.position == position };
    }

    operator fun get(partType: ToolPartType): ToolPart? {
        return parts.find { it.type == partType };
    }

    operator fun get(index: Int): ToolPart? {
        return parts.getOrNull(index);
    }

    operator fun contains(position: ToolPartPosition): Boolean {
        return parts.any { it.position == position };
    }

    operator fun contains(partType: ToolPartType): Boolean {
        return parts.any { it.type == partType };
    }

    operator fun contains(part: ToolPart): Boolean {
        return parts.contains(part);
    }

    fun indexOf(position: ToolPartPosition): Int {
        return parts.indexOfFirst { it.position == position };
    }

    fun indexOf(partType: ToolPartType): Int {
        return parts.indexOfFirst { it.type == partType };
    }

    fun indexOf(part: ToolPart): Int {
        return parts.indexOf(part);
    }

    override fun toString(): String {
        return "ToolComponent($toolType, ${parts.joinToString()})";
    }

    companion object {
        val CODEC: Codec<ToolComponent> = RecordCodecBuilder.create { instance ->
            instance.group(
                ToolType.CODEC.fieldOf("type").forGetter(ToolComponent::toolType),
                ToolPart.CODEC.listOf().fieldOf("parts").forGetter(ToolComponent::parts)
            ).apply(instance, ::ToolComponent);
        };

        val PACKET_CODEC: PacketCodec<ByteBuf, ToolComponent> = PacketCodec.tuple(
            ToolType.PACKET_CODEC, ToolComponent::toolType,
            ToolPart.PACKET_CODEC.collect(PacketCodecs.toList()), ToolComponent::parts,
            ::ToolComponent
        );
    }
}
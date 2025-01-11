package com.rhseung.modulus.init

import com.rhseung.modulus.tool.ToolMaterial
import com.rhseung.modulus.tool.ToolPart
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.tool.component.ToolPartsComponent
import com.rhseung.modulus.tool.ToolPosition
import com.rhseung.modulus.tool.ToolPrimitiveType
import com.rhseung.modulus.tool.ToolTier
import com.rhseung.modulus.tool.ToolTier.entries
import com.rhseung.modulus.util.ARGBColor
import io.netty.buffer.ByteBuf
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

object ModPacketCodecs : IModInit {
    val TOOL_TIER: PacketCodec<ByteBuf, ToolTier> = PacketCodecs.INTEGER.xmap({ it -> entries[it] }, ToolTier::ordinal);

    val TOOL_MATERIAL: PacketCodec<RegistryByteBuf, ToolMaterial> = PacketCodec.tuple(
        PacketCodecs.STRING, ToolMaterial::name,
        ARGBColor.Companion.PACKET_CODEC, ToolMaterial::color,
        TOOL_TIER, ToolMaterial::tier,
        PacketCodecs.INTEGER, ToolMaterial::durability,
        PacketCodecs.INTEGER, ToolMaterial::enchantmentValue,
        PacketCodecs.FLOAT, ToolMaterial::bonusDamage,
        PacketCodecs.FLOAT, ToolMaterial::miningSpeed,
        TagKey.packetCodec(RegistryKeys.ITEM), ToolMaterial::repairTag,
        ::ToolMaterial
    );

    val TOOL_POSITION: PacketCodec<ByteBuf, ToolPosition> = PacketCodecs.STRING.xmap(ToolPosition::valueOf, ToolPosition::name);

    val TOOL_PRIMITIVE_TYPE: PacketCodec<ByteBuf, ToolPrimitiveType> = PacketCodecs.STRING.xmap(ToolPrimitiveType::valueOf, ToolPrimitiveType::name);

    val TOOL_PART_TYPE: PacketCodec<ByteBuf, ToolPartType> = PacketCodec.tuple(
        PacketCodecs.STRING, ToolPartType::name,
        TOOL_POSITION, ToolPartType::position,
        ::ToolPartType
    );

    val TOOL_PART: PacketCodec<RegistryByteBuf, ToolPart> = PacketCodec.tuple(
        TOOL_PART_TYPE, ToolPart::partType,
        TOOL_MATERIAL, ToolPart::toolMaterial,
        ::ToolPart
    );

    val TOOL_PARTS_COMPONENT: PacketCodec<RegistryByteBuf, ToolPartsComponent> = PacketCodec.of(
        { value, buf ->
            buf.writeCollection(value.toolParts) { buf, part ->
                TOOL_PART.encode(buf as RegistryByteBuf, part)
            };
        },
        { buf ->
            val parts = buf.readCollection(
                { size -> mutableListOf<ToolPart>() },
                { buf -> TOOL_PART.decode(buf as RegistryByteBuf) }
            );

            ToolPartsComponent(parts);
        }
    );
}
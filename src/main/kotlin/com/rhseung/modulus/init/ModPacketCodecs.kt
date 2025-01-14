package com.rhseung.modulus.init

import com.rhseung.modulus.tool.ToolMaterial
import com.rhseung.modulus.tool.ToolPart
import com.rhseung.modulus.tool.ToolPartType
import com.rhseung.modulus.tool.component.ToolPartsComponent
import com.rhseung.modulus.tool.ToolPosition
import com.rhseung.modulus.tool.ToolTier
import com.rhseung.modulus.tool.ToolTier.entries
import com.rhseung.modulus.tool.ToolType
import com.rhseung.modulus.util.ARGBColor
import io.netty.buffer.ByteBuf
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

object ModPacketCodecs : IModInit {
    private fun <B, C, T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple(
        codec1: PacketCodec<in B, T1>,
        from1: (C) -> T1,
        codec2: PacketCodec<in B, T2>,
        from2: (C) -> T2,
        codec3: PacketCodec<in B, T3>,
        from3: (C) -> T3,
        codec4: PacketCodec<in B, T4>,
        from4: (C) -> T4,
        codec5: PacketCodec<in B, T5>,
        from5: (C) -> T5,
        codec6: PacketCodec<in B, T6>,
        from6: (C) -> T6,
        codec7: PacketCodec<in B, T7>,
        from7: (C) -> T7,
        codec8: PacketCodec<in B, T8>,
        from8: (C) -> T8,
        codec9: PacketCodec<in B, T9>,
        from9: (C) -> T9,
        to: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> C
    ): PacketCodec<B, C> {
        return object : PacketCodec<B, C> {
            override fun decode(buf: B): C {
                val obj1: T1 = codec1.decode(buf);
                val obj2: T2 = codec2.decode(buf);
                val obj3: T3 = codec3.decode(buf);
                val obj4: T4 = codec4.decode(buf);
                val obj5: T5 = codec5.decode(buf);
                val obj6: T6 = codec6.decode(buf);
                val obj7: T7 = codec7.decode(buf);
                val obj8: T8 = codec8.decode(buf);
                val obj9: T9 = codec9.decode(buf);
                return to(obj1, obj2, obj3, obj4, obj5, obj6, obj7, obj8, obj9);
            }

            override fun encode(buf: B, value: C) {
                codec1.encode(buf, from1(value));
                codec2.encode(buf, from2(value));
                codec3.encode(buf, from3(value));
                codec4.encode(buf, from4(value));
                codec5.encode(buf, from5(value));
                codec6.encode(buf, from6(value));
                codec7.encode(buf, from7(value));
                codec8.encode(buf, from8(value));
                codec9.encode(buf, from9(value));
            }
        };
    }
    
    val TOOL_TIER: PacketCodec<ByteBuf, ToolTier> = PacketCodecs.INTEGER.xmap({ it -> entries[it] }, ToolTier::ordinal);

    val TOOL_MATERIAL: PacketCodec<RegistryByteBuf, ToolMaterial> = tuple(
        PacketCodecs.STRING, ToolMaterial::name,
        ARGBColor.PACKET_CODEC, ToolMaterial::color,
        TOOL_TIER, ToolMaterial::tier,
        PacketCodecs.INTEGER, ToolMaterial::durability,
        PacketCodecs.INTEGER, ToolMaterial::enchantmentValue,
        PacketCodecs.FLOAT, ToolMaterial::bonusAttackDamage,
        PacketCodecs.FLOAT, ToolMaterial::bonusAttackSpeed,
        PacketCodecs.FLOAT, ToolMaterial::miningSpeed,
        TagKey.packetCodec(RegistryKeys.ITEM), ToolMaterial::repairTag,
        ::ToolMaterial
    );

    val TOOL_POSITION: PacketCodec<ByteBuf, ToolPosition> = PacketCodecs.STRING.xmap(ToolPosition::valueOf, ToolPosition::name);

    val TOOL_PART_TYPE: PacketCodec<ByteBuf, ToolPartType> = PacketCodec.tuple(
        PacketCodecs.STRING, ToolPartType::name,
        TOOL_POSITION, ToolPartType::position,
        PacketCodecs.FLOAT, ToolPartType::baseAttackDamage,
        PacketCodecs.FLOAT, ToolPartType::baseAttackSpeed,
        TagKey.packetCodec(RegistryKeys.BLOCK).collect(PacketCodecs.toList()), ToolPartType::mineableBlockTags,
        ::ToolPartType
    );

    val TOOL_PART: PacketCodec<RegistryByteBuf, ToolPart> = PacketCodec.tuple(
        TOOL_PART_TYPE, ToolPart::partType,
        TOOL_MATERIAL, ToolPart::toolMaterial,
        ::ToolPart
    );

    val TOOL_TYPE: PacketCodec<ByteBuf, ToolType> = PacketCodecs.STRING.xmap(ToolType::valueOf, ToolType::name);

    val TOOL_PARTS_COMPONENT: PacketCodec<RegistryByteBuf, ToolPartsComponent> = PacketCodec.of(
        { value, buf ->
            TOOL_TYPE.encode(buf, value.toolType);
            buf.writeMap(value.toolPartByPosition,
                { buf, k -> TOOL_POSITION.encode(buf, k) },
                { buf, v -> TOOL_PART.encode(buf as RegistryByteBuf, v) }
            );
        },
        { buf ->
            val toolType = TOOL_TYPE.decode(buf);
            val toolPartByPosition = buf.readMap(
                { buf -> TOOL_POSITION.decode(buf) },
                { buf -> TOOL_PART.decode(buf as RegistryByteBuf) }
            );

            ToolPartsComponent(toolType, toolPartByPosition);
        }
    );
}
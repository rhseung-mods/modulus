package com.rhseung.modulus.gear.tool.type

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.rhseung.blueprint.lang.Translatable
import com.rhseung.modulus.Modulus
import com.rhseung.modulus.gear.tool.part.ToolPartPosition
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs

data class ToolType(
    val name: String,
    val positions: List<ToolPartPosition>
): Translatable {

    override val translationKey: String = Modulus.id("tool.type.$name").toTranslationKey();

    override fun toString(): String {
        return "ToolType.${name.uppercase()}";
    }

    init {
        entries.add(this);
    }

    companion object {
        val entries = mutableListOf<ToolType>();

        val CODEC: Codec<ToolType> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("name").forGetter(ToolType::name),
                Codec.list(ToolPartPosition.CODEC).fieldOf("positions").forGetter(ToolType::positions)
            ).apply(instance, ::ToolType);
        };

        val PACKET_CODEC: PacketCodec<ByteBuf, ToolType> = PacketCodec.tuple(
            PacketCodecs.STRING, ToolType::name,
            ToolPartPosition.PACKET_CODEC.collect(PacketCodecs.toList()), ToolType::positions,
            ::ToolType
        );

        private fun layerOf(
            layer0: ToolPartPosition,
            layer1: ToolPartPosition? = null,
            layer2: ToolPartPosition? = null,
            layer3: ToolPartPosition? = null,
            layer4: ToolPartPosition? = null,
            layer5: ToolPartPosition? = null,
            layer6: ToolPartPosition? = null,
            layer7: ToolPartPosition? = null,
            layer8: ToolPartPosition? = null,
            layer9: ToolPartPosition? = null,
        ): List<ToolPartPosition> {
            val layers = listOf(
                layer0, layer1, layer2, layer3, layer4, layer5, layer6, layer7, layer8, layer9
            ).filter { it != null };

            if (layers.distinct().size != layers.size) {
                throw IllegalArgumentException("Duplicate layer detected");
            }

            return layers.map { it!! };
        }

        val SINGLE = ToolType("single", layerOf(
            ToolPartPosition.HANDLE,
            ToolPartPosition.HEAD
        ));

        val DOUBLE = ToolType("double", layerOf(
            ToolPartPosition.HANDLE,
            ToolPartPosition.RIGHT_HEAD,
            ToolPartPosition.LEFT_HEAD,
        ));
    }
}
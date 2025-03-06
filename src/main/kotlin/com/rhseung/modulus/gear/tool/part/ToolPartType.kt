package com.rhseung.modulus.gear.tool.part

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.rhseung.blueprint.lang.Translatable
import com.rhseung.modulus.Modulus
import com.rhseung.modulus.gear.tool.action.ToolAction
import com.rhseung.modulus.gear.tool.action.ToolActions
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs

data class ToolPartType(
    val name: String,
    val position: ToolPartPosition,
    val actions: List<ToolAction>
): Translatable {

    // todo: tool material type

    override val translationKey: String = Modulus.id("tool.part.$name").toTranslationKey();

    override fun toString(): String {
        return "ToolPartType.${name.uppercase()}";
    }

    init {
        if (entriesMap.containsKey(name))
            throw IllegalArgumentException("Duplicate tool part type: $name");

        entriesMap[name] = this;
    }

    companion object {
        private val entriesMap = mutableMapOf<String, ToolPartType>();
        val entries: List<ToolPartType>
            get() = entriesMap.values.toList();
        val entriesNotDefault: List<ToolPartType>
            get() = entries.filterNot { it.name.startsWith("default") };

        operator fun get(name: String): ToolPartType? = entriesMap[name];

        val CODEC: Codec<ToolPartType> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("name").forGetter(ToolPartType::name),
                ToolPartPosition.CODEC.fieldOf("position").forGetter(ToolPartType::position),
                Codec.list(ToolAction.CODEC).fieldOf("actions").forGetter(ToolPartType::actions)
            ).apply(instance, ::ToolPartType);
        };

        val PACKET_CODEC: PacketCodec<ByteBuf, ToolPartType> = PacketCodec.tuple(
            PacketCodecs.STRING, ToolPartType::name,
            ToolPartPosition.PACKET_CODEC, ToolPartType::position,
            ToolAction.PACKET_CODEC.collect(PacketCodecs.toList()), ToolPartType::actions,
            ::ToolPartType
        );

        val DEFAULT: Map<ToolPartPosition, ToolPartType> = ToolPartPosition.entries.associateWith { position ->
            ToolPartType("default_${position.name}", position, ToolActions.EMPTY);
        };

        val PICKAXE_LEFT_HEAD = ToolPartType("pickaxe_left_head", ToolPartPosition.LEFT_HEAD, ToolActions.EMPTY);

        val PICKAXE_RIGHT_HEAD = ToolPartType("pickaxe_right_head", ToolPartPosition.RIGHT_HEAD, ToolActions.EMPTY);

        val AXE_LEFT_HEAD = ToolPartType("axe_left_head", ToolPartPosition.LEFT_HEAD, ToolActions.AXE_ACTIONS);

        val AXE_RIGHT_HEAD = ToolPartType("axe_right_head", ToolPartPosition.RIGHT_HEAD, ToolActions.AXE_ACTIONS);

        val SHOVEL_HEAD = ToolPartType("shovel_head", ToolPartPosition.HEAD, ToolActions.SHOVEL_ACTIONS);

        val HOE_HEAD = ToolPartType("hoe_head", ToolPartPosition.LEFT_HEAD, ToolActions.HOE_ACTIONS);

        val BUTT_HEAD = ToolPartType("butt_head", ToolPartPosition.RIGHT_HEAD, ToolActions.EMPTY);

        val HANDLE = ToolPartType("handle", ToolPartPosition.HANDLE, ToolActions.EMPTY);
    }
}
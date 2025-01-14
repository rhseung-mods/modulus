package com.rhseung.modulus.util

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs

class ARGBColor : RGBColor {
    val A: Int;
    val a: Float
        get() = A.toFloat() / 255.0f;

    constructor(A: Int, R: Int, G: Int, B: Int) : super(R, G, B) {
        this.A = A.coerceIn(0, 255);
    }

    constructor(A: Int, H: Int, S: Float, V: Float) : super(H, S, V) {
        this.A = A.coerceIn(0, 255);
    }

    constructor(argb: Int) : super((argb shr 16) and 0xFF, (argb shr 8) and 0xFF, argb and 0xFF) {
        this.A = (argb shr 24) and 0xFF;
    }

    constructor(alpha: Int, rgb: RGBColor) : this(alpha, rgb.R, rgb.G, rgb.B);

    fun argb(): Int {
        return (A shl 24) or rgb();
    }

    fun toInt(): Int {
        return argb();
    }

    override fun toString(): String {
        return "#${Integer.toHexString(argb())}";
    }

    override fun darker(delta: Float): ARGBColor {
        return ARGBColor(A, H, S, V - delta);
    }

    override fun brighter(delta: Float): ARGBColor {
        return ARGBColor(A, H, S, V + delta);
    }

    companion object {
        val CODEC: Codec<ARGBColor> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("A").forGetter(ARGBColor::A),
                Codec.INT.fieldOf("R").forGetter(RGBColor::R),
                Codec.INT.fieldOf("G").forGetter(RGBColor::G),
                Codec.INT.fieldOf("B").forGetter(RGBColor::B)
            ).apply(instance, ::ARGBColor);
        };

        val PACKET_CODEC: PacketCodec<ByteBuf, ARGBColor> = PacketCodec.tuple(
            PacketCodecs.INTEGER, ARGBColor::A,
            PacketCodecs.INTEGER, RGBColor::R,
            PacketCodecs.INTEGER, RGBColor::G,
            PacketCodecs.INTEGER, RGBColor::B,
            ::ARGBColor
        );

        val EMPTY = ARGBColor(0, RGBColor.WHITE);
    }
}
package com.rhseung.modulus.util

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.item.ToolMaterial
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.math.roundToInt

open class RGBColor {
    val R: Int
    val r: Float
        get() = R.toFloat() / 255.0f;

    val G: Int
    val g: Float
        get() = G.toFloat() / 255.0f;

    val B: Int
    val b: Float
        get() = B.toFloat() / 255.0f;

    val H: Int
    val S: Float
    val V: Float

    constructor(R: Int, G: Int, B: Int) {
        this.R = R.coerceIn(0, 255)
        this.G = G.coerceIn(0, 255)
        this.B = B.coerceIn(0, 255)

        val r = this.R / 255.0F
        val g = this.G / 255.0F
        val b = this.B / 255.0F

        val max = kotlin.comparisons.maxOf(r, g, b)
        val min = kotlin.comparisons.minOf(r, g, b)

        this.H = ((when (max) {
            min -> 0.0F
            r -> (g - b) / (max - min)
            g -> 2 + (b - r) / (max - min)
            b -> 4 + (r - g) / (max - min)
            else -> 0.0F
        } * 60).roundToInt() + 360) % 360

        this.S = when (max) {
            0.0F -> 0.0F
            else -> (max - min) / max
        }

        this.V = max
    }

    constructor(H: Int, S: Float, V: Float) {
        this.H = H.coerceIn(0, 360)
        this.S = S.coerceIn(0.0F, 1.0F)
        this.V = V.coerceIn(0.0F, 1.0F)
        val max = (this.V * 255).roundToInt()
        val min = (max * (1 - this.S)).roundToInt()

        when (this.H) {
            in 300..<360 -> {
                this.R = max
                this.G = min
                this.B = (-((this.H - 360) / 60.0) * (max - min) + this.G).roundToInt()
            }

            in 0..<60 -> {
                this.R = max
                this.B = min
                this.G = ((this.H / 60.0) * (max - min) + this.B).roundToInt()
            }

            in 60..<120 -> {
                this.G = max
                this.B = min
                this.R = (-(this.H / 60.0 - 2) * (max - min) + this.B).roundToInt()
            }

            in 120..<180 -> {
                this.G = max
                this.R = min
                this.B = ((this.H / 60.0 - 2) * (max - min) + this.R).roundToInt()
            }

            in 180..<240 -> {
                this.B = max
                this.R = min
                this.G = (-(this.H / 60.0 - 4) * (max - min) + this.R).roundToInt()
            }

            in 240..<300 -> {
                this.B = max
                this.G = min
                this.R = ((this.H / 60.0 - 4) * (max - min) + this.G).roundToInt()
            }

            else -> error("impossible")
        }
    }

    constructor(rgb: Int) : this((rgb shr 16) and 0xFF, (rgb shr 8) and 0xFF, rgb and 0xFF);

    fun r(): Float {
        return r;
    }

    fun g(): Float {
        return g;
    }

    fun b(): Float {
        return b;
    }

    fun rgb(): Int {
        return (R shl 16) or (G shl 8) or B;
    }

    fun argb(alpha: Int) = ARGBColor(alpha, this);

    fun zeroAlpha() = argb(0);

    fun fullAlpha() = argb(255);

    fun withAlpha(alpha: Int) = argb(alpha);

    fun toInt(alpha: Int = 255): Int {
        return argb(alpha).toInt();
    }

    override fun toString(): String {
        return "#${Integer.toHexString(rgb())}";
    }

    override fun equals(other: Any?): Boolean {
        return other is RGBColor && other.R == R && other.G == G && other.B == B;
    }

    override fun hashCode(): Int {
        var result = R
        result = 31 * result + G
        result = 31 * result + B
        return result
    }

    open fun darker(delta: Float): RGBColor {
        return RGBColor(H, S, V - delta);
    }

    open fun brighter(delta: Float): RGBColor {
        return RGBColor(H, S, V + delta);
    }

    operator fun invoke(text: String): MutableText {
        return Text.literal(text).withColor(this.toInt());
    }

    companion object {
        val CODEC: Codec<RGBColor> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("R").forGetter(RGBColor::R),
                Codec.INT.fieldOf("G").forGetter(RGBColor::G),
                Codec.INT.fieldOf("B").forGetter(RGBColor::B)
            ).apply(instance, ::RGBColor);
        };

        val PACKET_CODEC: PacketCodec<ByteBuf, RGBColor> = PacketCodec.tuple(
            PacketCodecs.INTEGER, RGBColor::R,
            PacketCodecs.INTEGER, RGBColor::G,
            PacketCodecs.INTEGER, RGBColor::B,
            ::RGBColor
        );

        val WOOD = RGBColor(150, 116, 65);
        val STONE = RGBColor(149, 145, 141);
        val IRON = RGBColor(215, 215, 215);
        val DIAMOND = RGBColor(110, 236, 210);
        val NETHERITE = RGBColor(98, 88, 89);

        val BLACK = RGBColor(0x000000);
        val DARK_BLUE = RGBColor(0x0000AA);
        val DARK_GREEN = RGBColor(0x00AA00);
        val DARK_AQUA = RGBColor(0x00AAAA);
        val DARK_RED = RGBColor(0xAA0000);
        val DARK_PURPLE = RGBColor(0xAA00AA);
        val GOLD = RGBColor(0xFFAA00);
        val GRAY = RGBColor(0xAAAAAA);
        val DARK_GRAY = RGBColor(0x555555);
        val BLUE = RGBColor(0x5555FF);
        val GREEN = RGBColor(0x55FF55);
        val AQUA = RGBColor(0x55FFFF);
        val RED = RGBColor(0xFF5555);
        val LIGHT_PURPLE = RGBColor(0xFF55FF);
        val YELLOW = RGBColor(0xFFFF55);
        val WHITE = RGBColor(0xFFFFFF);

        val FUEL = RGBColor(0xE9B83B);

        fun Pair<RGBColor, RGBColor>.gradient(ratioOfFirst: Float): RGBColor {
            return RGBColor(
                (this.first.H * ratioOfFirst + this.second.H * (1 - ratioOfFirst)).roundToInt(),
                this.first.S * ratioOfFirst + this.second.S * (1 - ratioOfFirst),
                this.first.V * ratioOfFirst + this.second.V * (1 - ratioOfFirst)
            );
        }

        fun ToolMaterial.toColor() = when (this) {
            ToolMaterial.WOOD -> WOOD
            ToolMaterial.GOLD -> GOLD
            ToolMaterial.STONE -> STONE
            ToolMaterial.IRON -> IRON
            ToolMaterial.DIAMOND -> DIAMOND
            ToolMaterial.NETHERITE -> NETHERITE
            else -> error("Unknown tool material")
        }

        fun Formatting.toColor() = when (this) {
            Formatting.BLACK -> BLACK
            Formatting.DARK_BLUE -> DARK_BLUE
            Formatting.DARK_GREEN -> DARK_GREEN
            Formatting.DARK_AQUA -> DARK_AQUA
            Formatting.DARK_RED -> DARK_RED
            Formatting.DARK_PURPLE -> DARK_PURPLE
            Formatting.GOLD -> GOLD
            Formatting.GRAY -> GRAY
            Formatting.DARK_GRAY -> DARK_GRAY
            Formatting.BLUE -> BLUE
            Formatting.GREEN -> GREEN
            Formatting.AQUA -> AQUA
            Formatting.RED -> RED
            Formatting.LIGHT_PURPLE -> LIGHT_PURPLE
            Formatting.YELLOW -> YELLOW
            Formatting.WHITE -> WHITE
            else -> error("Formatting($this) cannot be converted to color")
        }
    }
}

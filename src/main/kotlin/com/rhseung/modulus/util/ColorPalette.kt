package com.rhseung.modulus.util

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs

class ColorPalette(val mainColorIndex: Int, vararg colors: RGBColor) {
    init {
        require(colors.size == 11) { "ColorPalette must have exactly 11 colors" }
    }

    val colors = colors.map(RGBColor::fullAlpha);
    val size = this.colors.size;
    val mainColor: ARGBColor = this.colors[mainColorIndex];

    override fun toString(): String {
        return "ColorPalette(${colors.joinToString()})";
    }

    override fun equals(other: Any?): Boolean {
        return other is ColorPalette && colors == other.colors;
    }

    override fun hashCode(): Int {
        var result = size;
        result = 31 * result + colors.hashCode();
        return result;
    }

    operator fun get(index: Int): ARGBColor {
        return colors[index];
    }

    companion object {
        fun fromList(mainColorIndex: Int, colors: List<RGBColor>): ColorPalette {
            return ColorPalette(mainColorIndex,*colors.toTypedArray());
        }

        fun toMapper(from: ColorPalette, to: ColorPalette) = ARGBColor.toMapper(from.colors, to.colors);

        val CODEC: Codec<ColorPalette> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("main_color_index").forGetter(ColorPalette::mainColorIndex),
                Codec.list(ARGBColor.CODEC).fieldOf("colors").forGetter(ColorPalette::colors)
            ).apply(instance, ColorPalette::fromList)
        };

        val PACKET_CODEC: PacketCodec<ByteBuf, ColorPalette> = PacketCodec.tuple(
            PacketCodecs.INTEGER, ColorPalette::mainColorIndex,
            ARGBColor.PACKET_CODEC.collect(PacketCodecs.toList()), ColorPalette::colors,
            ColorPalette::fromList
        );

        val DEFAULT = ColorPalette(3,
            RGBColor(255, 255, 255),
            RGBColor(229, 229, 229),
            RGBColor(204, 204, 204),
            RGBColor(178, 178, 178),
            RGBColor(153, 153, 153),
            RGBColor(127, 127, 127),
            RGBColor(102, 102, 102),
            RGBColor(76, 76, 76),
            RGBColor(51, 51, 51),
            RGBColor(25, 25, 25),
            RGBColor(0, 0, 0)
        );

        val ACACIA_WOOD = ColorPalette(3, RGBColor(189, 102, 56), RGBColor(156, 84, 45), RGBColor(111, 59, 31), RGBColor(84, 45, 24), RGBColor(73, 39, 21), RGBColor(53, 28, 15), RGBColor(40, 21, 11), RGBColor(26, 13, 7), RGBColor(10, 5, 3), RGBColor(3, 2, 1), RGBColor(0, 0, 0));
        val AMETHYST = ColorPalette(3, RGBColor(221, 193, 242), RGBColor(209, 167, 242), RGBColor(201, 143, 243), RGBColor(154, 92, 198), RGBColor(108, 73, 170), RGBColor(82, 54, 135), RGBColor(66, 39, 118), RGBColor(54, 28, 106), RGBColor(36, 12, 83), RGBColor(23, 6, 59), RGBColor(0, 0, 0));
        val ANDESITE = ColorPalette(3, RGBColor(216, 216, 216), RGBColor(187, 189, 190), RGBColor(143, 149, 156), RGBColor(105, 115, 126), RGBColor(77, 87, 101), RGBColor(54, 66, 79), RGBColor(36, 50, 59), RGBColor(24, 29, 39), RGBColor(14, 19, 23), RGBColor(7, 11, 11), RGBColor(2, 3, 3));
        val BAMBOO_WOOD = ColorPalette(3, RGBColor(200, 187, 94), RGBColor(176, 164, 70), RGBColor(136, 126, 46), RGBColor(107, 100, 36), RGBColor(94, 87, 31), RGBColor(71, 66, 24), RGBColor(55, 51, 18), RGBColor(38, 35, 13), RGBColor(20, 18, 6), RGBColor(10, 10, 3), RGBColor(2, 2, 2));
        val BASALT = ColorPalette(3, RGBColor(157, 157, 157), RGBColor(120, 119, 119), RGBColor(88, 87, 87), RGBColor(60, 60, 60), RGBColor(41, 41, 41), RGBColor(25, 25, 25), RGBColor(13, 13, 13), RGBColor(4, 4, 4), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0));
        val BIRCH_WOOD = ColorPalette(3, RGBColor(246, 227, 161), RGBColor(222, 200, 126), RGBColor(197, 170, 78), RGBColor(173, 146, 52), RGBColor(152, 128, 45), RGBColor(129, 105, 27), RGBColor(107, 87, 17), RGBColor(85, 66, 7), RGBColor(53, 42, 4), RGBColor(32, 26, 2), RGBColor(9, 5, 5));
        val BLACKSTONE = ColorPalette(3, RGBColor(130, 130, 130), RGBColor(95, 95, 95), RGBColor(66, 65, 65), RGBColor(40, 40, 40), RGBColor(23, 23, 23), RGBColor(11, 11, 11), RGBColor(1, 1, 1), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0));
        val CHERRY_WOOD = ColorPalette(3, RGBColor(230, 183, 178), RGBColor(204, 152, 146), RGBColor(175, 109, 101), RGBColor(151, 84, 75), RGBColor(132, 73, 66), RGBColor(110, 53, 47), RGBColor(90, 41, 35), RGBColor(70, 27, 22), RGBColor(44, 16, 13), RGBColor(26, 10, 8), RGBColor(7, 7, 7));
        val COBBLED_DEEPSLATE = ColorPalette(3, RGBColor(123, 123, 123), RGBColor(89, 88, 88), RGBColor(60, 59, 59), RGBColor(35, 35, 35), RGBColor(19, 19, 19), RGBColor(7, 7, 7), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0));
        val COBBLESTONE = ColorPalette(3, RGBColor(204, 204, 204), RGBColor(163, 163, 163), RGBColor(127, 126, 126), RGBColor(95, 95, 95), RGBColor(71, 71, 71), RGBColor(51, 51, 51), RGBColor(35, 35, 35), RGBColor(21, 21, 21), RGBColor(11, 11, 11), RGBColor(4, 4, 4), RGBColor(0, 0, 0));
        val COPPER = ColorPalette(3, RGBColor(255, 227, 221), RGBColor(242, 165, 147), RGBColor(227, 130, 108), RGBColor(180, 104, 77), RGBColor(154, 71, 44), RGBColor(121, 60, 40), RGBColor(109, 52, 32), RGBColor(95, 43, 24), RGBColor(76, 32, 16), RGBColor(61, 24, 11), RGBColor(30, 12, 5));
        val CRIMSON_HYPHAE = ColorPalette(3, RGBColor(149, 69, 100), RGBColor(121, 56, 80), RGBColor(83, 37, 55), RGBColor(61, 27, 40), RGBColor(53, 24, 35), RGBColor(37, 16, 24), RGBColor(26, 12, 17), RGBColor(16, 7, 11), RGBColor(4, 1, 2), RGBColor(0, 0, 0), RGBColor(0, 0, 0));
        val DARK_OAK_WOOD = ColorPalette(3, RGBColor(77, 50, 23), RGBColor(54, 35, 16), RGBColor(18, 12, 5), RGBColor(2, 1, 0), RGBColor(1, 1, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0));
        val DEEPSLATE = ColorPalette(3, RGBColor(123, 123, 123), RGBColor(89, 88, 88), RGBColor(60, 59, 59), RGBColor(35, 35, 35), RGBColor(19, 19, 19), RGBColor(7, 7, 7), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0));
        val DIAMOND = ColorPalette(3, RGBColor(255, 255, 255), RGBColor(215, 249, 245), RGBColor(164, 253, 240), RGBColor(51, 235, 216), RGBColor(43, 199, 181), RGBColor(38, 160, 150), RGBColor(30, 138, 130), RGBColor(21, 99, 98), RGBColor(14, 63, 63), RGBColor(8, 37, 35), RGBColor(0, 0, 0));
        val DIORITE = ColorPalette(3, RGBColor(216, 216, 216), RGBColor(197, 197, 197), RGBColor(176, 177, 177), RGBColor(147, 147, 147), RGBColor(116, 116, 116), RGBColor(90, 90, 90), RGBColor(67, 67, 67), RGBColor(47, 47, 47), RGBColor(31, 31, 31), RGBColor(17, 17, 17), RGBColor(7, 7, 7));
        val EMERALD = ColorPalette(3, RGBColor(255, 255, 255), RGBColor(130, 246, 173), RGBColor(14, 199, 84), RGBColor(17, 160, 54), RGBColor(16, 123, 36), RGBColor(14, 114, 34), RGBColor(9, 99, 27), RGBColor(3, 80, 19), RGBColor(2, 61, 14), RGBColor(1, 40, 9), RGBColor(0, 0, 0));
        val FLINT = ColorPalette(3, RGBColor(120, 120, 120), RGBColor(87, 86, 86), RGBColor(58, 57, 57), RGBColor(34, 34, 34), RGBColor(17, 17, 17), RGBColor(6, 6, 6), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0));
        val GOLD = ColorPalette(3, RGBColor(255, 255, 255), RGBColor(255, 255, 255), RGBColor(255, 255, 255), RGBColor(253, 255, 118), RGBColor(243, 216, 70), RGBColor(243, 192, 70), RGBColor(233, 177, 21), RGBColor(175, 122, 20), RGBColor(130, 93, 22), RGBColor(63, 46, 14), RGBColor(0, 0, 0));
        val GRANITE = ColorPalette(3, RGBColor(214, 212, 208), RGBColor(183, 162, 154), RGBColor(155, 121, 108), RGBColor(130, 85, 69), RGBColor(107, 65, 43), RGBColor(83, 42, 26), RGBColor(57, 25, 18), RGBColor(36, 22, 11), RGBColor(20, 9, 6), RGBColor(8, 2, 4), RGBColor(1, 0, 0));
        val IRON = ColorPalette(3, RGBColor(255, 255, 255), RGBColor(255, 255, 255), RGBColor(232, 232, 232), RGBColor(216, 216, 216), RGBColor(190, 190, 190), RGBColor(165, 165, 165), RGBColor(150, 150, 150), RGBColor(107, 107, 107), RGBColor(68, 68, 68), RGBColor(24, 24, 24), RGBColor(0, 0, 0));
        val JUNGLE_WOOD = ColorPalette(3, RGBColor(183, 135, 103), RGBColor(159, 110, 80), RGBColor(120, 81, 56), RGBColor(94, 64, 43), RGBColor(82, 56, 38), RGBColor(62, 42, 28), RGBColor(47, 32, 22), RGBColor(33, 22, 15), RGBColor(17, 11, 7), RGBColor(8, 5, 3), RGBColor(1, 1, 1));
        val LAPIS = ColorPalette(3, RGBColor(184, 202, 219), RGBColor(119, 149, 175), RGBColor(65, 110, 151), RGBColor(28, 77, 156), RGBColor(33, 73, 123), RGBColor(18, 51, 101), RGBColor(17, 46, 99), RGBColor(12, 40, 90), RGBColor(9, 30, 69), RGBColor(5, 22, 54), RGBColor(0, 0, 0));
        val LEATHER = ColorPalette(3, RGBColor(239, 222, 213), RGBColor(219, 179, 158), RGBColor(199, 136, 103), RGBColor(179, 94, 48), RGBColor(153, 80, 41), RGBColor(127, 67, 34), RGBColor(102, 53, 27), RGBColor(76, 40, 20), RGBColor(51, 26, 13), RGBColor(25, 13, 6), RGBColor(0, 0, 0));
        val MANGROVE_WOOD = ColorPalette(3, RGBColor(135, 64, 58), RGBColor(108, 51, 47), RGBColor(70, 33, 30), RGBColor(49, 23, 21), RGBColor(43, 20, 18), RGBColor(28, 13, 12), RGBColor(19, 9, 8), RGBColor(10, 4, 4), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0));
        val NETHERITE = ColorPalette(3, RGBColor(188, 173, 188), RGBColor(160, 149, 160), RGBColor(134, 123, 134), RGBColor(112, 103, 112), RGBColor(93, 86, 93), RGBColor(91, 79, 90), RGBColor(81, 68, 78), RGBColor(74, 41, 64), RGBColor(47, 33, 34), RGBColor(35, 16, 18), RGBColor(0, 0, 0));
        val OAK_WOOD = ColorPalette(3, RGBColor(197, 156, 91), RGBColor(173, 133, 68), RGBColor(132, 99, 45), RGBColor(104, 78, 35), RGBColor(91, 68, 31), RGBColor(68, 51, 23), RGBColor(52, 40, 18), RGBColor(36, 27, 12), RGBColor(19, 14, 6), RGBColor(9, 7, 3), RGBColor(1, 1, 1));
        val OBSIDIAN = ColorPalette(3, RGBColor(148, 119, 156), RGBColor(93, 79, 125), RGBColor(65, 54, 90), RGBColor(41, 34, 58), RGBColor(27, 22, 35), RGBColor(13, 11, 19), RGBColor(4, 3, 6), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0));
        val PALE_OAK_WOOD = ColorPalette(3, RGBColor(230, 219, 218), RGBColor(200, 187, 186), RGBColor(165, 145, 144), RGBColor(139, 117, 116), RGBColor(122, 103, 101), RGBColor(99, 81, 80), RGBColor(80, 64, 63), RGBColor(61, 46, 46), RGBColor(41, 28, 27), RGBColor(25, 17, 16), RGBColor(9, 9, 9));
        val QUARTZ = ColorPalette(3, RGBColor(255, 255, 255), RGBColor(242, 239, 237), RGBColor(244, 236, 230), RGBColor(246, 234, 223), RGBColor(227, 219, 196), RGBColor(182, 173, 150), RGBColor(144, 142, 128), RGBColor(101, 97, 86), RGBColor(69, 67, 60), RGBColor(42, 40, 34), RGBColor(0, 0, 0));
        val RABBIT_HIDE = ColorPalette(3, RGBColor(234, 225, 216), RGBColor(207, 186, 164), RGBColor(180, 147, 112), RGBColor(153, 109, 61), RGBColor(131, 93, 52), RGBColor(109, 77, 43), RGBColor(87, 62, 34), RGBColor(65, 46, 26), RGBColor(43, 31, 17), RGBColor(21, 15, 8), RGBColor(0, 0, 0));
        val REDSTONE = ColorPalette(3, RGBColor(229, 159, 151), RGBColor(229, 101, 87), RGBColor(230, 32, 8), RGBColor(189, 32, 8), RGBColor(151, 22, 7), RGBColor(120, 17, 1), RGBColor(101, 11, 1), RGBColor(82, 13, 6), RGBColor(54, 8, 3), RGBColor(29, 5, 2), RGBColor(0, 0, 0));
        val RESIN = ColorPalette(3, RGBColor(255, 238, 209), RGBColor(255, 211, 135), RGBColor(255, 195, 84), RGBColor(246, 159, 59), RGBColor(240, 133, 42), RGBColor(236, 114, 20), RGBColor(219, 95, 16), RGBColor(199, 73, 10), RGBColor(165, 59, 17), RGBColor(128, 42, 26), RGBColor(73, 24, 15));
        val SPRUCE_WOOD = ColorPalette(3, RGBColor(120, 90, 54), RGBColor(95, 71, 42), RGBColor(58, 44, 26), RGBColor(39, 29, 17), RGBColor(34, 26, 15), RGBColor(20, 15, 9), RGBColor(13, 10, 5), RGBColor(5, 4, 2), RGBColor(0, 0, 0), RGBColor(0, 0, 0), RGBColor(0, 0, 0));
        val STONE = ColorPalette(3, RGBColor(233, 233, 232), RGBColor(205, 203, 201), RGBColor(177, 174, 171), RGBColor(149, 145, 141), RGBColor(127, 124, 120), RGBColor(106, 103, 100), RGBColor(85, 82, 80), RGBColor(63, 62, 60), RGBColor(42, 41, 40), RGBColor(21, 20, 20), RGBColor(0, 0, 0));
        val STRING = ColorPalette(3, RGBColor(244, 244, 244), RGBColor(231, 231, 231), RGBColor(217, 217, 217), RGBColor(204, 204, 204), RGBColor(174, 174, 174), RGBColor(145, 145, 145), RGBColor(116, 116, 116), RGBColor(87, 87, 87), RGBColor(58, 58, 58), RGBColor(29, 29, 29), RGBColor(0, 0, 0));
        val WARPED_HYPHAE = ColorPalette(3, RGBColor(63, 147, 145), RGBColor(50, 119, 118), RGBColor(34, 80, 80), RGBColor(24, 58, 58), RGBColor(21, 50, 50), RGBColor(14, 34, 34), RGBColor(10, 25, 25), RGBColor(6, 14, 14), RGBColor(1, 3, 3), RGBColor(0, 0, 0), RGBColor(0, 0, 0));
        val WOOD = ColorPalette(3, RGBColor(191, 143, 55), RGBColor(173, 130, 50), RGBColor(137, 103, 39), RGBColor(117, 88, 33), RGBColor(117, 88, 33), RGBColor(99, 74, 28), RGBColor(89, 67, 25), RGBColor(73, 54, 21), RGBColor(40, 30, 11), RGBColor(22, 17, 6), RGBColor(0, 0, 0));
    }
}
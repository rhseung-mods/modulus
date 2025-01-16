package com.rhseung.modulus.util

object Colors {
    private val colors = mutableMapOf<String, ARGBColor>();

    fun register(name: String, color: ARGBColor): ARGBColor {
        colors[name] = color;
        return color;
    }

    fun register(name: String, rgb: Int): ARGBColor {
        return register(name, RGBColor(rgb).fullAlpha());
    }

    fun register(name: String, r: Int, g: Int, b: Int): ARGBColor {
        return register(name, RGBColor(r, g, b).fullAlpha());
    }

    operator fun get(name: String): ARGBColor {
        return colors[name] ?: error("color not found: $name");
    }

    operator fun set(name: String, color: ARGBColor) {
        colors[name] = color;
    }

    val TRANSPARENT = register("transparent", ARGBColor.EMPTY);
    val WHITE = register("white", 0xFFFFFF);
    val BLACK = register("black", 0x000000);

    val WOOD = register("wood", 0x967441);
    val OAK_WOOD = register("oak_wood", 0xbf934b);
    val SPRUCE_WOOD = register("spruce_wood", 0x694f2f);
    val BIRCH_WOOD = register("birch_wood", 0xf5dd8c);
    val JUNGLE_WOOD = register("jungle_wood", 0xb07b58);
    val ACACIA_WOOD = register("acacia_wood", 0xac5d33);
    val DARK_OAK_WOOD = register("dark_oak_wood", 0x3c2712);
    val BAMBOO_WOOD = register("bamboo_wood", 0xc2b54e);
    val CHERRY_WOOD = register("cherry_wood", 0xe1a8a1);
    val MANGROVE_WOOD = register("mangrove_wood", 0x773934);
    val PALE_OAK_WOOD = register("pale_oak_wood", 0xddcecd);
    val CRIMSON_HYPHAE = register("crimson_hyphae", 0x863e5a);
    val WARPED_HYPHAE = register("warped_hyphae", 0x398382);

    val STONE = register("stone", 0x95918d);
    val COBBLESTONE = register("cobblestone", 0x9a9a9a);
    val DEEPSLATE = register("deepslate", 0x3a3a3a);
    val COBBLED_DEEPSLATE = register("cobbled_deepslate", 0x3a3a3a);
    val FLINT = register("flint", 0x373737);
    val GRANITE = register("granite", 0xd38b70);
    val DIORITE = register("diorite", 0xeeeeee);
    val ANDESITE = register("andesite", 0xaabbcc);
    val BLACKSTONE = register("blackstone", 0x424242);
    val BASALT = register("basalt", 0x626262);
    val OBSIDIAN = register("obsidian", 0x44395e);

    val GOLD = register("gold", 0xf2ce63);
    val COPPER = register("copper", 0xd7805e);
    val IRON = register("iron", 0xd7d7d7);
    val DIAMOND = register("diamond", 0x6eecd2);
    val NETHERITE = register("netherite", 0x7c6066);

    val LEATHER = register("leather", 0xb35e30);
    val RABBIT_HIDE = register("rabbit_hide", 0x996d3d);
    val STRING = register("string", 0xcccccc);
}
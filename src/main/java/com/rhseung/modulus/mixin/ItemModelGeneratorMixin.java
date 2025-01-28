package com.rhseung.modulus.mixin;

import com.rhseung.modulus.util.ColorPalette;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.stream.IntStream;

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin {
    @Shadow @Final @Mutable public static List<String> LAYERS;

    static {
        LAYERS = IntStream.range(0, ColorPalette.SIZE).mapToObj(i -> "layer" + i).toList();
    }
}

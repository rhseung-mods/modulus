package com.rhseung.modulus.datagen

import com.rhseung.blueprint.color.ColorARGB
import com.rhseung.blueprint.color.Colors
import com.rhseung.blueprint.color.Palette
import com.rhseung.blueprint.datagen.BlueprintTextureProvider
import com.rhseung.blueprint.render.TextureImage
import com.rhseung.modulus.Modulus
import com.rhseung.modulus.mixin.accessor.PathResolverAccessor
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.TextureKey
import net.minecraft.util.Identifier
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension

class TextureProvider(output: FabricDataOutput) : BlueprintTextureProvider(output) {
    override fun generateTextures() {
        val pathResolver = this.texturesPathResolver as PathResolverAccessor;
        val itemTextures = pathResolver.rootPath.resolve(Modulus.MOD_ID).resolve(pathResolver.directoryName).resolve("item");

        itemTextures.listDirectoryEntries("*/*.png").forEach { path ->
            this.toolPartTexture(Modulus.id("item/${path.parent.nameWithoutExtension}/${path.nameWithoutExtension}"));
        };
    }

    private fun toolPartTexture(original: Identifier) {
        val baseImage = this.getImage(original);
        println(getPath(original));

        require(Palette.DEFAULT.toSet().containsAll(baseImage.getColors().map(ColorARGB::toRGB))) {
            "Palette colors must contain all base image colors(${Palette.DEFAULT})";
        };

        val textureMap: Map<TextureKey, Identifier> = (0..<Palette.SIZE).associate {
            TextureKey.of("layer$it") to original.withSuffixedPath("/$it")
        };

        textureMap.values.forEachIndexed { index, id ->
            val image = TextureImage(baseImage.width, baseImage.height);
            val targetColor = Palette.DEFAULT[index];

            val positions = baseImage.getPositions(targetColor);
            positions.forEach { (x, y) -> image[x, y] = Colors.WHITE; }

            this.saveImage(image, id);
        }
    }
}
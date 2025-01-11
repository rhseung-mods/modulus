package com.rhseung.modulus.util

import net.minecraft.registry.tag.TagKey
import net.minecraft.text.MutableText
import kotlin.text.Regex

object Utils {
    fun String.titlecase(): String {
        return this.split(Regex(" +")).joinToString(" ") {
            it.split("_").joinToString(" ") { it.replaceFirstChar { it.uppercase() } }
        };
    }

    fun <T> isMinecraftTag(tag: TagKey<T>): Boolean {
        return tag.id.namespace == "minecraft";
    }

    operator fun MutableText.plus(text: MutableText): MutableText {
        return this.append(text);
    }
}
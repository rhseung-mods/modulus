package com.rhseung.modulus.tool

import net.minecraft.text.MutableText
import net.minecraft.text.Text

interface Translatable {
    val translationKey: String;

    fun getName(): MutableText {
        return Text.translatable(translationKey);
    }
}
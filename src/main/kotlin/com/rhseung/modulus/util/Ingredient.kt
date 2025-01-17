package com.rhseung.modulus.util

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey

class Ingredient {
    val tags: MutableList<TagKey<Item>> = mutableListOf();
    val items: MutableList<RegistryEntry<Item>> = mutableListOf();

    override fun toString(): String {
        return "ItemList(tags=$tags, items=$items)";
    }

    override fun equals(other: Any?): Boolean {
        return other is Ingredient && other.tags == tags && other.items == items;
    }

    override fun hashCode(): Int {
        var result = tags.hashCode()
        result = 31 * result + items.hashCode()
        return result
    }

    constructor(vararg tags: TagKey<Item>) {
        this.tags.addAll(tags);
    }

    constructor(vararg items: Item) {
        this.items.addAll(items.map { it.registryEntry });
    }

    constructor(tags: List<TagKey<Item>>, items: List<RegistryEntry<Item>>) {
        this.tags.addAll(tags);
        this.items.addAll(items);
    }

    operator fun plus(tag: TagKey<Item>): Ingredient {
        tags.add(tag);
        return this;
    }

    operator fun plus(item: Item): Ingredient {
        items.add(item.registryEntry);
        return this;
    }

    operator fun plus(other: Ingredient): Ingredient {
        tags.addAll(other.tags);
        items.addAll(other.items);
        return this;
    }

    operator fun contains(stack: ItemStack): Boolean {
        return tags.any { stack.isIn(it) } || items.any { stack.itemMatches(it) };
    }

    companion object {
        val EMPTY = Ingredient(listOf(), listOf());
    }
}
package com.rhseung.modulus.tool

open class ToolPartType(val name: String, val position: ToolPosition) {
    open val isMain = false;

    override fun toString(): String {
        return name.uppercase();
    }

    override fun equals(other: Any?): Boolean {
        return other is ToolPartType && other.name == name && other.position == position;
    }

    override fun hashCode(): Int {
        var result = name.hashCode();
        result = 31 * result + position.hashCode();
        return result;
    }

    operator fun component1() = name;

    operator fun component2() = position;

    fun withMaterial(material: ToolMaterial): ToolPart {
        return ToolPart(this, material);
    }

    companion object {
        fun main(partType: ToolPartType): MainToolPartType {
            return MainToolPartType(partType.name, partType.position);
        }

        val PICKAXE_LEFT_HEAD = ToolPartType("pickaxe_left_head", ToolPosition.LEFT_HEAD);
        val PICKAXE_RIGHT_HEAD = ToolPartType("pickaxe_right_head", ToolPosition.RIGHT_HEAD);
        val AXE_HEAD = ToolPartType("axe_head", ToolPosition.LEFT_HEAD);
        val SHOVEL_HEAD = ToolPartType("shovel_head", ToolPosition.HEAD);
        val HOE_HEAD = ToolPartType("hoe_head", ToolPosition.LEFT_HEAD);

        val HANDLE = ToolPartType("handle", ToolPosition.HANDLE);
        val BINDING = ToolPartType("binding", ToolPosition.BINDING);

        val GRIP = ToolPartType("grip", ToolPosition.GRIP);
        val EXTRA = ToolPartType("extra", ToolPosition.EXTRA);
    }

    class MainToolPartType(name: String, position: ToolPosition) : ToolPartType(name, position) {
        override val isMain = true;
    }
}
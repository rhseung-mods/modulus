package com.rhseung.modulus.tool

enum class ToolPosition(vararg subPositions: ToolPosition) {
    LEFT_HEAD,
    RIGHT_HEAD,
    HEAD(LEFT_HEAD, RIGHT_HEAD),
    HANDLE,
    BINDING,
    GRIP,
    EXTRA;  // 머리 꼭대기에 봉다리처럼 보석 전시

    init {
        subPositions.forEach {
            if (it.subPositions.isNotEmpty())
                throw IllegalArgumentException("Subposition cannot have subpositions");
        }
    }

    val subPositions: Set<ToolPosition> = subPositions.toSet();
}
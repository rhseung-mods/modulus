package com.rhseung.modulus.tool

enum class ToolType(
    val necessaryPartPositions: List<ToolPosition>,
    val optionalPartPositions: List<ToolPosition>
) {
    SINGLE(
        listOf(
            ToolPosition.HANDLE,
            ToolPosition.HEAD.mainPosition(),
        ), listOf(
            ToolPosition.BINDING,
            ToolPosition.GRIP,
            ToolPosition.EXTRA
        )
    ),

    DOUBLE(
        listOf(
            ToolPosition.HANDLE,
            ToolPosition.RIGHT_HEAD,
            ToolPosition.LEFT_HEAD.mainPosition(),
        ), listOf(
            ToolPosition.BINDING,
            ToolPosition.GRIP,
            ToolPosition.EXTRA
        )
    ),
    ;

    val everyPartPositions = necessaryPartPositions + optionalPartPositions;

    val mainPartPosition = necessaryPartPositions.firstOrNull { it.isMain }
        ?: throw IllegalArgumentException("ToolType $name has no main part");

    fun isOptionalPart(partType: ToolPartType) = partType.position in optionalPartPositions;

    fun isOptionalPart(part: ToolPart) = part.partType.position in optionalPartPositions;

    fun isNecessaryPart(partType: ToolPartType) = partType.position in necessaryPartPositions;

    fun isNecessaryPart(part: ToolPart) = part.partType.position in necessaryPartPositions;

    fun withPartTypes(vararg partTypes: ToolPartType): Map<ToolPosition, ToolPartType> {
        require(partTypes.map { it.position }.containsAll(everyPartPositions)) { "${partTypes.toList()} does not have part types $everyPartPositions" };

        return partTypes.associateBy { it.position };
    }

    fun withNecessaryPartTypes(vararg partTypes: ToolPartType): Map<ToolPosition, ToolPartType> {
        require(partTypes.map { it.position }.containsAll(necessaryPartPositions))  { "${partTypes.toList()} does not have necessary part types $necessaryPartPositions" };

        return partTypes.associateBy { it.position };
    }

    fun withParts(vararg parts: ToolPart): Map<ToolPosition, ToolPart> {
        require(parts.map { it.partType.position }.containsAll(everyPartPositions)) { "${parts.toList()} does not have parts $everyPartPositions" };

        return parts.associateBy { it.partType.position };
    }

    fun withNecessaryParts(vararg parts: ToolPart): Map<ToolPosition, ToolPart> {
        require(parts.map { it.partType.position }.containsAll(necessaryPartPositions)) { "${parts.toList()} does not have necessary parts $necessaryPartPositions" };

        return parts.associateBy { it.partType.position };
    }

    init {
        require(necessaryPartPositions.count { it.isMain } == 1) { "ToolType $name has no or multiple main parts" };
        require(optionalPartPositions.all { !it.isMain }) { "ToolType $name has optional main parts" };

        for (pos1 in everyPartPositions) {
            for (pos2 in everyPartPositions) {
                if (pos1 != pos2 && pos2 in pos1.subPositions)
                    throw IllegalArgumentException("Position $pos2 is a subposition of $pos1");
            }
        }
    }
}
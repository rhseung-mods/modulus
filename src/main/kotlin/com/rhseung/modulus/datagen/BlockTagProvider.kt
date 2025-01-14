package com.rhseung.modulus.datagen

import com.rhseung.modulus.tool.ToolTier
import com.rhseung.modulus.util.Utils
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.registry.RegistryWrapper
import java.util.concurrent.CompletableFuture

class BlockTagProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<RegistryWrapper.WrapperLookup>
) : FabricTagProvider.BlockTagProvider(output, registriesFuture) {

    override fun configure(lookUp: RegistryWrapper.WrapperLookup) {
        ToolTier.entries.forEachIndexed { i, tier ->
            getOrCreateTagBuilder(tier.incorrectBlockTag).apply {
                for (j in i + 1..<ToolTier.entries.size) {
                    forceAddTag(ToolTier.entries[j].newMineableBlockTag);
                }
            }

            if (!Utils.isMinecraftTag(tier.newMineableBlockTag)) {
                getOrCreateTagBuilder(tier.newMineableBlockTag);
            }
        }

//        ToolType.VALUES.forEach { type ->
//            type.mineableBlockTags.forEach { tag ->
//                tag
//            }
//        }
    }
}
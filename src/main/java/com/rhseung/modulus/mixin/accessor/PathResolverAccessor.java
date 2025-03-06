package com.rhseung.modulus.mixin.accessor;

import net.minecraft.data.DataOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;

@Mixin(DataOutput.PathResolver.class)
public interface PathResolverAccessor {
    @Accessor
    Path getRootPath();

    @Accessor
    String getDirectoryName();
}

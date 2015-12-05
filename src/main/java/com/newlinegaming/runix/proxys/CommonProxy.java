package com.newlinegaming.runix.proxys;

import com.newlinegaming.runix.tile.TileLightBeam;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {

    public void registerRenderInformation() {} //NO-OP
    
    public void registerTileEnitiy() {
        GameRegistry.registerTileEntity(TileLightBeam.class, "TileLightBeam");
    }
}

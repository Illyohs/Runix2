package com.newlinegaming.runix.client.render.tile;

import com.newlinegaming.runix.common.utils.UtilInfo;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RenderTileLightBeam extends TileEntitySpecialRenderer {

    
    private final ResourceLocation TEXTURE = new ResourceLocation(UtilInfo.MOD_ID, "textures/fx/tempbeam.png");
    
    public RenderTileLightBeam() {}

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage) {
        
    }



}

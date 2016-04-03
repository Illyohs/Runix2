package com.newlinegaming.runix.proxys;

import net.minecraftforge.client.MinecraftForgeClient;

import com.newlinegaming.runix.client.render.block.GreekFireRenderer;
import com.newlinegaming.runix.client.render.block.HoarFrostRenderer;
import com.newlinegaming.runix.client.render.item.ItemRenderTransmutaionRod;
import com.newlinegaming.runix.client.render.tile.RenderTileLightBeam;
import com.newlinegaming.runix.item.ModItem;
import com.newlinegaming.runix.tile.TileLightBeam;
import net.minecraftforge.fml.client.registry.ClientRegistry;


public class ClientProxy extends CommonProxy {
	
    @Override
	public void registerRenderInformation() {
	    ClientRegistry.bindTileEntitySpecialRenderer(TileLightBeam.class, new RenderTileLightBeam());
	}
	
	public void registerTileEnitiy() {} //NO OP
}

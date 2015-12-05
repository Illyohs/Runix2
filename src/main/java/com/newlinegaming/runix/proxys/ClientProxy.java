package com.newlinegaming.runix.proxys;

import com.newlinegaming.runix.client.render.tile.RenderTileLightBeam;
import com.newlinegaming.runix.tile.TileLightBeam;

import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
	
    @Override
	public void registerRenderInformation() {
//	    ISimpleBlockRenderingHandler handler = null;
//	    RenderingRegistry.registerBlockHandler(handler);
	    ClientRegistry.bindTileEntitySpecialRenderer(TileLightBeam.class, new RenderTileLightBeam());
//	    MinecraftForgeClient.registerItemRenderer(ModItem.transRod, (IItemRenderer)new ItemRenderTransmutaionRod());
//	    RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler) new GreekFireRenderer());
//        RenderingRegistry.registerBlockHandler((ISimpleBlockRenderingHandler) new HoarFrostRenderer());
	}
	
	public void registerTileEnitiy() {} //NO OP
}

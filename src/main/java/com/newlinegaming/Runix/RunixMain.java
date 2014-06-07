package com.newlinegaming.Runix;

import com.newlinegaming.Runix.block.ModBlock;
import com.newlinegaming.Runix.fluids.ModFluid;
import com.newlinegaming.Runix.item.ModItem;
import com.newlinegaming.Runix.lib.LibRef;
import com.newlinegaming.Runix.proxys.CommonProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;

import com.newlinegaming.Runix.creativetabs.TabRunix;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = LibRef.MOD_ID, name = LibRef.MOD_NAME, version = LibRef.MOD_VERSION)
public class RunixMain {

    @Instance
    public static RunixMain instance;

    @SidedProxy(clientSide = LibRef.CLIENT_PROXY, serverSide = LibRef.COMMON_PROXY)
    public static CommonProxy proxy;

    public static CreativeTabs TabRunix = new TabRunix(CreativeTabs.getNextID(), LibRef.MOD_NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModBlock.init();
        ModFluid.init();
        ModItem.init();

    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        proxy.registerRenderInformation();
    }

    //Registry's
    public RunixMain() {

        Tiers tiers = new Tiers(); //load the list of block tiers

        MinecraftForge.EVENT_BUS.register(RuneHandler.getInstance());
    }
}

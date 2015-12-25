package us.illyohs.runix2.common

import net.minecraftforge.fml.common.{SidedProxy, Mod}
import net.minecraftforge.fml.common.Mod.{Instance, EventHandler}
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import us.illyohs.runix2.common.core.CommonProxy
import us.illyohs.runix2.common.util.LibInfo

/**
  * Created by illyohs on 12/25/15.
  */

@Mod(
  modid = LibInfo.MOD_ID,
  name = LibInfo.MOD_NAME,
  version = LibInfo.VERSION,
  modLanguage = "scala"
)
object Runix2 {

  @SidedProxy(clientSide = LibInfo.CLIENT_PROXY, serverSide = LibInfo.COMMON_PROXY)
  var proxy: CommonProxy = null

  @EventHandler
  def preInit(event: FMLPreInitializationEvent): Unit = {
    proxy.onPreInit(event)

  }

  @EventHandler
  def init(event: FMLInitializationEvent): Unit = {
    proxy.onInit(event)
  }

  @EventHandler
  def postInit(event: FMLPostInitializationEvent): Unit = {
    proxy.onPostInit(event)
  }
}

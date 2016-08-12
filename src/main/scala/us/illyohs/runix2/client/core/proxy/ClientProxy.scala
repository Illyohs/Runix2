package us.illyohs.runix2.client.core.proxy

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}

import us.illyohs.runix2.common.proxy.CommonProxy

class ClientProxy
  extends CommonProxy {

  override def preInit(e: FMLPreInitializationEvent): Unit = {
    super.preInit(e)
  }

  override def init(e: FMLInitializationEvent): Unit = {
    super.init(e)
  }

  override def postInit(e: FMLPostInitializationEvent): Unit = {
    super.postInit(e)
  }
}

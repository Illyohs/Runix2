package us.illyohs.runix2.common.proxy

import net.minecraft.util.ResourceLocation

import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.registry.{FMLControlledNamespacedRegistry, RegistryBuilder}

import us.illyohs.runix2.api.rune.Rune

class CommonProxy {

  final private val RUNES: ResourceLocation                               = new ResourceLocation("runix2:runes")
  final private var iRuneRegistry: FMLControlledNamespacedRegistry[Rune]= null

  this.iRuneRegistry = new RegistryBuilder[Rune]
    .setName(RUNES).setIDRange(0, 2048)
    .setType(classOf[Rune])
    .create().asInstanceOf[FMLControlledNamespacedRegistry[Rune]]

  def preInit(e:FMLPreInitializationEvent): Unit = {

  }

  def init(e:FMLInitializationEvent): Unit = {

  }

  def postInit(e:FMLPostInitializationEvent): Unit = {

  }

}

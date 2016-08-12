package us.illyohs.runix2.common.core.callback

import java.util

import net.minecraft.util.ResourceLocation

import net.minecraftforge.fml.common.registry.IForgeRegistry

import us.illyohs.runix2.api.rune.Rune

import com.google.common.collect.BiMap

object RuneCallBack {
  val INSTANCE = new RuneCallBack()
}

class RuneCallBack
  extends IForgeRegistry.AddCallback[Rune]
    with IForgeRegistry.ClearCallback[Rune]
    with IForgeRegistry.CreateCallback[Rune] {
  def onAdd(obj: Rune, id: Int, slaveset: util.Map[ResourceLocation, _]) {
  }

  def onClear(is: IForgeRegistry[Rune], slaveset: util.Map[ResourceLocation, _]) {
  }

  def onCreate(slaveset: util.Map[ResourceLocation, _], registries: BiMap[ResourceLocation, _ <: IForgeRegistry[_]]) {
  }
}
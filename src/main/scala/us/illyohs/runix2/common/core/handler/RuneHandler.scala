package us.illyohs.runix2.common.core.handler

import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

import us.illyohs.runix2.api.Runix2API
import us.illyohs.runix2.api.rune.Rune

object RuneHandler {

  @SubscribeEvent
  def onPlayerInteract(e:RightClickBlock): Unit = {

    val runeReg = Runix2API.RUNE

    for (i <- runeReg) {

    }
  }

  def patternShape(rune:Rune): Unit = {
    val pat = rune.pattern()
  }

}

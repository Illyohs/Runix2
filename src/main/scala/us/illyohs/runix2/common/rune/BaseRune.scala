package us.illyohs.runix2.common.rune

import us.illyohs.runix2.api.rune.Rune
import us.illyohs.runix2.common.core.util.LibInfo

abstract class BaseRune(name:String, isFlat:Boolean)
  extends Rune {
  this.setRegistryName(name)
  this.setUnLocalizedName(LibInfo.MOD_ID + ":" + name)
}

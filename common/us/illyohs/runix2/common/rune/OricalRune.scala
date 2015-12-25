package us.illyohs.runix2.common.rune

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import us.illyohs.runix2.api.rune.BaseRune

/**
  * Created by illyohs on 12/25/15.
  */
class OricalRune extends BaseRune("runix:orical", true) {

  override def runicTemplateOriginal(): Array[Array[Block]] = {
    val STONE = Blocks.stone
    val pattern: Array[Array[Block]] = Array(
    Array(STONE, STONE, STONE),
    Array(STONE, STONE, STONE),
    Array(STONE, STONE, STONE)
    )

    return pattern
//    return Array[Array[Array[Block]]].
  }
}

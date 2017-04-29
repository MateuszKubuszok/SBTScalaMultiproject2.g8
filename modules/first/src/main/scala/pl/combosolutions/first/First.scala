package pl.combosolutions.first

import com.typesafe.scalalogging.Logger
import pureconfig._

object First {

  val config = loadConfig[FirstConfig]("first").get

  val logger = Logger(getClass)

  def main(args: Array[String]): Unit = logger.info(s"Run first at version: ${config.version}")
}

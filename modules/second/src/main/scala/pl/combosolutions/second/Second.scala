package pl.combosolutions.second

import com.typesafe.scalalogging.Logger
import pureconfig.loadConfig

object Second {

  val config = loadConfig[SecondConfig]("first").get

  val logger = Logger(getClass)

  def main(args: Array[String]): Unit = logger.info(s"Run first at version: ${config.version}")
}

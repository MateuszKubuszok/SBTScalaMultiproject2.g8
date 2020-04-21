package $package$.second

import com.typesafe.scalalogging.Logger
import pureconfig._
import pureconfig.generic.auto._

object Second {

  val config = ConfigSource.default.at("second").load[SecondConfig].getOrElse(SecondConfig("undefined"))

  val logger = Logger(getClass)

  def main(args: Array[String]): Unit = logger.info(s"Run first at version: \${config.version}")
}

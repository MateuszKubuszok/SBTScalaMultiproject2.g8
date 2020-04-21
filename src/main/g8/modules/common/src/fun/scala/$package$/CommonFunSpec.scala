package $package$

import org.specs2.mutable.Specification

final class CommonFunSpec extends Specification {

  "Common integration" should {

    "function in Church" in {
      1 mustEqual 1
    }
  }
}

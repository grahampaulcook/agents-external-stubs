package uk.gov.hmrc.agentsexternalstubs.models

import play.api.libs.json.{JsArray, Json}
import uk.gov.hmrc.agentsexternalstubs.support.ValidatedMatchers
import uk.gov.hmrc.play.test.UnitSpec

class ServicesSpec extends UnitSpec with ValidatedMatchers {

  "Services" should {
    "read services definitions at bootstrap" in {
      Services.services should not be empty
    }

    "serialize services back to json" in {
      val entity = Services(services = Services.services)
      val json = Json.toJson(entity)
      (json \ "services").as[JsArray].value should not be empty
    }

    import org.scalatest.Inspectors._
    "have Enrolment generator and validator" in {
      forAll(Seq("foo", "bar", "baz", "zoo", "zig", "zag", "doc", "dot", "abc", "xyz")) { seed: String =>
        Services.services.foreach { s =>
          val enrolment = Generator.get(s.generator)(seed).get
          Enrolment.validate(enrolment) should be_Valid
        }
      }
    }

    "have knownFacts generator and validator" in {
      forAll(Seq("foo", "bar", "baz", "zoo", "zig", "zag", "doc", "dot", "abc", "xyz")) { seed: String =>
        Services.services.foreach { s =>
          s.knownFacts.foreach(kf => {
            val value = Generator.get(kf.valueGenerator)(seed).get
            kf.validate(value).isRight shouldBe true
          })
        }
      }
    }
  }
}

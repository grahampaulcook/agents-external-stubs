package uk.gov.hmrc.agentsexternalstubs.models.iv_models

import play.api.libs.json._
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.json.Mappings

sealed trait JourneyType

object JourneyType {
  object UpliftNino extends JourneyType
  object UpliftNoNino extends JourneyType

  val mapping = Mappings.mapEnum(UpliftNino, UpliftNoNino)

  implicit val format = mapping.jsonFormat
}

case class JourneyCreation(serviceContract: ServiceContract, journeyType: JourneyType)

object JourneyCreation {
  implicit val format: OFormat[JourneyCreation] = Json.format[JourneyCreation]
}

case class ServiceContract(
  nino: Option[Nino],
  reason: Option[String],
  origin: String,
  completionURL: String,
  failureURL: String,
  confidenceLevel: Int)

object ServiceContract {
  implicit val format: OFormat[ServiceContract] = Json.format[ServiceContract]
}

case class Journey(journeyId: String, journeyType: JourneyType, serviceContract: ServiceContract)

object Journey {

  val JOURNEYID = "journeyId"

  implicit val format = Json.format[Journey]
}

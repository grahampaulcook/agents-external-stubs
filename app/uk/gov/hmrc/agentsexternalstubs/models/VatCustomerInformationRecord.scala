package uk.gov.hmrc.agentsexternalstubs.models

import org.scalacheck.{Arbitrary, Gen}
import play.api.libs.json._
import uk.gov.hmrc.agentsexternalstubs.models.VatCustomerInformationRecord._

/**
  * ----------------------------------------------------------------------------
  * This VatCustomerInformationRecord code has been generated from json schema
  * by {@see uk.gov.hmrc.agentsexternalstubs.RecordCodeRenderer}
  * ----------------------------------------------------------------------------
  */
case class VatCustomerInformationRecord(
  vrn: String,
  approvedInformation: Option[ApprovedInformation] = None,
  inFlightInformation: Option[InFlightInformation] = None,
  id: Option[String] = None
) extends Record {

  override def uniqueKey: Option[String] = Option(vrn).map(VatCustomerInformationRecord.uniqueKey)
  override def lookupKeys: Seq[String] = Seq()
  override def withId(id: Option[String]): Record = copy(id = id)
}

object VatCustomerInformationRecord extends RecordUtils[VatCustomerInformationRecord] {

  implicit val arbitrary: Arbitrary[Char] = Arbitrary(Gen.alphaNumChar)
  implicit val recordType: RecordMetaData[VatCustomerInformationRecord] =
    RecordMetaData[VatCustomerInformationRecord](this)

  def uniqueKey(key: String): String = s"""vrn:${key.toUpperCase}"""

  import Validator._
  import Generator.GenOps._

  override val gen: Gen[VatCustomerInformationRecord] = for {
    vrn <- Generator.vrnGen
  } yield
    VatCustomerInformationRecord(
      vrn = vrn
    )

  sealed trait Address {
    def line1: String
    def line4: Option[String] = None
    def line2: String
    def line3: Option[String] = None
    def countryCode: String
  }

  object Address extends RecordUtils[Address] {

    override val gen: Gen[Address] =
      Gen.oneOf[Address](UkAddress.gen.map(_.asInstanceOf[Address]), ForeignAddress.gen.map(_.asInstanceOf[Address]))

    override val validate: Validator[Address] = {
      case x: UkAddress      => UkAddress.validate(x)
      case x: ForeignAddress => ForeignAddress.validate(x)
    }

    override val sanitizers: Seq[Update] = Seq()

    implicit val reads: Reads[Address] = new Reads[Address] {
      override def reads(json: JsValue): JsResult[Address] = {
        val r0 =
          UkAddress.formats.reads(json).flatMap(e => UkAddress.validate(e).fold(_ => JsError(), _ => JsSuccess(e)))
        val r1 = r0.orElse(
          ForeignAddress.formats
            .reads(json)
            .flatMap(e => ForeignAddress.validate(e).fold(_ => JsError(), _ => JsSuccess(e))))
        r1.orElse(
          aggregateErrors(
            JsError("Could not match json object to any variant of Address, i.e. UkAddress, ForeignAddress"),
            r0,
            r1))
      }

      private def aggregateErrors[T](errors: JsResult[T]*): JsError =
        errors.foldLeft(JsError())((a, r) =>
          r match {
            case e: JsError => JsError(a.errors ++ e.errors)
            case _          => a
        })
    }

    implicit val writes: Writes[Address] = new Writes[Address] {
      override def writes(o: Address): JsValue = o match {
        case x: UkAddress      => UkAddress.formats.writes(x)
        case x: ForeignAddress => ForeignAddress.formats.writes(x)
      }
    }

  }

  case class ApprovedInformation(
    customerDetails: CustomerDetails,
    PPOB: PPOB,
    correspondenceContactDetails: Option[CorrespondenceContactDetails] = None,
    bankDetails: Option[BankDetails] = None,
    businessActivities: Option[BusinessActivities] = None,
    flatRateScheme: Option[FlatRateScheme] = None,
    deregistration: Option[Deregistration] = None,
    returnPeriod: Option[Period] = None,
    groupOrPartnerMbrs: Option[Seq[GroupOrPartner]] = None)

  object ApprovedInformation extends RecordUtils[ApprovedInformation] {

    override val gen: Gen[ApprovedInformation] = for {
      customerDetails <- CustomerDetails.gen
      ppob            <- PPOB.gen
    } yield
      ApprovedInformation(
        customerDetails = customerDetails,
        PPOB = ppob
      )

    override val validate: Validator[ApprovedInformation] = Validator(
      checkObject(_.customerDetails, CustomerDetails.validate),
      checkObject(_.PPOB, PPOB.validate),
      checkObjectIfSome(_.correspondenceContactDetails, CorrespondenceContactDetails.validate),
      checkObjectIfSome(_.bankDetails, BankDetails.validate),
      checkObjectIfSome(_.businessActivities, BusinessActivities.validate),
      checkObjectIfSome(_.flatRateScheme, FlatRateScheme.validate),
      checkObjectIfSome(_.deregistration, Deregistration.validate),
      checkObjectIfSome(_.returnPeriod, Period.validate),
      checkEachIfSome(_.groupOrPartnerMbrs, GroupOrPartner.validate)
    )

    val correspondenceContactDetailsSanitizer: Update = seed =>
      entity =>
        entity.copy(
          correspondenceContactDetails =
            entity.correspondenceContactDetails.orElse(Generator.get(CorrespondenceContactDetails.gen)(seed)))

    val bankDetailsSanitizer: Update = seed =>
      entity => entity.copy(bankDetails = entity.bankDetails.orElse(Generator.get(BankDetails.gen)(seed)))

    val businessActivitiesSanitizer: Update = seed =>
      entity =>
        entity.copy(businessActivities = entity.businessActivities.orElse(Generator.get(BusinessActivities.gen)(seed)))

    val flatRateSchemeSanitizer: Update = seed =>
      entity => entity.copy(flatRateScheme = entity.flatRateScheme.orElse(Generator.get(FlatRateScheme.gen)(seed)))

    val deregistrationSanitizer: Update = seed =>
      entity => entity.copy(deregistration = entity.deregistration.orElse(Generator.get(Deregistration.gen)(seed)))

    val returnPeriodSanitizer: Update = seed =>
      entity => entity.copy(returnPeriod = entity.returnPeriod.orElse(Generator.get(Period.gen)(seed)))

    val groupOrPartnerMbrsSanitizer: Update = seed =>
      entity =>
        entity.copy(
          groupOrPartnerMbrs =
            entity.groupOrPartnerMbrs.orElse(Generator.get(Generator.nonEmptyListOfMaxN(3, GroupOrPartner.gen))(seed)))

    override val sanitizers: Seq[Update] = Seq(
      correspondenceContactDetailsSanitizer,
      bankDetailsSanitizer,
      businessActivitiesSanitizer,
      flatRateSchemeSanitizer,
      deregistrationSanitizer,
      returnPeriodSanitizer,
      groupOrPartnerMbrsSanitizer
    )

    implicit val formats: Format[ApprovedInformation] = Json.format[ApprovedInformation]

  }

  case class BankDetails(
    IBAN: Option[String] = None,
    BIC: Option[String] = None,
    accountHolderName: Option[String] = None,
    bankAccountNumber: Option[String] = None,
    sortCode: Option[String] = None,
    buildingSocietyNumber: Option[String] = None,
    bankBuildSocietyName: Option[String] = None)

  object BankDetails extends RecordUtils[BankDetails] {

    override val gen: Gen[BankDetails] = Gen const BankDetails(
      )

    override val validate: Validator[BankDetails] = Validator(
      check(_.IBAN.lengthMinMaxInclusive(1, 34), "Invalid length of IBAN, should be between 1 and 34 inclusive"),
      check(_.BIC.lengthMinMaxInclusive(1, 11), "Invalid length of BIC, should be between 1 and 11 inclusive"),
      check(
        _.accountHolderName.lengthMinMaxInclusive(1, 60),
        "Invalid length of accountHolderName, should be between 1 and 60 inclusive"),
      check(
        _.bankAccountNumber.matches(Common.bankAccountNumberPattern),
        s"""Invalid bankAccountNumber, does not matches regex ${Common.bankAccountNumberPattern}"""
      ),
      check(
        _.sortCode.matches(Common.sortCodePattern),
        s"""Invalid sortCode, does not matches regex ${Common.sortCodePattern}"""),
      check(
        _.buildingSocietyNumber.lengthMinMaxInclusive(1, 20),
        "Invalid length of buildingSocietyNumber, should be between 1 and 20 inclusive"),
      check(
        _.bankBuildSocietyName.lengthMinMaxInclusive(1, 40),
        "Invalid length of bankBuildSocietyName, should be between 1 and 40 inclusive")
    )

    val IBANSanitizer: Update = seed =>
      entity => entity.copy(IBAN = entity.IBAN.orElse(Generator.get(Generator.stringMinMaxN(1, 34))(seed)))

    val BICSanitizer: Update = seed =>
      entity => entity.copy(BIC = entity.BIC.orElse(Generator.get(Generator.stringMinMaxN(1, 11))(seed)))

    val accountHolderNameSanitizer: Update = seed =>
      entity =>
        entity.copy(
          accountHolderName = entity.accountHolderName.orElse(Generator.get(Generator.stringMinMaxN(1, 60))(seed)))

    val bankAccountNumberSanitizer: Update = seed =>
      entity =>
        entity.copy(
          bankAccountNumber =
            entity.bankAccountNumber.orElse(Generator.get(Generator.regex(Common.bankAccountNumberPattern))(seed)))

    val sortCodeSanitizer: Update = seed =>
      entity =>
        entity.copy(sortCode = entity.sortCode.orElse(Generator.get(Generator.regex(Common.sortCodePattern))(seed)))

    val buildingSocietyNumberSanitizer: Update = seed =>
      entity =>
        entity.copy(
          buildingSocietyNumber =
            entity.buildingSocietyNumber.orElse(Generator.get(Generator.stringMinMaxN(1, 20))(seed)))

    val bankBuildSocietyNameSanitizer: Update = seed =>
      entity =>
        entity.copy(bankBuildSocietyName =
          entity.bankBuildSocietyName.orElse(Generator.get(Generator.stringMinMaxN(1, 40))(seed)))

    override val sanitizers: Seq[Update] = Seq(
      IBANSanitizer,
      BICSanitizer,
      accountHolderNameSanitizer,
      bankAccountNumberSanitizer,
      sortCodeSanitizer,
      buildingSocietyNumberSanitizer,
      bankBuildSocietyNameSanitizer
    )

    implicit val formats: Format[BankDetails] = Json.format[BankDetails]

  }

  case class BusinessActivities(
    primaryMainCode: String,
    mainCode2: Option[String] = None,
    mainCode3: Option[String] = None,
    mainCode4: Option[String] = None)

  object BusinessActivities extends RecordUtils[BusinessActivities] {

    override val gen: Gen[BusinessActivities] = for {
      primaryMainCode <- Generator.regex(Common.primaryMainCodePattern)
    } yield
      BusinessActivities(
        primaryMainCode = primaryMainCode
      )

    override val validate: Validator[BusinessActivities] = Validator(
      check(
        _.primaryMainCode.matches(Common.primaryMainCodePattern),
        s"""Invalid primaryMainCode, does not matches regex ${Common.primaryMainCodePattern}"""),
      check(
        _.mainCode2.matches(Common.primaryMainCodePattern),
        s"""Invalid mainCode2, does not matches regex ${Common.primaryMainCodePattern}"""),
      check(
        _.mainCode3.matches(Common.primaryMainCodePattern),
        s"""Invalid mainCode3, does not matches regex ${Common.primaryMainCodePattern}"""),
      check(
        _.mainCode4.matches(Common.primaryMainCodePattern),
        s"""Invalid mainCode4, does not matches regex ${Common.primaryMainCodePattern}""")
    )

    val mainCode2Sanitizer: Update = seed =>
      entity =>
        entity.copy(
          mainCode2 = entity.mainCode2.orElse(Generator.get(Generator.regex(Common.primaryMainCodePattern))(seed)))

    val mainCode3Sanitizer: Update = seed =>
      entity =>
        entity.copy(
          mainCode3 = entity.mainCode3.orElse(Generator.get(Generator.regex(Common.primaryMainCodePattern))(seed)))

    val mainCode4Sanitizer: Update = seed =>
      entity =>
        entity.copy(
          mainCode4 = entity.mainCode4.orElse(Generator.get(Generator.regex(Common.primaryMainCodePattern))(seed)))

    override val sanitizers: Seq[Update] = Seq(mainCode2Sanitizer, mainCode3Sanitizer, mainCode4Sanitizer)

    implicit val formats: Format[BusinessActivities] = Json.format[BusinessActivities]

  }

  case class ChangeIndicators(
    customerDetails: Boolean,
    PPOBDetails: Boolean,
    correspContactDetails: Boolean,
    bankDetails: Boolean,
    businessActivities: Boolean,
    flatRateScheme: Boolean,
    deRegistrationInfo: Boolean,
    returnPeriods: Boolean,
    groupOrPartners: Boolean)

  object ChangeIndicators extends RecordUtils[ChangeIndicators] {

    override val gen: Gen[ChangeIndicators] = for {
      customerDetails       <- Generator.biasedBooleanGen
      ppobdetails           <- Generator.biasedBooleanGen
      correspContactDetails <- Generator.biasedBooleanGen
      bankDetails           <- Generator.biasedBooleanGen
      businessActivities    <- Generator.biasedBooleanGen
      flatRateScheme        <- Generator.biasedBooleanGen
      deRegistrationInfo    <- Generator.biasedBooleanGen
      returnPeriods         <- Generator.biasedBooleanGen
      groupOrPartners       <- Generator.biasedBooleanGen
    } yield
      ChangeIndicators(
        customerDetails = customerDetails,
        PPOBDetails = ppobdetails,
        correspContactDetails = correspContactDetails,
        bankDetails = bankDetails,
        businessActivities = businessActivities,
        flatRateScheme = flatRateScheme,
        deRegistrationInfo = deRegistrationInfo,
        returnPeriods = returnPeriods,
        groupOrPartners = groupOrPartners
      )

    override val validate: Validator[ChangeIndicators] = Validator()

    override val sanitizers: Seq[Update] = Seq()

    implicit val formats: Format[ChangeIndicators] = Json.format[ChangeIndicators]

  }

  case class ContactDetails(
    primaryPhoneNumber: Option[String] = None,
    mobileNumber: Option[String] = None,
    faxNumber: Option[String] = None,
    emailAddress: Option[String] = None)

  object ContactDetails extends RecordUtils[ContactDetails] {

    override val gen: Gen[ContactDetails] = Gen const ContactDetails(
      )

    override val validate: Validator[ContactDetails] = Validator(
      check(
        _.primaryPhoneNumber.matches(Common.primaryPhoneNumberPattern),
        s"""Invalid primaryPhoneNumber, does not matches regex ${Common.primaryPhoneNumberPattern}"""
      ),
      check(
        _.mobileNumber.matches(Common.primaryPhoneNumberPattern),
        s"""Invalid mobileNumber, does not matches regex ${Common.primaryPhoneNumberPattern}"""),
      check(
        _.faxNumber.matches(Common.primaryPhoneNumberPattern),
        s"""Invalid faxNumber, does not matches regex ${Common.primaryPhoneNumberPattern}"""),
      check(
        _.emailAddress.lengthMinMaxInclusive(3, 132),
        "Invalid length of emailAddress, should be between 3 and 132 inclusive")
    )

    val primaryPhoneNumberSanitizer: Update = seed =>
      entity =>
        entity.copy(
          primaryPhoneNumber =
            entity.primaryPhoneNumber.orElse(Generator.get(Generator.ukPhoneNumber.variant("primary"))(seed)))

    val mobileNumberSanitizer: Update = seed =>
      entity => entity.copy(mobileNumber = entity.mobileNumber.orElse(Generator.get(Generator.ukPhoneNumber)(seed)))

    val faxNumberSanitizer: Update = seed =>
      entity => entity.copy(faxNumber = entity.faxNumber.orElse(Generator.get(Generator.ukPhoneNumber)(seed)))

    val emailAddressSanitizer: Update = seed =>
      entity => entity.copy(emailAddress = entity.emailAddress.orElse(Generator.get(Generator.emailGen)(seed)))

    override val sanitizers: Seq[Update] =
      Seq(primaryPhoneNumberSanitizer, mobileNumberSanitizer, faxNumberSanitizer, emailAddressSanitizer)

    implicit val formats: Format[ContactDetails] = Json.format[ContactDetails]

  }

  case class CorrespondenceContactDetails(
    address: Address,
    RLS: Option[String] = None,
    contactDetails: Option[ContactDetails] = None)

  object CorrespondenceContactDetails extends RecordUtils[CorrespondenceContactDetails] {

    override val gen: Gen[CorrespondenceContactDetails] = for {
      address <- UkAddress.gen
    } yield
      CorrespondenceContactDetails(
        address = address
      )

    override val validate: Validator[CorrespondenceContactDetails] = Validator(
      checkObject(_.address, Address.validate),
      check(_.RLS.isOneOf(Common.RLSEnum), "Invalid RLS, does not match allowed values"),
      checkObjectIfSome(_.contactDetails, ContactDetails.validate)
    )

    val RLSSanitizer: Update = seed =>
      entity => entity.copy(RLS = entity.RLS.orElse(Generator.get(Gen.oneOf(Common.RLSEnum))(seed)))

    val contactDetailsSanitizer: Update = seed =>
      entity => entity.copy(contactDetails = entity.contactDetails.orElse(Generator.get(ContactDetails.gen)(seed)))

    override val sanitizers: Seq[Update] = Seq(RLSSanitizer, contactDetailsSanitizer)

    implicit val formats: Format[CorrespondenceContactDetails] = Json.format[CorrespondenceContactDetails]

  }

  case class CustomerDetails(
    organisationName: Option[String] = None,
    individual: Option[IndividualName] = None,
    dateOfBirth: Option[String] = None,
    tradingName: Option[String] = None,
    mandationStatus: String,
    registrationReason: Option[String] = None,
    effectiveRegistrationDate: Option[String] = None,
    businessStartDate: Option[String] = None)

  object CustomerDetails extends RecordUtils[CustomerDetails] {

    override val gen: Gen[CustomerDetails] = for {
      mandationStatus <- Gen.oneOf(Common.actionEnum)
    } yield
      CustomerDetails(
        mandationStatus = mandationStatus
      )

    override val validate: Validator[CustomerDetails] = Validator(
      check(
        _.organisationName.lengthMinMaxInclusive(1, 105),
        "Invalid length of organisationName, should be between 1 and 105 inclusive"),
      checkObjectIfSome(_.individual, IndividualName.validate),
      check(
        _.dateOfBirth.matches(Common.dateOfBirthPattern),
        s"""Invalid dateOfBirth, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.tradingName.lengthMinMaxInclusive(1, 160),
        "Invalid length of tradingName, should be between 1 and 160 inclusive"),
      check(_.mandationStatus.isOneOf(Common.actionEnum), "Invalid mandationStatus, does not match allowed values"),
      check(
        _.registrationReason.isOneOf(Common.registrationReasonEnum),
        "Invalid registrationReason, does not match allowed values"),
      check(
        _.effectiveRegistrationDate.matches(Common.dateOfBirthPattern),
        s"""Invalid effectiveRegistrationDate, does not matches regex ${Common.dateOfBirthPattern}"""
      ),
      check(
        _.businessStartDate.matches(Common.dateOfBirthPattern),
        s"""Invalid businessStartDate, does not matches regex ${Common.dateOfBirthPattern}""")
    )

    val organisationNameSanitizer: Update = seed =>
      entity => entity.copy(organisationName = entity.organisationName.orElse(Generator.get(Generator.company)(seed)))

    val individualSanitizer: Update = seed =>
      entity => entity.copy(individual = entity.individual.orElse(Generator.get(IndividualName.gen)(seed)))

    val dateOfBirthSanitizer: Update = seed =>
      entity =>
        entity.copy(
          dateOfBirth = entity.dateOfBirth.orElse(Generator.get(Generator.dateYYYYMMDDGen.variant("ofbirth"))(seed)))

    val tradingNameSanitizer: Update = seed =>
      entity => entity.copy(tradingName = entity.tradingName.orElse(Generator.get(Generator.tradingNameGen)(seed)))

    val registrationReasonSanitizer: Update = seed =>
      entity =>
        entity.copy(
          registrationReason =
            entity.registrationReason.orElse(Generator.get(Gen.oneOf(Common.registrationReasonEnum))(seed)))

    val effectiveRegistrationDateSanitizer: Update = seed =>
      entity =>
        entity.copy(
          effectiveRegistrationDate = entity.effectiveRegistrationDate.orElse(
            Generator.get(Generator.dateYYYYMMDDGen.variant("effectiveregistration"))(seed)))

    val businessStartDateSanitizer: Update = seed =>
      entity =>
        entity.copy(
          businessStartDate =
            entity.businessStartDate.orElse(Generator.get(Generator.dateYYYYMMDDGen.variant("businessstart"))(seed)))

    override val sanitizers: Seq[Update] = Seq(
      organisationNameSanitizer,
      individualSanitizer,
      dateOfBirthSanitizer,
      tradingNameSanitizer,
      registrationReasonSanitizer,
      effectiveRegistrationDateSanitizer,
      businessStartDateSanitizer
    )

    implicit val formats: Format[CustomerDetails] = Json.format[CustomerDetails]

  }

  case class Deregistration(
    deregistrationReason: Option[String] = None,
    effectDateOfCancellation: Option[String] = None,
    lastReturnDueDate: Option[String] = None)

  object Deregistration extends RecordUtils[Deregistration] {

    override val gen: Gen[Deregistration] = Gen const Deregistration(
      )

    override val validate: Validator[Deregistration] = Validator(
      check(
        _.deregistrationReason.isOneOf(Common.deregistrationReasonEnum),
        "Invalid deregistrationReason, does not match allowed values"),
      check(
        _.effectDateOfCancellation.matches(Common.dateOfBirthPattern),
        s"""Invalid effectDateOfCancellation, does not matches regex ${Common.dateOfBirthPattern}"""
      ),
      check(
        _.lastReturnDueDate.matches(Common.dateOfBirthPattern),
        s"""Invalid lastReturnDueDate, does not matches regex ${Common.dateOfBirthPattern}""")
    )

    val deregistrationReasonSanitizer: Update = seed =>
      entity =>
        entity.copy(
          deregistrationReason =
            entity.deregistrationReason.orElse(Generator.get(Gen.oneOf(Common.deregistrationReasonEnum))(seed)))

    val effectDateOfCancellationSanitizer: Update = seed =>
      entity =>
        entity.copy(
          effectDateOfCancellation = entity.effectDateOfCancellation.orElse(
            Generator.get(Generator.dateYYYYMMDDGen.variant("effect-ofcancellation"))(seed)))

    val lastReturnDueDateSanitizer: Update = seed =>
      entity =>
        entity.copy(
          lastReturnDueDate =
            entity.lastReturnDueDate.orElse(Generator.get(Generator.dateYYYYMMDDGen.variant("lastreturndue"))(seed)))

    override val sanitizers: Seq[Update] =
      Seq(deregistrationReasonSanitizer, effectDateOfCancellationSanitizer, lastReturnDueDateSanitizer)

    implicit val formats: Format[Deregistration] = Json.format[Deregistration]

  }

  case class FlatRateScheme(
    FRSCategory: Option[String] = None,
    FRSPercentage: Option[BigDecimal] = None,
    startDate: Option[String] = None,
    limitedCostTrader: Option[Boolean] = None)

  object FlatRateScheme extends RecordUtils[FlatRateScheme] {

    override val gen: Gen[FlatRateScheme] = Gen const FlatRateScheme(
      )

    override val validate: Validator[FlatRateScheme] = Validator(
      check(_.FRSCategory.isOneOf(Common.FRSCategoryEnum), "Invalid FRSCategory, does not match allowed values"),
      check(
        _.FRSPercentage.inRange(BigDecimal(0), BigDecimal(999.99), Some(BigDecimal(0.01))),
        "Invalid number FRSPercentage, must be in range <0,999.99>"),
      check(
        _.startDate.matches(Common.dateOfBirthPattern),
        s"""Invalid startDate, does not matches regex ${Common.dateOfBirthPattern}""")
    )

    val FRSCategorySanitizer: Update = seed =>
      entity =>
        entity.copy(FRSCategory = entity.FRSCategory.orElse(Generator.get(Gen.oneOf(Common.FRSCategoryEnum))(seed)))

    val FRSPercentageSanitizer: Update = seed =>
      entity =>
        entity.copy(
          FRSPercentage =
            entity.FRSPercentage.orElse(Generator.get(Generator.chooseBigDecimal(0, 999.99, Some(0.01)))(seed)))

    val startDateSanitizer: Update = seed =>
      entity =>
        entity.copy(
          startDate = entity.startDate.orElse(Generator.get(Generator.dateYYYYMMDDGen.variant("start"))(seed)))

    val limitedCostTraderSanitizer: Update = seed =>
      entity =>
        entity.copy(
          limitedCostTrader = entity.limitedCostTrader.orElse(Generator.get(Generator.biasedBooleanGen)(seed)))

    override val sanitizers: Seq[Update] =
      Seq(FRSCategorySanitizer, FRSPercentageSanitizer, startDateSanitizer, limitedCostTraderSanitizer)

    implicit val formats: Format[FlatRateScheme] = Json.format[FlatRateScheme]

  }

  case class ForeignAddress(
    override val line1: String,
    override val line2: String,
    override val line3: Option[String] = None,
    override val line4: Option[String] = None,
    postCode: Option[String] = None,
    override val countryCode: String)
      extends Address

  object ForeignAddress extends RecordUtils[ForeignAddress] {

    override val gen: Gen[ForeignAddress] = for {
      line1       <- Generator.address4Lines35Gen.map(_.line1)
      line2       <- Generator.address4Lines35Gen.map(_.line2)
      countryCode <- Gen.oneOf(Common.countryCodeEnum0)
    } yield
      ForeignAddress(
        line1 = line1,
        line2 = line2,
        countryCode = countryCode
      )

    override val validate: Validator[ForeignAddress] = Validator(
      check(_.line1.matches(Common.linePattern), s"""Invalid line1, does not matches regex ${Common.linePattern}"""),
      check(_.line2.matches(Common.linePattern), s"""Invalid line2, does not matches regex ${Common.linePattern}"""),
      check(_.line3.matches(Common.linePattern), s"""Invalid line3, does not matches regex ${Common.linePattern}"""),
      check(_.line4.matches(Common.linePattern), s"""Invalid line4, does not matches regex ${Common.linePattern}"""),
      check(
        _.postCode.matches(Common.postCodePattern),
        s"""Invalid postCode, does not matches regex ${Common.postCodePattern}"""),
      check(_.countryCode.isOneOf(Common.countryCodeEnum0), "Invalid countryCode, does not match allowed values")
    )

    val line3Sanitizer: Update = seed =>
      entity => entity.copy(line3 = entity.line3.orElse(Generator.get(Generator.address4Lines35Gen.map(_.line3))(seed)))

    val line4Sanitizer: Update = seed =>
      entity => entity.copy(line4 = entity.line4.orElse(Generator.get(Generator.address4Lines35Gen.map(_.line4))(seed)))

    val postCodeSanitizer: Update = seed =>
      entity => entity.copy(postCode = entity.postCode.orElse(Generator.get(Generator.postcode)(seed)))

    override val sanitizers: Seq[Update] = Seq(line3Sanitizer, line4Sanitizer, postCodeSanitizer)

    implicit val formats: Format[ForeignAddress] = Json.format[ForeignAddress]

  }

  case class FormInformation(formBundle: String, dateReceived: String)

  object FormInformation extends RecordUtils[FormInformation] {

    override val gen: Gen[FormInformation] = for {
      formBundle   <- Generator.regex(Common.formBundlePattern)
      dateReceived <- Generator.dateYYYYMMDDGen.variant("received")
    } yield
      FormInformation(
        formBundle = formBundle,
        dateReceived = dateReceived
      )

    override val validate: Validator[FormInformation] = Validator(
      check(
        _.formBundle.matches(Common.formBundlePattern),
        s"""Invalid formBundle, does not matches regex ${Common.formBundlePattern}"""),
      check(
        _.dateReceived.matches(Common.dateOfBirthPattern),
        s"""Invalid dateReceived, does not matches regex ${Common.dateOfBirthPattern}""")
    )

    override val sanitizers: Seq[Update] = Seq()

    implicit val formats: Format[FormInformation] = Json.format[FormInformation]

  }

  case class GroupOrPartner(
    typeOfRelationship: String,
    organisationName: Option[String] = None,
    individual: Option[IndividualName] = None,
    SAP_Number: String)

  object GroupOrPartner extends RecordUtils[GroupOrPartner] {

    override val gen: Gen[GroupOrPartner] = for {
      typeOfRelationship <- Gen.oneOf(Common.typeOfRelationshipEnum)
      sap_number         <- Generator.regex(Common.SAP_NumberPattern)
    } yield
      GroupOrPartner(
        typeOfRelationship = typeOfRelationship,
        SAP_Number = sap_number
      )

    override val validate: Validator[GroupOrPartner] = Validator(
      check(
        _.typeOfRelationship.isOneOf(Common.typeOfRelationshipEnum),
        "Invalid typeOfRelationship, does not match allowed values"),
      check(
        _.organisationName.lengthMinMaxInclusive(1, 105),
        "Invalid length of organisationName, should be between 1 and 105 inclusive"),
      checkObjectIfSome(_.individual, IndividualName.validate),
      check(
        _.SAP_Number.matches(Common.SAP_NumberPattern),
        s"""Invalid SAP_Number, does not matches regex ${Common.SAP_NumberPattern}""")
    )

    val organisationNameSanitizer: Update = seed =>
      entity => entity.copy(organisationName = entity.organisationName.orElse(Generator.get(Generator.company)(seed)))

    val individualSanitizer: Update = seed =>
      entity => entity.copy(individual = entity.individual.orElse(Generator.get(IndividualName.gen)(seed)))

    override val sanitizers: Seq[Update] = Seq(organisationNameSanitizer, individualSanitizer)

    implicit val formats: Format[GroupOrPartner] = Json.format[GroupOrPartner]

  }

  case class InFlightBankDetails(
    formInformation: FormInformation,
    IBAN: Option[String] = None,
    BIC: Option[String] = None,
    accountHolderName: Option[String] = None,
    bankAccountNumber: Option[String] = None,
    sortCode: Option[String] = None,
    buildingSocietyNumber: Option[String] = None,
    bankBuildSocietyName: Option[String] = None)

  object InFlightBankDetails extends RecordUtils[InFlightBankDetails] {

    override val gen: Gen[InFlightBankDetails] = for {
      formInformation <- FormInformation.gen
    } yield
      InFlightBankDetails(
        formInformation = formInformation
      )

    override val validate: Validator[InFlightBankDetails] = Validator(
      checkObject(_.formInformation, FormInformation.validate),
      check(_.IBAN.lengthMinMaxInclusive(1, 34), "Invalid length of IBAN, should be between 1 and 34 inclusive"),
      check(_.BIC.lengthMinMaxInclusive(1, 11), "Invalid length of BIC, should be between 1 and 11 inclusive"),
      check(
        _.accountHolderName.lengthMinMaxInclusive(1, 60),
        "Invalid length of accountHolderName, should be between 1 and 60 inclusive"),
      check(
        _.bankAccountNumber.matches(Common.bankAccountNumberPattern),
        s"""Invalid bankAccountNumber, does not matches regex ${Common.bankAccountNumberPattern}"""
      ),
      check(
        _.sortCode.matches(Common.sortCodePattern),
        s"""Invalid sortCode, does not matches regex ${Common.sortCodePattern}"""),
      check(
        _.buildingSocietyNumber.lengthMinMaxInclusive(1, 20),
        "Invalid length of buildingSocietyNumber, should be between 1 and 20 inclusive"),
      check(
        _.bankBuildSocietyName.lengthMinMaxInclusive(1, 40),
        "Invalid length of bankBuildSocietyName, should be between 1 and 40 inclusive")
    )

    val IBANSanitizer: Update = seed =>
      entity => entity.copy(IBAN = entity.IBAN.orElse(Generator.get(Generator.stringMinMaxN(1, 34))(seed)))

    val BICSanitizer: Update = seed =>
      entity => entity.copy(BIC = entity.BIC.orElse(Generator.get(Generator.stringMinMaxN(1, 11))(seed)))

    val accountHolderNameSanitizer: Update = seed =>
      entity =>
        entity.copy(
          accountHolderName = entity.accountHolderName.orElse(Generator.get(Generator.stringMinMaxN(1, 60))(seed)))

    val bankAccountNumberSanitizer: Update = seed =>
      entity =>
        entity.copy(
          bankAccountNumber =
            entity.bankAccountNumber.orElse(Generator.get(Generator.regex(Common.bankAccountNumberPattern))(seed)))

    val sortCodeSanitizer: Update = seed =>
      entity =>
        entity.copy(sortCode = entity.sortCode.orElse(Generator.get(Generator.regex(Common.sortCodePattern))(seed)))

    val buildingSocietyNumberSanitizer: Update = seed =>
      entity =>
        entity.copy(
          buildingSocietyNumber =
            entity.buildingSocietyNumber.orElse(Generator.get(Generator.stringMinMaxN(1, 20))(seed)))

    val bankBuildSocietyNameSanitizer: Update = seed =>
      entity =>
        entity.copy(bankBuildSocietyName =
          entity.bankBuildSocietyName.orElse(Generator.get(Generator.stringMinMaxN(1, 40))(seed)))

    override val sanitizers: Seq[Update] = Seq(
      IBANSanitizer,
      BICSanitizer,
      accountHolderNameSanitizer,
      bankAccountNumberSanitizer,
      sortCodeSanitizer,
      buildingSocietyNumberSanitizer,
      bankBuildSocietyNameSanitizer
    )

    implicit val formats: Format[InFlightBankDetails] = Json.format[InFlightBankDetails]

  }

  case class InFlightBusinessActivities(
    formInformation: FormInformation,
    primaryMainCode: String,
    mainCode2: Option[String] = None,
    mainCode3: Option[String] = None,
    mainCode4: Option[String] = None)

  object InFlightBusinessActivities extends RecordUtils[InFlightBusinessActivities] {

    override val gen: Gen[InFlightBusinessActivities] = for {
      formInformation <- FormInformation.gen
      primaryMainCode <- Generator.regex(Common.primaryMainCodePattern)
    } yield
      InFlightBusinessActivities(
        formInformation = formInformation,
        primaryMainCode = primaryMainCode
      )

    override val validate: Validator[InFlightBusinessActivities] = Validator(
      checkObject(_.formInformation, FormInformation.validate),
      check(
        _.primaryMainCode.matches(Common.primaryMainCodePattern),
        s"""Invalid primaryMainCode, does not matches regex ${Common.primaryMainCodePattern}"""),
      check(
        _.mainCode2.matches(Common.primaryMainCodePattern),
        s"""Invalid mainCode2, does not matches regex ${Common.primaryMainCodePattern}"""),
      check(
        _.mainCode3.matches(Common.primaryMainCodePattern),
        s"""Invalid mainCode3, does not matches regex ${Common.primaryMainCodePattern}"""),
      check(
        _.mainCode4.matches(Common.primaryMainCodePattern),
        s"""Invalid mainCode4, does not matches regex ${Common.primaryMainCodePattern}""")
    )

    val mainCode2Sanitizer: Update = seed =>
      entity =>
        entity.copy(
          mainCode2 = entity.mainCode2.orElse(Generator.get(Generator.regex(Common.primaryMainCodePattern))(seed)))

    val mainCode3Sanitizer: Update = seed =>
      entity =>
        entity.copy(
          mainCode3 = entity.mainCode3.orElse(Generator.get(Generator.regex(Common.primaryMainCodePattern))(seed)))

    val mainCode4Sanitizer: Update = seed =>
      entity =>
        entity.copy(
          mainCode4 = entity.mainCode4.orElse(Generator.get(Generator.regex(Common.primaryMainCodePattern))(seed)))

    override val sanitizers: Seq[Update] = Seq(mainCode2Sanitizer, mainCode3Sanitizer, mainCode4Sanitizer)

    implicit val formats: Format[InFlightBusinessActivities] = Json.format[InFlightBusinessActivities]

  }

  case class InFlightCorrespondenceContactDetails(
    formInformation: FormInformation,
    address: Option[Address] = None,
    contactDetails: Option[ContactDetails] = None)

  object InFlightCorrespondenceContactDetails extends RecordUtils[InFlightCorrespondenceContactDetails] {

    override val gen: Gen[InFlightCorrespondenceContactDetails] = for {
      formInformation <- FormInformation.gen
    } yield
      InFlightCorrespondenceContactDetails(
        formInformation = formInformation
      )

    override val validate: Validator[InFlightCorrespondenceContactDetails] = Validator(
      checkObject(_.formInformation, FormInformation.validate),
      checkObjectIfSome(_.address, Address.validate),
      checkObjectIfSome(_.contactDetails, ContactDetails.validate)
    )

    val addressSanitizer: Update = seed =>
      entity => entity.copy(address = entity.address.orElse(Generator.get(UkAddress.gen)(seed)))

    val contactDetailsSanitizer: Update = seed =>
      entity => entity.copy(contactDetails = entity.contactDetails.orElse(Generator.get(ContactDetails.gen)(seed)))

    override val sanitizers: Seq[Update] = Seq(addressSanitizer, contactDetailsSanitizer)

    implicit val formats: Format[InFlightCorrespondenceContactDetails] =
      Json.format[InFlightCorrespondenceContactDetails]

  }

  case class InFlightCustomerDetails(
    formInformation: FormInformation,
    organisationName: Option[String] = None,
    individual: Option[IndividualName] = None,
    dateOfBirth: Option[String] = None,
    tradingName: Option[String] = None,
    mandationStatus: String,
    registrationReason: Option[String] = None,
    effectiveRegistrationDate: Option[String] = None)

  object InFlightCustomerDetails extends RecordUtils[InFlightCustomerDetails] {

    override val gen: Gen[InFlightCustomerDetails] = for {
      formInformation <- FormInformation.gen
      mandationStatus <- Gen.oneOf(Common.actionEnum)
    } yield
      InFlightCustomerDetails(
        formInformation = formInformation,
        mandationStatus = mandationStatus
      )

    override val validate: Validator[InFlightCustomerDetails] = Validator(
      checkObject(_.formInformation, FormInformation.validate),
      check(
        _.organisationName.lengthMinMaxInclusive(1, 105),
        "Invalid length of organisationName, should be between 1 and 105 inclusive"),
      checkObjectIfSome(_.individual, IndividualName.validate),
      check(
        _.dateOfBirth.matches(Common.dateOfBirthPattern),
        s"""Invalid dateOfBirth, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.tradingName.lengthMinMaxInclusive(1, 160),
        "Invalid length of tradingName, should be between 1 and 160 inclusive"),
      check(_.mandationStatus.isOneOf(Common.actionEnum), "Invalid mandationStatus, does not match allowed values"),
      check(
        _.registrationReason.isOneOf(Common.registrationReasonEnum),
        "Invalid registrationReason, does not match allowed values"),
      check(
        _.effectiveRegistrationDate.matches(Common.dateOfBirthPattern),
        s"""Invalid effectiveRegistrationDate, does not matches regex ${Common.dateOfBirthPattern}"""
      )
    )

    val organisationNameSanitizer: Update = seed =>
      entity => entity.copy(organisationName = entity.organisationName.orElse(Generator.get(Generator.company)(seed)))

    val individualSanitizer: Update = seed =>
      entity => entity.copy(individual = entity.individual.orElse(Generator.get(IndividualName.gen)(seed)))

    val dateOfBirthSanitizer: Update = seed =>
      entity =>
        entity.copy(
          dateOfBirth = entity.dateOfBirth.orElse(Generator.get(Generator.dateYYYYMMDDGen.variant("ofbirth"))(seed)))

    val tradingNameSanitizer: Update = seed =>
      entity => entity.copy(tradingName = entity.tradingName.orElse(Generator.get(Generator.tradingNameGen)(seed)))

    val registrationReasonSanitizer: Update = seed =>
      entity =>
        entity.copy(
          registrationReason =
            entity.registrationReason.orElse(Generator.get(Gen.oneOf(Common.registrationReasonEnum))(seed)))

    val effectiveRegistrationDateSanitizer: Update = seed =>
      entity =>
        entity.copy(
          effectiveRegistrationDate = entity.effectiveRegistrationDate.orElse(
            Generator.get(Generator.dateYYYYMMDDGen.variant("effectiveregistration"))(seed)))

    override val sanitizers: Seq[Update] = Seq(
      organisationNameSanitizer,
      individualSanitizer,
      dateOfBirthSanitizer,
      tradingNameSanitizer,
      registrationReasonSanitizer,
      effectiveRegistrationDateSanitizer
    )

    implicit val formats: Format[InFlightCustomerDetails] = Json.format[InFlightCustomerDetails]

  }

  case class InFlightDeregistration(
    formInformation: FormInformation,
    deregistrationReason: String,
    deregDate: Option[String] = None,
    deregDateInFuture: Option[String] = None)

  object InFlightDeregistration extends RecordUtils[InFlightDeregistration] {

    override val gen: Gen[InFlightDeregistration] = for {
      formInformation      <- FormInformation.gen
      deregistrationReason <- Gen.oneOf(Common.deregistrationReasonEnum)
    } yield
      InFlightDeregistration(
        formInformation = formInformation,
        deregistrationReason = deregistrationReason
      )

    override val validate: Validator[InFlightDeregistration] = Validator(
      checkObject(_.formInformation, FormInformation.validate),
      check(
        _.deregistrationReason.isOneOf(Common.deregistrationReasonEnum),
        "Invalid deregistrationReason, does not match allowed values"),
      check(
        _.deregDate.matches(Common.dateOfBirthPattern),
        s"""Invalid deregDate, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.deregDateInFuture.matches(Common.dateOfBirthPattern),
        s"""Invalid deregDateInFuture, does not matches regex ${Common.dateOfBirthPattern}""")
    )

    val deregDateSanitizer: Update = seed =>
      entity =>
        entity.copy(
          deregDate = entity.deregDate.orElse(Generator.get(Generator.dateYYYYMMDDGen.variant("dereg"))(seed)))

    val deregDateInFutureSanitizer: Update = seed =>
      entity =>
        entity.copy(
          deregDateInFuture =
            entity.deregDateInFuture.orElse(Generator.get(Generator.dateYYYYMMDDGen.variant("dereg-infuture"))(seed)))

    override val sanitizers: Seq[Update] = Seq(deregDateSanitizer, deregDateInFutureSanitizer)

    implicit val formats: Format[InFlightDeregistration] = Json.format[InFlightDeregistration]

  }

  case class InFlightFlatRateScheme(
    formInformation: FormInformation,
    FRSCategory: Option[String] = None,
    FRSPercentage: Option[BigDecimal] = None,
    startDate: Option[String] = None,
    limitedCostTrader: Option[Boolean] = None)

  object InFlightFlatRateScheme extends RecordUtils[InFlightFlatRateScheme] {

    override val gen: Gen[InFlightFlatRateScheme] = for {
      formInformation <- FormInformation.gen
    } yield
      InFlightFlatRateScheme(
        formInformation = formInformation
      )

    override val validate: Validator[InFlightFlatRateScheme] = Validator(
      checkObject(_.formInformation, FormInformation.validate),
      check(_.FRSCategory.isOneOf(Common.FRSCategoryEnum), "Invalid FRSCategory, does not match allowed values"),
      check(
        _.FRSPercentage.inRange(BigDecimal(0), BigDecimal(999.99), Some(BigDecimal(0.01))),
        "Invalid number FRSPercentage, must be in range <0,999.99>"),
      check(
        _.startDate.matches(Common.dateOfBirthPattern),
        s"""Invalid startDate, does not matches regex ${Common.dateOfBirthPattern}""")
    )

    val FRSCategorySanitizer: Update = seed =>
      entity =>
        entity.copy(FRSCategory = entity.FRSCategory.orElse(Generator.get(Gen.oneOf(Common.FRSCategoryEnum))(seed)))

    val FRSPercentageSanitizer: Update = seed =>
      entity =>
        entity.copy(
          FRSPercentage =
            entity.FRSPercentage.orElse(Generator.get(Generator.chooseBigDecimal(0, 999.99, Some(0.01)))(seed)))

    val startDateSanitizer: Update = seed =>
      entity =>
        entity.copy(
          startDate = entity.startDate.orElse(Generator.get(Generator.dateYYYYMMDDGen.variant("start"))(seed)))

    val limitedCostTraderSanitizer: Update = seed =>
      entity =>
        entity.copy(
          limitedCostTrader = entity.limitedCostTrader.orElse(Generator.get(Generator.biasedBooleanGen)(seed)))

    override val sanitizers: Seq[Update] =
      Seq(FRSCategorySanitizer, FRSPercentageSanitizer, startDateSanitizer, limitedCostTraderSanitizer)

    implicit val formats: Format[InFlightFlatRateScheme] = Json.format[InFlightFlatRateScheme]

  }

  case class InFlightGroupOrPartner(
    formInformation: FormInformation,
    action: String,
    SAP_Number: Option[String] = None,
    typeOfRelationship: Option[String] = None,
    makeGrpMember: Option[Boolean] = None,
    makeControllingBody: Option[Boolean] = None,
    isControllingBody: Option[Boolean] = None,
    organisationName: Option[String] = None,
    tradingName: Option[String] = None,
    individual: Option[IndividualName] = None,
    PPOB: Option[PPOB] = None)

  object InFlightGroupOrPartner extends RecordUtils[InFlightGroupOrPartner] {

    override val gen: Gen[InFlightGroupOrPartner] = for {
      formInformation <- FormInformation.gen
      action          <- Gen.oneOf(Common.actionEnum)
    } yield
      InFlightGroupOrPartner(
        formInformation = formInformation,
        action = action
      )

    override val validate: Validator[InFlightGroupOrPartner] = Validator(
      checkObject(_.formInformation, FormInformation.validate),
      check(_.action.isOneOf(Common.actionEnum), "Invalid action, does not match allowed values"),
      check(
        _.SAP_Number.matches(Common.SAP_NumberPattern),
        s"""Invalid SAP_Number, does not matches regex ${Common.SAP_NumberPattern}"""),
      check(
        _.typeOfRelationship.isOneOf(Common.typeOfRelationshipEnum),
        "Invalid typeOfRelationship, does not match allowed values"),
      check(
        _.organisationName.lengthMinMaxInclusive(1, 160),
        "Invalid length of organisationName, should be between 1 and 160 inclusive"),
      check(
        _.tradingName.lengthMinMaxInclusive(1, 160),
        "Invalid length of tradingName, should be between 1 and 160 inclusive"),
      checkObjectIfSome(_.individual, IndividualName.validate),
      checkObjectIfSome(_.PPOB, PPOB.validate)
    )

    val SAP_NumberSanitizer: Update = seed =>
      entity =>
        entity.copy(
          SAP_Number = entity.SAP_Number.orElse(Generator.get(Generator.regex(Common.SAP_NumberPattern))(seed)))

    val typeOfRelationshipSanitizer: Update = seed =>
      entity =>
        entity.copy(
          typeOfRelationship =
            entity.typeOfRelationship.orElse(Generator.get(Gen.oneOf(Common.typeOfRelationshipEnum))(seed)))

    val makeGrpMemberSanitizer: Update = seed =>
      entity =>
        entity.copy(makeGrpMember = entity.makeGrpMember.orElse(Generator.get(Generator.biasedBooleanGen)(seed)))

    val makeControllingBodySanitizer: Update = seed =>
      entity =>
        entity.copy(
          makeControllingBody = entity.makeControllingBody.orElse(Generator.get(Generator.biasedBooleanGen)(seed)))

    val isControllingBodySanitizer: Update = seed =>
      entity =>
        entity.copy(
          isControllingBody = entity.isControllingBody.orElse(Generator.get(Generator.biasedBooleanGen)(seed)))

    val organisationNameSanitizer: Update = seed =>
      entity => entity.copy(organisationName = entity.organisationName.orElse(Generator.get(Generator.company)(seed)))

    val tradingNameSanitizer: Update = seed =>
      entity => entity.copy(tradingName = entity.tradingName.orElse(Generator.get(Generator.tradingNameGen)(seed)))

    val individualSanitizer: Update = seed =>
      entity => entity.copy(individual = entity.individual.orElse(Generator.get(IndividualName.gen)(seed)))

    val PPOBSanitizer: Update = seed => entity => entity.copy(PPOB = entity.PPOB.orElse(Generator.get(PPOB.gen)(seed)))

    override val sanitizers: Seq[Update] = Seq(
      SAP_NumberSanitizer,
      typeOfRelationshipSanitizer,
      makeGrpMemberSanitizer,
      makeControllingBodySanitizer,
      isControllingBodySanitizer,
      organisationNameSanitizer,
      tradingNameSanitizer,
      individualSanitizer,
      PPOBSanitizer
    )

    implicit val formats: Format[InFlightGroupOrPartner] = Json.format[InFlightGroupOrPartner]

  }

  case class InFlightInformation(changeIndicators: ChangeIndicators, inflightChanges: InflightChanges)

  object InFlightInformation extends RecordUtils[InFlightInformation] {

    override val gen: Gen[InFlightInformation] = for {
      changeIndicators <- ChangeIndicators.gen
      inflightChanges  <- InflightChanges.gen
    } yield
      InFlightInformation(
        changeIndicators = changeIndicators,
        inflightChanges = inflightChanges
      )

    override val validate: Validator[InFlightInformation] = Validator(
      checkObject(_.changeIndicators, ChangeIndicators.validate),
      checkObject(_.inflightChanges, InflightChanges.validate))

    override val sanitizers: Seq[Update] = Seq()

    implicit val formats: Format[InFlightInformation] = Json.format[InFlightInformation]

  }

  case class InFlightPPOBDetails(
    formInformation: FormInformation,
    address: Option[Address] = None,
    contactDetails: Option[ContactDetails] = None,
    websiteAddress: Option[String] = None)

  object InFlightPPOBDetails extends RecordUtils[InFlightPPOBDetails] {

    override val gen: Gen[InFlightPPOBDetails] = for {
      formInformation <- FormInformation.gen
    } yield
      InFlightPPOBDetails(
        formInformation = formInformation
      )

    override val validate: Validator[InFlightPPOBDetails] = Validator(
      checkObject(_.formInformation, FormInformation.validate),
      checkObjectIfSome(_.address, Address.validate),
      checkObjectIfSome(_.contactDetails, ContactDetails.validate),
      check(
        _.websiteAddress.lengthMinMaxInclusive(1, 132),
        "Invalid length of websiteAddress, should be between 1 and 132 inclusive")
    )

    val addressSanitizer: Update = seed =>
      entity => entity.copy(address = entity.address.orElse(Generator.get(UkAddress.gen)(seed)))

    val contactDetailsSanitizer: Update = seed =>
      entity => entity.copy(contactDetails = entity.contactDetails.orElse(Generator.get(ContactDetails.gen)(seed)))

    val websiteAddressSanitizer: Update = seed =>
      entity =>
        entity.copy(websiteAddress = entity.websiteAddress.orElse(Generator.get(Generator.stringMinMaxN(1, 132))(seed)))

    override val sanitizers: Seq[Update] = Seq(addressSanitizer, contactDetailsSanitizer, websiteAddressSanitizer)

    implicit val formats: Format[InFlightPPOBDetails] = Json.format[InFlightPPOBDetails]

  }

  case class InFlightReturnPeriod(
    formInformation: FormInformation,
    changeReturnPeriod: Option[Boolean] = None,
    nonStdTaxPeriodsRequested: Option[Boolean] = None,
    ceaseNonStdTaxPeriods: Option[Boolean] = None,
    stdReturnPeriod: Option[String] = None,
    nonStdTaxPeriods: Option[NonStdTaxPeriods] = None)

  object InFlightReturnPeriod extends RecordUtils[InFlightReturnPeriod] {

    override val gen: Gen[InFlightReturnPeriod] = for {
      formInformation <- FormInformation.gen
    } yield
      InFlightReturnPeriod(
        formInformation = formInformation
      )

    override val validate: Validator[InFlightReturnPeriod] = Validator(
      checkObject(_.formInformation, FormInformation.validate),
      check(
        _.stdReturnPeriod.isOneOf(Common.stdReturnPeriodEnum),
        "Invalid stdReturnPeriod, does not match allowed values"),
      checkObjectIfSome(_.nonStdTaxPeriods, NonStdTaxPeriods.validate)
    )

    val changeReturnPeriodSanitizer: Update = seed =>
      entity =>
        entity.copy(
          changeReturnPeriod = entity.changeReturnPeriod.orElse(Generator.get(Generator.biasedBooleanGen)(seed)))

    val nonStdTaxPeriodsRequestedSanitizer: Update = seed =>
      entity =>
        entity.copy(
          nonStdTaxPeriodsRequested =
            entity.nonStdTaxPeriodsRequested.orElse(Generator.get(Generator.biasedBooleanGen)(seed)))

    val ceaseNonStdTaxPeriodsSanitizer: Update = seed =>
      entity =>
        entity.copy(
          ceaseNonStdTaxPeriods = entity.ceaseNonStdTaxPeriods.orElse(Generator.get(Generator.biasedBooleanGen)(seed)))

    val stdReturnPeriodSanitizer: Update = seed =>
      entity =>
        entity.copy(
          stdReturnPeriod = entity.stdReturnPeriod.orElse(Generator.get(Gen.oneOf(Common.stdReturnPeriodEnum))(seed)))

    val nonStdTaxPeriodsSanitizer: Update = seed =>
      entity =>
        entity.copy(nonStdTaxPeriods = entity.nonStdTaxPeriods.orElse(Generator.get(NonStdTaxPeriods.gen)(seed)))

    override val sanitizers: Seq[Update] = Seq(
      changeReturnPeriodSanitizer,
      nonStdTaxPeriodsRequestedSanitizer,
      ceaseNonStdTaxPeriodsSanitizer,
      stdReturnPeriodSanitizer,
      nonStdTaxPeriodsSanitizer)

    implicit val formats: Format[InFlightReturnPeriod] = Json.format[InFlightReturnPeriod]

  }

  case class IndividualName(
    title: Option[String] = None,
    firstName: Option[String] = None,
    middleName: Option[String] = None,
    lastName: Option[String] = None)

  object IndividualName extends RecordUtils[IndividualName] {

    override val gen: Gen[IndividualName] = Gen const IndividualName(
      )

    override val validate: Validator[IndividualName] = Validator(
      check(_.title.isOneOf(Common.titleEnum), "Invalid title, does not match allowed values"),
      check(
        _.firstName.lengthMinMaxInclusive(1, 35),
        "Invalid length of firstName, should be between 1 and 35 inclusive"),
      check(
        _.middleName.lengthMinMaxInclusive(1, 35),
        "Invalid length of middleName, should be between 1 and 35 inclusive"),
      check(_.lastName.lengthMinMaxInclusive(1, 35), "Invalid length of lastName, should be between 1 and 35 inclusive")
    )

    val titleSanitizer: Update = seed =>
      entity => entity.copy(title = entity.title.orElse(Generator.get(Gen.oneOf(Common.titleEnum))(seed)))

    val firstNameSanitizer: Update = seed =>
      entity => entity.copy(firstName = entity.firstName.orElse(Generator.get(Generator.forename())(seed)))

    val middleNameSanitizer: Update = seed =>
      entity =>
        entity.copy(middleName = entity.middleName.orElse(Generator.get(Generator.forename().variant("middle"))(seed)))

    val lastNameSanitizer: Update = seed =>
      entity => entity.copy(lastName = entity.lastName.orElse(Generator.get(Generator.surname)(seed)))

    override val sanitizers: Seq[Update] =
      Seq(titleSanitizer, firstNameSanitizer, middleNameSanitizer, lastNameSanitizer)

    implicit val formats: Format[IndividualName] = Json.format[IndividualName]

  }

  case class InflightChanges(
    customerDetails: Option[InFlightCustomerDetails] = None,
    PPOBDetails: Option[InFlightPPOBDetails] = None,
    correspondenceContactDetails: Option[InFlightCorrespondenceContactDetails] = None,
    bankDetails: Option[InFlightBankDetails] = None,
    businessActivities: Option[InFlightBusinessActivities] = None,
    flatRateScheme: Option[InFlightFlatRateScheme] = None,
    deregister: Option[InFlightDeregistration] = None,
    returnPeriod: Option[InFlightReturnPeriod] = None,
    groupOrPartner: Option[Seq[InFlightGroupOrPartner]] = None)

  object InflightChanges extends RecordUtils[InflightChanges] {

    override val gen: Gen[InflightChanges] = Gen const InflightChanges(
      )

    override val validate: Validator[InflightChanges] = Validator(
      checkObjectIfSome(_.customerDetails, InFlightCustomerDetails.validate),
      checkObjectIfSome(_.PPOBDetails, InFlightPPOBDetails.validate),
      checkObjectIfSome(_.correspondenceContactDetails, InFlightCorrespondenceContactDetails.validate),
      checkObjectIfSome(_.bankDetails, InFlightBankDetails.validate),
      checkObjectIfSome(_.businessActivities, InFlightBusinessActivities.validate),
      checkObjectIfSome(_.flatRateScheme, InFlightFlatRateScheme.validate),
      checkObjectIfSome(_.deregister, InFlightDeregistration.validate),
      checkObjectIfSome(_.returnPeriod, InFlightReturnPeriod.validate),
      checkEachIfSome(_.groupOrPartner, InFlightGroupOrPartner.validate)
    )

    val customerDetailsSanitizer: Update = seed =>
      entity =>
        entity.copy(customerDetails = entity.customerDetails.orElse(Generator.get(InFlightCustomerDetails.gen)(seed)))

    val PPOBDetailsSanitizer: Update = seed =>
      entity => entity.copy(PPOBDetails = entity.PPOBDetails.orElse(Generator.get(InFlightPPOBDetails.gen)(seed)))

    val correspondenceContactDetailsSanitizer: Update = seed =>
      entity =>
        entity.copy(
          correspondenceContactDetails =
            entity.correspondenceContactDetails.orElse(Generator.get(InFlightCorrespondenceContactDetails.gen)(seed)))

    val bankDetailsSanitizer: Update = seed =>
      entity => entity.copy(bankDetails = entity.bankDetails.orElse(Generator.get(InFlightBankDetails.gen)(seed)))

    val businessActivitiesSanitizer: Update = seed =>
      entity =>
        entity.copy(
          businessActivities = entity.businessActivities.orElse(Generator.get(InFlightBusinessActivities.gen)(seed)))

    val flatRateSchemeSanitizer: Update = seed =>
      entity =>
        entity.copy(flatRateScheme = entity.flatRateScheme.orElse(Generator.get(InFlightFlatRateScheme.gen)(seed)))

    val deregisterSanitizer: Update = seed =>
      entity => entity.copy(deregister = entity.deregister.orElse(Generator.get(InFlightDeregistration.gen)(seed)))

    val returnPeriodSanitizer: Update = seed =>
      entity => entity.copy(returnPeriod = entity.returnPeriod.orElse(Generator.get(InFlightReturnPeriod.gen)(seed)))

    val groupOrPartnerSanitizer: Update = seed =>
      entity =>
        entity.copy(
          groupOrPartner = entity.groupOrPartner.orElse(
            Generator.get(Generator.nonEmptyListOfMaxN(3, InFlightGroupOrPartner.gen))(seed)))

    override val sanitizers: Seq[Update] = Seq(
      customerDetailsSanitizer,
      PPOBDetailsSanitizer,
      correspondenceContactDetailsSanitizer,
      bankDetailsSanitizer,
      businessActivitiesSanitizer,
      flatRateSchemeSanitizer,
      deregisterSanitizer,
      returnPeriodSanitizer,
      groupOrPartnerSanitizer
    )

    implicit val formats: Format[InflightChanges] = Json.format[InflightChanges]

  }

  case class NonStdTaxPeriods(
    period01: Option[String] = None,
    period02: Option[String] = None,
    period03: Option[String] = None,
    period04: Option[String] = None,
    period05: Option[String] = None,
    period06: Option[String] = None,
    period07: Option[String] = None,
    period08: Option[String] = None,
    period09: Option[String] = None,
    period10: Option[String] = None,
    period11: Option[String] = None,
    period12: Option[String] = None,
    period13: Option[String] = None,
    period14: Option[String] = None,
    period15: Option[String] = None,
    period16: Option[String] = None,
    period17: Option[String] = None,
    period18: Option[String] = None,
    period19: Option[String] = None,
    period20: Option[String] = None,
    period21: Option[String] = None,
    period22: Option[String] = None)

  object NonStdTaxPeriods extends RecordUtils[NonStdTaxPeriods] {

    override val gen: Gen[NonStdTaxPeriods] = Gen const NonStdTaxPeriods(
      )

    override val validate: Validator[NonStdTaxPeriods] = Validator(
      check(
        _.period01.matches(Common.dateOfBirthPattern),
        s"""Invalid period01, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period02.matches(Common.dateOfBirthPattern),
        s"""Invalid period02, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period03.matches(Common.dateOfBirthPattern),
        s"""Invalid period03, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period04.matches(Common.dateOfBirthPattern),
        s"""Invalid period04, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period05.matches(Common.dateOfBirthPattern),
        s"""Invalid period05, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period06.matches(Common.dateOfBirthPattern),
        s"""Invalid period06, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period07.matches(Common.dateOfBirthPattern),
        s"""Invalid period07, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period08.matches(Common.dateOfBirthPattern),
        s"""Invalid period08, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period09.matches(Common.dateOfBirthPattern),
        s"""Invalid period09, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period10.matches(Common.dateOfBirthPattern),
        s"""Invalid period10, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period11.matches(Common.dateOfBirthPattern),
        s"""Invalid period11, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period12.matches(Common.dateOfBirthPattern),
        s"""Invalid period12, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period13.matches(Common.dateOfBirthPattern),
        s"""Invalid period13, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period14.matches(Common.dateOfBirthPattern),
        s"""Invalid period14, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period15.matches(Common.dateOfBirthPattern),
        s"""Invalid period15, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period16.matches(Common.dateOfBirthPattern),
        s"""Invalid period16, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period17.matches(Common.dateOfBirthPattern),
        s"""Invalid period17, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period18.matches(Common.dateOfBirthPattern),
        s"""Invalid period18, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period19.matches(Common.dateOfBirthPattern),
        s"""Invalid period19, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period20.matches(Common.dateOfBirthPattern),
        s"""Invalid period20, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period21.matches(Common.dateOfBirthPattern),
        s"""Invalid period21, does not matches regex ${Common.dateOfBirthPattern}"""),
      check(
        _.period22.matches(Common.dateOfBirthPattern),
        s"""Invalid period22, does not matches regex ${Common.dateOfBirthPattern}""")
    )

    val period01Sanitizer: Update = seed =>
      entity => entity.copy(period01 = entity.period01.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period02Sanitizer: Update = seed =>
      entity => entity.copy(period02 = entity.period02.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period03Sanitizer: Update = seed =>
      entity => entity.copy(period03 = entity.period03.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period04Sanitizer: Update = seed =>
      entity => entity.copy(period04 = entity.period04.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period05Sanitizer: Update = seed =>
      entity => entity.copy(period05 = entity.period05.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period06Sanitizer: Update = seed =>
      entity => entity.copy(period06 = entity.period06.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period07Sanitizer: Update = seed =>
      entity => entity.copy(period07 = entity.period07.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period08Sanitizer: Update = seed =>
      entity => entity.copy(period08 = entity.period08.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period09Sanitizer: Update = seed =>
      entity => entity.copy(period09 = entity.period09.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period10Sanitizer: Update = seed =>
      entity => entity.copy(period10 = entity.period10.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period11Sanitizer: Update = seed =>
      entity => entity.copy(period11 = entity.period11.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period12Sanitizer: Update = seed =>
      entity => entity.copy(period12 = entity.period12.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period13Sanitizer: Update = seed =>
      entity => entity.copy(period13 = entity.period13.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period14Sanitizer: Update = seed =>
      entity => entity.copy(period14 = entity.period14.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period15Sanitizer: Update = seed =>
      entity => entity.copy(period15 = entity.period15.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period16Sanitizer: Update = seed =>
      entity => entity.copy(period16 = entity.period16.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period17Sanitizer: Update = seed =>
      entity => entity.copy(period17 = entity.period17.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period18Sanitizer: Update = seed =>
      entity => entity.copy(period18 = entity.period18.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period19Sanitizer: Update = seed =>
      entity => entity.copy(period19 = entity.period19.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period20Sanitizer: Update = seed =>
      entity => entity.copy(period20 = entity.period20.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period21Sanitizer: Update = seed =>
      entity => entity.copy(period21 = entity.period21.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    val period22Sanitizer: Update = seed =>
      entity => entity.copy(period22 = entity.period22.orElse(Generator.get(Generator.dateYYYYMMDDGen)(seed)))

    override val sanitizers: Seq[Update] = Seq(
      period01Sanitizer,
      period02Sanitizer,
      period03Sanitizer,
      period04Sanitizer,
      period05Sanitizer,
      period06Sanitizer,
      period07Sanitizer,
      period08Sanitizer,
      period09Sanitizer,
      period10Sanitizer,
      period11Sanitizer,
      period12Sanitizer,
      period13Sanitizer,
      period14Sanitizer,
      period15Sanitizer,
      period16Sanitizer,
      period17Sanitizer,
      period18Sanitizer,
      period19Sanitizer,
      period20Sanitizer,
      period21Sanitizer,
      period22Sanitizer
    )

    implicit val formats: Format[NonStdTaxPeriods] = Json.format[NonStdTaxPeriods]

  }

  case class PPOB(
    address: Address,
    RLS: Option[String] = None,
    contactDetails: Option[ContactDetails] = None,
    websiteAddress: Option[String] = None)

  object PPOB extends RecordUtils[PPOB] {

    override val gen: Gen[PPOB] = for {
      address <- UkAddress.gen
    } yield
      PPOB(
        address = address
      )

    override val validate: Validator[PPOB] = Validator(
      checkObject(_.address, Address.validate),
      check(_.RLS.isOneOf(Common.RLSEnum), "Invalid RLS, does not match allowed values"),
      checkObjectIfSome(_.contactDetails, ContactDetails.validate),
      check(
        _.websiteAddress.lengthMinMaxInclusive(1, 132),
        "Invalid length of websiteAddress, should be between 1 and 132 inclusive")
    )

    val RLSSanitizer: Update = seed =>
      entity => entity.copy(RLS = entity.RLS.orElse(Generator.get(Gen.oneOf(Common.RLSEnum))(seed)))

    val contactDetailsSanitizer: Update = seed =>
      entity => entity.copy(contactDetails = entity.contactDetails.orElse(Generator.get(ContactDetails.gen)(seed)))

    val websiteAddressSanitizer: Update = seed =>
      entity =>
        entity.copy(websiteAddress = entity.websiteAddress.orElse(Generator.get(Generator.stringMinMaxN(1, 132))(seed)))

    override val sanitizers: Seq[Update] = Seq(RLSSanitizer, contactDetailsSanitizer, websiteAddressSanitizer)

    implicit val formats: Format[PPOB] = Json.format[PPOB]

  }

  case class Period(stdReturnPeriod: Option[String] = None, nonStdTaxPeriods: Option[NonStdTaxPeriods] = None)

  object Period extends RecordUtils[Period] {

    override val gen: Gen[Period] = Gen const Period(
      )

    override val validate: Validator[Period] = Validator(
      check(
        _.stdReturnPeriod.isOneOf(Common.stdReturnPeriodEnum),
        "Invalid stdReturnPeriod, does not match allowed values"),
      checkObjectIfSome(_.nonStdTaxPeriods, NonStdTaxPeriods.validate)
    )

    val stdReturnPeriodSanitizer: Update = seed =>
      entity =>
        entity.copy(
          stdReturnPeriod = entity.stdReturnPeriod.orElse(Generator.get(Gen.oneOf(Common.stdReturnPeriodEnum))(seed)))

    val nonStdTaxPeriodsSanitizer: Update = seed =>
      entity =>
        entity.copy(nonStdTaxPeriods = entity.nonStdTaxPeriods.orElse(Generator.get(NonStdTaxPeriods.gen)(seed)))

    override val sanitizers: Seq[Update] = Seq(stdReturnPeriodSanitizer, nonStdTaxPeriodsSanitizer)

    implicit val formats: Format[Period] = Json.format[Period]

  }

  case class UkAddress(
    override val line1: String,
    override val line2: String,
    override val line3: Option[String] = None,
    override val line4: Option[String] = None,
    postCode: String,
    override val countryCode: String)
      extends Address

  object UkAddress extends RecordUtils[UkAddress] {

    override val gen: Gen[UkAddress] = for {
      line1       <- Generator.address4Lines35Gen.map(_.line1)
      line2       <- Generator.address4Lines35Gen.map(_.line2)
      postCode    <- Generator.postcode
      countryCode <- Gen.const("GB")
    } yield
      UkAddress(
        line1 = line1,
        line2 = line2,
        postCode = postCode,
        countryCode = countryCode
      )

    override val validate: Validator[UkAddress] = Validator(
      check(_.line1.matches(Common.linePattern), s"""Invalid line1, does not matches regex ${Common.linePattern}"""),
      check(_.line2.matches(Common.linePattern), s"""Invalid line2, does not matches regex ${Common.linePattern}"""),
      check(_.line3.matches(Common.linePattern), s"""Invalid line3, does not matches regex ${Common.linePattern}"""),
      check(_.line4.matches(Common.linePattern), s"""Invalid line4, does not matches regex ${Common.linePattern}"""),
      check(
        _.postCode.matches(Common.postCodePattern),
        s"""Invalid postCode, does not matches regex ${Common.postCodePattern}"""),
      check(_.countryCode.isOneOf(Common.countryCodeEnum1), "Invalid countryCode, does not match allowed values")
    )

    val line3Sanitizer: Update = seed =>
      entity => entity.copy(line3 = entity.line3.orElse(Generator.get(Generator.address4Lines35Gen.map(_.line3))(seed)))

    val line4Sanitizer: Update = seed =>
      entity => entity.copy(line4 = entity.line4.orElse(Generator.get(Generator.address4Lines35Gen.map(_.line4))(seed)))

    override val sanitizers: Seq[Update] = Seq(line3Sanitizer, line4Sanitizer)

    implicit val formats: Format[UkAddress] = Json.format[UkAddress]

  }

  override val validate: Validator[VatCustomerInformationRecord] = Validator(
    check(_.vrn.matches(Common.vrnPattern), s"""Invalid vrn, does not matches regex ${Common.vrnPattern}"""),
    checkObjectIfSome(_.approvedInformation, ApprovedInformation.validate),
    checkObjectIfSome(_.inFlightInformation, InFlightInformation.validate)
  )

  val approvedInformationSanitizer: Update = seed =>
    entity =>
      entity.copy(approvedInformation = entity.approvedInformation.orElse(Generator.get(ApprovedInformation.gen)(seed)))

  val inFlightInformationSanitizer: Update = seed =>
    entity =>
      entity.copy(inFlightInformation = entity.inFlightInformation.orElse(Generator.get(InFlightInformation.gen)(seed)))

  override val sanitizers: Seq[Update] = Seq(approvedInformationSanitizer, inFlightInformationSanitizer)

  implicit val formats: Format[VatCustomerInformationRecord] = Json.format[VatCustomerInformationRecord]
  object Common {
    val RLSEnum = Seq("0001", "0002", "0003", "0004", "0005", "0006", "0007", "0008", "0009")
    val actionEnum = Seq("1", "2", "3", "4")
    val postCodePattern = """^[A-Z]{1,2}[0-9][0-9A-Z]?\s?[0-9][A-Z]{2}|BFPO\s?[0-9]{1,10}$"""
    val primaryPhoneNumberPattern = """^[A-Z0-9 )/(*#-]+$"""
    val dateOfBirthPattern = """^\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$"""
    val registrationReasonEnum = Seq(
      "0001",
      "0002",
      "0003",
      "0004",
      "0005",
      "0006",
      "0007",
      "0008",
      "0009",
      "0010",
      "0011",
      "0012",
      "0013",
      "0014")
    val linePattern = """^[A-Za-z0-9 \-,.&'\/()!]{1,35}$"""
    val countryCodeEnum0 = Seq(
      "AD",
      "AE",
      "AF",
      "AG",
      "AI",
      "AL",
      "AM",
      "AN",
      "AO",
      "AQ",
      "AR",
      "AS",
      "AT",
      "AU",
      "AW",
      "AX",
      "AZ",
      "BA",
      "BB",
      "BD",
      "BE",
      "BF",
      "BG",
      "BH",
      "BI",
      "BJ",
      "BM",
      "BN",
      "BO",
      "BQ",
      "BR",
      "BS",
      "BT",
      "BV",
      "BW",
      "BY",
      "BZ",
      "CA",
      "CC",
      "CD",
      "CF",
      "CG",
      "CH",
      "CI",
      "CK",
      "CL",
      "CM",
      "CN",
      "CO",
      "CR",
      "CS",
      "CU",
      "CV",
      "CW",
      "CX",
      "CY",
      "CZ",
      "DE",
      "DJ",
      "DK",
      "DM",
      "DO",
      "DZ",
      "EC",
      "EE",
      "EG",
      "EH",
      "ER",
      "ES",
      "ET",
      "EU",
      "FI",
      "FJ",
      "FK",
      "FM",
      "FO",
      "FR",
      "GA",
      "GD",
      "GE",
      "GF",
      "GG",
      "GH",
      "GI",
      "GL",
      "GM",
      "GN",
      "GP",
      "GQ",
      "GR",
      "GS",
      "GT",
      "GU",
      "GW",
      "GY",
      "HK",
      "HM",
      "HN",
      "HR",
      "HT",
      "HU",
      "ID",
      "IE",
      "IL",
      "IM",
      "IN",
      "IO",
      "IQ",
      "IR",
      "IS",
      "IT",
      "JE",
      "JM",
      "JO",
      "JP",
      "KE",
      "KG",
      "KH",
      "KI",
      "KM",
      "KN",
      "KP",
      "KR",
      "KW",
      "KY",
      "KZ",
      "LA",
      "LB",
      "LC",
      "LI",
      "LK",
      "LR",
      "LS",
      "LT",
      "LU",
      "LV",
      "LY",
      "MA",
      "MC",
      "MD",
      "ME",
      "MF",
      "MG",
      "MH",
      "MK",
      "ML",
      "MM",
      "MN",
      "MO",
      "MP",
      "MQ",
      "MR",
      "MS",
      "MT",
      "MU",
      "MV",
      "MW",
      "MX",
      "MY",
      "MZ",
      "NA",
      "NC",
      "NE",
      "NF",
      "NG",
      "NI",
      "NL",
      "NO",
      "NP",
      "NR",
      "NT",
      "NU",
      "NZ",
      "OM",
      "PA",
      "PE",
      "PF",
      "PG",
      "PH",
      "PK",
      "PL",
      "PM",
      "PN",
      "PR",
      "PS",
      "PT",
      "PW",
      "PY",
      "QA",
      "RE",
      "RO",
      "RS",
      "RU",
      "RW",
      "SA",
      "SB",
      "SC",
      "SD",
      "SE",
      "SG",
      "SH",
      "SI",
      "SJ",
      "SK",
      "SL",
      "SM",
      "SN",
      "SO",
      "SR",
      "SS",
      "ST",
      "SV",
      "SX",
      "SY",
      "SZ",
      "TC",
      "TD",
      "TF",
      "TG",
      "TH",
      "TJ",
      "TK",
      "TL",
      "TM",
      "TN",
      "TO",
      "TP",
      "TR",
      "TT",
      "TV",
      "TW",
      "TZ",
      "UA",
      "UG",
      "UM",
      "UN",
      "US",
      "UY",
      "UZ",
      "VA",
      "VC",
      "VE",
      "VG",
      "VI",
      "VN",
      "VU",
      "WF",
      "WS",
      "YE",
      "YT",
      "ZA",
      "ZM",
      "ZW"
    )
    val stdReturnPeriodEnum = Seq("MA", "MB", "MC", "MM")
    val primaryMainCodePattern = """^[0-9]{5}$"""
    val deregistrationReasonEnum =
      Seq("0001", "0002", "0003", "0004", "0005", "0006", "0007", "0008", "0009", "0010", "0011")
    val typeOfRelationshipEnum = Seq("01", "02", "03", "04")
    val bankAccountNumberPattern = """^[0-9]{8}$"""
    val vrnPattern = """^[0-9]{9}$"""
    val FRSCategoryEnum = Seq(
      "001",
      "002",
      "003",
      "004",
      "005",
      "006",
      "007",
      "008",
      "009",
      "010",
      "011",
      "012",
      "013",
      "014",
      "015",
      "016",
      "017",
      "018",
      "019",
      "020",
      "021",
      "022",
      "023",
      "024",
      "025",
      "026",
      "027",
      "028",
      "029",
      "030",
      "031",
      "032",
      "033",
      "034",
      "035",
      "036",
      "037",
      "038",
      "039",
      "040",
      "041",
      "042",
      "043",
      "044",
      "045",
      "046",
      "047",
      "048",
      "049",
      "050",
      "051",
      "052",
      "053",
      "054"
    )
    val titleEnum = Seq("0001", "0002", "0003", "0004", "0005", "0006", "0007", "0008", "0009", "0010", "0011", "0012")
    val countryCodeEnum1 = Seq("GB")
    val formBundlePattern = """^[0-9]{12}$"""
    val sortCodePattern = """^[0-9]{6}$"""
    val SAP_NumberPattern = """^[0-9]{42}$"""
  }
}
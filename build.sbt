import sbt.Tests.{Group, SubProcess}
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*Filters?;MicroserviceAuditConnector;Module;GraphiteStartUp;.*\.Reverse[^.]*""",
    ScoverageKeys.coverageMinimum := 80.00,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true,
    parallelExecution in Test := false
  )
}

lazy val compileDeps = Seq(
  ws,
  "uk.gov.hmrc" %% "bootstrap-play-25" % "1.7.0",
  "uk.gov.hmrc" %% "auth-client" % "2.6.0",
  "uk.gov.hmrc" %% "agent-mtd-identifiers" % "0.10.0",
  "de.threedimensions" %% "metrics-play" % "2.5.13",
  "uk.gov.hmrc" %% "domain" % "5.2.0",
  "com.github.blemale" %% "scaffeine" % "2.5.0",
  "uk.gov.hmrc" %% "agent-kenshoo-monitoring" % "3.0.1",
  "uk.gov.hmrc" %% "play-reactivemongo" % "6.2.0",
  "org.typelevel" %% "cats-core" % "1.2.0",
  "uk.gov.hmrc" %% "stub-data-generator" % "0.5.3",
  ws
)

def testDeps(scope: String) = Seq(
  "uk.gov.hmrc" %% "hmrctest" % "3.0.0" % scope,
  "org.scalatest" %% "scalatest" % "3.0.5" % scope,
  "org.mockito" % "mockito-core" % "2.19.0" % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % scope,
  "uk.gov.hmrc" %% "reactivemongo-test" % "3.1.0" % scope,
  "com.github.tomakehurst" % "wiremock" % "2.18.0" % scope
)

lazy val root = (project in file("."))
  .settings(
    name := "agents-external-stubs",
    organization := "uk.gov.hmrc",
    scalaVersion := "2.11.11",
    PlayKeys.playDefaultPort := 9009,
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.bintrayRepo("hmrc", "release-candidates"),
      Resolver.typesafeRepo("releases"),
      Resolver.jcenterRepo
    ),
    libraryDependencies ++= compileDeps ++ testDeps("test") ++ testDeps("it"),
    publishingSettings,
    scoverageSettings,
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources",
    routesImport ++= Seq("uk.gov.hmrc.agentsexternalstubs.binders.UrlBinders._"),
    scalafmtOnCompile in Compile := true,
    scalafmtOnCompile in Test := true
  )
  .configs(IntegrationTest)
  .settings(
    Keys.fork in IntegrationTest := false,
    Defaults.itSettings,
    unmanagedSourceDirectories in IntegrationTest += baseDirectory(_ / "it").value,
    parallelExecution in IntegrationTest := false,
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    scalafmtOnCompile in IntegrationTest := true
  )
  .enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)

inConfig(IntegrationTest)(scalafmtCoreSettings)

def oneForkedJvmPerTest(tests: Seq[TestDefinition]) = {
  tests.map { test =>
    new Group(test.name, Seq(test), SubProcess(ForkOptions(runJVMOptions = Seq(s"-Dtest.name=${test.name}"))))
  }
}
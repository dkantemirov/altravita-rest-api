import sbt.Keys._
import CommonConfigurations._
import CommonKeys._
import CommonSettings._
import LocalSettings._
import Dependencies._

commonSettings

libraryDependencies ++= Dependencies.module.`rest-api`.all

enablePlugins(WebappPlugin)

def mapDev(conf: Configuration) = {
  conf.name match {
    case development.name => Seq(buildProducedArtifact := (packagedArtifact in webappDevelopmentPackage).value)
    case production.name => Seq(buildProducedArtifact := (packagedArtifact in webappProductionPackage).value)
  }
}
deploymentSettings(mapDev)
deploymentSettings(webappSettings)

localSettings
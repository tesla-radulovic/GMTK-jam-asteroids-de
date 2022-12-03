import mill._, scalalib._, scalajslib._
/*
val crossMatrix = for {
  crossVersion <- Seq("2.13.1")
  platform <- Seq("jvm", "js")
} yield (crossVersion, platform)

object digbuild extends mill.Cross[DBModule](crossMatrix:_*)
class DBModule(crossVersion: String, platform: String) extends Module {
  def suffix = T { crossVersion + "_" + platform }
*/

object gmtk extends ScalaJSModule {
  
  def scalaVersion = "2.12.11"
  def scalaJSVersion = "0.6.33"
  def ivyDeps = Agg(
      ivy"org.scala-js::scalajs-dom::0.9.7"
    )
}

name := "Remoting"

version := "1.0"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
	"com.typesafe.akka" % "akka-actor_2.10" % "2.3.6",
	"com.typesafe.akka" % "akka-agent_2.10" % "2.3.6",
	"com.typesafe.akka" % "akka-remote_2.10" % "2.3.6"
)

name := "Project4a"

version := "1.0"

scalaVersion := "2.11.2"

libraryDependencies ++= {
    val akkaV = "2.3.6"
    val sprayV = "1.3.2"
	Seq(
		"io.spray"            %%  "spray-can"     % sprayV,
    		"io.spray"            %%  "spray-routing" % sprayV,
    		"io.spray"            %%  "spray-http" % sprayV,
    		"io.spray"            %%  "spray-util" % sprayV,
		"com.typesafe.akka" % "akka-actor_2.11" % "2.3.6",
		"com.typesafe.akka" % "akka-agent_2.11" % "2.3.6",
		"com.typesafe.akka" % "akka-remote_2.11" % "2.3.6",
		"io.spray" %%  "spray-json" % "1.3.1"
	)
}

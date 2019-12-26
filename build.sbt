name := "LearningScala"

version := "0.1"

scalaVersion := "2.12.6"

resolvers += "akka" at "http://repo.akka.io/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.19",
//  "com.typesafe.akka" %% "akka-slf4j" % "2.6.1",
  "com.typesafe.akka" %% "akka-protobuf" % "2.5.19",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.5.19",
  "com.typesafe.akka" %% "akka-stream" % "2.5.19",
  "com.typesafe.akka" %% "akka-http"   % "10.1.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.6"

)





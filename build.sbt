
name := "MTProtoServer"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= {
  val akkaV             = "2.5.0"
  val akkaHttpV         = "10.0.7"

  Seq(
    "com.typesafe.akka"          %% "akka-http"                           % akkaHttpV,
    "com.typesafe.akka"          %% "akka-persistence"                    % akkaV,
    "com.typesafe.akka"          %% "akka-stream"                         % akkaV,
    "com.typesafe.akka"          %% "akka-http-spray-json"                % akkaHttpV,
    "com.typesafe.akka"          %% "akka-testkit"                        % akkaV % "test"
  )
}
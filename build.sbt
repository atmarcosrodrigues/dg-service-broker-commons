name := "service-broker-commons"

version := "0.0.3"

// organization name (e.g., the package name of the project)
organization := "com.silibrina.tecnova.commons"

// project description
description := "Project focused on the reusable parts, extensions and plugins of the service-broker"

// Enables publishing to maven repo
publishMavenStyle := true

// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

// some dependencies uses scala, we force a version here to avoid warnings
scalaVersion := "2.11.6"

lazy val commons = project in file(".")

// http://mvnrepository.com/artifact/com.typesafe.play/play_2.11
libraryDependencies += "com.typesafe.play" % "play_2.11" % "2.4.6"

// http://mvnrepository.com/artifact/ch.qos.logback/logback-classic
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

// http://mvnrepository.com/artifact/junit/junit
libraryDependencies += "junit" % "junit" % "4.12" % "test"

// http://mvnrepository.com/artifact/org.reflections/reflections
libraryDependencies += "org.reflections" % "reflections" % "0.9.10"

libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"

// http://mvnrepository.com/artifact/org.jongo/jongo
libraryDependencies += "org.jongo" % "jongo" % "1.3.0"

// http://mvnrepository.com/artifact/org.mongodb/mongo-java-driver
libraryDependencies += "org.mongodb" % "mongo-java-driver" % "3.2.2"

// https://mvnrepository.com/artifact/commons-configuration/commons-configuration
libraryDependencies += "commons-configuration" % "commons-configuration" % "1.10"

// https://mvnrepository.com/artifact/com.rabbitmq/amqp-client
libraryDependencies += "com.rabbitmq" % "amqp-client" % "3.6.4"

// https://mvnrepository.com/artifact/org.apache.tika/tika-core
libraryDependencies += "org.apache.tika" % "tika-core" % "1.13"

// https://mvnrepository.com/artifact/org.apache.tika/tika-parsers
libraryDependencies += "org.apache.tika" % "tika-parsers" % "1.13"

// https://mvnrepository.com/artifact/com.novocode/junit-interface
libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"

// https://mvnrepository.com/artifact/org.pacesys/openstack4j-core
libraryDependencies += "org.pacesys" % "openstack4j-core" % "3.0.3"

// https://mvnrepository.com/artifact/org.pacesys.openstack4j.connectors/openstack4j-httpclient
libraryDependencies += "org.pacesys.openstack4j.connectors" % "openstack4j-httpclient" % "3.0.3"

// https://mvnrepository.com/artifact/com.typesafe/config
libraryDependencies += "com.typesafe" % "config" % "1.3.1"


fork in Test := true // allow to apply extra setting to Test

concurrentRestrictions in Global += Tags.limit(Tags.Test, 1) // No parallelism running tests.

javaOptions in Test += "-Dconfig.resource=default.conf" // apply extra setting here


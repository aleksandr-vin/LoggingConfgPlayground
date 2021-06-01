package com.example.foo

import ch.qos.logback.classic.{Level, Logger, LoggerContext}
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import org.slf4j.{LoggerFactory, Marker, MarkerFactory}

import scala.jdk.CollectionConverters._

class Confidential {

  private val appender = {
    val lc = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    val name = "CONFIDENTIAL"
    val ple = new PatternLayoutEncoder
    ple.setPattern(s"!!!${name}!!! %logger - %message%n")
    ple.setContext(lc)
    ple.start()
    val appender = new ConsoleAppender[ILoggingEvent]
    appender.setName(name)
    appender.setEncoder(ple)
    appender.setContext(lc)
    appender.start()
    appender
  }

  private def secureAppender(l: Logger): Unit = {
    l.addAppender(appender)
  }

  /////// Check if we will inherit new appenders
  private def checkInheritance(l: Logger): Unit = {
    val lc = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    val name = "BAD-BAD-BAD"
    val ple = new PatternLayoutEncoder
    ple.setPattern(s"xxx${name}xxx %logger - %date - %level - %marker - %message%n")
    ple.setContext(lc)
    ple.start()
    val appender = new ConsoleAppender[ILoggingEvent]
    appender.setName(name)
    appender.setEncoder(ple)
    appender.setContext(lc)
    appender.start()

    val lp: Logger = LoggerFactory.getLogger("com.example.foo").asInstanceOf[Logger]
    lp.addAppender(appender)

    val appenders = l.iteratorForAppenders().asScala.toSet
    println(s";;; Effective appenders: ${appenders}")
    require(!appenders.exists(_.getName.compare(name) == 0), "Should not inherit appenders")
  }

  private val confidentialLogger = {
    /// (1) We can suffix logger name with random string to prevent configuring it through file,
    /// even can randomize the whole name
    val l: Logger = LoggerFactory.getLogger("com.example.foo.Confidential").asInstanceOf[Logger]

    l.setLevel(Level.INFO)

    //l.detachAndStopAllAppenders() // don't need this

    /// (2) Detaching current appenders of the logger
    l.iteratorForAppenders().asScala.foreach(l.detachAppender)
    l.setAdditive(false)

    val lc = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]

    val allAppenders = lc.getLoggerList.asScala.flatMap(_.iteratorForAppenders().asScala).toSet
    println(s";;; All appenders: $allAppenders")

    secureAppender(l)
    checkInheritance(l)

    l
  }

  private def verify(l: Logger): Unit = {
    val appenders = l.iteratorForAppenders().asScala.toSet
    require(!l.isAdditive, "Confidential logger MUST NOT be additive!")
    require(appenders == Set(appender), "Appender of the confidential logger is tampered!")
  }

  private val confidentialC3Marker: Marker = MarkerFactory.getMarker("C3")

  def message(format: String, args: Any*): Unit = {
    verify(confidentialLogger)
    confidentialLogger.info(confidentialC3Marker, format, args:_*)
  }
}

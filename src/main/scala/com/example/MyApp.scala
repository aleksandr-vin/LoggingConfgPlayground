package com.example

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.{Level, Logger, LoggerContext}
import ch.qos.logback.core.ConsoleAppender
import com.example.foo.Confidential
import org.slf4j.LoggerFactory

object MyApp extends App {

  val logger = LoggerFactory.getLogger("com.example.MyApp")

  println("; MyApp in stdout")
  logger.info("MyApp is here")

  val x = new Confidential()
  x.message("Some private info {} !!!!!!!!!!!!!", "Bob")

  val y = new Foo()

  // Now tampering the logger programmatically
  val l: Logger = LoggerFactory.getLogger("com.example.foo.Confidential").asInstanceOf[Logger]
  l.setAdditive(true)
  try {
    // How about sending some more confidential info
    x.message("Some private info {} !!!!!!!!!!!!!", "Jay")
  } catch {
    case _: IllegalArgumentException =>
      logger.info("Confidential logger additivity tampering detected")
  }
  l.setAdditive(false)

  // Now tampering the logger appender
  val lc = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
  val name = "HACKED"
  val ple = new PatternLayoutEncoder
  ple.setPattern(s"xxx${name}xxx %logger - %date - %level - %marker - %message%n")
  ple.setContext(lc)
  ple.start()
  val appender = new ConsoleAppender[ILoggingEvent]
  appender.setName(name)
  appender.setEncoder(ple)
  appender.setContext(lc)
  appender.start()

  l.addAppender(appender)
  try {
    // How about sending some more confidential info
    x.message("Some private info {} !!!!!!!!!!!!!", "Batman")
  } catch {
    case _: IllegalArgumentException =>
      logger.info("Confidential logger appender tampering detected")
  }
}

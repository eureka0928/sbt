/*
 * sbt
 * Copyright 2023, Scala center
 * Copyright 2011 - 2022, Lightbend, Inc.
 * Copyright 2008 - 2010, Mark Harrah
 * Licensed under Apache License 2.0 (see LICENSE)
 */

package sbt.internal.util

import java.io.{ ByteArrayOutputStream, InputStream }
import java.nio.channels.ClosedChannelException
import verify.BasicTestSuite

object TerminalSpec extends BasicTestSuite:
  private def createTerminal(out: ByteArrayOutputStream = new ByteArrayOutputStream()) =
    new Terminal.TerminalImpl(
      new Terminal.WriteableInputStream(new InputStream { def read() = -1 }, "test"),
      out,
      new ByteArrayOutputStream(),
      "test"
    ):
      private[sbt] def getSizeImpl: (Int, Int) = (80, 24)
      override def isColorEnabled: Boolean = false
      override def isAnsiSupported: Boolean = false
      override private[sbt] def progressState: ProgressState = new ProgressState(1)
      override def isSuccessEnabled: Boolean = true
      override def isSupershellEnabled: Boolean = false
      override def isEchoEnabled: Boolean = true
      override def setEchoEnabled(toggle: Boolean): Unit = ()
      override def getBooleanCapability(capability: String): Boolean = false
      override def getNumericCapability(capability: String): Integer = null
      override def getStringCapability(capability: String): String = null
      override private[sbt] def getAttributes: Map[String, String] = Map.empty
      override private[sbt] def setAttributes(attributes: Map[String, String]): Unit = ()
      override private[sbt] def setSize(width: Int, height: Int): Unit = ()
      override private[sbt] def enterRawMode(): Unit = ()
      override private[sbt] def exitRawMode(): Unit = ()

  test("closed TerminalImpl outputStream write(byte[]) should throw ClosedChannelException"):
    val term = createTerminal()
    term.close()
    try
      term.outputStream.write("hello".getBytes("UTF-8"))
      fail("Expected ClosedChannelException")
    catch case _: ClosedChannelException => () // expected

  test("closed TerminalImpl outputStream write(int) should throw ClosedChannelException"):
    val term = createTerminal()
    term.close()
    try
      term.outputStream.write(65)
      fail("Expected ClosedChannelException")
    catch case _: ClosedChannelException => () // expected

  test("safeTerminalOut should not throw for closed terminal"):
    val term = createTerminal()
    val safeOut = ConsoleOut.safeTerminalOut(term)
    term.close()
    // safeTerminalOut catches ClosedChannelException, so this should not throw.
    // This is the same mechanism needed when logging during shutdown —
    // e.g., when cancelAndShutdown() logs through a terminal that was closed concurrently.
    safeOut.println("message during shutdown")

  test("proxyOutputStream should not throw when active terminal is closed"):
    val term = createTerminal()
    val prev = Terminal.set(term)
    try
      term.close()
      // proxyOutputStream delegates to activeTerminal.get().outputStream.
      // When the active terminal is closed, TerminalImpl.throwIfClosed raises
      // ClosedChannelException. The proxyOutputStream must catch this to prevent
      // stack traces during shutdown (e.g., when cancelAndShutdown() calls
      // println through System.out which is backed by proxyOutputStream).
      Terminal.proxyOutputStream.write("shutdown message".getBytes("UTF-8"))
      Terminal.proxyOutputStream.write(10)
      Terminal.proxyOutputStream.flush()
    finally Terminal.set(prev)

end TerminalSpec

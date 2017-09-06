package com.bytepace.server.messages

/**
  * Created by vital on 28.08.2017.
  */
sealed trait CipherMessages
case class GenerateKeys(username: String) extends CipherMessages

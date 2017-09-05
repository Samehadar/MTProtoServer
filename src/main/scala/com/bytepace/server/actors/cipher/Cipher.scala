package com.bytepace.server.actors.cipher

import akka.actor.{Actor, ActorLogging, Props}
import com.bytepace.server.messages.{CipherKeys, GenerateKeys}

/**
  * Created by vital on 28.08.2017.
  */
class Cipher() extends Actor with ActorLogging {


  override def receive: Receive = {
    case GenerateKeys =>
      log.info("Receive GenerateKeys message")
      sender() ! generateKeys()
  }

  def generateKeys(): CipherKeys = {
    CipherKeys("key p", "key g", "key r") //todo:: set keys here
  }
}

object Cipher {
  def props: Props = Props(new Cipher)
}
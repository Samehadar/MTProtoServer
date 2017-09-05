package com.bytepace.server.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.util.ByteString
import com.bytepace.server.actors.cipher.Cipher
import com.bytepace.server.messages.MyJsonProtocol._
import com.bytepace.server.messages._
import spray.json._

/**
  * Created by vital on 28.08.2017.
  */
class Pipeline extends Actor with ActorLogging {

  def receive = {
    //from TcpHandler messages
    case Login(user) =>

    case Logout(user) =>

    case mes @ GetKeys() =>
      log.info("Receive message " + mes)
      context.actorOf(Cipher.props, "cipher") ! GenerateKeys

    case SendMessage(from, to, mes) =>

    case GetConnectedUsers() =>

    case RestartServer() =>


    case keys @ CipherKeys(p,g,r) =>
      log.info("Receive message " + keys)
      context.parent ! Send(ByteString(keys.toJson.toString).toArray)
  }

}

object Pipeline {
  def props: Props = Props(new Pipeline())
}
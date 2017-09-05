package com.bytepace.server.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.ByteString
import com.bytepace.server.actors.cipher.Cipher
import com.bytepace.server.messages.MyJsonProtocol._
import com.bytepace.server.messages._
import spray.json._

/**
  * Created by vital on 28.08.2017.
  */
class Pipeline extends Actor with ActorLogging {

  // Context actors
  private val cipherActor = context.actorOf(Cipher.props, "cipher")

  def receive: Receive = {
    //from TcpHandler messages
    case message @ Login(user) =>
      sender forward message

    case message @ Logout(user) =>
      sender forward message

    case StartChatWith(friendName, openKey) =>
      //todo::

    case msg @ GetKeys() =>
      log.info("Receive message " + msg)
      cipherActor ! GenerateKeys

    case SendMessage(from, to, msg) =>
    //todo::

    case msg @ GetConnectedUsers() =>
      log.info("Receive message " + msg)
      context.actorSelection("akka://server/user/front/sessionManager") ! msg

    case RestartServer() =>
    //todo::



    case keys @ CipherKeys(p,g,r) =>
      log.info("Receive message " + keys)
      context.parent ! Send(ByteString(keys.toJson.toString).toArray)

    case users @ Users(_) =>
      context.parent ! Send(ByteString(users.toJson.toString).toArray)

  }

}

object Pipeline {
  def props: Props = Props(new Pipeline())
}
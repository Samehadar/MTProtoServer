package com.bytepace.server.actors

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.util.{ByteString, Timeout}
import com.bytepace.server.messages.MyJsonProtocol._
import com.bytepace.server.messages._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by vital on 28.08.2017.
  */
class Pipeline extends Actor with ActorLogging {

  def receive: Receive = {
    //from TcpHandler messages
    case msg @ Login(user) =>
      log.info("Receive message " + msg)
      context.actorSelection("akka://server/user/front/keyStore") ! GenerateKeys(user)

    case message @ Logout(user) =>
      sender forward message

    case StartChatWith(friendName, openKey) =>
      implicit val timeout = Timeout(5.second)
      (context.actorSelection("akka://server/user/front/sessionManager") ? GetSession(friendName))
        .mapTo[UserSession]
        .foreach{ userSession =>
          userSession.session.foreach(_ ! Send(ByteString(
            ChatRequest("startChatWith", friendName, openKey
            ).toJson.toString).toArray)
          )
        }

    case SendMessage(from, to, msg) =>
    //todo::

    case msg @ GetConnectedUsers() =>
      log.info("Receive message " + msg)
      context.actorSelection("akka://server/user/front/sessionManager") ! msg

    case RestartServer() =>
    //todo::



    case keys @ CipherKeys(username, p, g, r) =>
      log.info("Receive message " + keys)
      context.parent forward keys

    case users @ Users(_, _) =>
      context.parent ! Send(ByteString(users.toJson.toString).toArray)

  }

}

object Pipeline {
  def props: Props = Props(new Pipeline())
}
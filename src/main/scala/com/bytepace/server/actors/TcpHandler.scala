package com.bytepace.server.actors

import akka.pattern.ask
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.Tcp
import akka.io.Tcp.{Close, PeerClosed, Received}
import akka.util.{ByteString, Timeout}
import com.bytepace.server.messages._
import spray.json._

import MyJsonProtocol._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by vital on 28.08.2017.
  */
class TcpHandler(connection: ActorRef, session: ActorRef, sessionManager: ActorRef) extends Actor with ActorLogging {
  implicit val timeout = Timeout(5.second)

  // sign death pact
  context watch connection
  // watch so we can Close
  context watch session

  private val pipeline = context.actorOf(Pipeline.props, "pipeline")

  def receive: Receive = {
    case Received(data) =>
      receiveData(data)
    case PeerClosed     => context stop self
    case evt: Tcp.Event => //todo :: nothing?
    case mes @ Send(data) =>
      log.info("Response from pipelineActor: " + ByteString(data).decodeString("US-ASCII"))
      session ! mes

    case Login(username) =>
      (sessionManager ? AddSession(username, self))
        .mapTo[SessionManagerResponse]
        .foreach{ response =>
          log.info("Response from SessionManager: " + response.response)
          session ! Send(ByteString(response.toJson.toString).toArray)
        }

    case Logout(username) =>
      (sessionManager ? RemoveSession(username))
        .mapTo[SessionManagerResponse]
        .foreach{ response =>
          log.info("Response from SessionManager: " + response.response)
          session ! Send(ByteString(response.toJson.toString).toArray)
          connection ! Close
        }
  }

  // ----- actions -----
  def receiveData(data: ByteString): Unit = {
    //Распаковываем сообщение, отправляем по назначению
    val json = data.utf8String.parseJson.asJsObject

    val event: Seq[Event] = json.getFields("type") map { x =>
      x.toString.replace("\"", "") match {
        case "login" =>
          json.convertTo[Login]
        case "logout" =>
          json.convertTo[Logout]
        case "startChat" =>
          json.convertTo[StartChatWith]
        case "getConnectedUsers" =>
          json.convertTo[GetConnectedUsers]
        case "getKeys" =>
          json.convertTo[GetKeys]
        case "sendMessage" =>
          json.convertTo[SendMessage]
        case "restartServer" =>
          json.convertTo[RestartServer]
      }
    }
    log.info("Event type is " + event.head.toString)

    pipeline ! event.head
  }
}

object TcpHandler {
  //todo:: remove connection if it's unused
  def props(connection: ActorRef, session: ActorRef, sessionManager: ActorRef): Props = Props(new TcpHandler(connection, session, sessionManager))
}

package com.bytepace.server.actors

import akka.pattern.ask
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.Tcp
import akka.io.Tcp.{PeerClosed, Received, Write}
import akka.util.{ByteString, Timeout}
import com.bytepace.server.messages._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by vital on 28.08.2017.
  */
class TcpHandler(connection: ActorRef, session: ActorRef) extends Actor with ActorLogging {

  def receive = {
    case Received(data) =>
      receiveData(data)
    case PeerClosed     => context stop self
    case evt: Tcp.Event => //todo :: nothing?
    case mes @ Send(data) =>
      log.info("Response from pipelineActor: " + ByteString(data).decodeString("US-ASCII"))
      session ! mes

//    case response @ Send(data) => session forward response
  }

  // ----- actions -----
  def receiveData(data: ByteString): Unit = {
    import MyJsonProtocol._
    //Распаковываем сообщение, отправляем по назначению
    val json = data.utf8String.parseJson.asJsObject

    val event: Seq[Event] = json.getFields("type") map { x =>
      x.toString.replace("\"", "") match {
        case "login" =>
          json.convertTo[Login]
        case "logout" =>
          json.convertTo[Logout]
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
    implicit val timeout = Timeout(5.second)

    context.actorOf(Pipeline.props, "pipeline") ! event.head
  }
}

object TcpHandler {
  //todo:: remove connection if it's unused
  def props(connection: ActorRef, session: ActorRef): Props = Props(new TcpHandler(connection, session))
}

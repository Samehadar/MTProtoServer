package com.bytepace.server.messages

import akka.actor.ActorRef

/**
  * SessionManager messages
  */
trait SessionMessage
case class AddSession(username: String, actorHandler: ActorRef) extends SessionMessage
case class RemoveSession(username: String) extends SessionMessage
case class GetSession(username: String) extends SessionMessage
case class UserSession(session: Option[ActorRef])
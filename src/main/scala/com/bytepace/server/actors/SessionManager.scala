package com.bytepace.server.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.bytepace.server.messages.{AddSession, GetSession, RemoveSession, SessionManagerResponse}

/**
  * Actor that contains username with session actorRef
  */
class SessionManager extends Actor with ActorLogging {

  private var sessions = Map.empty[String, ActorRef]

  override def receive: Receive = {

    case AddSession(username, actorHandler) =>
      sessions += (username -> actorHandler)
      println("Map: ")
      println(sessions.mkString("\n"))
      sender ! SessionManagerResponse("Session " + actorHandler.path + " has been added")

    case RemoveSession(username) =>
      sessions = sessions.filterNot(_._1 == username)
      println("Map: ")
      println(sessions.mkString("\n"))
      sender ! SessionManagerResponse("Session for user [" + username + "] has been removed")

    case GetSession(username) =>
      val session = sessions get username
      sender ! session

    case _ =>
  }
}


object SessionManager {
  def props: Props = Props(new SessionManager())
}


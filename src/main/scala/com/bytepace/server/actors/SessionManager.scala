package com.bytepace.server.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.bytepace.server.messages._

/**
  * Actor that contains username with session actorRef
  */
class SessionManager extends Actor with ActorLogging {

  private var sessions = Map.empty[String, ActorRef]

  override def receive: Receive = {

    case AddSession(username, actorHandler) =>
      sessions += (username -> actorHandler)
      printSessions()
      log.info("Session " + actorHandler.path + " has been added")
      sender ! SessionManagerResponse("login", "ok")

    case RemoveSession(username) =>
      sessions = sessions.filterNot(_._1 == username)
      printSessions()
      log.info("Session for user [" + username + "] has been removed")
      sender ! SessionManagerResponse("logout", "ok")

    case GetSession(username) =>
      val session = sessions get username
      sender ! session

    case GetConnectedUsers() =>
      printSessions()
      sender ! Users("getConnectedUsers", sessions.keys.toList)

    case _ =>
  }

  private def printSessions() = {
    println("Map: ")
    println(sessions.mkString("\n"))
  }
}


object SessionManager {
  def props: Props = Props(new SessionManager())
}


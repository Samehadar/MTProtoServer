package com.bytepace.server

import akka.actor.{ActorSystem, Props}
import com.bytepace.server.actors.ServerTCP

/**
  * Created by vital on 28.08.2017.
  */
object Main extends App {
  // create the actor system and actors
  val actorSystem = ActorSystem("server")

  val actorNet = actorSystem.actorOf(ServerTCP.props("127.0.0.1", 8888), "front")
}

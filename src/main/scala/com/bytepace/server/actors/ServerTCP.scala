package com.bytepace.server.actors

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, Props}
import akka.io.Tcp._
import akka.io.{IO, Tcp}

/**
  * Created by vital on 28.08.2017.
  */
class ServerTCP(address: String, port: Int) extends Actor with ActorLogging {
  var idCounter = 0L

  override def preStart() {
    log.info( "Starting tcp net server" )

    import context.system
    val opts = List(SO.KeepAlive(on = true), SO.TcpNoDelay(on = true))
    IO(Tcp) ! Bind(self, new InetSocketAddress(address, port), options = opts )
  }

  override def receive = {
    case b @ Bound(localAddress) ⇒
    // do some logging or setup ...

    case CommandFailed(_: Bind) ⇒
      log.info("Command failed tcp server")
      context stop self

    case c @ Connected(remote, local) ⇒
      log.info("New incoming tcp connection on server")
      createSession(remote, local)
  }

  private def createSession(remote: InetSocketAddress, local: InetSocketAddress) = {
    val connection = sender

    val sessionActorProps = Props( new Session( idCounter, connection, remote, local ))
    val session = context.actorOf(sessionActorProps , remote.getHostName + ":" + remote.getPort)

    val tcpHandler = context.actorOf(TcpHandler.props(connection, session), "TCPHandler")

    connection ! Register(tcpHandler)
  }
}

object ServerTCP {
  // safe constructor
  def props(address: String, port: Int) = Props(new ServerTCP(address, port))
}
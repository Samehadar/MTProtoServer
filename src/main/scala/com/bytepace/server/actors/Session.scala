package com.bytepace.server.actors

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef}
import akka.event.Logging
import akka.io.Tcp
import akka.io.Tcp.Write
import akka.util.ByteString

// ----- Класс-сообщение реализующий команду отправки на клиент
case class Send( data: Array[Byte] )

class Session(val id: Long,
              connect: ActorRef,
             remote: InetSocketAddress,
             local: InetSocketAddress) extends Actor {

  val log = Logging(context.system, this)

  override def preStart() {
    // initialization code
    log.info("Session start: {}", toString)
  }

  override def receive = {
//    case init.Event(data) ⇒ receiveData(data)  // Обрабатываем получение сообщения
    case Send(data) ⇒ sendData(data) // Обрабатываем отправку сообщения
    case _: Tcp.ConnectionClosed ⇒ Closed()
    case _ ⇒ log.info( "unknown message" )
  }

  override def postStop() {
    // clean up resources
    log.info("Session stop: {}", toString)
  }



  def sendData(data: Array[Byte] ) {
    log.info("Response: " + ByteString(data).decodeString("US-ASCII"))
    val msg: ByteString = ByteString(data) // Упаковываем сообщение
    connect ! Write( msg ) // отправляем
  }

  def Closed(){
    context stop self
  }

//  // ----- override -----
//  override def toString =
//    "{ Id: %d, Type:TCP, Connected: %s, IP: %s:%s }".format ( id, connected, clientIpAddress, clientPort )
}
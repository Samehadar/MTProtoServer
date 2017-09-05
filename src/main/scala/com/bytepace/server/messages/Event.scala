package com.bytepace.server.messages

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by vital on 28.08.2017.
  */
sealed trait Event
case class Login(user: String) extends Event
case class Logout(user: String) extends Event
case class GetKeys() extends Event
case class SendMessage(from: String, to: String, message: String) extends Event
case class GetConnectedUsers() extends Event
case class RestartServer() extends Event

case class CipherKeys(p: String, g: String, r: String)

object MyJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val loginFormat = jsonFormat1(Login)
  implicit val logoutFormat = jsonFormat1(Logout)
  implicit val getConnectedUsersFormat = jsonFormat0(GetConnectedUsers)
  implicit val sendMessageFormat = jsonFormat3(SendMessage)
  implicit val getKeysFormat = jsonFormat0(GetKeys)
  implicit val restartFormat = jsonFormat0(RestartServer)
  implicit val cipherKeysFormat = jsonFormat3(CipherKeys)
}
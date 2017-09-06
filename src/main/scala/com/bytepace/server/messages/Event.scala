package com.bytepace.server.messages

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  *
  */
sealed trait Event
// Incoming messages from client-side
case class Login(username: String) extends Event
case class Logout(username: String) extends Event
case class StartChatWith(`with`: String, key: String) extends Event
case class GetKeys() extends Event
case class SendMessage(from: String, to: String, message: String) extends Event
case class GetConnectedUsers() extends Event
case class RestartServer() extends Event

// Outgoing message
case class ChatRequest(`type`: String, `with`: String, key: String)

// Inside server messages
case class CipherKeys(`type`: String, p: String, g: String, r: String)
case class SessionManagerResponse(`type`: String, response: String)
case class Users(`type`: String, users: List[String])


object MyJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val loginFormat = jsonFormat1(Login)
  implicit val logoutFormat = jsonFormat1(Logout)
  implicit val startChatWithFormat = jsonFormat2(StartChatWith)
  implicit val getConnectedUsersFormat = jsonFormat0(GetConnectedUsers)
  implicit val sendMessageFormat = jsonFormat3(SendMessage)
  implicit val getKeysFormat = jsonFormat0(GetKeys)
  implicit val restartFormat = jsonFormat0(RestartServer)

  implicit val cipherKeysFormat = jsonFormat4(CipherKeys)
  implicit val sessionManagerResponseFormat = jsonFormat2(SessionManagerResponse)
  implicit val usersFormat = jsonFormat2(Users)

  implicit val chatRequestFormat = jsonFormat3(ChatRequest)
}
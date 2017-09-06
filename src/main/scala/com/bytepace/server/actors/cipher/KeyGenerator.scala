package com.bytepace.server.actors.cipher

import java.util.Random
import java.math.BigInteger

import akka.actor.{Actor, ActorLogging, Props}
import com.bytepace.server.messages.{CipherKeys, GenerateKeys}
import com.bytepace.server.utils.ExhaustibleStream

class KeyGenerator() extends Actor with ActorLogging {

  private val keyStore = Key.generateKeyStream()

  override def receive: Receive = {
    case GenerateKeys(username) =>
      log.info("Receive GenerateKeys message")
      sender() ! generateKeys(username)
  }

  def generateKeys(username: String): CipherKeys = {
    //todo:: add generating key when keyStore is empty
    val key = keyStore.drawNextOne()

    CipherKeys(username, key.generator.toString, key.server.toByteArray.mkString(""), key.prime.toByteArray.mkString(""))
  }
}

object KeyGenerator {
  def props: Props = Props(new KeyGenerator)
}



case class Key(rnd: Random) {
  val generator: Int = 3 + rnd.nextInt(5)
  val server: BigInteger = new BigInteger(2047, rnd)
  val prime: BigInteger = new BigInteger(2048, 99, rnd)// =================== todo:: change to generate security prime number

  override def toString: String = {
    "g = " + generator +
      "\nserver key = " + server +
      "\nprime = " + prime + '\n'
  }
}


object Key {
  val rnd = new Random()

  def apply(): Key = {
    new Key(rnd)
  }

  def generateKeyStream(): ExhaustibleStream[Key] = {
    var keyStore = List[Key]()
    for (i <- 1 to 5) {
      val newKey = Key()
      print("Generated key: " + newKey.toString)
      keyStore = newKey :: keyStore
    }
    ExhaustibleStream[Key](keyStore: _*)
  }
}

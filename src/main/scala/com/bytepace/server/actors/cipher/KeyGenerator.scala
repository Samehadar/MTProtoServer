package com.bytepace.server.actors.cipher

import java.util.Random
import java.math.BigInteger

import akka.actor.{Actor, ActorLogging, Props}
import com.bytepace.server.messages.{CipherKeys, GenerateKeys}
import com.bytepace.server.utils.ExhaustibleStream

/**
  * Created by vital on 28.08.2017.
  */
case class Key(rnd: Random) {
  val generator: Int = 3
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

class KeyGenerator() extends Actor with ActorLogging {

  private val keyStore = Key.generateKeyStream()

  override def receive: Receive = {
    case GenerateKeys =>
      log.info("Receive GenerateKeys message")
      sender() ! generateKeys()
  }

  def generateKeys(): CipherKeys = {
    val key = keyStore.drawNextOne()

    CipherKeys("getKeys", key.generator.toString, key.server.toByteArray.mkString(""), key.prime.toByteArray.mkString(""))
  }
}

object KeyGenerator {
  def props: Props = Props(new KeyGenerator)
}
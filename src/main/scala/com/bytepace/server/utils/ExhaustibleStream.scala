package com.bytepace.server.utils

import scala.collection.SortedSet


class ExhaustibleStream[A](elements: A*) {
  private var stored = elements

  def drawNext(count: Int): Seq[A] = {
    val (taken, remaining) = stored.splitAt(count)
    stored = remaining
    taken
  }
  def drawNextOne(): A = {
    val (taken, remaining) = stored.splitAt(1)
    stored = remaining
    taken.head
  }

  def rest: Seq[A] = stored
  def toSortedSet(implicit ordering: Ordering[A]): SortedSet[A] = {
    var slots = SortedSet.empty[A] ++ stored
    slots
  }
  def diff(elements: A*): ExhaustibleStream[A] = {
    ExhaustibleStream(stored diff elements :_*)
  }
}

object ExhaustibleStream {
  def apply[A](elements: A*): ExhaustibleStream[A] = new ExhaustibleStream[A](elements:_*)
}

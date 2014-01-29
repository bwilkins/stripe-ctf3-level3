package com.stripe.ctf.instantcodesearch

import java.io._
import java.nio.file._

import com.twitter.concurrent.Broker

import scala.collection.mutable.HashMap
import scala.collection.mutable.HashMap._
import scala.collection.mutable.MutableList
import scala.collection.mutable.MutableList._

abstract class SearchResult
case class Match(path: String, line: Int) extends SearchResult
case class Done() extends SearchResult

class Searcher(index : Index)  {
  val root = FileSystems.getDefault().getPath(index.path)

  def search(needle : String, b : Broker[SearchResult]) = {
    index.lines.foreach {
      case (line_key, line) =>
      if (line.contains(needle)) {
        b !! new Match(line_key._1, line_key._2);
      }
    }

    b !! new Done()
  }
}

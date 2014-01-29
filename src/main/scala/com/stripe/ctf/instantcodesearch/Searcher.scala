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
    val matches = HashMap[(String, Int), MutableList[String]]()
    val tris = needle.sliding(3).toList
    tris.foreach {
      tri => getMatches(tri).foreach {
        m => if (matches.contains(m)) {
            if (!matches(m).contains(tri)) {
              matches(m) += tri
            }
          } else {
            matches(m) = MutableList(tri)
          }
      }
    }

    matches.foreach {
      case (key, tris_found) => // if (tris.forall(tris_found.contains)) {
        if (checkMatch(needle, key)) {
          b !! new Match(key._1, key._2);
        }
      // }
    }

    b !! new Done()
  }

  def getMatches(needle: String) : Iterable[(String, Int)] = {
    try {
        if (index.words.contains(needle)) {
          return index.words(needle)
        }
      } catch {
        case e: IOException => {
          return Nil
        }
      }
    return Nil
  }

  def checkMatch(needle: String, at: (String, Int)) : Boolean = {
    if (index.lines.contains(at)) {
      return index.lines(at).contains(needle)
    }
    return false
  }
}

package com.stripe.ctf.instantcodesearch

import java.io._
import scala.collection.mutable.Map
import scala.collection.mutable.Map._
import scala.collection.mutable.LinkedList
import scala.collection.mutable.LinkedList._

class Index(repoPath: String) {
  val words = Map[String, LinkedList[(String, Int)]]()

  def path() = repoPath

  def addFile(file: String, text: String) = {
    text.linesIterator.zipWithIndex.foreach {
      case (line, line_number) => line.split(" ").foreach {
        word => if (words.contains(word)) {
            words(word).append(LinkedList[(String, Int)]((file, line_number+1)))
          } else {
            words += (word -> LinkedList[(String, Int)]((file, line_number+1)))
          }
      }
    }
  }
}


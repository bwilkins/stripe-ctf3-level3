package com.stripe.ctf.instantcodesearch

import java.io._
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashMap._
import scala.collection.mutable.MutableList
import scala.collection.mutable.MutableList._

class Index(repoPath: String) {
  val lines = HashMap[(String, Int), String]()
  val words = HashMap[String, MutableList[(String, Int)]]()

  def path() = repoPath

  def addFile(file: String, text: String) = {
    text.lines.zipWithIndex.foreach {
      case (line, line_number) =>
        lines( (file, line_number+1) ) = line
        line.split(" ").foreach {
        word => word.sliding(3).foreach {
          tri => if (!words.contains(tri)) {
            words(tri) = new MutableList()
          }
          words(tri) += ((file, line_number+1))
        }
      }
    }
  }
}


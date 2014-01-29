package com.stripe.ctf.instantcodesearch

import java.io._
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashMap._
import scala.collection.mutable.MutableList
import scala.collection.mutable.MutableList._

class Index(repoPath: String) {
  val lines = HashMap[(String, Int), String]()

  def path() = repoPath

  def addFile(file: String, text: String) = {
    text.lines.zipWithIndex.foreach {
      case (line, line_number) => lines( (file, line_number+1) ) = line
    }
  }
}


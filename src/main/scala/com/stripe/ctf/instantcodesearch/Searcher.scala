package com.stripe.ctf.instantcodesearch

import java.io._
import java.nio.file._

import com.twitter.concurrent.Broker

abstract class SearchResult
case class Match(path: String, line: Int) extends SearchResult
case class Done() extends SearchResult

class Searcher(index : Index)  {
  val root = FileSystems.getDefault().getPath(index.path)

  def search(needle : String, b : Broker[SearchResult]) = {
    for(m <- fetchResult(needle)) {
      b !! m
    }

    b !! new Done()
  }

  def fetchResult(needle: String) : Iterable[SearchResult] = {
    try {
        if (index.words.contains(needle)) {
          return index.words(needle).
                 map { case (l, n) => new Match(l, n) }
        }
      } catch {
        case e: IOException => {
          return Nil
        }
      }
    return Nil
  }
}

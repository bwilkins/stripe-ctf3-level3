package com.stripe.ctf.instantcodesearch

import java.nio.file._
import com.twitter.util.{Future, Promise, FuturePool}
import com.twitter.concurrent.Broker
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpResponseStatus}

class SearchServer(port : Int, id : Int) extends AbstractSearchServer(port, id) {
  val IndexPath = "instantcodesearch-" + id + ".index"
  case class Query(q : String, broker : Broker[SearchResult])
  val root = FileSystems.getDefault().getPath(IndexPath)
  var index : Index = new Index(root.toAbsolutePath.toString)
  val indexer = new Indexer(index, id)
  lazy val searcher = new Searcher(index)
  @volatile var indexed = false

  override def healthcheck() = {
    Future.value(successResponse())
  }

  override def isIndexed() = {
    if (indexed) {
      Future.value(successResponse())
    }
    else {
      Future.value(errorResponse(HttpResponseStatus.OK, "Not indexed"))
    }
  }
  override def index(path: String) = {
    FuturePool.unboundedPool {
      System.err.println("[node #" + id + "] Indexing path: " + path)
      indexer.index(path)
      // System.err.println("[node #" + id + "] Writing index to: " + IndexPath)
      System.err.println("[node #" + id + "] Indexing complete")
      indexed = true
    }

    Future.value(successResponse())

  }

  override def query(q: String) = {
    System.err.println("[node #" + id + "] Searching for: " + q)
    handleSearch(q)
  }

  def handleSearch(q: String) = {
    val searches = new Broker[Query]()
    searches.recv foreach { q =>
      FuturePool.unboundedPool {searcher.search(q.q, q.broker)}
    }

    val matches = new Broker[SearchResult]()
    val err = new Broker[Throwable]
    searches ! new Query(q, matches)

    val promise = Promise[HttpResponse]
    var results = List[Match]()

    matches.recv foreach { m =>
      m match {
        case m : Match => results = m :: results
        case Done() => promise.setValue(querySuccessResponse(results))
      }
    }

    promise
  }
}

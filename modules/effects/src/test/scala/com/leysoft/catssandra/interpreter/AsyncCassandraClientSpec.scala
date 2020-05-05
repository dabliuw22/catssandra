package com.leysoft.catssandra.interpreter

import cats.effect.{ContextShift, IO, Timer}
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.Row
import com.leysoft.catssandra.connection.Session
import com.leysoft.catssandra.syntax._
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.ExecutionContext

protected[interpreter] final class AsyncCassandraClientSpec
    extends AsyncWordSpec {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)

  implicit val f: Row => Row = r => r

  "CassandraClient.execute() With Recursion" should {
    "Return Two Records" in {
      Cassandra
        .apply[IO](sessionRec)
        .flatMap(client => client.execute[Row](cql("SELECT * FROM test").query))
        .map(list => assert(list.size == 2))
        .unsafeToFuture
    }
  }

  "CassandraClient.execute() With Recursion" should {
    "Return One Records" in {
      Cassandra
        .apply[IO](session)
        .flatMap(client => client.execute[Row](cql("SELECT * FROM test").query))
        .map(list => assert(list.size == 1))
        .unsafeToFuture
    }
  }

  "CassandraClient.stream() With Recursion" should {
    "Return Two Records" in {
      fs2.Stream
        .eval(Cassandra.apply[IO](sessionRec))
        .flatMap(client => client.stream[Row](cql("SELECT * FROM test").query))
        .compile
        .toList
        .map(list => assert(list.size == 2))
        .unsafeToFuture
    }
  }

  "CassandraClient.stream() With Recursion" should {
    "Return One Records" in {
      fs2.Stream
        .eval(Cassandra.apply[IO](session))
        .flatMap(client => client.stream[Row](cql("SELECT * FROM test").query))
        .compile
        .toList
        .map(list => assert(list.size == 1))
        .unsafeToFuture
    }
  }

  def sessionRec: Session = new Session {
    override def cql: CqlSession = new FakeRecCqlSession
  }

  def session: Session = new Session {
    override def cql: CqlSession = new FakeCqlSession
  }
}

package com.leysoft.catssandra.interpreter

import cats.effect.IO
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.Row
import com.leysoft.catssandra.AsyncSpec
import com.leysoft.catssandra.connection.Session
import com.leysoft.catssandra.syntax._

protected[interpreter] final class AsyncCassandraClientSpec extends AsyncSpec {

  implicit val decoder: Decoder[Row] = Decoder.instance[Row](r => r)

  "CassandraClient.execute() With Recursion" should {
    "Return Two Records" in {
      Cassandra
        .apply[IO](sessionRec)
        .flatMap(client => client.execute[Row](cql"SELECT * FROM test".query))
        .map(list => assert(list.size == 2))
        .unsafeToFuture
    }
  }

  "CassandraClient.execute() With Recursion" should {
    "Return One Records" in {
      Cassandra
        .apply[IO](session)
        .flatMap(client => client.execute[Row](cql"SELECT * FROM test".query))
        .map(list => assert(list.size == 1))
        .unsafeToFuture
    }
  }

  "CassandraClient.stream() With Recursion" should {
    "Return Two Records" in {
      fs2.Stream
        .eval(Cassandra.apply[IO](sessionRec))
        .flatMap(client => client.stream[Row](cql"SELECT * FROM test".query))
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
        .flatMap(client => client.stream[Row](cql"SELECT * FROM test".query))
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

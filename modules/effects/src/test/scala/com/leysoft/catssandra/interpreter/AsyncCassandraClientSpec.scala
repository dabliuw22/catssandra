package com.leysoft.catssandra.interpreter

import cats.effect.IO
import com.leysoft.catssandra.AsyncSpec
import com.leysoft.catssandra.syntax._

protected[interpreter] final class AsyncCassandraClientSpec extends AsyncSpec {

  implicit val decoder: Decoder[String] =
    Decoder.instance[String](r => r.getString("test"))

  "CassandraClient.execute() With Recursion" should {
    "Return Two Records" in {
      Cassandra
        .apply[IO](sessionRec)
        .flatMap(
          client =>
            client.execute[String](cql("SELECT test FROM test.tests").query)
        )
        .map(list => assert(list.size == 2))
        .unsafeToFuture
    }
  }

  "CassandraClient.execute() With Recursion" should {
    "Return One Records" in {
      Cassandra
        .apply[IO](session)
        .flatMap(
          client =>
            client.execute[String](cql("SELECT test FROM test.tests").query)
        )
        .map(list => assert(list.size == 1))
        .unsafeToFuture
    }
  }

  "CassandraClient.stream() With Recursion" should {
    "Return Two Records" in {
      fs2.Stream
        .eval(Cassandra.apply[IO](sessionRec))
        .flatMap(
          client =>
            client.stream[String](cql("SELECT test FROM test.tests").query)
        )
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
        .flatMap(
          client =>
            client.stream[String](cql("SELECT test FROM test.tests").query)
        )
        .compile
        .toList
        .map(list => assert(list.size == 1))
        .unsafeToFuture
    }
  }
}

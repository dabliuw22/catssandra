package com.leysoft.catssandra.interpreter

import java.util.UUID

import cats.effect.IO
import com.leysoft.catssandra.CassandraItSpec
import com.leysoft.catssandra.syntax._

final class AsyncCassandraClientItSpec extends CassandraItSpec {

  private type TestType = (String, String)

  private implicit val d: Decoder[TestType] = Decoder.instance[TestType] {
    row =>
      (
        row.getString("id"),
        row.getString("name")
      )
  }

  private val tuple: TestType = (UUID.randomUUID.toString, "test")

  "CassandraClient.option" should {
    "Return One Record" in {
      val effect = for {
        client <- Cassandra.make[IO](session)
        result <- client.option[TestType](cql(s"""SELECT * FROM test_key.tests
               |WHERE id = '${tuple._1}'""".stripMargin).query)
        status = assert(result.contains(tuple))
      } yield status
      effect.unsafeToFuture
    }
  }

  "CassandraClient.option" should {
    "Return None Record" in {
      val effect = for {
        client <- Cassandra.make[IO](session)
        result <- client.option[TestType](
                    cql(
                      s"""SELECT * FROM test_key.tests
                      |WHERE id = '${UUID.randomUUID.toString}'""".stripMargin
                    ).query
                  )
        status = assert(result.isEmpty)
      } yield status
      effect.unsafeToFuture
    }
  }

  "CassandraClient.execute" should {
    "Return One Record" in {
      val effect = for {
        client <- Cassandra.make[IO](session)
        result <-
          client.execute[TestType](cql(s"SELECT * FROM test_key.tests").query)
        status = assert(result == List(tuple))
      } yield status
      effect.unsafeToFuture
    }
  }

  override protected def beforeAll: Unit = {
    create("""CREATE KEYSPACE test_key WITH replication = {
        |  'class': 'SimpleStrategy',
        |  'replication_factor' : 1
        |}""".stripMargin)
    create("""CREATE TABLE test_key.tests (
        |    id text PRIMARY KEY,
        |    name text
        |)""".stripMargin)
    create(s"""INSERT INTO test_key.tests(id, name)
         |VALUES ('${tuple._1}', '${tuple._2}')
         |IF NOT EXISTS""".stripMargin)
  }
}

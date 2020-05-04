package com.leysoft.catssandra

import java.util.UUID

import cats.effect.{ExitCode, IO, IOApp}
import com.datastax.oss.driver.api.core.cql.Row
import com.leysoft.catssandra.algebra.{Command, Query}
import com.leysoft.catssandra.connection.Session
import com.leysoft.catssandra.interpreter.Cassandra
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object App extends IOApp {

  val logger: Logger[IO] = Slf4jLogger.getLoggerFromName[IO]("App")

  val f: Row => (String, String, Float) =
    row =>
      (
        row.getString("id"),
        row.getString("name"),
        row.getFloat("stock")
    )

  override def run(args: List[String]): IO[ExitCode] =
    Session(keyspace = Some("test")).use { session =>
      for {
        client <- Cassandra(session)
        _ <- client
              .command(command(UUID.randomUUID().toString, "p22", 100))
        all <- client.execute(queryAll(), f)
        option <- client
                   .option(queryOne("491ae396-42e7-4483-a3ef-e729c486980f"), f)
        _ <- logger.info(s"ALL: $all, OPTION: $option")
      } yield ExitCode.Success
    }

  def queryAll(): Query = Query("SELECT * FROM test.products")

  def queryOne(id: String): Query =
    Query(s"SELECT * FROM test.products WHERE id = '$id'")

  def command(id: String, name: String, stock: Float): Command =
    Command(s"""
      |INSERT INTO test.products(id, name, stock)
      |VALUES ('$id', '$name', $stock)
      |""".stripMargin)
}

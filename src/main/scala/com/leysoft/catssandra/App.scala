package com.leysoft.catssandra

import java.util.UUID

import cats.effect.{ExitCode, IO, IOApp}
import com.leysoft.catssandra.connection.Session
import com.leysoft.catssandra.interpreter.Cassandra
import com.leysoft.catssandra.syntax._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object App extends IOApp {

  private val logger: Logger[IO] = Slf4jLogger.getLoggerFromName[IO]("App")

  private type Product = (String, String, Float)

  private implicit val decoder: Decoder[Product] = Decoder.instance[Product] {
    row =>
      (
        row.getString("id"),
        row.getString("name"),
        row.getFloat("stock")
      )
  }

  override def run(args: List[String]): IO[ExitCode] =
    Session.resource[IO]().use { session =>
      for {
        client <- Cassandra.make[IO](session)
        insert <- client
                    .command(command(UUID.randomUUID().toString, "p22", 100))
        _ <- logger.info(s"INSERT: $insert")
        greaterThan <- client.execute[Product](queryGreaterThan(100))
        _ <- logger.info(s"GREATER THAN: $greaterThan")
        stream <- client.stream[Product](queryAll).compile.toList
        _ <- logger.info(s"STREAM: $stream")
        option <- client
                    .option[Product](
                      queryOne("491ae396-42e7-4483-a3ef-e729c486980f")
                    )
        _ <- logger.info(s"OPTION: $option")
      } yield ExitCode.Success
    }

  def queryAll: Query = cql("SELECT * FROM test.products").query

  def queryGreaterThan(stock: Float): Query =
    cql(s"""SELECT * FROM test.products
       |WHERE stock > $stock
       |ALLOW FILTERING""".stripMargin).query

  def queryOne(id: String): Query =
    cql(s"""SELECT * FROM test.products
         |WHERE id = '$id'""".stripMargin).query

  def command(id: String, name: String, stock: Float): Command =
    cql(
      s"""INSERT INTO test.products(id, name, stock)
         |VALUES ('$id', '$name', $stock)
         |IF NOT EXISTS""".stripMargin
    ).command
}

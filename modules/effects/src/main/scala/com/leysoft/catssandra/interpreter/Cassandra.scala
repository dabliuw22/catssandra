package com.leysoft.catssandra.interpreter

import cats.effect.{Async, ContextShift}
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.syntax.option._
import com.datastax.oss.driver.api.core.cql.{AsyncResultSet, Row}
import com.leysoft.catssandra.algebra.CassandraClient
import com.leysoft.catssandra.connection.Session
import com.leysoft.catssandra.effect.AsyncTask
import com.leysoft.catssandra.syntax._
import fs2.{Chunk, Stream}

import scala.jdk.CollectionConverters._

object Cassandra {

  private[catssandra] final class AsyncCassandraClient[F[_]: Async: ContextShift] private (
    session: Session
  ) extends CassandraClient[F] {

    override def command(command: Command): F[Unit] =
      async(command.value).map(_ => ())

    override def execute[A](query: Query)(implicit fa: Row => A): F[List[A]] =
      async(query.value).flatMap(rec).map(_.map(fa))

    override def stream[A](query: Query)(implicit fa: Row => A): Stream[F, A] =
      Stream.eval(async(query.value)).flatMap(rs => chunk(rs)).map(fa)

    override def option[A](query: Query)(implicit fa: Row => A): F[Option[A]] =
      async(query.value).map(_.some.flatMap(rs => Option(rs.one)).map(fa))

    private def async(cql: String): F[AsyncResultSet] =
      AsyncTask[F, AsyncResultSet](
        Async[F].pure(session.cqlSession.executeAsync(cql))
      )

    private def rec(rs: AsyncResultSet): F[List[Row]] = {
      val current = rows(rs)
      Async[F].defer {
        if (rs.hasMorePages)
          AsyncTask[F, AsyncResultSet](Async[F].pure(rs.fetchNextPage))
            .flatMap(rec)
            .map(r => current ::: r)
        else
          Async[F].pure(current)
      }
    }

    private def chunk(rs: AsyncResultSet): Stream[F, Row] = {
      val current = Stream
        .eval(Async[F].pure(rows(rs)))
        .flatMap(res => Stream.chunk(Chunk.seq(res)))
      if (rs.hasMorePages)
        Stream
          .eval(AsyncTask(Async[F].pure(rs.fetchNextPage)))
          .flatMap(res => current ++ chunk(res))
      else current
    }

    private def rows(rs: AsyncResultSet): List[Row] =
      rs.currentPage.asScala.toList
  }

  private object AsyncCassandraClient {

    def make[F[_]: Async: ContextShift](
      session: Session
    ): F[CassandraClient[F]] =
      Async[F].delay(new AsyncCassandraClient(session))
  }

  def apply[F[_]: Async: ContextShift](
    session: Session
  ): F[CassandraClient[F]] =
    AsyncCassandraClient.make[F](session)
}

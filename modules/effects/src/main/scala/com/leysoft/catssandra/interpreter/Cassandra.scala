package com.leysoft.catssandra.interpreter

import cats.effect.{Async, ContextShift}
import cats.syntax.functor._
import cats.syntax.option._
import com.datastax.oss.driver.api.core.cql.{AsyncResultSet, Row}
import com.leysoft.catssandra.algebra._
import com.leysoft.catssandra.connection.Session
import com.leysoft.catssandra.effect.AsyncTask

import scala.jdk.CollectionConverters._

object Cassandra {

  private[catssandra] final class AsyncCassandraClient[F[_]: Async: ContextShift](
    session: Session
  ) extends CassandraClient[F] {

    override def command(command: Command): F[Unit] =
      async(command.value).map(_ => ())

    override def execute[A](query: Query, fa: Row => A): F[List[A]] =
      async(query.value).map(_.currentPage.asScala.toList.map(fa))

    override def option[A](query: Query, fa: Row => A): F[Option[A]] =
      async(query.value).map(_.some.flatMap(rs => Option(rs.one)).map(fa))

    private def async(cql: String): F[AsyncResultSet] =
      AsyncTask[F, AsyncResultSet](
        Async[F].pure(session.cqlSession.executeAsync(cql))
      )
  }

  def apply[F[_]: ContextShift](
    session: Session
  )(implicit F: Async[F]): F[CassandraClient[F]] =
    F.pure(new AsyncCassandraClient[F](session))
}

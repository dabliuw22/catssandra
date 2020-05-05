package com.leysoft.catssandra.algebra

import com.datastax.oss.driver.api.core.cql.Row

import fs2.Stream

trait CassandraClient[F[_]] {

  def command(command: Command): F[Unit]

  def execute[A](query: Query, fa: Row => A): F[List[A]]

  def stream[A](query: Query, fa: Row => A): Stream[F, A]

  def option[A](query: Query, fa: Row => A): F[Option[A]]
}

final case class Command(value: String)

final case class Query(value: String)

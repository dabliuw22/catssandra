package com.leysoft.catssandra.algebra

import com.datastax.oss.driver.api.core.cql.Row

trait CassandraClient[F[_]] {

  def command(command: Command): F[Unit]

  def execute[A](query: Query, fa: Row => A): F[List[A]]

  def option[A](query: Query, fa: Row => A): F[Option[A]]
}

final case class Command(value: String)

final case class Query(value: String)

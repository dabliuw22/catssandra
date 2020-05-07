package com.leysoft.catssandra

import com.datastax.oss.driver.api.core.cql.Row

package object syntax {

  trait Decoder[A] {

    def decode(row: Row): A
  }

  object Decoder {

    final def instance[A](fa: Row => A): Decoder[A] = (row: Row) => fa(row)
  }

  final class Cql private (val value: String) {

    def command: Command = Command(value)

    def query: Query = Query(value)
  }

  private object Cql {

    def apply(value: String): Cql = new Cql(value)
  }

  def cql(value: String): Cql = Cql(value)

  final class Command private (val value: String)

  private object Command {

    def apply(value: String): Command = new Command(value)
  }

  final class Query private (val value: String)

  private object Query {

    def apply(value: String): Query = new Query(value)
  }
}

package com.leysoft.catssandra

import com.datastax.oss.driver.api.core.cql.Row

package object syntax {

  trait Decoder[A] {

    def decode(row: Row): A
  }

  object Decoder {

    final def instance[A](fa: Row => A): Decoder[A] = (row: Row) => fa(row)
  }

  implicit class CqlStringContext(val sc: StringContext) extends AnyVal {

    def cql(args: Any*): Cql = {
      val strings = sc.parts.iterator
      val expressions = args.iterator
      val buffer = new StringBuilder
      while (expressions.hasNext) {
        buffer.append(expressions.next.toString)
        buffer.append(strings.next)
      }
      Cql(buffer.toString)
    }
  }

  final class Cql private (val value: String) {

    def command: Command = Command(value)

    def query: Query = Query(value)
  }

  private object Cql {

    def apply(value: String): Cql = new Cql(value)
  }

  final class Command private (val value: String)

  private object Command {

    def apply(value: String): Command = new Command(value)
  }

  final class Query private (val value: String)

  private object Query {

    def apply(value: String): Query = new Query(value)
  }
}

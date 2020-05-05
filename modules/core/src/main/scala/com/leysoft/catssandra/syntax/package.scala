package com.leysoft.catssandra

package object syntax {

  def cql(value: String): Cql = Cql(value)

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

package com.leysoft.catssandra.algebra

import com.leysoft.catssandra.syntax._
import fs2.Stream

trait CassandraClient[F[_]] {

  def command(command: Command): F[Int]

  def execute[A](query: Query)(implicit decoder: Decoder[A]): F[List[A]]

  def stream[A](query: Query)(implicit decoder: Decoder[A]): Stream[F, A]

  def option[A](query: Query)(implicit decoder: Decoder[A]): F[Option[A]]
}

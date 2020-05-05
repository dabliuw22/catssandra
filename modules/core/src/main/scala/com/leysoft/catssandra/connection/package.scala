package com.leysoft.catssandra

import java.net.InetSocketAddress

import cats.effect.{Async, ContextShift, Resource}
import com.datastax.oss.driver.api.core.CqlSession

import scala.jdk.CollectionConverters._

package object connection {

  case class Credentials(username: String, password: String)

  case class Node(host: String, port: Int)

  object Node {

    def apply(host: String = "127.0.0.1", port: Int = 9042) =
      new Node(host, port)
  }

  trait Session {

    def cql: CqlSession
  }

  private case class DefaultSession private (cqlSession: CqlSession)
      extends Session {

    override def cql: CqlSession = cqlSession
  }

  private object DefaultSession {

    def apply(cqlSession: CqlSession): Session = new DefaultSession(cqlSession)
  }

  object Session {

    def apply[F[_]: Async: ContextShift](
      keyspace: Option[String] = None,
      credentials: Option[Credentials] = None,
      datacenter: (String, List[Node]) = ("datacenter1", List(Node()))
    ): Resource[F, Session] =
      Resource
        .fromAutoCloseable(
          Async[F].delay {
            val cqlSession = CqlSession.builder
              .withLocalDatacenter(datacenter._1)
              .addContactPoints(
                datacenter._2
                  .map(node => new InetSocketAddress(node.host, node.port))
                  .asJavaCollection
              )
            credentials match {
              case Some(credentials) =>
                cqlSession.withAuthCredentials(credentials.username,
                                               credentials.password)
              case _ => ()
            }
            keyspace match {
              case Some(keyspace) => cqlSession.withKeyspace(keyspace)
              case _              => ()
            }
            cqlSession.build()
          }
        )
        .map(DefaultSession(_))
  }
}

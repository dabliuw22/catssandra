package com.leysoft.catssandra

import java.net.InetSocketAddress

import com.datastax.oss.driver.api.core.CqlSession
import com.dimafeng.testcontainers.CassandraContainer
import com.leysoft.catssandra.connection.Session
import com.leysoft.catssandra.it.ContainerItSpec

abstract class CassandraItSpec extends ContainerItSpec {

  override def container: CassandraContainer =
    CassandraContainer
      .Def(
        dockerImageName = "cassandra:3"
      )
      .createContainer

  private lazy val cqlSession: CqlSession = CqlSession.builder
    .withLocalDatacenter("datacenter1")
    .addContactPoint(new InetSocketAddress("127.0.0.1", 9042))
    .build

  private lazy val session = new Session {
    override def cql: CqlSession = cqlSession
  }

  override protected def beforeAll: Unit = container.start

  override protected def afterAll: Unit = {
    session.cql.close
    container.stop
  }

  protected def create(cql: String): Unit = session.cql.execute(cql)
}

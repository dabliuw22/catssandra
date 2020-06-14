package com.leysoft.catssandra.it

import com.leysoft.catssandra.test.AsyncSpec
import org.testcontainers.containers.CassandraContainer

trait ContainerItSpec extends AsyncSpec {

  protected val container = new CassandraContainer()
}

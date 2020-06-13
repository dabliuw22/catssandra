package com.leysoft.catssandra.test

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

protected[catssandra] abstract class Spec
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll

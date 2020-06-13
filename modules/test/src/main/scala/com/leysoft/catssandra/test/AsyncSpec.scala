package com.leysoft.catssandra.test

import java.util.concurrent.Executors

import cats.effect.{Blocker, ContextShift, IO, Timer}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.ExecutionContext

protected[catssandra] abstract class AsyncSpec
    extends AsyncWordSpec
    with Matchers
    with BeforeAndAfterAll {

  protected implicit def contextShift: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  protected implicit def timer: Timer[IO] = IO.timer(ExecutionContext.global)

  protected def blocker: Blocker =
    Blocker.liftExecutorService(Executors.newCachedThreadPool)
}

package com.leysoft.catssandra

import cats.effect.{ContextShift, IO, Timer}
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.ExecutionContext

protected[catssandra] abstract class AsyncSpec extends AsyncWordSpec {
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val timer: Timer[IO] = IO.timer(ExecutionContext.global)
}

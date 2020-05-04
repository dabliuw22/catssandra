package com.leysoft.catssandra.effect

import java.util.concurrent.CompletionStage

import cats.effect.{Async, ContextShift}
import cats.effect.syntax.bracket._
import cats.syntax.flatMap._

object AsyncTask {

  private[catssandra] type AsyncTask[A] = CompletionStage[A]

  def apply[F[_]: Async: ContextShift, A](fa: F[AsyncTask[A]]): F[A] =
    liftAsyncTask(fa)

  private[catssandra] def liftAsyncTask[F[_], G <: AsyncTask[A], A](
    fa: F[G]
  )(implicit F: Async[F], cs: ContextShift[F]): F[A] =
    fa.flatMap { future =>
      F.async[A] { cb =>
          future.handle[Unit] { (v: A, t: Throwable) =>
            if (t != null) cb(Left(t))
            else cb(Right(v))
          }
          ()
        }
        .guarantee(cs.shift)
    }
}

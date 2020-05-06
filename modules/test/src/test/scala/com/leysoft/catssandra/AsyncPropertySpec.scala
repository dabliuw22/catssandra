package com.leysoft.catssandra

import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

protected[catssandra] abstract class AsyncPropertySpec
    extends AsyncSpec
    with ScalaCheckDrivenPropertyChecks

package com.leysoft.catssandra.test

import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

protected[catssandra] abstract class AsyncPropertySpec
    extends AsyncSpec
    with ScalaCheckDrivenPropertyChecks

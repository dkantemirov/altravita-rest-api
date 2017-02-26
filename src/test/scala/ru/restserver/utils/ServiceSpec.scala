package ru.restserver.utils

import org.scalatest.{AsyncWordSpec, Matchers}

abstract class ServiceSpec extends AsyncWordSpec with Matchers {
  lazy val reportService = Boot.reportService
  lazy val template = Boot.template

  def produce = afterWord("produce")
  def run = afterWord("run")
}

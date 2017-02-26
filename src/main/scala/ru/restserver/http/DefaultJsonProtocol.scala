package ru.restserver.http

import org.json4s.ext.JavaTypesSerializers
import org.json4s.native.JsonMethods._
import org.json4s.{ DefaultFormats, _ }
import spray.httpx.Json4sSupport

trait DefaultJsonProtocol extends Json4sSupport {
  implicit def json4sFormats =
    DefaultFormats ++
      JavaTypesSerializers.all

  def serialize[A <: AnyRef](value: A): String = serialization.write(value)

  def deserialize[A: Manifest](json: String): A = {
    val parsed = parse(json)
    parsed.camelizeKeys.extract[A]
  }
}

object JsonProtocol extends DefaultJsonProtocol

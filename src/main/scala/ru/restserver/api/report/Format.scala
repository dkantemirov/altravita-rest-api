package ru.restserver.api.report

sealed abstract case class Format(strValue: String) {
  override def toString: String = strValue
}

object Format {
  object Pdf extends Format("pdf")
  object Html extends Format("html")

  def Default = Pdf
  def all = Set(Pdf, Html)

  def apply(strValue: String): Format = strValue match {
    case Html.strValue => Html
    case _ => Default
  }
}


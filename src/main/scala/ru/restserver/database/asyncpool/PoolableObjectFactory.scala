package ru.restserver.database.asyncpool

trait PoolableObjectFactory[T] {
  def apply: T

  @throws[AsyncPoolException]("if pooled object is invalid before using the pooled")
  def check(pooled: T): Unit

  @throws[AsyncPoolException]("if pooled object is invalid when additional check after using the pooled")
  def postCheck(pooled: T): Unit = check(pooled)
}
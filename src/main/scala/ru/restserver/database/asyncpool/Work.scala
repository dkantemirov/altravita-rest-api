package ru.restserver.database.asyncpool

case class Work[A, B](work: A => B)
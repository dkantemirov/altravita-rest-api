package ru.restserver.database

import java.sql.Connection

class DatabaseConnection(val wrapped: Option[Connection])

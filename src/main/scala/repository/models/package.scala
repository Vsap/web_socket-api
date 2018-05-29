package repository



package object models {

  case class User(username: String, password: String)
  case class FTable(id: Long = 0L, name: String)
  case class UserToTable(id: Option[Long], userId: String, tableId: Long)
}

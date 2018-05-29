package repository

import repository.models._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.concurrent.Future


class UsersToTablesTables(tag: Tag) extends Table[UserToTable](tag, "users_to_tables") {

  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val userId = column[String]("username")
  val tableId = column[Long]("table_id")

  val userIdFk = foreignKey("user_id_fk", userId, TableQuery[UsersTable])(_.username)
  val genreIdFk = foreignKey("table_id_fk", tableId, TableQuery[TablesTable])(_.id)

  val * = (id.?, userId, tableId) <> (UserToTable.apply _ tupled, UserToTable.unapply)

}

object UsersToTablesTable{
  val table = TableQuery[UsersToTablesTables]
}


class UsersToTablesRepository(db: Database){

  def create(userToTable: UserToTable) = db.run(
    UsersToTablesTable.table returning UsersToTablesTable.table += userToTable
  )

  def getById(id: Long): Future[UserToTable] = db.run(
    UsersToTablesTable.table.filter(_.id === id).result.head
  )

  def getByUsername(username: String): Future[Seq[UserToTable]] = db.run(
    UsersToTablesTable.table.filter(_.userId === username).result
  )

  def getByTableId(tableId: Long): Future[Seq[UserToTable]] = db.run(
    UsersToTablesTable.table.filter(_.tableId === tableId).result
  )
}
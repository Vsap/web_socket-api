package repository

import repository.models._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class UsersTable(tag: Tag) extends Table[User](tag, "users"){
  val username = column[String]("username", O.PrimaryKey)
  val password = column[String]("password")

  def * = (username, password) <> (User.apply _ tupled, User.unapply)
}

object UsersTable{
  val table = TableQuery[UsersTable]
}


class UsersRepository(db: Database){

  def create(user: User): Future[User] = db.run(
    UsersTable.table returning UsersTable.table += user)

  def update(user: User): Future[Int] = db.run(
    UsersTable.table.filter(_.username === user.username).update(user)
  )

  def delete(user: User): Future[Int] = db.run(
    UsersTable.table.filter(_.username === user.username).delete
  )

  def getByName(username: String): Future[User] = db.run(
    UsersTable.table.filter(_.username === username).result.head
  )

  def getUser(user: User): Future[Option[User]] = db.run(
    UsersTable.table.filter{u => u.username === user.username && u.password === user.password}.result.headOption
  )

  def getSubscribedTables(name: String): Future[Seq[FTable]] = {
    val query = for{
      table <- TablesTable.table.filter(_.id in
        UsersToTablesTable.table.filter(_.userId === name).map(_.tableId)
      ).result
    }yield table

    db.run(query)
  }

}

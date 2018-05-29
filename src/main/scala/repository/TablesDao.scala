package repository

import repository.models._
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class TablesTable(tag: Tag) extends Table[FTable] (tag, "tables"){
  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val name = column[String]("name")
  val participants = column[Int]("participants")

  def * = (id, name, participants).mapTo[FTable]
}


object TablesTable{
  lazy val table = TableQuery[TablesTable]
}


class TablesTableRepository(db: Database){

  def create(table: FTable): Future[FTable] = db.run(
    TablesTable.table returning TablesTable.table += table
  )

  def update(table: FTable): Future[Int] = db.run(
    TablesTable.table.filter(_.id === table.id).update(table)
  )

  def getById(id: Long): Future[FTable] = db.run(
    TablesTable.table.filter(_.id === id).result.head
  )

  def getByName(name: String): Future[FTable] = db.run(
    TablesTable.table.filter(_.name === name).result.head
  )

  def remove(tableId: Long): Future[Int] = db.run(
    TablesTable.table.filter(_.id === tableId).delete
  )

  def getAll: Future[Seq[FTable]] = db.run(
    TablesTable.table.result
  )



  def getParticipants(tableId: Long): Future[Seq[User]] = {
    val query = for{
      userToTable <- UsersTable.table.filter(_.username in
        UsersToTablesTable.table
          .filter(_.tableId === tableId)
          .map(_.userId)).result
    }yield userToTable

    db.run(query)
  }

}

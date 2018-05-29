import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import repository._
import repository.models.{FTable, User, UserToTable}


object Main {

  val db: Database =
    Database.forURL(
      "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=vlad"
    )

  val tableRep = new TablesTableRepository(db)
  val usersRep = new UsersRepository(db)
  val u2t = new UsersToTablesRepository(db)


  val n = usersRep.getByName("user71")
  Thread.sleep(3 * 1000)
  println(n)

  def init() = {
    Await.result(db.run(TablesTable.table.schema.create), Duration.Inf)
    Await.result(db.run(UsersTable.table.schema.create), Duration.Inf)
    Await.result(db.run(UsersToTablesTable.table.schema.create), Duration.Inf)
  }

  def fill() = {
    for(i <- 1 to 100) {
      Await.result(tableRep.create(FTable(None, "film" + i)), Duration.Inf)
      println(i)
      }
    for(i <- 1 to 100){
      Await.result(usersRep.create(User("user" + i, "passw" + i)), Duration.Inf)
      println(i)
    }
    for(i <- 1 to 100; j <- 1 to 60) {
      Await.result(u2t.create(UserToTable(None, "user" + ((i % j + j) % 100), i % j + 1)), Duration.Inf)
      println(i*j)
    }

  }
  println("hihihi")
}

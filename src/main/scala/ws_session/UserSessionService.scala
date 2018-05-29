package ws_session


import akka.actor.ActorRef
import akka.http.scaladsl.model.ws.Message
import repository.UsersRepository
import repository.models._
import ws_session._
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration



class UserSessionService(val tablesMonitorActor: ActorRef, val selfSessionActor: ActorRef, db: Database) {

  private[this] val usersRep = new UsersRepository(db)
  @volatile var role: Option[String] = None

  def isAuthorized: Boolean = role.getOrElse("user") == "admin"

  def subscribe: TableList = ???
  def unsubscribe: Unit = ???

  def login(user: User): Boolean = {
    val authotization = Await.result(usersRep.getUser(user), Duration.Inf)
    authotization contains user
  }
  def addTable(table: FTable): Unit = ???
}



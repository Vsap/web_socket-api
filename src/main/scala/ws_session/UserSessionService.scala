package ws_session


import akka.actor.ActorRef
import akka.http.scaladsl.model.ws.Message
import repository.models._
import ws_session._



class UserSessionService(val tablesMonitorActor: ActorRef, val selfSessionActor: ActorRef) {

  @volatile var role: Option[String] = None

  def isAuthorized: Boolean = role.getOrElse("") == "admin"

  def subscribe: TableList = ???
  def unsubscribe: Unit = ???

  def addTable(table: FTable): Unit = ???
}



import repository.models._
import upickle._
import upickle.default.{macroRW, ReadWriter => RW}

package object ws_session {
  sealed trait ConnectionMessage
  sealed trait MessageIn extends ConnectionMessage
  sealed trait MessageOut extends ConnectionMessage
  sealed trait UserCommand
  sealed trait TableResult
  sealed trait TableNotification
  sealed trait AdminCommand extends UserCommand


  @upickle.key("login")
  case class Login(username: String, password: String) extends MessageIn

  @upickle.key("login_failed")
  case object LoginFailed extends MessageOut

  @upickle.key("login_succesful")
  case class LoginSuccessful(userType: String = "admin") extends MessageOut

  @upickle.key("ping")
  case class Ping(seq: Int) extends MessageIn

  @upickle.key("pong")
  case class Pong(seq: Int) extends MessageOut

  @upickle.key("subscribe_tables")
  case object SubscribeTables extends MessageIn with UserCommand

  @upickle.key("table_list")
  case class TableList(tables: Seq[FTable]) extends MessageOut with TableResult

  @upickle.key("unsubscribe_tables")
  case object UnsubscribeTables extends MessageIn with UserCommand

  //Next: Authorized users only. Otherwise: NotAuthorized
  @upickle.key("not_authorized")
  case object NotAuthorized extends MessageOut with TableResult

  @upickle.key("update_table")
  case class UpdateUser(table: FTable) extends MessageIn with AdminCommand

  @upickle.key("remove_failed")
  case class RemoveUser(id: Long) extends MessageIn with AdminCommand

  @upickle.key("update_failed")
  case class UpdateFailed(id: Long) extends MessageOut with TableResult

  @upickle.key("removal_failed")
  case class RemovalFailed(id: Long) extends MessageOut with TableResult

  @upickle.key("table_added")
  case object TableAdded extends MessageOut with TableNotification

  @upickle.key("table_updated")
  case class TableUpdated(table: FTable) extends MessageOut with TableNotification

  @upickle.key("table_removed")
  case class TableRemoved(tableId: Long) extends MessageOut with TableNotification

  @upickle.key("add_table")
  case class AddUser(afterId: Int, table: FTable) extends MessageIn with AdminCommand


}

package object JsonConversions {
  import ws_session._

  implicit def loginRW: RW[Login] = macroRW
  implicit def loginFailedRW: RW[LoginFailed.type] = macroRW
  implicit def loginSuccessRW: RW[LoginSuccessful] = macroRW
  implicit def pingRW: RW[Ping] = macroRW
  implicit def pongRW: RW[Pong] = macroRW
  implicit def notAuthRW: RW[NotAuthorized.type] = macroRW
  implicit def subscribeRW: RW[SubscribeTables.type] = macroRW
  implicit def tableListRW: RW[TableList] = macroRW
  implicit def unsubscribeRW: RW[UnsubscribeTables.type] = macroRW
  implicit def updateTableRW: RW[UpdateUser] = macroRW
  implicit def removeTableRW: RW[RemoveUser] = macroRW
  implicit def updateFailedRW: RW[UpdateFailed] = macroRW
  implicit def removalFailedRW: RW[RemovalFailed] = macroRW
  implicit def tableAddedRW: RW[TableAdded.type] = macroRW
  implicit def addTableRW: RW[AddUser] = macroRW

  implicit def modelFTableRW: RW[FTable] = macroRW

  def unmarshal(text: String): MessageIn = {

    ???
  }
}

//  {
//    "$type": "add_table",
//    "after_id": 1,
//    "table": {
//      "name": "table - Foo Fighters",
//      "participants": 4
//    }
//  }

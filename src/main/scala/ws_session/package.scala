import repository.models._
import upickle._
import upickle.default.{macroRW, ReadWriter => RW}



package object ws_session {
  sealed trait ConnectionMessage
  sealed trait MessageIn extends ConnectionMessage
  sealed trait MessageOut extends ConnectionMessage
  sealed trait TableCommand
  sealed trait TableResult
  sealed trait AdminCommand extends TableCommand



  @upickle.key("login")
  case class Login(username: String, password: String) extends MessageIn
  @upickle.key("login_successful")
  case object LoginFailed extends MessageOut
  case class LoginSuccessful(userType: String = "admin") extends MessageOut
  @upickle.key("ping")
  case class Ping(seq: Int) extends MessageIn
  @upickle.key("pong")
  case class Pong(seq: Int) extends MessageOut

  case object SubscribeTables extends MessageIn with TableCommand
  case class TableList(tables: List[FTable]) extends MessageOut with TableResult
  case object UnsubscribeTables extends MessageIn with TableCommand
  //Next: Authorized users only. Otherwise: NotAuthorized
  case object NotAuthorized extends MessageOut with TableResult
  case class UpdateTable(table: FTable) extends MessageIn with AdminCommand
  case class RemoveTable(id: Long) extends MessageIn with AdminCommand
  case class UpdateFailed(id: Long) extends MessageOut
  case class RemovalFailed(id: Long) extends MessageOut
  case object TableAdded extends MessageOut
  case class AddTable(afterId: Int, table: FTable) extends MessageIn
}


  package object JsonConversions{
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
    implicit def updateTableRW: RW[UpdateTable] = macroRW
    implicit def removeTableRW: RW[RemoveTable] = macroRW
    implicit def updateFailedRW: RW[UpdateFailed] = macroRW
    implicit def removalFailedRW: RW[RemovalFailed] = macroRW
    implicit def tableAddedRW: RW[TableAdded.type] = macroRW
    implicit def addTableRW: RW[AddTable] = macroRW

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




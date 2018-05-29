package ws_session

import akka.actor.{Actor, ActorRef}
import repository.TablesTableRepository
import repository.models.{FTable, User}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await




class TablesMonitorActor(db: Database) extends Actor{

  val tablesRep = new TablesTableRepository(db)
  @volatile
  var subscriptions: Set[ActorRef] = Set.empty[ActorRef]
  var tables: List[FTable] = List.empty[FTable]
  override def receive: Receive = {
    //TODO: Subscription by username not by session
    case SubscribeTables => subscriptions += sender
      tablesRep.getAll.foreach{seqTables =>
      sender ! TableList(seqTables)}
    case UnsubscribeTables => subscriptions -= sender

    case AddUser(afterId, table) =>
      tablesRep.create(table).foreach{res => subscriptions.foreach(_ ! TableAdded(res))}

    case UpdateUser(table: FTable) =>
      tablesRep.update(table).foreach{res =>
        if(res > 0) subscriptions.foreach(_ ! TableUpdated(table))
        else sender ! UpdateFailed(table.id)
      }
    case RemoveUser(tableId: Long) =>
      tablesRep.remove(tableId).foreach{res =>
        if(res > 0) subscriptions.foreach(_ ! TableRemoved(tableId))
        else sender ! RemovalFailed(tableId)
      }
    case _ =>
  }
}

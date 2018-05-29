package ws_session

import akka.actor.{Actor, ActorRef}
import repository.TablesTableRepository
import repository.models.FTable
import slick.jdbc.PostgresProfile.api._
import ws_session._




class TablesMonitorActor(db: Database) extends Actor{

  val tablesRep = new TablesTableRepository(db)
  @volatile
  var subscriptions: Set[ActorRef] = Set.empty[ActorRef]
  var tables: List[FTable] = List.empty[FTable]
  override def receive: Receive = {
    //TODO: Subscription by username not by session
    case SubscribeTables(user: ActorRef) => subscriptions += user; user ! TableList(tables)
    case UnsubscribeTables(user: ActorRef) => subscriptions -= user
    case UpdateTable(table: FTable, user: ActorRef) =>
      tablesRep.update(table).foreach{res =>
        if(res > 0) subscriptions.foreach(_ ! TableUpdated(table))
        else user ! UpdateFailed(table.id)
      }
    case AddTable(table: FTable, user: ActorRef) => subscriptions.foreach(_ ! TableAdded(table))
    case RemoveTable(table: FTable, user: ActorRef) => subscriptions.foreach(_ ! TableRemoved(table))
    case _ =>
  }
}

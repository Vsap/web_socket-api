package ws_session

import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.http.scaladsl.model.ws.TextMessage
import ws_session._

class ConnectionActor extends Actor {

  private var connection: Option[ActorRef] = None

  import ws_session.ConnectionActor._
  def receive: Receive = {
    case InitSession(a) => connection = Some(a); context.watch(a); println("initializer")
    case Terminated(a) if connection.contains(a) => connection = None; println("terminated"); context.stop(self)
    case EndSession =>
      connection.foreach(context.stop)
      context.stop(self)

    case TextMessage.Strict("ping") =>
      connection.foreach(_ ! TextMessage.Strict("pong"))
    case tm: TextMessage => connection.foreach(_ ! ("echo: " + tm.textStream))
    case _ => // ignore
  }

  override def postStop(): Unit = connection.foreach(context.stop)
}


object ConnectionActor{
  case class InitSession(sourceAcror: ActorRef)
  case object EndSession
}
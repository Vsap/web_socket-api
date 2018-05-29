import akka.actor.{ActorSystem, Props}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.Message
import ws_session.ConnectionActor._
import ws_session.{ConnectionActor}

import scala.io.StdIn


object WebMain extends App{

  implicit val actorSystem = ActorSystem("akka-system")
  implicit val flowMaterializer = ActorMaterializer()

  val interface = "localhost"
  val port = 8080

  def sessionFlow: Flow[Message, Message, _] = {
    val sessionActor = actorSystem.actorOf(Props(classOf[ConnectionActor]))
    val sink = Sink.actorRef(sessionActor, EndSession)
    val source =
      Source.actorRef(8, OverflowStrategy.fail)
        .mapMaterializedValue{actorRef =>
          sessionActor ! InitSession(actorRef)
          actorRef
        }
    Flow.fromSinkAndSource(sink, source)
  }

  val route = pathEndOrSingleSlash{handleWebSocketMessages(sessionFlow)}
  val binding = Http().bindAndHandle(route, interface, port)
  println(s"Server is now online at ws://$interface:$port\nPress RETURN to stop...")
  StdIn.readLine()

  import actorSystem.dispatcher

  binding.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate())
  println("Server is down...")



}
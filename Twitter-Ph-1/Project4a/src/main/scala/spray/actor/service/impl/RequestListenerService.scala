package spray.actor.service.impl

import akka.actor.Actor
import spray.can.Http
import spray.util._
import spray.http._
import HttpMethods._

class RequestListenerService extends Actor {

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case HttpRequest(POST, Uri.Path(path), _, _, _) if path startsWith "/tweet/update"=>
      println("TWEET RECEIVED")
      println(path)
      sender ! HttpResponse(entity = "TWEET RECEIVED!")
    case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/timeout" =>
      println()
    case _: HttpRequest => sender ! HttpResponse(status = 404, entity = "Unknown resource!")
  }
}
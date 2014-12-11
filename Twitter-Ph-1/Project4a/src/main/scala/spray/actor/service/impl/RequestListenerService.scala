package spray.actor.service.impl

import akka.actor.Actor
import spray.can.Http
import spray.util._
import spray.http._
import HttpMethods._
import main.scala.common._

class RequestListenerService(akkaServerIP: String, localAddress: String, serverPort: Int) extends Actor {

  val selfPath = "akka.tcp://SprayServer@" + localAddress + ":" + serverPort + "/user/RequestListener"
  val akkaServerPath = "akka.tcp://AkkaServerServer@" + akkaServerIP + ":" + serverPort + "/user/"

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)

    //GET Usertimeline Request
    case HttpRequest(GET, Uri.Path(path), header, entity, protocol) if path startsWith "/timeline/usertimeline" =>
      val args: Array[String] = path.split("/")
      val service = args(1)
      val endPoint = "Get" + args(2)
      val userName = args(3)
      val akkaRequest = new AkkaRequest(selfPath, endPoint, userName, "", "")
      val akkaServer = context.actorSelection(akkaServerPath + "TimelineServiceRouter")
      akkaServer ! akkaRequest

    //Response from akka server for Usertimeline
    case LoadUserTimelineResp(tweets: Map[String, String]) =>
      println("Result Length : " + tweets.size)
      

    //GET Hometimeline
    case HttpRequest(GET, Uri.Path(path), header, entity, protocol) if path startsWith "/timeline/hometimeline" =>
      val args: Array[String] = path.split("/")
      val service = args(1)
      val endPoint = "Get" + args(2)
      val userName = args(3)
      val akkaRequest = new AkkaRequest(selfPath, endPoint, userName, "", "")
      val akkaServer = context.actorSelection(akkaServerPath + "TimelineServiceRouter")
      akkaServer ! akkaRequest

    //Response from akka server for Hometimeline
    case LoadHomeTimelineResp(tweets: Map[String, String]) =>
      println("Result Length : " + tweets.size)
     
      
      
      
      
      
      
      
      
      
      
      
      
      
    case HttpRequest(GET, Uri.Path(path), header, entity, protocol) =>
      val args: Array[String] = path.split("/")
      val service = args(1)
      val endPoint = GET + args(2)
      val userName = args(3)
      val tweetuuid = args(4)

      if (service equalsIgnoreCase ("tweet")) {
        val akkaRequest = new AkkaRequest(selfPath, endPoint, userName, "", "")
        val akkaServer = context.actorSelection(akkaServerPath + "TweetsServiceRouter")
        //akkaServer ! akkaRequest
      } else if (service equalsIgnoreCase ("timeline")) {
        val akkaRequest = new AkkaRequest(selfPath, endPoint, userName, "", "")
        val akkaServer = context.actorSelection(akkaServerPath + "TimelineServiceRouter")
        //akkaServer ! akkaRequest
      }
      sender ! HttpResponse()
    case HttpRequest(POST, Uri.Path(path), header, entity, protocol) =>
      println(path)

    case AkkaResponse =>
    //send httprequest

    /*case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/tweet/update"=>
      println("TWEET RECEIVED")
      println(path)
      sender ! HttpResponse(entity = "TWEET RECEIVED!")
    case HttpRequest(GET, Uri.Path(path), _, _, _) if path startsWith "/timeout" =>
      println()
    case _: HttpRequest => sender ! HttpResponse(status = 404, entity = "Unknown resource!")*/

    //    case HttpRequest(method, uri, header, entity, protocol) =>
    //      println(method + " , " + uri + " , " + header + " , " + entity + " , " + protocol)
    //      sender ! HttpResponse(entity = "received")
    //      RESPONSE : GET , http://localhost:9080/ping , List(Accept-Language: en-US, en;q=0.8, Accept-Encoding: gzip, deflate, sdch, User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.65 Safari/537.36, Accept: text/html, application/xhtml+xml, application/xml;q=0.9, image/webp, */*;q=0.8, Connection: keep-alive, Host: localhost:9080) , Empty , HTTP/1.1

  }
}
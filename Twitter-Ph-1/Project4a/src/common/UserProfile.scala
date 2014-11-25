package common

import scala.collection.mutable.ListBuffer

class UserProfile(name: String, follower: ListBuffer[String], home: ListBuffer[String], user: ListBuffer[String]) {

  val username: String = name
  val followers = follower
  val homeTimeline = home
  val userTimeline = user
}
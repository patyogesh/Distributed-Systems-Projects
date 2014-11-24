package common

class UserProfile(name: String, follower: List[String], home: List[String], user: List[String]) {

  val username: String = name
  val followers: List[String] = follower
  val homeTimeline: List[String] = home
  val userTimeline: List[String] = user
}
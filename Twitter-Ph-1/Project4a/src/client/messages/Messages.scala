package client.messages

sealed trait Messages
case class TweetToServer() extends Messages
case class LoadTimeline() extends Messages
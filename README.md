DOS-Fall-14
===========

This is a set of projects I am doing for a Distributed Operating systems (Fall 2014 Class)

Twitter Simulator
Project4a:

Project4b:

Observations:
	With increase in # of tweets per sec, processor is not bottle neck but memory is
	With increase in # of tweets per sec for 1M useers, client bottlenecks, no prob with server
	With increase in avg # of tweets per day per user for 1M useers, client bottlenecks, no prob with server

TODO
  1. Differant ports for parallel connections from SOAP client to Spray server. 
	Current design creates 2*cores Spray servers BUT all instances listen on same port which may underutilize channel.
	It also assumes that Spray internall multiplex/demultiplex multiple parallem connection though on same physical connections
  
  Solution	:
	 USE differant ports for each instance. This will enable *multiple parallel physical connections* from SOAP 
	 client to SPRAY server.
	 
  2. Non-blocking processing of request response at Spray. Do not wait for Akka response (using futures)
	 to reply to client.
	 
  Solution:
	  Use NAT kind of a feature that keeps track of requence source (IP/Sender-reference) and use it to reply to 
	  client once AKKA resp received.
	  

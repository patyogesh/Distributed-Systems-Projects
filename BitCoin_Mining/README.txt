AUTHORS:
========
	Bhavnesh Gugnani (UFID : 83467874)
    	bhavnesh.gugnani@ufl.edu

	Yogesh Patil  (UFID: 00422561)
    	ypatil@cise.ufl.edu
-------------------------------------------------------------------------------------------------------------------------------------------------------
							README
-------------------------------------------------------------------------------------------------------------------------------------------------------

+--------------------------+
| Task Distribution Method |
+--------------------------+
	We have heirarchial structure that constitutes multiple actors namely SuperMaster, Master and Worker.
	Role and responsibility of each of the actors is as below:
	
	Worker:
		Worker actor does actual job of hashing using SHA-256 and checks if its valid bitcoin with 'k' leading zeroes.
		Once found, it immediately reports found bitcoin to its Master and Master to SuperMaster who eventually displays it.

	Master:
		Every newly added machine starts ONE Master that registers itself with SuperMaster indicating its availability. 
		On successful registration, SuperMaster assigns work unit to Master. 
		Master in turn starts 'x' number of Workers where (x = 2 * cores) to perform actual work
		Master Uses Round Robin Distribution policy to assign work to all Workers. 

		Additional instance of Master also runs on machine on which SuperMaster is running. 
		This is to make system always available in case remote machines/nodes are not available.
		Master program takes SuperMaster's IP address as a user input.

	SuperMaster:
		This is TOP level actor that takes user input: 
    			- 'k' (where k is number of leading zeroes in bitcoin)
    		This actor outputs the Bitcoin and string pair as mined and returned by Workers/Masters

		As Masters register their availability to SuperMaster, it delegates them work in the form of differant 'lengths of a string to hash'
		to each Master which in turn delegates the string combinations of that lengths to Workers in Round Robin fashion

	Fail Safe:
		Each time Super Master assigns work to Master, it keeps keeps local registration of work assigned to each Master.
		In case the Master fails before work completion, Super Master provides the complete job to another Master.

+--------------------------+
| Work Unit Description    |
+--------------------------+
		Printable characters in ASCII value range of 33 to 126 are used to append to gatorlink ID to generate possible bitcoins on hashing. 
		We start appending various length characters to gatorlink ID and start delegating differant length strings to differant Masters.
			- Masters get length from SuperMasters
			- Masters pass len and start char to workers in round robin manner

		# of sub problems that Master gets from Super Master is the length of String for possible bitcoin

		# of sub problems that Worker  gets work from Master is 93 ^ (len -1)
		where   93 : printable characters
			len - possible length assigned to Master by SuperMaster
		
		We have used Iterative Back-Tracking algorithm to generate all possible strings of given length. This will ensure all possible values being checked for potential bitcoin rather than Randomly generated string and missing some bitcoins.
		Since the length assigned to each Master grows linearly and each Worker problem size will grow exponentially, thie scheme ensure all Workers
		get Equal amount of work balancing load. This also follows the basic of bitcoin mining that the problem becomes exponentially hard as the 
		bitcoins are found .



+----------------------------+
| HOW TO RUN		     |
+----------------------------+
		We ran this project using sbt as well as using Eclipse IDE.
		Tested on CISE lin* machines using sbt.

		Program file runs in TWO modes
		1. SuperMaster (Server)
			- input arguments is 'k' (where k = 1,2,.....n)
			
			sbt> run <k>

		2. Master/Worker (Client)
			- input arguments is 'k' Server_IP_Address 
				(where k = 1,2,.....n)

			sbt> run <k> <Server_ip>

		Server Mines infinitely, so manual intervention is required to stop the application.

		Observation: At times we noted the connection from remoate
			     clients to server timed out and some akka logs
			     showed up saying 'dead characters reported'. Thiswas intermitant though.


+----------------------------+
| Result of Program for k = 4|
+----------------------------+
 Result of program for k = 4

bhavnesh.gugnani!k+	000025b002377b78771044b998101866ef5e8d43c6de91334b00a1e82ad6f23e
bhavnesh.gugnani'ow	0000b1e8866377642667dcb1c1f94c2c4d8dff1f7cc574c3ede6ca4f465279f4
bhavnesh.gugnani%0[	0000682aaa558b0dd8c2a4e3ab3cc6853bc01a4eeb4fc28bd1aa5e87903e24c6
bhavnesh.gugnani2.&	0000c59f7929387186e591cb4b81c64dda5e3614e92c7bf3c3709f37bd851552
bhavnesh.gugnani,K1	0000faaae2404d962d24ef91e16e752a970d730500057fafcc709f148b7a10d9
bhavnesh.gugnani?0Z	000098ec939d952f0add22f5e072853aa3973409ee91ff841fcbf7ac8bc9a7e6
bhavnesh.gugnaniNZ@	00005800ecb5e5aaa706c5932218594fb2a9c3eaa606fe48ce8f63c7efebdc3b
bhavnesh.gugnani[}D	0000b7decb918921b65cc3ba073519a4aca356a5e7829ecd1c82bd3c2c712903
bhavnesh.gugnani`&|	000028f9526b66d32a05a7f8b5528feb10947edea24ef60c7094a392ffbfc8b2
bhavnesh.gugnani!!"w	0000aa22f416b690f4467f4a588ecf7f055cb84ee6c1665139e61b9fd0d837b7
bhavnesh.gugnani(!rc	000047f7e029e440f3e040375c3c3ffbb8e2fcf063afdc8792c2efee64b1a434
bhavnesh.gugnani%$f&	000037b71948060f2240a6141536d670d9258de3eb7a71a5ab16d36cc5473d49
bhavnesh.gugnani%%!;	0000d808320e08a9e412e27eeabbd5d168d64f4de434a627f41975fe2223e083
bhavnesh.gugnani'#Op	0000da3c59f137fcc8e41de8aaf662f3d849682f7547119929af5d031185907c
bhavnesh.gugnani#'xZ	0000cbec777ecb909f332b147807f5b54abc00c7873c306b727f3e48f96eee16
bhavnesh.gugnani#(;I	0000105879de326bbc75cecc7f8b7347111e2b83bc5b5329537b057f33a053f9
bhavnesh.gugnani'%Of	0000dfc2869389813ae8d7ddedd6bdf08628c2d222b638690ebeeb1029612818
bhavnesh.gugnani!*'4	0000e5ce4a2d516cb7a0674a7186a834b7862e7e7a921654872b8c26bfb9d731
bhavnesh.gugnani(*bP	0000a09ecb4d71fb67498a50ee94ff3f6631e05e161afa50131f56cca7678dda
bhavnesh.gugnani%*x1	0000fc4a37385be3949b860993df3767d5fb3062028175c89d4694179a4e41e1
bhavnesh.gugnani&+a1	0000aa4f62ddec4a0fc66c1563fbff303768f76abc8b69a57cae7fc0e1741a8d
bhavnesh.gugnani&-P4	0000c912add92d0641aad2e2e4f458d213ccd3f35e8140455a43b4d59079093e
bhavnesh.gugnani%/i&	0000dc28ce8f2110a18ff2b70aded7a9bd242443b1af5d28bee8a6bc309e414a
bhavnesh.gugnani%0dy	00008fe954696f33d43a7c9aa5f84fdeaf0f1c326a6b1c577bd34e853c8d7a41
bhavnesh.gugnani"3X5	00003ebe9dcaa5b9f423e835d49916ccd16463b3925ae88fd9c006b4500b97b5
bhavnesh.gugnani$18x	0000c64c79cad6594a23bf8b3e359f7d2be0de074ff456daa224b65ffa40f7dd

+---------------+
| Running Time  |
+---------------+
 Running time for above as reported by UNIX time utility for  k = 5 is as follows

 real	0m250.618s
 user	0m843.016s
 sys	0m15.987s
 
 Note: ratio of CPU times when run on a machines with 4 cores

 		CPU_TIME : REAL_TIME is 
		-----------------------
 		843.016  : 250.618
		3.372    : 1

+-----------------------------------+
| Bitcoins with most leading 0's    |
+-----------------------------------+
 Coin with the most 0's we have managed to find is 6 ZEROES

+---------------------------------------+
| Largest number of working machines    |
+---------------------------------------+
 Largest number working machines we're abale to run our code with is 4

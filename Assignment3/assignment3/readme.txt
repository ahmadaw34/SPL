
GUIDE TO RUN THE CODE:

TreadPerClient:
	- mvn compile
	- mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="7777"

Reactor:
	- mvn compile
	- mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 5"

Client:
	- make clean all
	- ./bin/echoClient 127.0.0.1 7777

Input correctness:
	- Date format: DD-MM-YYYY
	- To edit filtered words go to: ./322390592_315250985/Server/src/main/java/bgu/spl/net/impl/objects/Messages.java (line:11) there is a private field which contains an array of strings for the words to filter.


Example of the correct format of written commands:
	1. REGISTER command:
		REGISTER <username> <password> <DD-MM-YYYY>
	2. LOGIN command:
		LOGIN <username> <passowrd> <captcha "0 for faliure or 1 for success" >
	3. FOLLOW command:
		FOLLOW <0/1 "0 for follow / 1 for unfollow"> <username> 
	4. POST command:
		POST <message_to_post>
	5. PM command:
		PM <username_to_send_to> <message_to_send>
	6. LOGSTAT command:
		LOGSTAT
	7. STAT command:
		STAT <user1>|<user2>|<user3>...
	8. LOGOUT command: 
		LOGOUT
	9. BLOCK command:
		BLOCK <username_to_block>

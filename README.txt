
Simulated OSPF Project
-------------------------------------------------------------------------------------------------------------------------------------------------------

	The OSPF protocal (short for Open Shortest Path First) routes packets using Dijkstra’s shortest path algorithm. The program consists of two sections, 
	Building and Adapting. The first simply makes the initial graph of routes, and the second changes various attributes of the graph to change what paths
	can be traveresed.

	
	--- Included Programs ---
		Graph.java

	--- Usage ---
		The file is to be ran through a command line from either a terminal built into the OS (like MS-CMD or iOs Terminal), 
		or the provided command window found in most IDEs. The structure needed for to run the program is "java graph.java network.txt", 
		and a brief description of the internal processes is as follows.

		Upon running, as stated, the main graph is built. Then users are allowed to pick from the following:

		addEdge( String v, String w, Double weight) --> Add additional edge, directed
		edgeUp(String v,String w) --> Add created edge to 'Up' list
		edgeDown(String v,String w) --> Add created edge to 'Down' list
		deleteEdge(String v,String w) --> Removes an edge from the starting node
		vertexUp(vertex v) --> Add created vertex to 'Up' list
		vertexDown(vertex v) --> Add created vertex to 'Down' list
		print()   --> Prints all nodes and their weighted edges 
		reachable() --> prints all nodes and their reachable descendants provided all are up
		path(String start, String end) --> Open Shortest Path First protocol 

		Each will print out their respective results or simply continue to prompt the user for next steps.

	--- Summary of Performance --- 
		This algorithm is seemingly scalable to any length of intitial file.
		
		Any error messages given to alert the user of incorrect input are given in the same environment they've typed from. Whereas some are 
		explicitly written, others are provided by the language in the form of runtime exceptions, but none should show if the files given
		exist and the inputs are correct. 
	
	--- Developemental Software Stack --- 
		*Language: Java
		*Version: 16.0.2 (compiler included)
		*IDE used: Visual Studio Code

	--- Credits --- 
		This system was made by Tim Hillmann utilizing java's built-in utilities file, and is modified from code by Mark A Weiss and Srinivas 
		Akella.
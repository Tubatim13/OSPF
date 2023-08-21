/*
 * Author: Tim Hillmann
 * Date: April 26, 2023
 * Purpose: This program is modified from code by Mark A Weiss and Srinivas Akella. 
 *          It reads characters from a text file given in the command-line, 
 *          assessess each line to form a weighted, and possibly directed graph.
 *          From there, various commands can be made to adjust said graph.
 * Language: Java
 */

import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;



// Used to signal violations of preconditions for
// various shortest path algorithms.
class GraphException extends RuntimeException
{
    public GraphException( String name )
    {
        super( name );
    }
}

// Represents a vertex in the graph.
class Vertex implements Comparable<Vertex>
{
    public String     name;   // Vertex name
    public List<Vertex> adj;    // Adjacent vertices
    public List<Double> weight;
    public Vertex     prev;   // Previous vertex on shortest path
    public double        dist;   // Distance of path

    public Vertex( String nm)
      { name = nm; adj = new LinkedList<Vertex>( ); weight = new LinkedList<Double>() ; reset( ); }

    public void reset( )
      { dist = Graph.INFINITY; prev = null; }    

    @Override
    public int compareTo(Vertex other) {
        return Double.compare(dist, other.dist);
    }
      
}

// Graph class: evaluate shortest paths of weighted graoh.
//
// CONSTRUCTION: with no parameters.
//
// ******************PUBLIC OPERATIONS**********************
// void addUndirectedEdge( String v, String w, Double weight)
//                              --> Add additional edge
// void addEdge( String v, String w, Double weight )
//                              --> Add additional edge, directed
// void edgeUp(String v,String w) --> Add created edge to 'Up' list
// void edgeDown(String v,String w) --> Add created edge to 'Down' list
// void deleteEdge(String v,String w) --> Removes an edge from the starting node
// void vertexUp(vertex v) --> Add created vertex to 'Up' list
// void vertexDown(vertex v) --> Add created vertex to 'Down' list
// void printPath( String w )   --> Print path after alg is run
// void print()   --> Prints all nodes and their weighted edges 
// void reachable() --> prints all nodes and their reachable descendants provided all are up
// void ospf(String s) --> Open Shortest Path First protocol 

public class Graph
{
    public static final int INFINITY = Integer.MAX_VALUE;
    private Map<String,Vertex> vertexMap = new HashMap<String,Vertex>( );
    private List<String> edgesUp = new ArrayList<String>();
    private List<String> edgesDown = new ArrayList<String>();
    private List<Vertex> vertexUp = new ArrayList<Vertex>();
    private List<Vertex> vertexDown = new ArrayList<Vertex>();

    /**
     * Add a new undirected edge to the graph.
     */
    public void addUndirectedEdge( String sourceName, String destName, double weightAmt )
    {
        Vertex v = getVertex( sourceName );
        Vertex w = getVertex( destName );
        v.adj.add( w );
        v.weight.add(weightAmt);
        edgeup(sourceName, destName);
        w.adj.add( v );
        w.weight.add(weightAmt);
        edgeup(destName, sourceName);

    }

    /**
     * Add a new directed edge to the graph.
     */
    public void addEdge(String tailvertex, String headvertex,double transmit_time)
    {
        Vertex v = getVertex( tailvertex );
        Vertex w = getVertex( headvertex );
        edgeup(tailvertex, headvertex);
        v.adj.add( w );
        v.weight.add(transmit_time);
    }

    /**
     * Add given edge to up list.
     */
    public void edgeup(String tailvertex,String headvertex){
        edgesUp.add(tailvertex + " to " + headvertex);
        if(edgesDown.contains(tailvertex + " to " + headvertex)){
            edgesDown.remove(edgesDown.indexOf(tailvertex + " to " + headvertex));
        }
    }

    /**
     * Add given edge to down list.
     */
    public void edgedown(String tailvertex,String headvertex){
        edgesDown.add(tailvertex + " to " + headvertex);
        if(edgesUp.contains(tailvertex + " to " + headvertex)){
            edgesUp.remove(edgesUp.indexOf(tailvertex + " to " + headvertex));
        }
    }

    /**
     * Add given vertex to up list.
     */
    public void vertexup(Vertex v){
        vertexUp.add(v);
        if(vertexDown.contains(v)){
            vertexDown.remove(vertexDown.indexOf(v));
        }
    }

    /**
     * Add given vertex to down list.
     */
    public void vertexdown(Vertex v){
        vertexDown.add(v);
        if(vertexUp.contains(v)){
            vertexUp.remove(vertexUp.indexOf(v));
        }
    }

    /**
     * Delete edge entirely from current neighbors of starting point
     */
    public void deleteEdge(String tailvertex, String headvertex)
    {
        Vertex v = getVertex( tailvertex );
        Vertex w = getVertex( headvertex );
        int index = v.adj.indexOf( w );
        v.adj.remove( w );
        v.weight.remove(v.weight.get(index));
    }

    /**
     * Driver routine to print total distance.
     * It calls recursive routine to print shortest path to
     * destNode after a shortest path algorithm has run.
     */
    public void printPath( String destName )
    {
        Vertex w = vertexMap.get( destName );
        if( w == null )
            throw new NoSuchElementException( "Destination vertex not found" );
        else if( w.dist == INFINITY )
            System.out.println( destName + " is unreachable" );
        else
        {
            printPath( w );
            double rounded = (double)((int)(w.dist * 100))/100.0;
            System.out.print( " " + rounded);
            System.out.println( );
        }
    }

    /**
     * Print all Nodes and their Edges in alphabetical order, with weights of each
     */
    public void print(){
        ArrayList<String> ordered = new ArrayList<String>();
        for(Map.Entry<String, Vertex> entry : vertexMap.entrySet()) {
            ordered.add(entry.getKey());
        }
        Collections.sort(ordered);

        for(String entry : ordered) {
            String key = entry;
            Vertex v = vertexMap.get(key);
            
            System.out.print(key);

            if(vertexDown.contains(v))
                System.out.println(" -- Down");
            else
                System.out.println("");

            ArrayList<String> orderedInternal = new ArrayList<String>();
            for(Vertex w : v.adj){
                orderedInternal.add(w.name);
            }
            Collections.sort(orderedInternal);
            for(String name : orderedInternal){
                Vertex w = getVertex(name);
                System.out.print("\t" + w.name + " " + v.weight.get(v.adj.indexOf(w)));
                if(edgesDown.contains(v.name + " to " + w.name)){
                    System.out.println(" -- Down");

                }else{
                    System.out.println("");
                }
            }
        }
    }

    /**
     * If vertexName is not present, add it to vertexMap and to the up list.
     * In either case, return the Vertex.
     */
    private Vertex getVertex( String vertexName )
    {
        Vertex v = vertexMap.get( vertexName );
        if( v == null ){
            v = new Vertex( vertexName );
            vertexMap.put( vertexName, v );
        }
        vertexup(v);
        return (v);
    }

    /**
     * Recursive routine to print shortest path to dest
     * after running shortest path algorithm. The path
     * is known to exist.
     */
    private void printPath( Vertex dest )
    {
        if( dest.prev != null )
        {
            printPath( dest.prev );
            System.out.print( " " );
        }
        System.out.print( dest.name );
    }
    
    /**
     * Initializes the vertex output info prior to running
     * any shortest path algorithm.
     */
    private void clearAll( )
    {
        for( Vertex v : vertexMap.values( ) )
            v.reset( );
    }

    /**
     * Compiles a list of all reachable destination Nodes from a list of starting Nodes, displayed alphabetically
     */
    /*
     * The algorithm uses a queue for breadth-first search, which originally has a worst-case time 
     * complexity of O(V+E). However,the vertices are processed in alphabetical order and the queue 
     * size is upper-bounded by the total amount of vertices, O(V). Therefore, the overall time 
     * complexity results in O(V^2 + E).
     */
    public void reachable(){
        Map<String, ArrayList<Vertex>> reachableDestinations = new HashMap<>();

        // Loop through each point in the graph in alphabetical order
        ArrayList<String> ordered = new ArrayList<String>();
        for(Map.Entry<String, Vertex> entry : vertexMap.entrySet()) {
            ordered.add(entry.getKey());
        }
        Collections.sort(ordered);

        for(String entry : ordered) {
            // Initialize an array to keep track of visited nodes
            ArrayList<Vertex> visited = new ArrayList<Vertex>();

            // Initialize an array to store the reachable destinations for this node
            ArrayList<Vertex> destinations = new ArrayList<Vertex>();

            // Initialize a queue with the current point as the starting node
            Queue<Vertex> queue = new LinkedList<>();
            Vertex v = vertexMap.get(entry);
            queue.add(v);

            // While nodes in the queue
            while (!queue.isEmpty()) {
                // Dequeue to next point 
                Vertex current = queue.remove();

                if (!visited.contains(current) && !vertexDown.contains(current)) {
                    // Add it to the set of visited nodes
                    visited.add(current);
    
                    // Add all neighboring nodes
                    for (Vertex neighbor : current.adj) {
                        if (!visited.contains(neighbor) && !edgesDown.contains(current + " to " + neighbor) && !vertexDown.contains(neighbor)) {
                            queue.add(neighbor);
                        }
                    }
    
                    // Add all neighboring nodes to reachable destinations
                    for (Vertex neighbor : current.adj) {
                        if (!visited.contains(neighbor) && !vertexDown.contains(neighbor) && !edgesDown.contains(current + " to " + neighbor)) {
                            destinations.add(neighbor);
                        }
                    }
                }
            }
    
            // Add reachable destinations to the map
            reachableDestinations.put(entry, destinations);
        }
    
        // Print the reachable destinations for each point
        ArrayList<String> ordered_after = new ArrayList<String>();
        for(Map.Entry<String, ArrayList<Vertex>> entry : reachableDestinations.entrySet()) {
            ordered_after.add(entry.getKey());
        }
        Collections.sort(ordered);

        for(String entry : ordered) {
            String key = entry;
            ArrayList<Vertex> v = reachableDestinations.get(key);

            if(vertexDown.contains(vertexMap.get(key)))
                continue;
            else
                System.out.println(key);

            ArrayList<String> orderedInternal = new ArrayList<String>();
            for(Vertex w : v){
                orderedInternal.add(w.name);
            }
            Collections.sort(orderedInternal);
            ArrayList<String> printed = new ArrayList<String>();
            for(String name : orderedInternal){
                if(!printed.contains(name)){
                    Vertex w = vertexMap.get(name);
                    System.out.println("\t" + w.name);
                    printed.add(name);
                }
            }
        }
    }
    

    /**
     * Simulated OSPF (Open Shortest Path First) protocol that checks for downed edges and vertices
     */

    public void ospf( String startName )
    {

        clearAll();

        if(vertexDown.contains(vertexMap.get(startName))){
            System.out.println("Starting location is down");
            return;
        }

        Vertex start = vertexMap.get(startName);
        if (start == null) {
            throw new NoSuchElementException("Start vertex not found");
        }

        PriorityQueue<Vertex> pq = new PriorityQueue<Vertex>();
        start.dist = 0;
        pq.offer(start);

        while (!pq.isEmpty()) {
            Vertex v = pq.poll();

            // Visit all neighbors of the current vertex
            for (Vertex w : v.adj) {
                if(vertexDown.contains(w) ||
                edgesDown.contains(v.name + " to " + w.name))
                    continue;

                double weight = v.weight.get(v.adj.indexOf(w));

                // If we found a shorter path to the neighbor, update its distance
                if (v.dist + weight < w.dist) {
                    pq.remove(w);
                    w.dist = v.dist + weight;
                    w.prev = v;
                    pq.offer(w);
                }
            }
        }
    }



    /**
     * Process a request; return false if end of file.
     */
    public static boolean processRequest( Scanner in, Graph g )
    {
        String query = in.nextLine();

        //path request
        if(query.startsWith("path")){
            try
            {
                query = query.substring(query.indexOf(" ")+1);
                String startName = query.substring(0,query.indexOf(" "));

                query = query.substring(query.indexOf(" ")+1);
                String destName = query;

                g.ospf( startName );
                g.printPath( destName );
            }
            catch( NoSuchElementException e )
            { System.out.println("One of the vertices given is invalid"); }
            catch( GraphException e )
            { System.err.println( e ); }
            return true;
        }
        //print request
        else if(query.startsWith("print")){
            g.print();
            return true;
        }
        //quit request
        else if(query.startsWith("quit")){
            return false;
        }
        //reachable request
        else if(query.startsWith("reachable")){
            g.reachable();
            return true;
        }
        //edgedown request
        else if(query.startsWith("edgedown")){
            query = query.substring(query.indexOf(" ")+1);
            String startName = query.substring(0,query.indexOf(" "));
            query = query.substring(query.indexOf(" ")+1);
            String destName = query;

            if(!g.vertexMap.containsKey(startName) || !g.vertexMap.containsKey(destName)){
                System.out.println("One or more of the vertices requested do not exist yet and thus their " +
                "edge cannot be listed as Down");
                return true;
            }

            g.edgedown(startName,destName);
            return true;
        }
        //edgeup request
        else if(query.startsWith("edgeup")){
            query = query.substring(query.indexOf(" ")+1);
            String startName = query.substring(0,query.indexOf(" "));
            query = query.substring(query.indexOf(" ")+1);
            String destName = query;

            if(!g.vertexMap.containsKey(startName) || !g.vertexMap.containsKey(destName)){
                System.out.println("One or more of the vertices requested do not exist yet and thus their " +
                "edge cannot be listed as Up");
                return true;
            }

            g.edgeup(startName,destName);
            return true;
        }
        //vertexdown request
        else if(query.startsWith("vertexdown")){
            query = query.substring(query.indexOf(" ")+1);
            String startName = query.substring(0);
            if(!g.vertexMap.containsKey(startName)){
                System.out.println("The vertex requested does not exist yet and thus cannot " +
                "be labeled as down");
                return true;
            }
            Vertex v = g.getVertex(startName);
            g.vertexdown(v);
            return true;
        }
        //vertexup request
        else if(query.startsWith("vertexup")){
            query = query.substring(query.indexOf(" ")+1);
            String startName = query.substring(0);
            if(!g.vertexMap.containsKey(startName)){
                System.out.println("The vertex requested does not exist yet and thus cannot " +
                "be labeled as Up");
                return true;
            }
            Vertex v = g.vertexMap.get(startName);
            g.vertexup(v);
            return true;
        }
        //addedge request
        else if(query.startsWith("addedge")){
            query = query.substring(query.indexOf(" ")+1);
            String startName = query.substring(0,query.indexOf(" "));
            query = query.substring(query.indexOf(" ")+1);
            String destName = query.substring(0,query.indexOf(" "));
            query = query.substring(query.indexOf(" ")+1);
            double weight = Double.parseDouble(query);

            g.addEdge(startName, destName, weight);
            return true;
        }
        //deleteedge request
        else if(query.startsWith("deleteedge")){
            query = query.substring(query.indexOf(" ")+1);
            String startName = query.substring(0,query.indexOf(" "));

            query = query.substring(query.indexOf(" ")+1);
            String destName = query;
            
            if(!g.vertexMap.containsKey(startName) || !g.vertexMap.containsKey(destName)){
                System.out.println("One or more of the vertices requested do not exist yet and thus their " +
                "edge cannot be deleted");
                return true;
            }

            g.deleteEdge(startName, destName);
            return true;
        }
        return false;
        
    }

    /**
     * A main routine that:
     * 1. Reads a file containing edges (supplied as a command-line parameter);
     * 2. Forms the graph;
     * 3. Repeatedly prompts for request
     * The data file is a sequence of lines of the format
     *    source destination 
     */
    public static void main(String[] args )
    {
        Graph g = new Graph( );
        try
        {
            FileReader fin = new FileReader(args[0]);
            Scanner graphFile = new Scanner( fin );

            // Read the edges and insert
            String line;
            while( graphFile.hasNextLine( ) )
            {
                line = graphFile.nextLine( );
                StringTokenizer st = new StringTokenizer( line );

                try
                {
                    if( st.countTokens( ) != 3 )
                    {
                        System.err.println( "Skipping ill-formatted line " + line );
                        continue;
                    }
                    String source  = st.nextToken( );
                    String dest    = st.nextToken( );
                    double weight = Double.parseDouble(st.nextToken( ));
                    g.addUndirectedEdge( source, dest, weight);
                }
                catch( NumberFormatException e )
                  { System.err.println( "Skipping ill-formatted line " + line ); }
             }
             graphFile.close();
         }
         catch( IOException e )
           { System.err.println( e ); }

         System.out.println( "File read..." );
         System.out.println( g.vertexMap.size( ) + " vertices" );

         Scanner in = new Scanner( System.in );
         while( processRequest( in, g ) )
             ;
    }
}

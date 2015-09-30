package routing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class Routing {
    
    String ipFile;
    Ssp mainSSP;
    int sourceNode;
    int destNode;
    
    public Routing() {
        ipFile = "";
        mainSSP = new Ssp();
        sourceNode = -1;
        destNode = -1;
    }
    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();
        Routing routing = new Routing();
        
        routing.processCommandLineArguments(args);
        routing.mainSSP.processFile();
        routing.processFile();
        for(int i=0;i<routing.mainSSP.numVertices;i++) {
            routing.mainSSP.sourceNode = i;
            routing.mainSSP.runDijkstra();
            for(int j=0;j<routing.mainSSP.numVertices;j++) {
                int nextHop = routing.mainSSP.getNextHop(j);
                if(nextHop != -1) {
                    routing.mainSSP.vertices[i].btrie.addNode(routing.getKey(routing.mainSSP.vertices[j].ip), nextHop);
                }
            }
            routing.mainSSP.vertices[i].btrie.cleanUp();
            if(routing.mainSSP.vertices[i].btrie.checkStructure() == false) {
                System.out.println("Structure incorrect");
                System.exit(1);
            }
            //routing.mainSSP.vertices[i].btrie.displayTrie();
        }
        
        routing.transferPacket(routing.sourceNode, routing.destNode);
        /*
        routing.mainTrie.addNode(routing.getKey("192.168.1.2"), 3);
        routing.mainTrie.addNode(routing.getKey("192.168.100.2"), 3);
        routing.mainTrie.addNode(routing.getKey("192.168.255.255"), 3);
        routing.mainTrie.addNode(routing.getKey("127.0.0.1"), 2);
        routing.mainTrie.addNode(routing.getKey("255.255.255.255"), 3);
        routing.mainTrie.addNode(routing.getKey("0.0.0.1"), 1);
        routing.mainTrie.addNode(routing.getKey("4.25.255.90"), 1);
        routing.mainTrie.addNode(routing.getKey("40.168.1.2"), 2);
        routing.mainTrie.addNode(routing.getKey("192.255.1.2"), 3);
        routing.mainTrie.addNode(routing.getKey("192.168.255.2"), 5);
        routing.mainTrie.addNode(routing.getKey("192.168.1.255"), 5);
        routing.mainTrie.addNode(routing.getKey("255.0.0.0"), 3);
        routing.mainTrie.addNode(routing.getKey("2.1.1.2"), 1);
        routing.mainTrie.addNode(routing.getKey("172.161.1.2"), 5);
        routing.mainTrie.addNode(routing.getKey("45.254.1.2"), 2);
        routing.mainTrie.addNode(routing.getKey("45.168.255.2"), 2);
        routing.mainTrie.addNode(routing.getKey("45.254.254.254"), 2);
        routing.mainTrie.addNode(routing.getKey("96.96.96.96"), 2);
        routing.mainTrie.addNode(routing.getKey("1.19.1.96"), 1);
        routing.mainTrie.addNode(routing.getKey("0.255.0.255"), 1);
        routing.mainTrie.addNode(routing.getKey("56.56.12.34"), 2);
        routing.mainTrie.addNode(routing.getKey("1.2.3.4"), 1);
        
        
        routing.mainTrie.displayTrie();
        boolean success = routing.mainTrie.checkStructure();
        if(success == false) {
            System.out.println("Error in structure .... exiting");
            System.exit(1);
        }
        else
            System.out.println("Structure OK ... ");
        routing.mainTrie.cleanUp();
        System.out.println("Clean Up Done ... ");
        success = routing.mainTrie.checkStructure();
        if(success == false) {
            System.out.println("Error in structure .... exiting");
            System.exit(1);
        }
        else
            System.out.println("Structure OK ... ");
        routing.mainTrie.displayTrie();
        */
        final long endTime = System.currentTimeMillis();
        System.out.println("Execution Time: " + (endTime - startTime) + "ms"); /* Execution time of the algorithm */
    }
    
    private void transferPacket(int source, int dest) {
        System.out.print(source + " -- ");
        if(source == dest) {
            System.out.println("\n DONE ... ");
        }
        else {
            int hop = this.mainSSP.vertices[source].btrie.getNextHop(this.getKey(this.mainSSP.vertices[dest].ip));
            if (hop == -1) {
                System.out.println("\nNo Path found ... ");
                System.exit(1);
            }
            this.transferPacket(hop, dest);
        }
        
    }
    
    private void processFile() {
        StringTokenizer st;
        try {
            BufferedReader br = new BufferedReader(new FileReader(this.ipFile));
            
            String inputLines;
            int vertexCount = 0;
            while((inputLines = br.readLine()) != null) {
                if(!inputLines.trim().isEmpty()) { /* Avoid empty lines in the input file */
                    this.mainSSP.vertices[vertexCount].ip = inputLines.trim();
                    vertexCount++;
                }
            }
            if(vertexCount != this.mainSSP.numVertices) {
                System.out.println("Not sufficient ips ... Exiting ...");
                System.exit(1);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found ... Exiting");
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("Error in File Format ... Exiting");
            System.exit(1);
        }
    }
    
    private void processCommandLineArguments(String[] args) {
        if(args.length != 4) {
            System.out.println("Arguments not specified properly ....");
            System.out.println("Format: java -jar routing.jar <graph file> <ip file> <source vertex index> <destination vertex index>");
            System.exit(1);
        }
        this.mainSSP.inputFile = args[0];
        this.ipFile = args[1];
        this.sourceNode = Integer.parseInt(args[2]);
        this.destNode = Integer.parseInt(args[3]);
    }
    
    private boolean[] getKey(String ip) {
        
        StringTokenizer st = new StringTokenizer(ip, ".");
        int ip1 = Integer.parseInt(st.nextToken());
        int ip2 = Integer.parseInt(st.nextToken());
        int ip3 = Integer.parseInt(st.nextToken());
        int ip4 = Integer.parseInt(st.nextToken());
        //System.out.println(ip1 + "." + ip2 + "." + ip3 + "." + ip4);
        //System.out.println(Integer.toBinaryString(ip1) + " " + Integer.toBinaryString(ip2) + " " + Integer.toBinaryString(ip3) + " " + Integer.toBinaryString(ip4));
        boolean[] b = new boolean[32];
        for(int j=0;j<8;j++) {
            b[7-j] = (((ip1 >> j) & 1) == 1) ? true:false;
        }
        for(int j=0;j<8;j++) {
            b[15-j] = (((ip2 >> j) & 1) == 1) ? true:false;
        }
        for(int j=0;j<8;j++) {
            b[23-j] = (((ip3 >> j) & 1) == 1) ? true:false;
        }
        for(int j=0;j<8;j++) {
            b[31-j] = (((ip4 >> j) & 1) == 1) ? true:false;
        }
        
        return b;
    }
}

package logesticHW1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

import logesticHW1.BFS.Node;

public class DFS {
	public HashMap<Integer,ArrayList<Integer>> list;
	public HashMap<Integer,Node> node;
	public ArrayList<Integer> G;
   public DFS() {
	   list = new HashMap<Integer,ArrayList<Integer>> ();
	   node = new HashMap<Integer,Node>();
	   G = new ArrayList<Integer>();
   }
   public class Node {        
   	public int color;//1:white 2: grey 3:black
   	public ArrayList<Node> adj;
   	public Node() {        		
   	}
 
   }
   void createNetwork() {
   	node = new HashMap<Integer,Node>();
   	for(int i:list.keySet()) {
   		G.add(i);
   		node.put(i, new Node());
   	}
   	for(int i:G) {
   		node.get(i).color=1;
   	}
   }
   boolean runDFS() {
	   for(int u:G) {
		   if(node.get(u).color==1) {
			   DFSVisit(u);
		   }
	   }
	   for(int i:G) {
		   if(node.get(i).color==1) {
			   System.out.println("not connected");
			   return false;
		   }		  
	   }
	   System.out.println("connceted");
	   return true;
   }
   void DFSVisit(int u) {
	   node.get(u).color=2;
	   for(int v:list.get(u)) {
		   if(node.get(v).color==1) {
			   DFSVisit(v);
		   }
	   }
	   node.get(u).color=3;
   }
   void readData(String filename) {
		try {
			
			Scanner scanner = new Scanner(new File(filename));
		    ArrayList<String> str = new ArrayList<String>();   	   
			while(scanner.hasNext()) {
				String[] row = scanner.nextLine().split("/t");	
				str.add(row[0]);
			   }
			int size = str.size();
			//System.out.println("size"+size);
			Integer[] column1 = new Integer[size];
			Integer[] column2 = new Integer[size];
			for(int i=0;i<size;i++) {
				//System.out.println(str.get(i));
				String[] temp = str.get(i).trim().split("\\s+");					
				//System.out.println(temp[0]);
				//System.out.println(temp[1]);				
				column1[i]=(int)Double.parseDouble(temp[0]);
				column2[i]=(int)Double.parseDouble(temp[1]);
			}
			
			for(int i=1;i<size;i++) {
				if(list.containsKey(column1[i])==true) {
					//System.out.println(i);
					list.get(column1[i]).add(column2[i]);
				}else {
					//System.out.println(i);
					list.put(column1[i],new ArrayList<Integer>(Arrays.asList(column2[i])) );
				}
			}
			for(int i=1;i<size;i++) {
				if(list.containsKey(column2[i])==true) {
					//System.out.println(i);
					list.get(column2[i]).add(column1[i]);
				}else {
					//System.out.println(i);
					list.put(column2[i],new ArrayList<Integer>(Arrays.asList(column1[i])) );
				}
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
   public static void main(String arg[]) {
	   DFS exa = new DFS();
	   exa.readData("test3.graph.txt");
	   exa.createNetwork();
	   exa.runDFS();
   }
}

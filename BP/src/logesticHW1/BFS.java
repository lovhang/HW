package logesticHW1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class BFS {
	public HashMap<Integer,ArrayList<Integer>> list;
	public ArrayList<Integer> G;
	public Integer s;
	public HashMap<Integer,Node> node;
	public double M;
	public LinkedList<Integer> Q;
	public ArrayList<Integer>  groupA;
	public ArrayList<Integer>  groupB;
        public BFS() {
        	list = new HashMap<Integer,ArrayList<Integer>>();
        	G =  new ArrayList<Integer>();
        	Q =  new LinkedList<Integer>();
        	groupA = new ArrayList<Integer>();
        	groupB = new ArrayList<Integer>();       
        	s=0;
        	M=100000;
        }
        
        public class Node {        
        	public int color;//1:white 2: grey 3:black
        	public double d;//distance 
        	public int pi;//parents
        	public ArrayList<Node> adj;
        	public double group;
        	public Node() {        		
        	}
      
        }
        void createNetwork() {
        	node = new HashMap<Integer,Node>();
        	for(int i:list.keySet()) {
        		G.add(i);
        		node.put(i, new Node());
        	}
  	
        }
        boolean runBFS() {
        	G.remove(s);
        	for(int i:G) {
        		node.get(i).color=1;
        		node.get(i).group=0;;
        	}
        	//System.out.println("node 1 color" + node.get(1).color);
        	node.get(s).color = 2;
        	node.get(s).group=1;
        	Q.add(s);
        	while(Q.isEmpty()==false) {
        		int u = Q.getFirst();//get the first element in Q DEQUEUE
        		Q.poll();//remove the first element
        		for(int v:list.get(u)) {
        			if(node.get(v).color==1) {
        				node.get(v).color = 2;
        				//node[v].d=node[v].d+1;
        				//node[v].pi = u;
        				node.get(v).group = node.get(u).group*(-1);
        				for(int w:list.get(v)) {
        					if(node.get(w).group == node.get(v).group) {
        						System.out.println("not bipartite");
        						return false;
        					}
        				}
        				Q.add(v);;//ENQUEUE
        			}
        			
        		}
        		node.get(u).color = 3;
        	}
        	
        	for(int i:G) {
        		if(node.get(i).group==-1) {
        			groupA.add(i);
        		}else {
        			groupB.add(i);
        		}
        		
        	}
        	System.out.println("bipartite");
        	return true;
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
					String[] temp = str.get(i).split(" ");					
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
        void outputGroup() {
        	
        	System.out.println("V1 list:");
        	for(int i=0;i<groupA.size();i++) {
        		System.out.print(groupA.get(i)+",");
        	}
        	System.out.println("");
        	System.out.println("V2 list:");
        	for(int i=0;i<groupB.size();i++) {
        		System.out.print(groupB.get(i)+",");
        	}
        }
        public static void main(String arg[]) {
        	BFS exa = new BFS();
        	exa.readData("test1.graph.txt");
        	exa.createNetwork();
        	System.out.println(exa.runBFS());
        	exa.outputGroup();
        	
        }
}

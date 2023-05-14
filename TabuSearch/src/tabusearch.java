import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class tabusearch {
	private double[] x;
	private double[] y;
	private int n;
	private double[][] dis;
	private ArrayList<Integer> Xnow;
	private int cn;//candidate number
	HashMap<Integer,Double> shortermcost;//cost-frequency 
	HashMap<Integer,Double> totalcost;//cost
	HashMap<Integer,ArrayList<Integer>> candidatelist;//
	HashMap<Integer,ArrayList<Integer>> swaplist;//
	private int[][] tabutenure;
	private int[][] frequency;
	private double M;//bigM
	private int ite;//iteration number
	private double bestcost;//currently best cost
	private int tsp;//tabustep
	private double df;//diversification factor
       public tabusearch() {
    	   Xnow = new ArrayList<>();    
    	   cn=10;
    	   M=1000000;
    	   bestcost=M;
    	   tsp=8;
    	   df=5.0;
    	   ite=1000;
       }
       public void ts() {
    	   readdata();
    	   Xnow=initiate();    	   
       }
       
       public void mainbody() {
    	   readdata();
    	   Xnow = initiate();
    	   //n=Xnow.size();
    	   for(int i=0;i<ite;i++) {
    		   System.out.println("=======iteration "+i+"===========");
    		   candidate(Xnow);
    		   Xnow = select();
    		   //ttoutput();
    		   System.out.println("=========best cost  "+bestcost+"==========");
    		   updatetenure();
    	   }
       }
       
       public void candidate(ArrayList<Integer> a) {
    	   //initiate shorterm list from 1 to cn
    	     shortermcost = new HashMap<Integer,Double>();
    	     totalcost = new HashMap<Integer,Double>();
    	     candidatelist = new HashMap<Integer,ArrayList<Integer>>(); 
    	     swaplist = new HashMap<Integer,ArrayList<Integer>>();
    	     for(int i=1;i<=cn;i++) {
    	    	 shortermcost.put(i, M);
    	    	 totalcost.put(i,M);
    	    	 candidatelist.put(i, new ArrayList<Integer>());
    	    	 swaplist.put(i,new ArrayList<Integer>());
    	     }
    	     //System.out.println(shortermcost);
    	     //start to generate neighbor
    	     for(int i=0;i<a.size()-1;i++) {
    	    	 for(int j=i+1;j<a.size();j++) {
    	    		 //System.out.println("=============");
    	    		 ArrayList<Integer> temp = swap(a,i,j);
    	    		 insert(temp,i,j);    	    		 
    	    		 //System.out.println(shortermcost);
    	    		 //System.out.println(candidatelist);
    	    		 //System.out.println(swaplist);
    	    	  }
    	     }
    	       //System.out.println(shortermcost);
    	       //System.out.println(totalcost);
    		   //System.out.println(candidatelist);
    		  //System.out.println(swaplist);
    	     
       }
       //select best candidate for next iteration
       public ArrayList<Integer> select() {
    	   
    	   ArrayList<Integer> best = new ArrayList<Integer>();
    	   ArrayList<Integer> removelist = new ArrayList<Integer>();
    	   ArrayList<Integer> costlist = new ArrayList<Integer>();//total cost for all candidate 
    	   double tempbestcost = totalcost.get(1);
    	   //updatetenure();
    	   if(tempbestcost<bestcost) {
    		   bestcost=tempbestcost;
    		   best=candidatelist.get(1);
               int ti = swaplist.get(1).get(0);
               int tj = swaplist.get(1).get(1);
               frequency[ti][tj]=frequency[ti][tj]+1;
               tabutenure[ti][tj]=tsp;
               System.out.println(ti+" == "+tj);
    	   }else {
    	      for(int i=1;i<=cn;i++) {
    		      ArrayList<Integer> swape = swaplist.get(i);
    		      if(tabutenure[swape.get(0)][swape.get(1)]>0) {
    			     removelist.add(i);
    	   	      }
    	      }
    	      //System.out.println(swaplist);
    	      //System.out.println(removelist);
    	      for(int i:removelist) {
    	    	  shortermcost.remove(i);
    	    	  totalcost.remove(i);
    	   	      candidatelist.remove(i);
    	   	      swaplist.remove(i);
    	      }
    	      int first=n;
    	      for(int i:swaplist.keySet()) {
 	                if(i<first) {
 	                	first=i;
 	                }
    	      }
    	      //System.out.println(first);
    	      //System.out.println(swaplist);
    	      int ti = swaplist.get(first).get(0);
              int tj = swaplist.get(first).get(1);
              frequency[ti][tj]=frequency[ti][tj]+1;
              tabutenure[ti][tj]=tsp;
              //System.out.println(ti+" == "+tj);
       	      best = candidatelist.get(first);
    	   }
    	   return best;
       }
       
       public void updatetenure() {
    	   for(int i=1;i<n;i++) {
    		   for(int j=i+1;j<=n;j++) {
    			   if(tabutenure[i][j] >0) {
    				   tabutenure[i][j]=tabutenure[i][j]-1;
    			   }
    		   }
    	   }
       }
       //output tabutanure
       public void ttoutput() {
    	   System.out.println("=========tabu list output=================");
    	   for(int i=1;i<n;i++) {
    		   for(int j=i+1;j<=n;j++) {
    			   if(tabutenure[i][j] >0) {
    				   System.out.println(i+" -- "+j+" : "+tabutenure[i][j]);
    			   }
    		   }
    	   }
    	   
    	   System.out.println("=========frequecy list output=================");
    	   for(int i=1;i<n;i++) {
    		   for(int j=i+1;j<=n;j++) {
    			   if(frequency[i][j] >0) {
    				   System.out.println(i+" -- "+j+" : "+frequency[i][j]);
    			   }
    		   }
    	   }
       }
       public ArrayList<Integer> swap(ArrayList<Integer> a, int b, int c){
    	   ArrayList<Integer> temp = new ArrayList<Integer>();
    	   if(b>c) {
    		   System.out.println("b>c");
    	   }else if(b==0 && c==a.size()-1) {
    		   temp.add(a.get(a.size()-1));
    		   for(int i=b+1;i<c;i++) {
    			   temp.add(a.get(i));
    		   }
    		   temp.add(a.get(0));
    	   }else if(b==0 && c<a.size()-1) {
    		   temp.add(a.get(c));
    		   for(int i=b+1;i<c;i++) {
    			   temp.add(a.get(i));
    		   }
    		   temp.add(a.get(0));
    		   for(int i=c+1;i<a.size();i++) {
    			   temp.add(a.get(i));
    		   }
    	   }else if(b>0 && c==a.size()-1) {
    		   for(int i=0;i<b;i++) {
    			   temp.add(a.get(i));
    		   }
    		   temp.add(a.get(a.size()-1));
    		   for(int i=b+1;i<c;i++) {
    			   temp.add(a.get(i));
    		   }
    		   temp.add(a.get(b));
    	   }else {
    		   for(int i=0;i<b;i++) {
    			   temp.add(a.get(i));    			   
    		   }
			   temp.add(a.get(c));
			   for(int i=b+1;i<c;i++) {
				   temp.add(a.get(i));
			   }
			   temp.add(a.get(b));
			   for(int i=c+1;i<a.size();i++) {
				   temp.add(a.get(i));
			   }
    	   }
    	   return temp;
       }
       
       public void insert(ArrayList<Integer> a, int b, int c) {
    	   double cost1 = getcost(a);   	   
    	   HashMap<Integer,Double> tempshortermcost = (HashMap<Integer, Double>) shortermcost.clone();
    	   HashMap<Integer,Double> temptotalcost = (HashMap<Integer, Double>) totalcost.clone();
    	   HashMap<Integer,ArrayList<Integer>> tempcandidatelist = (HashMap<Integer, ArrayList<Integer>>) candidatelist.clone();
    	   ArrayList<Integer> swape = new ArrayList<Integer>();
    	   int h=a.get(b);
    	   int t=a.get(c);
    	   double cost=cost1+df*frequency[h][t];//cost minus frequency
    	   if(h<t) {
    	   swape.add(h);swape.add(t);
    	   }else{
    	   swape.add(t);swape.add(h);		  
    	   }
    	   HashMap<Integer,ArrayList<Integer>> tempswaplist = (HashMap<Integer, ArrayList<Integer>>) swaplist.clone();
    	   //System.out.println("cost"+cost);
    	   for(int i=1;i<=cn;i++) {
    		   if(cost<shortermcost.get(i)) {
    			   tempshortermcost.put(i, cost);
    			   temptotalcost.put(i, cost1);
    			   tempcandidatelist.put(i, a);   
    			   tempswaplist.put(i, swape);
                   for(int j=i;j<=cn;j++) {
                	   tempshortermcost.put(j+1, shortermcost.get(j));
                	   temptotalcost.put(j+1, totalcost.get(i));
                	   tempcandidatelist.put(j+1, candidatelist.get(j));
                	   tempswaplist.put(j+1, swaplist.get(j));
                   }
                   tempshortermcost.remove(cn+1);
                   temptotalcost.remove(cn+1);
                   tempcandidatelist.remove(cn+1);
                   tempswaplist.remove(cn+1);
                   break;
    		   }
    		   tempshortermcost.put(i, shortermcost.get(i));
    		   temptotalcost.put(i, totalcost.get(i));
    		   tempcandidatelist.put(i, candidatelist.get(i));
    		   tempswaplist.put(i, swaplist.get(i));
    		   //System.out.println("tempcost " +tempshortermcost);
    	   }
    	   shortermcost = tempshortermcost;
    	   totalcost = temptotalcost;
    	   candidatelist = tempcandidatelist;
    	   swaplist = tempswaplist;
       }
       
       
       public double getcost(ArrayList<Integer> a) {
    	   double temp=0;
    	   for(int i=0;i<a.size()-1;i++) {
    		   int h = a.get(i);
    		   int t = a.get(i+1);
    		   temp = temp+dis[h][t];
    	   }
    	   temp = temp+dis[a.get(a.size()-1)][a.get(0)];
    	   return temp;
       }
       
       
       public void readdata() {
    	   try {
    		   File file = new File("622.txt");
    		   BufferedReader br = new BufferedReader(new FileReader(file));
    		   br.readLine();br.readLine();br.readLine();br.readLine();br.readLine();
    		   String st = "";
    		   st=br.readLine();
    		   //System.out.println(st);
    		   String[] col = st.split("\\s+");
    		   //System.out.println(col[2]);
    		   n=Integer.parseInt(col[2]);
    			x = new double[n+1];
    			y = new double[n+1];
    			dis = new double[n+1][n+1];	
    			tabutenure = new int[n+1][n+1];
    			frequency = new int[n+1][n+1];
    			br.readLine();br.readLine();
    			String line = br.readLine();
    			while(line != null) {
    				if(line.equals("EOF")==false) {
    				   //System.out.println(line);
    				   col = line.split("\\s+");
    				   int k = Integer.parseInt(col[0]);
    				   double a=Integer.parseInt(col[1]);
    				   double b=Integer.parseInt(col[2]);
    	               x[k]=a;y[k]=b;
    				}
    				line = br.readLine();
    			}
    			for(int i=1;i<=n;i++) {
    				   for(int j=1;j<=n;j++) {
    					   dis[i][j] = Math.sqrt((x[i]-x[j])*(x[i]-x[j])+(y[i]-y[j])*(y[i]-y[j]));
    					   tabutenure[i][j]=0;
    					   frequency[i][j]=0;
    				   }
    			   }
    	   }catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
       }
       
       
       public ArrayList<Integer> initiate(){
    	   ArrayList<Integer> temp = new ArrayList<Integer>();
    	   ArrayList<Integer> node = new ArrayList<Integer>();
    	   for(int i=1;i<=n;i++) {
    		   node.add(i);
    	   }
    	   for(int i=1;i<=n;i++) {
    		   int p =ThreadLocalRandom.current().nextInt(0, node.size());
    		   int t=node.get(p);
    		   temp.add(t);
    		   node.remove(p);
    	   }
    	   return temp;
       }
       
       
       public void test() {
    	   readdata();	  
    	   mainbody();  	   
       }
       
       
       public static void main(String[] args) {
    	   tabusearch exa = new tabusearch();
    	   exa.test();
       }
       
}

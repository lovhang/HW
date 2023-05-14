import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class smallNW {
   private double[] x;
   private double[] y;
   private int n;
   private double[][] dis;
   private ArrayList<edge> route;
   private edge[][] edgeset;
   private int TL;//tempreture length
   private double T;
   private double alp;// alpha
   private double M;//bigM
   private double rcost;
   private double dc;//delta c
   private double fc;//final cost
   public smallNW() {
	   n=6;
	   x = new double[n+1];
	   y = new double[n+1];
	   dis = new double[n+1][n+1];
	   for(int i=1;i<=n;i++) {
		   x[i]=Math.random()*100+1;
		   y[i]=Math.random()*100+1;
	   }
	   for(int i=1;i<=n;i++) {
		   for(int j=1;j<=n;j++) {
			   dis[i][j] = Math.sqrt((x[i]-x[j])*(x[i]-x[j])+(y[i]-y[j])*(y[i]-y[j]));
		   }
	   }
	   edgeset = new edge[n+1][n+1];
	   for(int i=1;i<=n;i++) {
		   for(int j=i+1;j<=n;j++) {
			  edgeset[i][j]=new edge(i,j,dis[i][j]);
		   }
	   }
	   route = new ArrayList<edge>();
	   
   }
   public void initiate() {
	   
	   for(int i=1;i<=n-1;i++) {
		   route.add(edgeset[i][i+1]);
	   }
	   route.add(edgeset[1][6]);
   }
   //generate neighbour solution
   public ArrayList<edge> genenei(ArrayList<edge> e) {
	  
	   ArrayList<Double> costlist = new ArrayList<Double>();

			   double cost=0;			   
			   edge tempa = new edge(1,2,2.0);; edge tempb = new edge(1,2,2.0);;
			   int tempah =0;int tempbh=0;int tempat=0;int tempbt=0;
			   ArrayList<edge> temp = new ArrayList<edge>();
			   int i=0; int j=1;
				
			        temp =(ArrayList<edge>) e.clone();
			        i = ThreadLocalRandom.current().nextInt(0, temp.size() );		   
			        tempa=temp.get(i);
			        tempah=tempa.getA();
			        tempat=tempa.getB();
			        temp.remove(i);
			        tempbh = tempah;
			        tempbt = tempat;
			        while(tempbh == tempah || tempbt == tempat) {
			          j = ThreadLocalRandom.current().nextInt(0, temp.size() );
			          //System.out.println(j);
			          tempb=temp.get(j);
			          tempbh=tempb.getA();
			          tempbt=tempb.getB();
			          //output(temp);   	     
			   }	
			   //System.out.println("i "+i+" j "+j);
			   temp.remove(j);

			   //System.out.println("out put temp======");
			   //output(temp);
			   //System.out.println("ah "+tempah+" at "+tempat+" bh "+tempbh+" bt "+tempbt);
			   if(tempah<tempbh) {
			       temp.add(edgeset[tempah][tempbh]);
			      
			       cost=cost+edgeset[tempah][tempbh].cost();
			   }else {
				   temp.add(edgeset[tempbh][tempah]);
				
				   cost=cost+edgeset[tempbh][tempah].cost();
			   }
			   if(tempat<tempbt) {
				   temp.add(edgeset[tempat][tempbt]);
				
				   cost=cost+edgeset[tempat][tempbt].cost();
			   }else {
				   temp.add(edgeset[tempbt][tempat]);
				  
				   cost=cost+edgeset[tempbt][tempat].cost();
			   }
			   if(isfeasible(temp)==true) {
				   cost=-tempa.cost()-tempb.cost();
				   costlist.add(cost);				  
			   }else {
				    cost=0;
				    temp =(ArrayList<edge>) e.clone();				   
				    temp.remove(i);
				    temp.remove(j);
				    if(tempah<tempbt) {       
				       temp.add(edgeset[tempah][tempbt]);
				   
				       cost=cost+edgeset[tempah][tempbt].cost();
				    }else {
					   temp.add(edgeset[tempbt][tempah]);
					 
					   cost=cost+edgeset[tempbt][tempah].cost();
				    }
				    if(tempat<tempbh) {
					   temp.add(edgeset[tempat][tempbh]);
				
					   cost=cost+edgeset[tempat][tempbh].cost();
				    }else {
					   temp.add(edgeset[tempbh][tempat]);
					
					   cost=cost+edgeset[tempbh][tempat].cost();
				    }
				    cost=-tempa.cost()-tempb.cost();
				    costlist.add(cost);		   
	            }
			   
			   return temp;
          }
   //circle the route to delete elements. if it become empty it is feasible, otherwise not.
   public boolean isfeasible(ArrayList<edge> es) {
	   ArrayList<edge> el =(ArrayList<edge>) es.clone();
	   edge temp = el.get(0);
	   el.remove(0);
	   int iterate=0;
	   while(iterate==0){
		   int tempc=0;
	      for(int i=0;i<el.size();i++){
	    	  edge e = el.get(i);
		         if( connect(e,temp)==true ){
		        	 temp=e;
		    	     tempc=1;
		    	     el.remove(i);
		    	     //output(el);
		    	    // System.out.println("===============");
		    	     break;
		         }
	         }
	      if(tempc==0){
	    	  iterate=1;
	      }
	   }
	   if(el.size()==0) {
		   //System.out.println("feasible path");
		   return true;
	   }else {
		   //System.out.println("infeasible path");
		   return false;
	   }
   }
   
   public boolean connect(edge a, edge b) {
	   int tempah=a.getA();
	   int tempat=a.getB();
	   int tempbh=b.getA();
	   int tempbt=b.getB();
	   if(tempah==tempbh) {
		   return true; 
	   }else if(tempah==tempbt) {
		   return true;
	   }else if(tempat==tempbh) {
		   return true;
	   }else if(tempat==tempbt){
		   return true;
	   }else {
		   return false;
	   }
			   
   }
   
   public void mainbody() {
	   readdata();//read date
	   route=genefeasi();//generate initial route
	   rcost = statecost(route);//calculate cost for route
	   TL = 200;//set temp length
	   alp=0.99;
	   T=1000;
	   double tc=0.001;//temp crateria
	   double e = 2.71828;
	   ArrayList<edge> neighbor = new ArrayList<edge>();
	   fc = rcost;
	while(T>tc) {   
	   for(int i=1;i<TL;i++) {
		   neighbor = genenei(route);
		   rcost = statecost(route);
		   double tempcost = statecost(neighbor);
		   dc=tempcost-rcost;
		
		   if(dc<=0) {
			   route=neighbor;
			   //System.out.println("accept by pre: "+rcost+" cur: "+tempcost);
		   }else {
			   double q = Math.random();
			   double mi = -(dc/T);
			   double ratio = Math.pow(e,mi);
			   if(q<ratio) {
				   route = neighbor;
				   //System.out.println("acctpt by q: "+q+" < ratio "+ratio);
			   }else {
			   //System.out.println("reject by q: "+q+" > ratio "+ratio);
			   }
		   }
		   double updatecost = statecost(route);
		   if (fc>updatecost) {
			   fc=updatecost;
		   }
		   //System.out.println(updatecost);
		   System.out.println("best state so far" + fc);
		   System.out.println("===============");		   
	   }
	   T=T*alp;
	}
	   
   }
   public void readdata() {
	   
	   try {
		File file = new File("380.txt");
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
		//route = new ArrayList<edge>();
		//System.out.println(n);
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
			   }
		   }
		   edgeset = new edge[n+1][n+1];
		   for(int i=1;i<=n;i++) {
			   for(int j=i+1;j<=n;j++) {
				  edgeset[i][j]=new edge(i,j,dis[i][j]);
			   }
		   }

	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
   
   public ArrayList<edge> genefeasi(){
	   int h=1;
	   ArrayList<Integer> node = new ArrayList<Integer>();
	   ArrayList<edge> tempf = new ArrayList<edge>();
	 for(int i=1;i<=n;i++) {
		node.add(i); 
	 }
	 node.remove(0);
	 //System.out.println(node.size());
	 for(int i=1;i<n;i++) {
		 //System.out.println(i);
	  int p =ThreadLocalRandom.current().nextInt(0, node.size());	
	  int t=node.get(p);
	  if(h<t) {
		  tempf.add(edgeset[h][t]);
		  edgeset[h][t].name();
	  }else if(h>t){
		  tempf.add(edgeset[t][h]);		
		  edgeset[t][h].name();
	  }
	  node.remove(p);
	  h=t;
	  //System.out.println("h "+h);
	 }
	 tempf.add(edgeset[1][h]);
	
	 return tempf;
   }
   public double statecost(ArrayList<edge> a) {
	   double temp=0;
	   for(int i=0;i<a.size();i++) {
		   //a.get(i).name();
		   temp = temp+ a.get(i).cost();
		   //System.out.println("cost "+a.get(i).cost()+" total cost "+temp);
	   }
	   return temp;
   }
   
   public void test() {
	   mainbody();
   }
   
   public void output(ArrayList<edge> a) {
	   for(int i=0;i<a.size();i++) {
		   a.get(i).name();
		   double c = a.get(i).cost();
		   //System.out.println(c);
	   }
   }
   public void test1() {
	   readdata();//read date
	   route=genefeasi();//generate initial route
	   statecost(route);
	   //output(route);
   }
   public static void main(String[] args) {
	   smallNW exa = new smallNW();
	   exa.test();
   }
}

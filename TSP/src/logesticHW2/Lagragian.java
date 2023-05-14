package logesticHW2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Lagragian {
	private int size;
	private double[][] c;
	private Set<Set<Integer>> subset;
	private HashMap<Integer,ArrayList<Integer>> tree;
	private ArrayList<Integer> T;//node that in tree
	private ArrayList<Integer> N;//node that not in tree
	private double mincost;
	private int h;//head of cut
	private int t;//tail of cut
	private double eta;
	private int K;//iteraion step
	private double M;
	private double r;
	private double[] u;
	private double[] g;//gradient for node i
	private int[][] x;
	private double lamda;
	private double L[];
	private double objV;//objective value
	private double[][] ce;//cost
	private double base; //summation of subgradient
	
	public Lagragian(String input,int K,double M,double r,double eta) {
		data a = new data(input);
		//this.c=a.c;
		this.ce=a.c;
		//this.subset=a.subsets;
		this.size=a.size;
		g = new double[size];
		u = new double[size];
		x = new int[size][size];
		u = new double[size];
		this.K=K;
		this.M=M;
		this.r=r;
		
		//System.out.println(N);		
		//creattree();
	}
	void creattree() {
//minimum spanning tree with its neighbour		
	tree= new HashMap<Integer,ArrayList<Integer>>();
	T=new ArrayList<Integer>();
	N=new ArrayList<Integer>();
	T.add(1);
	for(int i=2;i<size;i++) {
		N.add(i);
	}
	   while(N.isEmpty() == false) {
		   mincost=Double.MAX_VALUE;
		  // System.out.println("FN"+N);
		   for(int i:T) {
			   for(int j:N) {
				   //System.out.println(c[i][j]);
				   if(c[i][j]<mincost) {
					   
					  mincost=c[i][j];
					  h=i;
					  t=j;
					  //System.out.println(h+"--"+t+"=="+c[i][j]);
				   }
			   }
		   }		   
           //System.out.println(h+"--"+t);           
		   N.remove(N.indexOf(t));
		   //System.out.println("AF"+N);
		   //System.out.println(N);
		   T.add(t);		 
		   if(tree.containsKey(h)){
		   tree.get(h).add(t);
		   }else {
			   tree.put(h, new ArrayList<Integer>() {{add(t);}});
		   }
		   if(tree.containsKey(t)){
			tree.get(t).add(h);   
		   }
		   else {
			   tree.put(t, new ArrayList<Integer>() {{add(h);}});
		   }		   
	   }
	  // System.out.println(tree);
	   
//looking for least 2 cost egde orinated from node 0
	   int node=0;
	   for(int i=1;i<size;i++) {
		   N.add(i);
	   }
	  
	   
	   ArrayList<Integer> templist = new ArrayList<Integer>();
	   for(int j=0;j<2;j++) {
		   mincost = Double.MAX_VALUE;
	     for(int i:N) {
		   if(c[0][i]<mincost) {
			   node=i;			   
			   mincost=c[0][i];
		   }
	     }
		//System.out.println(node);
	    templist.add(node);
	    tree.get(node).add(0);
	    N.remove(N.indexOf(node));
	   }
	   tree.put(0, templist);
	  // System.out.println(tree);	  
	}
	void update() {
//udapte cost	
		//System.out.println(u[0]);
		c=new double[size][size];
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				if(j != i) {
					//System.out.println(ce[i][j]);
					//System.out.println(u[i]);
					//System.out.println(u[j]);
				c[i][j]=ce[i][j]-u[i]-u[j];
				
				
				//System.out.println(c[i][j]);
				}
			}
		}
		
		x=new int[size][size];
		creattree();
		for(int i:tree.keySet()) {
			for(int j:tree.get(i)) {
				x[i][j]=1;
			}
		}
		//objV=0;
		double temp =0;
		
		for(int i=0;i<size;i++) {	
			for(int j=i;j<size;j++) {
				temp=temp+ce[i][j]*x[i][j];
			}
		}
		if(temp < objV) {
			objV=temp;
		}
//update u 		
		System.out.println(tree);
		for(int i=1;i<size;i++) {
			g[i]=2;
		}
		for(int i=1;i<size;i++) {
			for(int j:tree.get(i)) {
				g[i]=g[i]-x[i][j];
			}
		}
		/*
		 for(int i=1;i<size;i++) {   
			 System.out.println("g."+i+":"+g[i]);
		 }
		System.out.println("g1"+g[1]);
		*/
		 base=0;
		double temp1=0;
		for(int i=1;i<size;i++) {
			temp1=temp1+g[i]*g[i];
		}
		base = Math.sqrt(temp1);
		System.out.println("base:"+base);
		
		System.out.println("lamda:"+lamda);
		for(int i=1;i<size;i++) {
			if((u[i]+lamda*g[i]/base)<=0) {
				u[i]=0;
			}else {
				u[i]=u[i]+lamda*g[i]/base;
			}
		}
		System.out.println("u1:"+u[1]);
	}   
	void runLagrangian() {
		u[0]=0;
		for(int i=1;i<size;i++) {
			u[i]=1;
		}
		lamda=M*r;
		int iteration=0;
		base=1;
		objV=Double.MAX_VALUE;
		while(iteration <K && base>0) {
			System.out.println("=========itaration"+iteration+"========");
			
			lamda=lamda*r;
			update();
			
			System.out.println("objective value :"+objV);
			iteration++;
		}
	}
	void output() {
		for(int i:tree.keySet()) {
			for(int j:tree.get(i)) {
			System.out.println(i+"--"+j);
		}
	}
	}
	public static void main(String[] args)throws Exception{
		Lagragian exa = new Lagragian("TSP_instance_n_70_s_5.dat",70,100,0.5,0.1);
		//exa.output();
		exa.runLagrangian();
	}
}

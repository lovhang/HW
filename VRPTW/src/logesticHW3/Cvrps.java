package logesticHW3;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ilog.concert.*;
import ilog.cplex.*;
public class Cvrps {
private double[][] c;
private IloIntVar[][][] x;
private IloIntVar[][] xp;
private IloNumVar[] y;
private  IloLinearNumExpr obj;
private IloLinearNumExpr expr; 
private IloCplex cplex;
private String[] col;
private ArrayList<Integer> N;//collection of node
private int n;//number of node
private int K;//number of vehicle
private double[] xcoord;
private double[] ycoord;
private double[] demand;
private double capacity;
private double[][] s;//saving
private int[] head;//node that is head of route
private HashMap<Integer,ArrayList<int[]>> route;//route for vehicle
private ArrayList<int[]> edgelist;
private ArrayList<Double> safelist;
private double[] cap;//load on vehicle
private HashMap<Integer,int[]> heads;//head for vehicle k
private double objV;//objective value
	public Cvrps() {
		
	}
	void readData(String a) {		
		try {
			File file = new File(a);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String st ="";
			
			br.readLine();
			st=br.readLine();
			col=st.split(" ");
			st=col[9];
			char[] ch = st.toCharArray();
			String temp = ""+ch[0];
			K=Integer.parseInt(temp);
			br.readLine();
			st=br.readLine();
			
			col = st.split(" ");	
			//System.out.println(st+"");
			n= Integer.parseInt(col[col.length-1]);	
			xcoord = new double[n+2];
			ycoord = new double[n+2];
			demand = new double[n+2];
			c = new double[n+2][n+2];
			s = new double[n+2][n+2];
			br.readLine();	
			st=br.readLine();
			col=st.split(" ");
			capacity = Double.parseDouble(col[2]);	
			br.readLine();
			
			
			for(int i=1;i<n+1;i++) {
				st = br.readLine();
				col = st.split(" ");
				//N.add(Integer.parseInt(col[0]));
				xcoord[i]=Double.parseDouble(col[2]);
				ycoord[i]=Double.parseDouble(col[3]);				
			}
			xcoord[n+1]=xcoord[1];
			ycoord[n+1]=ycoord[1];//depot nodes
			br.readLine();
			for(int i=1;i<n+1;i++) {
				st = br.readLine();
				col=st.split(" ");
				demand[i]=Double.parseDouble(col[1]);
			}
			demand[n+1]=demand[1];
			for(int i=1;i<n+2;i++) {
				//System.out.println(ycoord[i]);
			}
			for(int i=1;i<n+2;i++) {
				for(int j=1;j<n+2;j++) {					
					c[i][j]= Math.sqrt((xcoord[i]-xcoord[j])*(xcoord[i]-xcoord[j])+(ycoord[i]-ycoord[j])*(ycoord[i]-ycoord[j]));
				}
			}
			System.out.println("==============");
			System.out.println("===truck:"+K+"=====");
			System.out.println("===capacity:"+capacity+"====");
			System.out.println("===number of node:"+n+"====");
		} catch (FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	Set<Set<Integer>> powerSet(ArrayList<Integer> set) {
		ArrayList<Integer> element = set;
		final int SET_LENGTH = 1 << element.size();		
		Set<Set<Integer>> powerSet = new HashSet<>();	
		//System.out.println(SET_LENGTH);
		for(int binarySet=0; binarySet<SET_LENGTH;binarySet++) {
			//System.out.println("binarySet=="+binarySet);
			Set<Integer> subset =new HashSet<>();
			
			for(int bit = 0;bit< element.size();bit++) {
				//System.out.println("bit==="+bit);
				int mask =1 << bit;
				//System.out.println("mask==="+mask);
				if((binarySet & mask)!=0) {
					//System.out.println("add element to subset"+bit);
					subset.add(element.get(bit));
				}						
			}
			powerSet.add(subset);
		}
		return powerSet;
	}

	
	void polyCVRP() {
		try {
			
			cplex = new IloCplex();
			xp= new IloIntVar[n+2][n+2];
			y = new IloNumVar[n+2];
			for(int i=1;i<n+2;i++) {
				for(int j=1;j<n+2;j++) {
					xp[i][j]=cplex.boolVar();
					xp[i][j].setName("x."+i+"."+j);
				}
				y[i]=cplex.numVar(demand[i], capacity);
				y[i].setName("y."+i);
			}
			obj = cplex.linearNumExpr();
			for(int i=1;i<n+2;i++) {
				for(int j=1;j<n+2;j++) {
					obj.addTerm(c[i][j], xp[i][j]);
				}
			}
			
			cplex.addMinimize(obj);
//constraint 1			
			for(int i=2;i<n+1;i++) {
				expr=cplex.linearNumExpr();
				for(int j=2;j<n+2;j++) {
					if(j != i) {
						expr.addTerm(1.0, xp[i][j]);
					}
				}
				cplex.addEq(expr, 1);
			}
//constraint 2			
			for(int h =2;h<n+1;h++) {
				expr=cplex.linearNumExpr();
				for(int i=1;i<n+1;i++) {
					if(i != h) {
					expr.addTerm(1.0, xp[i][h]);
					}
				}
				
				for(int j=2;j<n+2;j++) {
					if(j != h) {
						expr.addTerm(-1.0, xp[h][j]);
					}
				}
				cplex.addEq(expr, 0);
			}
//cconstraint 3			
			expr=cplex.linearNumExpr();
			for(int j=2;j<n+1;j++) {
				expr.addTerm(1.0, xp[1][j]);
			}
			cplex.addLe(expr, K);
//constraint 4			
			for(int i=1;i<n+2;i++) {
				for(int j=1;j<n+2;j++) {
					expr=cplex.linearNumExpr();
					expr.addTerm(1.0, y[j]);
					expr.addTerm(-1.0, y[i]);
					expr.addTerm(-demand[j], xp[i][j]);
					expr.addTerm(-capacity,xp[i][j] );
					cplex.addGe(expr, -capacity);
					
				}
			}
			cplex.exportModel("polyCVRP.lp");
			cplex.setParam(IloCplex.Param.TimeLimit, 600);
			if(cplex.solve()==true) {
				System.out.println("objective value:"+cplex.getObjValue());
			}
		
			
			cplex.end();
			
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    void solve() {
    	try {
			if(cplex.solve()==true) {
				System.out.println("objective value:"+cplex.getObjValue());
				
			}
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    void CWalgorithm() {
//sort the safe    	
    	edgelist = new ArrayList<int[]>();
    	safelist = new ArrayList<Double>();
    	for(int i=2;i<n+1;i++) {
    		for(int j=i+1;j<n+1;j++) {
    			s[i][j]=c[1][i]+c[1][j]-c[i][j];
    			//System.out.println(s[i][j]);
    		}
    	}

        for(int i=2;i<n+1;i++) {
        	for(int j=i+1;j<n+1;j++) {
        		int[] temp = new int[] {i,j};
        		edgelist.add(temp);
        		safelist.add(s[i][j]);
        	}
        }
        for(int i=0;i<edgelist.size()-1;i++) {
        for(int j=0;j<edgelist.size()-i-1;j++) {
        	if(safelist.get(j)<safelist.get(j+1)) {
        		Collections.swap(edgelist, j, j+1);
        		Collections.swap(safelist, j, j+1);
        	}
        }
      }
        
//pick the largest safe into route
        route = new HashMap<Integer,ArrayList<int[]>>();
        heads = new HashMap<Integer,int[]>();
        head = new int[2];
        cap = new double[K];
        int k=0;
        while(k<K && edgelist.isEmpty()==false) {
        	//initiate
        	
            ArrayList<int[]> temp = new ArrayList<int[]>();
            temp.add(edgelist.get(0));
            route.put(k, temp);
            head[0]=edgelist.get(0)[0];
            head[1]=edgelist.get(0)[1];
            edgelist.remove(0);
        	addedge(k);
        	heads.put(k, new int[] {head[0],head[1]});
        	delete(head[0]);
        	delete(head[1]);
        	k++;
        }        
        objV=0;
       for(int i=0;i<route.size();i++) {   	  
    	   System.out.println("====vehicle:"+i+"========");
    	   System.out.println("head"+heads.get(i)[0]+"--end"+heads.get(i)[1]);
    	   objV=objV+c[1][heads.get(i)[0]]+c[1][heads.get(i)[1]];
    	   
    	   for(int[] j:route.get(i)){
    		   System.out.println(j[0]+"--"+j[1]);
    		   objV=objV+c[j[0]][j[1]];
    		   System.out.println(c[j[0]][j[1]]);
    	   }
    	   System.out.println("cap"+cap[i]);
    	
       }
       System.out.println("objective value"+objV);
    }
    void addedge(int k) {
    	for(int i=0 ;i< edgelist.size();i++) {
    	if(head[0]==edgelist.get(i)[0] && cap[k]+demand[edgelist.get(i)[1]]<capacity) {
    		
    		route.get(k).add(edgelist.get(i));
    		head[0]=edgelist.get(i)[1];
    		cap[k] = cap[k]+demand[edgelist.get(i)[1]];
    		//System.out.println("delete"+edgelist.get(i)[0]);
    		delete(edgelist.get(i)[0]);   		
    	   }else if(head[0]==edgelist.get(i)[1] && cap[k]+demand[edgelist.get(i)[0]]<capacity) {    		
    		   route.get(k).add(edgelist.get(i));
    		   head[0]=edgelist.get(i)[0];
    		   cap[k] = cap[k]+demand[edgelist.get(i)[0]];
    		   delete(edgelist.get(i)[1]);//delete interior node   		   
    	   }else if(head[1]==edgelist.get(i)[0] && cap[k]+demand[edgelist.get(i)[1]]<capacity) {
    		 
    		   route.get(k).add(edgelist.get(i));
    		   head[1]=edgelist.get(i)[1];
    		   cap[k] = cap[k]+demand[edgelist.get(i)[1]];
    		   delete(edgelist.get(i)[0]);    		   
    	   }else if(head[1]==edgelist.get(i)[1] && cap[k]+demand[edgelist.get(i)[0]]<capacity) { 			 
    		   route.get(k).add(edgelist.get(i));
    		   head[1]=edgelist.get(i)[0];
    		   cap[k] = cap[k]+demand[edgelist.get(i)[0]];
    		   delete(edgelist.get(i)[1]);
    	     }
    	   }
    	}
//delete the edge that associate with interior node i
    void delete(int i) {
    	for(int j=0;j<edgelist.size();j++) {   	
    		if(j>=edgelist.size()) {
    			break;
    		}
    		//System.out.println("==============checking edge:"+edgelist.get(j)[0]+"--"+edgelist.get(j)[1]+"==================");
    		if(i==edgelist.get(j)[0] ) {
    			//System.out.println("remove:"+edgelist.get(j)[0]+"--"+edgelist.get(j)[1]);
    			edgelist.remove(j);
    			j--;
    			//n--;
    		}else if(i==edgelist.get(j)[1]) {
    			//System.out.println("remove:"+edgelist.get(j)[0]+"=="+edgelist.get(j)[1]);
    			edgelist.remove(j);
    			j--;
    			//n--;
    		}
    		
    	}
    }

	public static void main(String[] args)throws Exception{
		Cvrps exa= new Cvrps();
		exa.readData("CVRP\\A-n45-k7.txt");
		exa.CWalgorithm();
		//exa.polyCVRP();
		
		//exa.model();
		//exa.solve();	
	}
  }


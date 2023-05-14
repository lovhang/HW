import java.util.ArrayList;
import java.util.HashMap;

import ilog.cplex.IloCplex;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;

public class formulation {
	private HashMap<Integer,cell> Alcell;//all cell collection
	private ArrayList<Integer> Sccell;//source cell collection
	private ArrayList<Integer> Itcell;//intermediate cell collection
	private ArrayList<Integer> Skcell;//sink cell collection
	private HashMap<Integer,cell> Alcelp;//all cell collection for pedestrian
	private ArrayList<Integer> Sccelp;//source cell collection for pedestrian 
	private ArrayList<Integer> Itcelp;//intermediate cell collection for pedestrian
	private ArrayList<Integer> Skcelp;//sink cell collection for pedestrian
	private int cellnum;
	private int cellnump;
	private Integer TT=0;//total time
	private IloLinearNumExpr obj;
	private IloLinearNumExpr num_expr;
////variable for vehicle network///	
	private IloNumVar x[][];
	private IloNumVar y[][][];
	private IloNumVar d[][];//demand distribution variable
////variable for pedestrian network///	
	private IloNumVar xp[][];
	private IloNumVar yp[][][];
	private IloNumVar dp[][];
	private IloNumVar out[];
	private IloNumVar in[];
	private IloNumVar P[];
	private double[][] B;
	private double[] M;//vectio M
	private double alph;//average number of people in one vehicle
	private double sig;//ratio between propagation and free flow speed
	private double D;//total demand
	private double ld;//load rate(# ppl/t)
	private double tc;//transit capacity
	private IloCplex cplex;
	public formulation(network1 a) {
		Alcell = new HashMap<Integer,cell>();
		Sccell = new ArrayList<Integer>();
		Itcell = new ArrayList<Integer>();
		Skcell = new ArrayList<Integer>();
		this.Alcell = (HashMap<Integer, cell>) a.Alcell.clone();
		this.Sccell = (ArrayList<Integer>) a.Sccell.clone();
		this.Itcell = (ArrayList<Integer>) a.Itcell.clone();
		this.Skcell = (ArrayList<Integer>) a.Skcell.clone();
		this.Alcelp = (HashMap<Integer, cell>) a.Alcelp.clone();
		this.Sccelp = (ArrayList<Integer>) a.Sccelp.clone();
		this.Itcelp = (ArrayList<Integer>) a.Itcelp.clone();
		this.Skcelp = (ArrayList<Integer>) a.Skcelp.clone();
		this.cellnum=a.cell_num;this.cellnump=a.cell_nump;
	}
	private void initiate() {
		TT=21;
		alph=1;
		sig=1;
		D=30;
		ld=3;
		tc=10;
		B=new double[cellnump][TT];
		for(int i:Alcelp.keySet()) {
			for(int t=0;t<TT;t++) {
				B[i][t]=0;
			}
		}
		B[1][0]=1;B[1][1]=1.0;B[32][7]=1.0;B[32][8]=1.0;
		M=new double[TT];
		M[0]=1;M[1]=1;M[2]=1;M[3]=1;M[4]=1;M[5]=1;M[6]=9375001.063;M[7]=9960938.505;M[8]=9997559.595;M[9]=9999848.413;M[10]=9999991.464;
		M[11]=99999999.405;M[12]=9999999.964;M[13]=9999999.999;M[14]=10000000.000;M[15]=10000001.000;M[16]=10000002.000;M[17]=10000003.000;
		M[19]=10000004.000;M[20]=10000005.000;
	}
	private void CreateModel() {
		x = new IloNumVar[cellnum][TT];
		y = new IloNumVar[cellnum][cellnum][TT];
		d = new IloNumVar[cellnum][TT];
		xp= new IloNumVar[cellnump][TT];
		yp= new IloNumVar[cellnump][cellnump][TT];
		dp= new IloNumVar[cellnump][TT];
		out = new IloNumVar[TT];
		in = new IloNumVar[TT];
		P = new IloNumVar[TT];
	    try {
	    	cplex = new IloCplex();
	    	for(int i:Alcell.keySet()) {
	    		for(int t=0;t<TT;t++) {
	    				x[i][t] = cplex.numVar(0, Double.MAX_VALUE);
	    				x[i][t].setName("x."+i+"."+t); 
	    			
	    		}
	    	}
	    	for(int i:Alcell.keySet()) {
	    		for(int j:Alcell.get(i).getSucessor()) {
	    			for(int t=0;t<TT;t++) {
	    					y[i][j][t] = cplex.numVar(0, Double.MAX_VALUE);
	    					y[i][j][t].setName("y."+i+"."+j+"."+t);
	    			}
	    		}
	    	}
	    	for(int i:Sccell) {
	    		for(int t=0;t<TT;t++) {
	    				d[i][t] = cplex.numVar(0, Double.MAX_VALUE);
	    				d[i][t].setName("d."+i+"."+t);
	    		}
	    	}
	    	for(int i:Alcelp.keySet()) {
	    		for(int t=0;t<TT;t++) {
	    				xp[i][t] = cplex.numVar(0, Double.MAX_VALUE);
	    				xp[i][t].setName("xp."+i+"."+t); 
	    			
	    		}
	    	}
	    	for(int i:Alcelp.keySet()) {
	    		//System.out.println(i);
	    		for(int j:Alcelp.get(i).getSucessor()) {
	    			for(int t=0;t<TT;t++) {
	    					yp[i][j][t] = cplex.numVar(0, Double.MAX_VALUE);
	    					yp[i][j][t].setName("yp."+i+"."+j+"."+t);
	    			}
	    		}
	    	}
	    	for(int i:Sccelp) {
	    		for(int t=0;t<TT;t++) {
	    				dp[i][t] = cplex.numVar(0.0, Double.MAX_VALUE);
	    				dp[i][t].setName("dp."+i+"."+t);
	    		}
	    	}
	    	
	    	for(int t=0;t<TT;t++) {
	    		in[t] = cplex.numVar(0.0, ld);
	    		in[t].setName("in."+t);
	    		
	    		out[t] = cplex.numVar(0.0, ld);
	    		out[t].setName("out."+t);
	    		
	    		P[t] = cplex.numVar(0, tc);
	    		P[t].setName("P."+t);
	    	}
	    	
/**
 * objective function	    	
 */
	    	obj = cplex.linearNumExpr();
	    	for(int t=0;t<TT;t++) {
	    		for(int j:Skcell) {
	    			for(int i:Alcell.get(j).getPredecessor()) {
	    				obj.addTerm(M[t]*alph, y[i][j][t]);
	    				//obj.addTerm(M[t], y[i][j][t][2]);
	    			}
	    		}
	    	}
	    	
	    	cplex.addMinimize(obj);

/**
 * constraints	    	
 */
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Itcell) {
	    			num_expr = cplex.linearNumExpr();
	    			num_expr.addTerm(1.0, x[i][t]);
	    			num_expr.addTerm(-1.0, x[i][t-1]);
	    			for(int j:Alcell.get(i).getSucessor()) {
	    				num_expr.addTerm(1.0, y[i][j][t-1]);
	    			}
	    			for(int k:Alcell.get(i).getPredecessor()) {
	    				num_expr.addTerm(-1.0, y[k][i][t-1]);
	    			}
	    			cplex.addEq(num_expr, 0);
	    		}
	    	}
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Sccell) {
	    			num_expr = cplex.linearNumExpr();
	    			num_expr.addTerm(1.0, x[i][t]);
	    			num_expr.addTerm(-1.0, x[i][t-1]);
	    			num_expr.addTerm(-1.0/alph,d[i][t-1]);
	    			for(int j:Alcell.get(i).getSucessor()) {
	    				num_expr.addTerm(1.0, y[i][j][t-1]);
	    			}
	    			cplex.addEq(num_expr, 0);
	    		}
	    	}
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Skcell) {
	    			num_expr = cplex.linearNumExpr();
	    			num_expr.addTerm(1.0, x[i][t]);
	    			num_expr.addTerm(-1.0, x[i][t-1]);
	    			for(int k:Alcell.get(i).getPredecessor()) {
	    				num_expr.addTerm(-1.0, y[k][i][t-1]);
	    			}
	    			cplex.addEq(num_expr, 0);
	    		}
	    	}
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Alcell.keySet()) {
	    			num_expr = cplex.linearNumExpr();
	    			num_expr.addTerm(-1.0, x[i][t]);
	    			for(int j:Alcell.get(i).getSucessor()) {
	    				num_expr.addTerm(1.0, y[i][j][t]);
	    			}
	    			cplex.addLe(num_expr, 0);
	    		}
	    	}
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Alcell.keySet()) {
	    			num_expr = cplex.linearNumExpr();
	    			for(int j:Alcell.get(i).getSucessor()) {
	    				num_expr.addTerm(1.0, y[i][j][t]);
	    			}
	    			cplex.addLe(num_expr, Alcell.get(i).getFlow());
	    		}
	    	}
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Alcell.keySet()) {
	    			num_expr = cplex.linearNumExpr();
	    			for(int k:Alcell.get(i).getPredecessor()) {
	    				num_expr.addTerm(1.0, y[k][i][t]);
	    			}
	    			cplex.addLe(num_expr, Alcell.get(i).getFlow());
	    		}
	    	}
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Alcell.keySet()) {
	    			num_expr = cplex.linearNumExpr();
	    			for(int k:Alcell.get(i).getPredecessor()) {
	    				num_expr.addTerm(1.0, y[k][i][t]);
	    			}
	    			num_expr.addTerm(sig, x[i][t]);
	    			cplex.addLe(num_expr, sig*Alcell.get(i).getCapacity());
	    		}
	    	}
////////////////////////conservation constraints for pedestrian network/////////////
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Itcelp) {
	    			//System.out.println(t+"."+i);
	    			num_expr = cplex.linearNumExpr();
	    			num_expr.addTerm(1.0, xp[i][t]);
	    			num_expr.addTerm(-1.0, xp[i][t-1]);
	    			for(int j:Alcelp.get(i).getSucessor()) {
	    				num_expr.addTerm(1.0, yp[i][j][t-1]);
	    			}
	    			for(int k:Alcelp.get(i).getPredecessor()) {
	    				num_expr.addTerm(-1.0, yp[k][i][t-1]);
	    			}
	    			num_expr.addTerm(-B[i][t], out[t-1]);
	    			num_expr.addTerm(B[i][t], in[t-1]);
	    			cplex.addEq(num_expr, 0);
	    		}
	    	}
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Sccelp) {
	    			num_expr = cplex.linearNumExpr();
	    			num_expr.addTerm(1.0, xp[i][t]);
	    			num_expr.addTerm(-1.0, xp[i][t-1]);
	    			num_expr.addTerm(-1.0,dp[i][t-1]);
	    			for(int j:Alcelp.get(i).getSucessor()) {
	    				num_expr.addTerm(1.0, yp[i][j][t-1]);
	    			}
	    			num_expr.addTerm(B[i][t], in[t-1]);
	    			cplex.addEq(num_expr, 0);
	    		}
	    	}
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Skcelp) {
	    			num_expr = cplex.linearNumExpr();
	    			num_expr.addTerm(1.0, xp[i][t]);
	    			num_expr.addTerm(-1.0, xp[i][t-1]);
	    			for(int k:Alcelp.get(i).getPredecessor()) {
	    				num_expr.addTerm(-1.0, yp[k][i][t-1]);
	    			}
	    			num_expr.addTerm(-B[i][t], out[t-1]);
	    			cplex.addEq(num_expr, 0);
	    		}
	    	}
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Alcelp.keySet()) {
	    			num_expr = cplex.linearNumExpr();
	    			num_expr.addTerm(-1.0, xp[i][t]);
	    			for(int j:Alcelp.get(i).getSucessor()) {
	    				num_expr.addTerm(1.0, yp[i][j][t]);
	    			}
	    			cplex.addLe(num_expr, 0);
	    		}
	    	}
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Alcelp.keySet()) {
	    			num_expr = cplex.linearNumExpr();
	    			for(int j:Alcelp.get(i).getSucessor()) {
	    				num_expr.addTerm(1.0, yp[i][j][t]);
	    			}
	    			cplex.addLe(num_expr, Alcelp.get(i).getFlow());
	    		}
	    	}
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Alcelp.keySet()) {
	    			num_expr = cplex.linearNumExpr();
	    			for(int k:Alcelp.get(i).getPredecessor()) {
	    				num_expr.addTerm(1.0, yp[k][i][t]);
	    			}
	    			cplex.addLe(num_expr, Alcelp.get(i).getFlow());
	    		}
	    	}
	    	
	    	for(int t=1;t<TT;t++) {
	    		for(int i:Alcelp.keySet()) {
	    			num_expr = cplex.linearNumExpr();
	    			for(int k:Alcelp.get(i).getPredecessor()) {
	    				num_expr.addTerm(1.0, yp[k][i][t]);
	    			}
	    			num_expr.addTerm(sig, xp[i][t]);
	    			cplex.addLe(num_expr, sig*Alcelp.get(i).getCapacity());
	    		}
	    	}
/////////////////////////////other constraints///////////////////////////	
	    	for(int t=1;t<TT;t++) {
	    		num_expr = cplex.linearNumExpr();
	    		num_expr.addTerm(1.0, P[t]);
	    		num_expr.addTerm(-1.0, P[t-1]);
	    		num_expr.addTerm(-1.0, in[t-1]);
	    		num_expr.addTerm(1.0, out[t-1]);
	    		cplex.addEq(num_expr, 0);
	    	}
	    	for(int t=0;t<TT;t++) {
	    		double temp=0;
	    		for(int i:Alcelp.keySet()) {
	    			temp=temp+B[i][t];
	    		}
	    		if(temp==0) {
	    			num_expr = cplex.linearNumExpr();
	    			num_expr.addTerm(1.0, in[t]);
	    			cplex.addEq(num_expr, 0);
	    			num_expr = cplex.linearNumExpr();
	    			num_expr.addTerm(1.0, out[t]);
	    			cplex.addEq(num_expr, 0);
	    		}
	    	}
	    	num_expr = cplex.linearNumExpr();
	    	num_expr.addTerm(1.0, P[0]);
	    	cplex.addEq(num_expr, 0);
	    	
	    	num_expr = cplex.linearNumExpr();
	    	for(int t=0;t<TT;t++) {
	    		for(int i:Sccell) {
	    			num_expr.addTerm(1.0, d[i][t]);	    			
	    		}
	    		for(int i:Sccelp) {
	    			num_expr.addTerm(1.0, dp[i][t]);
	    		}
	    	}
	    	cplex.addEq(num_expr, D);
	    	
	    	for(int i :Sccell) {
	    		num_expr = cplex.linearNumExpr();
	    		num_expr.addTerm(1.0, x[i][TT-1]);
	    		cplex.addEq(num_expr, 0);
	    	}
	    	for(int i:Sccelp) {
	    		num_expr = cplex.linearNumExpr();
	    		num_expr.addTerm(1.0, xp[i][TT-1]);
	    		cplex.addEq(num_expr, 0);
	    	}
	    	
	    	for(int i :Itcell) {
	    		num_expr = cplex.linearNumExpr();
	    		num_expr.addTerm(1.0, x[i][TT-1]);
	    		cplex.addEq(num_expr, 0);
	    	}
	        for(int i:Itcelp) {
	    		num_expr = cplex.linearNumExpr();
	    		num_expr.addTerm(1.0, xp[i][TT-1]);
	    		cplex.addEq(num_expr, 0);
	        }
	        
	        num_expr = cplex.linearNumExpr();
	        for(int i:Skcell) {
	        	num_expr.addTerm(1.0, x[i][TT-1]);
	        }
	        for(int i:Skcelp) {
	        	num_expr.addTerm(1.0, xp[i][TT-1]);
	        }
	        cplex.addEq(num_expr, D);
	        
	    	for(int i:Alcell.keySet()) {
	    		num_expr = cplex.linearNumExpr();
	    		num_expr.addTerm(1.0, x[i][0]);
	    		cplex.addEq(num_expr, 0);
	    	}
	    	for(int i:Alcelp.keySet()) {
	    		num_expr = cplex.linearNumExpr();
	    		num_expr.addTerm(1.0, xp[i][0]);
	    		cplex.addEq(num_expr, 0);
	    	}
	    	for(int i:Alcell.keySet()) {
	    		for(int j:Alcell.get(i).getSucessor()) {
	    		    num_expr = cplex.linearNumExpr();
	    		    num_expr.addTerm(1.0, y[i][j][0]);
	    		    cplex.addEq(num_expr, 0);
	    		}
	    	}
	    	for(int i:Alcelp.keySet()) {
	    		for(int j:Alcelp.get(i).getSucessor()) {
	    		    num_expr = cplex.linearNumExpr();
	    		    num_expr.addTerm(1.0, yp[i][j][0]);
	    		    cplex.addEq(num_expr, 0);
	    		}
	    	}
	/////////////////test constriants//////////////////
	    	/*
	    	num_expr = cplex.linearNumExpr();
	    	num_expr.addTerm(1.0, in[0]);
	    	cplex.addEq(num_expr, 3.0);
	    	num_expr = cplex.linearNumExpr();
	    	num_expr.addTerm(1.0, in[1]);
	    	cplex.addEq(num_expr, 3.0);
	    	num_expr = cplex.linearNumExpr();
	    	num_expr.addTerm(1.0, out[0]);
	    	cplex.addEq(num_expr, 0.0);
	    	num_expr = cplex.linearNumExpr();
	    	num_expr.addTerm(1.0, out[1]);
	    	cplex.addEq(num_expr, 0.0);*/
	    	/*
	    	num_expr = cplex.linearNumExpr();
	    	num_expr.addTerm(1.0, d[1][20]);
	    	cplex.addEq(num_expr, 0);*/
	    	for(int t=2;t<TT;t++) {
	    		num_expr = cplex.linearNumExpr();
	    		num_expr.addTerm(1.0, dp[1][t]);
	    		cplex.addEq(num_expr, 0);
	    		num_expr = cplex.linearNumExpr();
	    		num_expr.addTerm(1.0, d[1][t]);
	    		cplex.addEq(num_expr, 0);
	    		num_expr = cplex.linearNumExpr();
	    		num_expr.addTerm(1.0, dp[36][t]);
	    		cplex.addEq(num_expr, 0);
	    		num_expr = cplex.linearNumExpr();
	    		num_expr.addTerm(1.0, d[14][t]);
	    		cplex.addEq(num_expr, 0);
	    	}
	    	for(int t=0;t<7;t++) {
	    		num_expr = cplex.linearNumExpr();
		    	num_expr.addTerm(1.0, out[t]);
		    	cplex.addEq(num_expr, 0.0);
	    	}
	    	/*
	    	num_expr = cplex.linearNumExpr();
	    	num_expr.addTerm(1.0, yp[32][33][9]);
	    	cplex.addEq(num_expr, 1.0);
	    	num_expr = cplex.linearNumExpr();
	    	num_expr.addTerm(1.0, yp[32][33][10]);
	    	cplex.addEq(num_expr, 1.0);
	    	num_expr = cplex.linearNumExpr();
	    	num_expr.addTerm(1.0, yp[32][33][11]);
	    	cplex.addEq(num_expr, 1.0);*/
	    	num_expr = cplex.linearNumExpr();
	    	for(int i:Sccell) {
	    		for(int t=0;t<TT;t++) {
	    			num_expr.addTerm(1.0, d[i][t]);
	    		}
	    	}
	    	num_expr.addTerm(-1.0, x[13][TT-1]);
	    	cplex.addEq(num_expr, 0);
	    	num_expr = cplex.linearNumExpr();
	    	for(int i:Sccelp) {
	    		for(int t=0;t<TT;t++) {
	    			num_expr.addTerm(1.0, dp[i][t]);
	    		}
	    	}
	    	num_expr.addTerm(-1.0, xp[35][TT-1]);
	    	cplex.addEq(num_expr, 0);
	    }catch(IloException e) {
	    	
	    }	    
	}
	private void solve() {
		 try{
  		    cplex.exportModel("originalProblem.lp");  
  		    if( cplex.solve()==true) {
  		    	System.out.println("problem solved");
  		    }else {
  		    	System.out.println("problem infeasible");
  		    }
			   
			    double temp = cplex.getObjValue();
			    System.out.println(temp);
			    
			 }catch(IloException e){
      			e.printStackTrace();
      			}
	}
	private void output() {
		try {
			/*
		for(int t=0;t<TT;t++) {
			System.out.println("x.1."+t+"=="+cplex.getValue(x[1][t]));
		}System.out.println("=============");
		for(int t=0;t<TT;t++) {
			System.out.println("x.14."+t+"=="+cplex.getValue(x[14][t]));
		}System.out.println("=============");*/
		/*			
        for(int t=1;t<TT;t++) {
        	System.out.println("x.13."+t+"=="+cplex.getValue(x[13][t][1]));
        }System.out.println("=============");
		for(int t=1;t<TT;t++) {
			System.out.println(cplex.getValue(d[1][t][1]));
		}System.out.println("=============");
		for(int t=1;t<TT;t++) {
			System.out.println(cplex.getValue(d[14][t][1]));
		}System.out.println("=============");*/
		/*
		System.out.println("=============");
		for(int t=0;t<TT;t++) {
			System.out.println(cplex.getValue(y[14][15][t][1]));
		}
		System.out.println("=============");
		for(int t=0;t<TT;t++) {
			System.out.println(cplex.getValue(x[13][t][1]));
		}
		*/	
		/*	
		for(int i:Alcell.keySet()) {
			for(int t=0;t<TT;t++) {			
			System.out.println("x."+i+".14=="+cplex.getValue(x[i][t][1]));
			}
		}*/
			for(int i:Sccell) {
				for(int t=0;t<TT;t++) {
					//System.out.println(i+"."+t);
					System.out.println("d."+i+"."+t+": "+cplex.getValue(d[i][t]));
				}
			}
			for(int i:Sccelp) {
				for(int t=0;t<TT;t++) {
					System.out.println("dp."+i+"."+t+": "+cplex.getValue(dp[i][t]));
				}
			}
			for(int t=0;t<TT;t++) {
				System.out.println("xp.1."+t+": "+cplex.getValue(xp[1][t]));
			}
			for(int t=0;t<TT;t++) {
				System.out.println("xp.35."+t+": "+cplex.getValue(xp[35][t]));
			}
			for(int t=0;t<TT;t++) {
				System.out.println("xp.32."+t+": "+cplex.getValue(xp[32][t]));
			}
			for(int t=0;t<TT;t++) {
				System.out.println("yp.32.33."+t+": "+cplex.getValue(yp[32][33][t]));
			}
			
			for(int t=0;t<TT;t++) {				
				System.out.println("in."+t+": "+cplex.getValue(in[t]));
			}
			for(int t=0;t<TT;t++) {
				System.out.println("out."+t+": "+cplex.getValue(out[t]));
			}
			for(int t=0;t<TT;t++) {
				System.out.println("P."+t+": "+cplex.getValue(P[t]));
			}
			//System.out.println("x.13."+(TT-1)+": "+cplex.getValue(x[13][TT-1]));
			//System.out.println("xp.35."+(TT-1)+": "+cplex.getValue(xp[35][TT-1]));
			for(int t=0;t<TT;t++) {
				System.out.println("x.13."+t+": "+cplex.getValue(x[13][t]));
			}
			for(int t=0;t<TT;t++) {
				System.out.println("xp.35."+t+": "+cplex.getValue(xp[35][t]));
			}
			for(int t=0;t<TT;t++) {
				System.out.println("y.1.2."+t+": "+cplex.getValue(y[1][2][t]));
			}
			for(int t=0;t<TT;t++) {
				System.out.println("y.1.3."+t+": "+cplex.getValue(y[1][3][t]));
			}
			for(int t=0;t<TT;t++) {
				System.out.println("yp.1.2."+t+": "+cplex.getValue(yp[1][2][t]));
			}
			for(int t=0;t<TT;t++) {
				System.out.println("yp.1.3."+t+": "+cplex.getValue(yp[1][3][t]));
			}
		}catch(IloException e) {
			e.printStackTrace();
		}
	}
	public void run() {		
		initiate();
		CreateModel();
		solve();	
		output();
		cplex.end();
	}
}

package logesticHW3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Columngeneration {
	private double[][] c;
    private IloNumVar[][] lambda;
    private IloIntVar[][][] x;
    private IloNumVar[][] y;//arriving time
	private  IloLinearNumExpr obj;
	private IloLinearNumExpr expr; 
	private IloCplex cplex;
	private String[] col;
	private ArrayList<Integer> N;//collection of node
	private int n;//number of node
	private int K;//number of vehicle
	private double cap;//capacity
	private double[] xcoord;
	private double[] ycoord;
	private double[] demand;
	private double[] a;//ready time
	private double[] b;//due date
	private double[] s;//service time
   public Columngeneration() {
	  
 
   }
   void readData() {		
		try {
			File file = new File("VRPTW\\R105.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			xcoord = new double[26];
			ycoord = new double[26];
			a = new double[26];
			b = new double[26];
			demand = new double[26];
			s = new double[26];
			c = new double[26][26];
			x = new IloIntVar[26][26][26];
			String st ="";
			br.readLine();
			br.readLine();
			br.readLine();
			br.readLine();
			st=br.readLine();
			//System.out.println(st);
			col=st.split("\\s+ ");
			//System.out.println(col.length);
			K=Integer.parseInt(col[1]);
			y = new IloNumVar[26][K];
			cap = Double.parseDouble(col[2]);
			//System.out.println(K+"."+cap);
			br.readLine();
			br.readLine();
			br.readLine();
			br.readLine();
			
			n=26;
			for(int i=0;i<n;i++) {
				st = br.readLine();
				col=st.split("\\s+ ");
				//System.out.println(col[2]);
			       xcoord[i]=Double.parseDouble(col[2]);
			       ycoord[i]=Double.parseDouble(col[3]);
			       demand[i]=Double.parseDouble(col[4]);
			       a[i]=Double.parseDouble(col[5]);
			       b[i]=Double.parseDouble(col[6]);
			       s[i]=Double.parseDouble(col[7]);
			    }
		 System.out.println(n);
			for(int i=1;i<n;i++) {
				for(int j=1;j<n;j++) {					
					c[i][j]= Math.sqrt((xcoord[i]-xcoord[j])*(xcoord[i]-xcoord[j])+(ycoord[i]-ycoord[j])*(ycoord[i]-ycoord[j]));
				}
			}
			System.out.println("==============");
			System.out.println("===truck:"+K+"=====");
			System.out.println("===capacity:"+cap+"====");
			System.out.println("===number of node:"+n+"====");
		} catch (FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
   void initial() {
	   //initial feasible solution
	   try {
		 double M=2000;  
		cplex = new IloCplex();
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) {
				for(int k=0;k<K;k++) {
					x[i][j][k]=cplex.boolVar();
					x[i][j][k].setName("x."+i+"."+j+"."+k);
				}
			}
		}
		for(int i=0;i<n;i++) {
			for(int k=0;k<K;k++) {
				y[i][k]=cplex.numVar(0, Double.MAX_VALUE);
				y[i][k].setName("y."+i+"."+k);
			}
		}
//objective function		
		obj=cplex.linearNumExpr();
		for(int i=0;i<n;i++) {
			for(int j=0;j<n;j++) {
				if(j != i) {
					for(int k=0;k<K;k++) {
						obj.addTerm(c[i][j], x[i][j][k]);
					}
				}
			}
		}
		cplex.addMinimize(obj);
//constraint 1
		for(int i=1;i<n;i++) {
			expr=cplex.linearNumExpr();
			for(int j=0;j<n;j++) {
				if(j != i) {
					for(int k=0;k<K;k++) {
						expr.addTerm(1.0, x[i][j][k]);
					}
				}
			}
			cplex.addEq(expr, 1);
		}
//constraint 2
		for(int k=0;k<K;k++) {
			expr=cplex.linearNumExpr();
			for(int i=1;i<n;i++) {
				expr.addTerm(1.0, x[0][i][k]);
			}
			cplex.addEq(expr, 1);
		}
//constraint 3
		for(int k=0;k<K;k++) {
			for(int i=1;i<n;i++) {
				expr=cplex.linearNumExpr();
				for(int j=0;j<n;j++) {
					if(j != i) {
						expr.addTerm(1.0, x[i][j][k]);
						expr.addTerm(-1.0, x[j][i][k]);
					}
				}
				cplex.addEq(expr, 0);
			}
		}
//constraint 4
		for(int k=0;k<K;k++) {
			expr=cplex.linearNumExpr();
			for(int i=1;i<n;i++) {
				expr.addTerm(1.0, x[i][0][k]);
			}
			cplex.addEq(expr, 1);
		}
//constraint 5
		for(int k=0;k<K;k++) {
			expr=cplex.linearNumExpr();
			for(int i=0;i<n;i++) {
				for(int j=0;j<n;j++) {
					if(j != i) {
						expr.addTerm(demand[i], x[i][j][k]);
					}
				}
			}
			cplex.addLe(expr, cap);
		}
		/*
//constraint 6
		
		for(int k=0;k<K;k++) {
			for(int i=0;i<n;i++) {
				for(int j=0;j<n;j++) {
					if(j != i) {
						expr=cplex.linearNumExpr();
						expr.addTerm(1.0, y[i][k]);
						expr.addTerm(M, x[i][j][k]);
						expr.addTerm(-1.0, y[j][k]);
						cplex.addLe(expr, M-c[i][j]-s[i]);
					}
				}
			}
		}
//constraint 7
		for(int i=1;i<n;i++) {
			for(int k=0;k<K;k++) {
				expr=cplex.linearNumExpr();
				expr.addTerm(1.0, y[i][k]);
				cplex.addGe(expr, a[i]);
                cplex.addLe(expr, b[i]);
			}
		}*/
		//cplex.setParam(IloCplex.Param.MIP.Limits.Solutions, 1);
		cplex.exportModel("initial.lp");
		if(cplex.solve()==true) {
			System.out.println(cplex.getObjValue());	
		
			for(int k=0;k<K;k++) {
				for(int i=0;i<n;i++) {
					for(int j=0;j<n;j++) {
						if(cplex.getValue(x[i][j][k])>0.99 && cplex.getValue(x[i][j][k])<1.01) {
							System.out.println(i+"--"+j);
						}
					}
				}
			}	
		}else {
			System.out.println("unsolved");
		}
		cplex.end();
		
	} catch (IloException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   
	   
	   
   }
	public static void main(String[] args)throws Exception{
		Columngeneration exa = new Columngeneration();
		exa.readData();
		exa.initial();
	}
}

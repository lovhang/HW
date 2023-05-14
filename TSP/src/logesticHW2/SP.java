package logesticHW2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ilog.concert.*;
import ilog.cplex.*;

public class SP {
	private int T;
	private HashMap<Integer,Integer> N; 
	private IloIntVar[][][] x;
	private int size;
	private double[][] c;
	private Set<Set<Integer>> subset;
	private IloCplex cplex;
	private IloLinearNumExpr obj;
	private IloLinearNumExpr expr;
    public SP(String input) {
    	data a = new data(input);
		this.c=a.c;
		//this.subset=a.subsets;
		this.size=a.size;
		creatmodel();
		solve();
    	
    }

    void creatmodel() {
    	try {
    		cplex = new IloCplex();
    		x=new IloIntVar[size][size][size];
    		for(int i=0;i<size;i++) {
    			for(int j=0;j<size;j++) {
    				for(int t=1;t<size;t++) {
    					x[i][j][t] = cplex.boolVar();
    				}
    			}
    		}
    		obj = cplex.linearNumExpr();
    		for(int i=0;i<size;i++) {
    			for( int j=0;j<size;j++) {
    				if( j != i) {
    					for(int t=1;t<size;t++) {
    						obj.addTerm(c[i][j], x[i][j][t]);
    					}
    				}
    			}
    		}
    		cplex.addMinimize(obj);
//constraint 1    		
    		expr=cplex.linearNumExpr();
    		for(int j=1;j<size;j++) {   			
    				expr.addTerm(1.0, x[1][j][1]);
    		}
    		cplex.addEq(expr, 1);
    		
//constraint 2
    		for(int i=0;i<size;i++) {
    			expr=cplex.linearNumExpr();
    			for(int j=1;j<size-1;j++) {
    					expr.addTerm(1.0,x[i][j][2] );
    					
    				}
    			expr.addTerm(-1.0, x[1][i][1]);
    				cplex.addEq(expr, 0);
    			}
    		
//constraint 3    
    		for(int i=0;i<size;i++) {
    			for(int t=2;t<size-1;t++) {
    				expr=cplex.linearNumExpr();
    				for(int j=1;j<size;j++) {
    					if(j != i) {
    						expr.addTerm(1.0, x[i][j][t]);
    						expr.addTerm(-1.0, x[j][i][t-1]);
    					}
    				}
    				cplex.addEq(expr, 0);
    			}
    		}
//constraint 4    		
    		for(int i=0;i<size;i++) {
    			expr=cplex.linearNumExpr();
    			expr.addTerm(1.0, x[i][1][size-1]);
    			for(int j=2;j<size;j++) {
					if(j != i) {
						expr.addTerm(-1.0, x[j][i][size-2]);
					}
    			}
    			cplex.addEq(expr, 0);
			}
//constraint 5    		
    		expr=cplex.linearNumExpr();
    		for(int i=2;i<size;i++) {    			
    				expr.addTerm(1.0, x[i][1][size-1]);
    		}
    		cplex.addEq(expr, 1);
//constraint 6    		
    		for(int i=2;i<size;i++) {
    				expr=cplex.linearNumExpr();
    				for(int t=3;t<size-1;t++) {
    					for(int j=1;j<size;j++) {
    						if(j != i) {
    							expr.addTerm(1.0, x[i][j][t]);
    						}
    				}
    			}
    				expr.addTerm(1.0, x[i][1][size-1]);
    				cplex.addLe(expr, 1);
    		}
    		cplex.setParam(IloCplex.Param.TimeLimit, 600);
    	}catch(IloException e) {
    		
    	}
    }
    void solve() {
    	try {
			if(cplex.solve()==true) {
				System.out.println("obj=="+cplex.getObjValue());
			}else {
				System.out.println("problem not solved");
				System.out.println("obj=="+cplex.getObjValue());
			}
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static void main(String[] args)throws Exception{
      		SP exa = new SP("TSP_instance_n_50_s_5.dat");     		
      	}
   
}

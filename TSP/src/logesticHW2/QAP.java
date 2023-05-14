package logesticHW2;

import java.util.Set;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

public class QAP {
	private int size;
	private double[][] c;
	private Set<Set<Integer>> subset;
	private IloCplex cplex;
	private IloLinearNumExpr obj;
	private IloLinearNumExpr expr;
	private IloIntVar[][] x;
	private IloIntVar[][][] w;
      public QAP(String input) {
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
			x = new IloIntVar[size][size];
			w = new IloIntVar[size][size][size];
			for(int i=0;i<size;i++) {
				for(int j=0;j<size;j++) {
					x[i][j]=cplex.boolVar();
				}
			}
			for(int i=0;i<size;i++) {
				for(int j=0;j<size;j++) {
					for(int s=0;s<size;s++) {
						w[i][j][s]=cplex.boolVar();
					}
				}
			}
		obj=cplex.linearNumExpr();
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				if(j != i) {
					for(int s=0;s<size-1;s++) {
						obj.addTerm(c[i][j], w[i][j][s]);
					}
					obj.addTerm(c[i][j], w[i][j][size-1]);
				}
			}
		}
		cplex.addMinimize(obj);
		
//constraint 1
		for(int i=0;i<size;i++) {
			expr=cplex.linearNumExpr();			
			for(int s=0;s<size;s++) {
				expr.addTerm(1.0, x[i][s]);
			}
			cplex.addEq(expr, 1);
		}
//constraint 2
		for(int s=0;s<size;s++) {
			expr=cplex.linearNumExpr();
			for(int i=0;i<size;i++) {
				expr.addTerm(1.0, x[i][s]);
			}
			cplex.addEq(expr,1);
		}
//constraint 3(1)
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				if(j != i) {
					for(int s=0;s<size-1;s++) {
						expr=cplex.linearNumExpr();
						expr.addTerm(1.0, w[i][j][s]);
						expr.addTerm(-1.0, x[i][s]);
						expr.addTerm(-1.0, x[j][s+1]);
						cplex.addGe(expr, -1);
					}
				}
			}
		}
//constraint 4(4)
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				if(j != i) {
					expr=cplex.linearNumExpr();
					expr.addTerm(1.0, w[i][j][size-1]);
					expr.addTerm(-1.0, x[i][size-1]);
					expr.addTerm(-1.0, x[j][0]);
					cplex.addGe(expr, -1);
				}
			}
		}	
		cplex.setParam(IloCplex.Param.TimeLimit, 600);
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
      public void solve() {
    	  try {
			if(cplex.solve()==true) {
				  System.out.println("obj="+cplex.getObjValue());
			  }
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
      public static void main(String[] args)throws Exception{
  		QAP exa = new QAP("TSP_instance_n_15_s_5.dat");
		
  	}
}

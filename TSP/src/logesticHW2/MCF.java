package logesticHW2;

import java.util.Set;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class MCF {
	private int size;
	private double[][] c;
	private Set<Set<Integer>> subset;
	private IloCplex cplex;
	private IloLinearNumExpr obj;
	private IloLinearNumExpr expr;
	private IloIntVar[][] x;
	private IloNumVar[][][] y;
	private double[][] d;
	public MCF(String input) {
		data a = new data(input);
		this.c=a.c;
		//this.subset=a.subsets;
		this.size=a.size;
		d = new double[size][size];
		for(int i=0;i<size;i++) {
			for(int k=1;k<size;k++) {
				d[i][k]=0;
			}
		}
		for(int k=1;k<size;k++) {
			for(int i=0;i<size;i++) {
				if(i==0) {
					d[i][k]=1;
				}else if(k == i) {
					d[i][k]=-1;
				}else {
					d[i][k]=0;
				}
			}
		}/*
		for(int i=0;i<size;i++) {
			//System.out.println("size:"+size);
			//System.out.println(i);
			for(int k=1;k<size;k++) {
				System.out.println(k);
				System.out.println("k."+i+"."+k+":"+d[i][k]);
			}
		}*/
		/*
		for(int k=1;k<size;k++) {
			d[1][k]=1;
		}
		for(int i=0;i<size;i++) {
			for(int k=1;k<size;k++) {
				if(k==i) {
					d[i][k]=-1;
				}
			}
		}*/
		creatmodel();
		solve();
	}
	 void creatmodel() {
		try {
			x=new IloIntVar[size][size];
			y=new IloNumVar[size][size][size];
			cplex = new IloCplex();
			for(int i=0;i<size;i++) {
				for(int j=0;j<size;j++) {
					x[i][j]=cplex.boolVar();
					x[i][j].setName("x."+i+"."+j);
					for(int k=1;k<size;k++) {
						y[i][j][k]=cplex.numVar(0, Double.MAX_VALUE);
						y[i][j][k].setName("y."+i+"."+j+"."+k);
					}
				}
			}
			
			obj=cplex.linearNumExpr();
			for(int i=0;i<size;i++) {
				for(int j=0;j<size;j++) {
					if(j != i) {
						obj.addTerm(c[i][j], x[i][j]);
					}
				}
			}
			cplex.addMinimize(obj);
//constraint 1			
			for(int i=0;i<size;i++) {
				expr=cplex.linearNumExpr();
				for(int j=0;j<size;j++) {
					if(j != i) {
						expr.addTerm(1.0, x[i][j]);
					}
				}
				cplex.addEq(expr, 1);
			}
//constraint 2
			for(int j=0;j<size;j++) {
				expr=cplex.linearNumExpr();
				for(int i=0;i<size;i++) {
					if(i != j) {
						expr.addTerm(1.0, x[i][j]);
					}
				}
				cplex.addEq(expr, 1);
			}
//constraint 3
			for(int i=0;i<size;i++) {
				for(int k=1;k<size;k++) {
					expr=cplex.linearNumExpr();
				    for(int j=0;j<size;j++) {
				    	if(j != i) {
				    		expr.addTerm(1.0, y[i][j][k]);
				    		expr.addTerm(-1.0, y[j][i][k]);
				    	}
				    }
				    cplex.addEq(expr, d[i][k]);
				}
			}
//constraint 4
			for(int i=0;i<size;i++) {
				for(int j=0;j<size;j++) {
					if(j != i) {
						for(int k=1;k<size;k++) {
							expr=cplex.linearNumExpr();
							expr.addTerm(1.0, y[i][j][k]);
							expr.addTerm(-1.0, x[i][j]);
							cplex.addLe(expr, 0);
						}
					}
				}
			}
		   cplex.exportModel("MCFmodel.lp");
		   cplex.setParam(IloCplex.Param.TimeLimit, 600);
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	 void solve() {
		 try {
			if(cplex.solve()==true) {				 
				System.out.println(cplex.getObjValue());
			 }
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
	 }
	 public static void main(String[] args)throws Exception{
			MCF exa = new MCF("TSP_instance_n_50_s_5.dat");
			
		}
}

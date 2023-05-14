package logesticHW2;

import java.util.Set;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class MTZ {

	private int size;
	private double[][] c;
	private Set<Set<Integer>> subset;
	private IloCplex cplex;
	private IloLinearNumExpr obj;
	private IloLinearNumExpr expr;
	private IloIntVar[][] x;
	private IloNumVar[] u;
	public MTZ(String input) {
		data a = new data(input); 
		this.c=a.c;
		this.subset=a.subsets;
		this.size=a.size;
		creatmodel();
		solve();
	}
	public void creatmodel()  {
		try {
			cplex = new IloCplex();
		x = new IloIntVar[size][size];
		u = new IloNumVar[size];
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				x[i][j]=cplex.boolVar();
			}
		}
		for(int i=0;i<size;i++) {
			u[i]=cplex.numVar(0, size*2);
		}
			
//obj		
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
//constarint 2
		for(int j=0;j<size;j++) {
			expr=cplex.linearNumExpr();
			for(int i=0;i<size;i++) {
				if(i != j) {
					expr.addTerm(1.0,x[i][j]);
				}
			}
			cplex.addEq(expr, 1);
		}	
		for(int i=1;i<size;i++) {
			for(int j=1;j<size;j++) {
				expr=cplex.linearNumExpr();
				expr.addTerm(1.0, u[i]);
				expr.addTerm(-1.0,u[j]);
				expr.addTerm(size, x[i][j]);
				cplex.addLe(expr, size-1);
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
				System.out.println("obj=="+cplex.getObjValue());
				for(int i=0;i<size;i++) {
					for(int j=0; j<size ; j++) {
						if(i != j) {
						if(cplex.getValue(x[i][j])>0.99 && cplex.getValue(x[i][j])<1.01) {
							System.out.println("x."+i+"."+j);
						}
						}
					}
				}
			
			};
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	public static void main(String[] args)throws Exception{
		MTZ exa = new MTZ("TSP_instance_n_10_s_647.dat");
		
	}
}

package logesticHW1;

import ilog.concert.*;
import ilog.cplex.*;

public class cargoTrans {
	public int[] N1;
	public int[] N2;
	public double[] d;
	public double[][] t;
	public IloIntVar[][] x;
	public IloCplex cplex;
	public IloLinearNumExpr expr;
	public IloLinearNumExpr obj;
   public cargoTrans() {
	   N1 = new int[] {1,2,3,4,5,6,7,8,9,10,11,12};
	   N2 = new int[] {1,2,3};
	   t = new double[13][4];
	   x = new IloIntVar[14][14];
	   t[1][1]=2;
	   t[1][2]=3;
	   t[1][3]=2;
	   t[2][1]=1;
	   t[2][2]=2;
	   t[2][3]=1;
	   t[3][1]=2;
	   t[3][2]=3;
	   t[3][3]=2;
	   t[4][1]=1;
	   t[4][2]=2;
	   t[4][3]=1;
	   t[5][1]=2;
	   t[5][2]=3;
	   t[5][3]=2;
	   t[6][1]=1;
	   t[6][2]=2;
	   t[6][3]=1;
	   t[7][1]=2;
	   t[7][2]=3;
	   t[7][3]=2;
	   t[8][1]=2;
	   t[8][2]=3;
	   t[8][3]=2;
	   t[9][1]=1;
	   t[9][2]=2;
	   t[9][3]=1;
	   t[10][1]=1;
	   t[10][2]=2;
	   t[10][3]=1;
	   t[11][1]=1;
	   t[11][2]=2;
	   t[11][3]=1;
	   t[12][1]=1;
	   t[12][2]=2;
	   t[12][3]=1;
	   
   }
   void creatModel() {
	   try {
		cplex = new IloCplex();
		for(int i=0;i<14;i++) {
			   for(int j=0;j<14;j++) {
				   x[i][j]=cplex.boolVar();
				   x[i][j].setName("x."+i+"."+j);
			   }
		   }
		
		obj = cplex.linearNumExpr();
		for(int i:N1) {
			obj.addTerm(1.0, x[0][i]);
		}
		cplex.addMinimize(obj);
		
		for(int i:N1) {
			expr=cplex.linearNumExpr();
			expr.addTerm(1.0, x[0][i]);
			for(int j:N2) {
				expr.addTerm(-1.0, x[i][j]);
				expr.addTerm(1.0, x[j][i]);
			}
			cplex.addEq(expr, 0);			
		}		
		for(int j:N2) {
			expr=cplex.linearNumExpr();
			expr.addTerm(1.0, x[j][4]);
			for(int i:N2) {
				expr.addTerm(-1.0, x[j][i]);
				expr.addTerm(1.0, x[i][j]);
			}
			cplex.addEq(expr, 0);			
		}
		for(int i : N1) {
			expr=cplex.linearNumExpr();
			for(int j :N2) {
				expr.addTerm(1.0, x[i][j]);
				expr.addTerm(-1.0, x[j][i]);
			}
			cplex.addEq(expr, 1);
		}
		for(int j : N2) {
			expr=cplex.linearNumExpr();
			for(int i :N1) {
				expr.addTerm(1.0, x[i][j]);
				expr.addTerm(-1.0, x[j][i]);
			}
			cplex.addEq(expr, -1);
		}
		expr=cplex.linearNumExpr();
		expr.addTerm(1.0, x[1][1]);
		expr.addTerm(1.0, x[2][1]);
		expr.addTerm(1.0, x[3][3]);
		expr.addTerm(1.0, x[4][1]);
		expr.addTerm(1.0, x[5][2]);
		expr.addTerm(1.0, x[6][1]);
		expr.addTerm(1.0, x[7][1]);
		expr.addTerm(1.0, x[8][3]);
		expr.addTerm(1.0, x[9][2]);
		expr.addTerm(1.0, x[10][2]);
		expr.addTerm(1.0, x[11][2]);
		expr.addTerm(1.0, x[12][3]);
		cplex.addEq(expr, 1);
	} catch (IloException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   
   }
   void solve()  {
	   try {
		cplex.solve();
		System.out.print(cplex.getObjValue());
	} catch (IloException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
   public static void main(String arg[]) {
	   cargoTrans exa = new cargoTrans();
	   exa.creatModel();
	   exa.solve();
   }
}

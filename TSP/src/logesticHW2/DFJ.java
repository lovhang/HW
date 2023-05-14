package logesticHW2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;

public class DFJ {
	private int size;
	private double[][] c;
	private Set<Set<Integer>> subset;
	private IloCplex cplex;
	private IloLinearNumExpr obj;
	private IloLinearNumExpr expr;
	private IloIntVar[][] x;
	private HashMap<Integer,ArrayList<Integer>> nw;//network result
	private ArrayList<ArrayList<Integer>> list;//subset list
	private ArrayList<Integer> vis;//list for node visited
	private ArrayList<Integer> nvis;//list for node not visited
	private ArrayList<Integer> sublist;//sublist
public DFJ(String input) {
	data a = new data(input);
	this.c=a.c;
	//this.subset=a.subsets;
	this.size=a.size;
	nvis = new ArrayList<Integer>();
	nw = new HashMap<Integer,ArrayList<Integer>> ();
	for(int i=0;i<size;i++) {
		nvis.add(i);
		nw.put(i, new ArrayList<Integer>());
	}
	/*
	for(int i=0;i<size;i++) {
		for(int j=0;j<size;j++) {
			System.out.println("c."+i+"."+j+"==="+c[i][j]);
		}
	}*/
}
void creatmodel() {
	try {
		cplex = new IloCplex();
		x = new IloIntVar[size][size];
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				x[i][j]=cplex.boolVar();
			}
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
//constraint 3 subtour elimination
		for(Set<Integer> s :subset) {
			if(s.size()>0 && s.size()<size) {
				expr=cplex.linearNumExpr();
				for(int i:s) {
					for(int j:s) {
						if(j != i) {
							expr.addTerm(1.0, x[i][j]);
						}
					}
				}
				cplex.addLe(expr, s.size()-1);
			}
		}
	} catch (IloException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}
void solve() {
	try {
		if(cplex.solve()==true) {
			
			System.out.println("problem solved");
		}
		cplex.end();
	} catch (IloException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
void Plainloop() {
	creatmodel();
	solve();
}
void LazycutSolve() throws UnknownObjectException, IloException {
	try {
		cplex = new IloCplex();
		x = new IloIntVar[size][size];
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				x[i][j]=cplex.boolVar();
				x[i][j].setName("x."+i+"."+j);
			}
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

		
		cplex.exportModel("lazycutmodel.lp");
		cplex.use(new Tsp(x,cplex));
		if(cplex.solve()) {
			System.out.println("obj value=="+cplex.getObjValue());
		}
	
		cplex.end();
	} catch (IloException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	
}
public class Tsp extends IloCplex.LazyConstraintCallback{
	private IloIntVar[][] xc;
	private IloCplex cplexc;
	public Tsp(IloIntVar[][] x,IloCplex cplex) throws UnknownObjectException, IloException {
		xc =x;
		cplexc=cplex;
	}	
	
	@Override
	protected void main() throws IloException {
		// TODO Auto-generated method stub
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++) {
				if(i != j) {
					if(getValue(xc[i][j])==1) {					
						if(nw.containsKey(i)) {
							if(nw.get(i).contains(j)==false) {
						nw.get(i).add(j);
							}
						}else {
						int a =j;
						nw.put(i, new ArrayList<Integer>(){{add(a);}});	
						}
						if(nw.containsKey(j)) {
							if(nw.get(j).contains(i)==false) {
							nw.get(j).add(i);
							}
							}else {
								int b=i;
								nw.put(j, new ArrayList<Integer>(){{add(b);}});	
							}
			         }
				}
		}
	}
		System.out.println(nw);
		creatsubset();
		System.out.println(list);
		if(list.size()>=2) {
			IloRange[] globalcut1 = new IloRange[list.size()];	
			IloRange[] globalcut2 = new IloRange[list.size()];
			for(int i=0;i<list.size();i++) {
				expr=cplexc.linearNumExpr();
				for(int j:list.get(i)) {
					for(int k:list.get(i)) {
						if(j != k && k>j) {
							expr.addTerm(1.0, xc[j][k]);;
						}
					}
				}
				globalcut1[i]=cplexc.addLe(expr, list.get(i).size()-1);
				expr=cplexc.linearNumExpr();
				for(int j:list.get(i)) {
					for(int k:list.get(i)) {
						if(j != k && k<j) {
							expr.addTerm(1.0, xc[j][k]);;
						}
					}
				}
				globalcut2[i]=cplexc.addLe(expr, list.get(i).size()-1);	
				add(globalcut1[i]);
				add(globalcut2[i]);
				
			
			}
			
		}
		
	}
}
void DPF(int a) {
	
	//System.out.println(nvis);
	sublist.add(a);
	nvis.remove(nvis.indexOf(a));	
	vis.add(a);
	//System.out.println(nvis);
	for(int i:nw.get(a)) {
		//System.out.println(i);
		//System.out.println(nvis.contains(i));
		if(nvis.contains(i)==true) {
			//System.out.println(nvis);
			//System.out.println("enter"+i);
			DPF(i);
		}
	}
	//System.out.println(sublist);
}
void creatsubset() {
	list = new ArrayList<ArrayList<Integer>>();
	nvis = new ArrayList<Integer>();
	vis = new ArrayList<Integer>();
	for(int i:nw.keySet()) {
		if(nvis.contains(i)) {
		sublist = new ArrayList<Integer>();
		DPF(i);
		list.add(sublist);
		//System.out.println(nvis);
		}
	}
	System.out.println(list.size());
}
void testDPF() {
	nw = new HashMap<Integer,ArrayList<Integer>>();
	nvis = new ArrayList<Integer>();
	vis = new ArrayList<Integer>();
	list = new ArrayList<ArrayList<Integer>>();
	sublist = new ArrayList<Integer>();
	nw.put(1, new ArrayList<Integer>() {{add(2);add(3);}});
	nw.put(2, new ArrayList<Integer>() {{add(1);add(3);}});
	nw.put(3, new ArrayList<Integer>() {{add(1);add(2);}});
	nw.put(4, new ArrayList<Integer>() {{add(5);}});
	nw.put(5, new ArrayList<Integer>() {{add(4);}});
	//System.out.println(nw);
	for(int i=1;i<6;i++) {
		nvis.add(i);
	}
	//DPF(1);
	creatsubset();
	System.out.println(list);
}
public static void main(String[] args)throws Exception{
	DFJ exa = new DFJ("TSP_instance_n_25_s_5.dat");
	//exa.testDPF();
	
	//exa.Plainloop();
	exa.LazycutSolve();	
}
}

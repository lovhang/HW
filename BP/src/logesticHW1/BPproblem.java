package logesticHW1;
import ilog.concert.*;
import ilog.cplex.*;
import java.io.*;
import java.util.ArrayList;
public class BPproblem {
	private IloIntVar[][] x;//variables xij
	private IloNumVar[] bv;//variable b represent number of bin used;
	private IloCplex cplex;
	private IloLinearNumExpr obj;//cplex objective function
	private IloLinearNumExpr expr;//cplex constraints
	private int N;//number of bins
	private double K;//length of bins
	private int b;//if bin j is used
	private double[] l;
	public ArrayList<Double> a;
	public ArrayList<Double> data;
           public BPproblem() {
        	   data = new ArrayList<Double>();
        	   a = new ArrayList<Double>();
        	   b=0;        	  
           }
           void creatModel() {
        	   try {
        		  
        		   cplex = new IloCplex();
        		   x = new IloIntVar[N][N];
        		   bv = new IloNumVar[N];
        		   for(int i=0;i<N;i++) {
        			   for(int j=0;j<N;j++) {
        				   x[i][j]=cplex.boolVar();
        				   x[i][j].setName("x."+i+"."+j);
        			   }
        		   }
        		   for(int j=0;j<N;j++) {
        			   bv[j]=cplex.boolVar();
        			   bv[j].setName("b."+j);
        		   }
        		   obj = cplex.linearNumExpr();
        		
        			   for(int j=0;j<N;j++) {
        				   obj.addTerm(1.0, bv[j]);
        			   }
        		   
        		   cplex.addMinimize(obj);
//constraints 1        		   
        		   for(int j=0;j<N;j++) {
        			   expr=cplex.linearNumExpr();
        			   for(int i=0;i<N;i++) {
        				   expr.addTerm(a.get(i), x[i][j]);          				   
        			   }
        			   expr.addTerm(-K, bv[j]);
        			   cplex.addLe(expr, 0);
        		   }
    
//constraint 2        		   
        		   for(int i=0;i<N;i++) {
        			   expr=cplex.linearNumExpr();
        			   for(int j=0;j<N;j++) {
        				   expr.addTerm(1.0, x[i][j]);
        			   }
        			   cplex.addEq(expr, 1);
        		   }
        		   cplex.setParam(IloCplex.Param.TimeLimit, 1200);
        		   
        		   cplex.exportModel("model.lp");
        	   }catch(IloException e) {
        		   e.printStackTrace();
        	   }
           }
           void solve() {
        	   try {
        		b=0;
				if(cplex.solve()==true) {
					
					System.out.println("Solution from IP"+cplex.getObjValue());//output the number of bins used
					System.out.println("problem solved");   
					
				   }
			} catch (IloException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           }
           void readData(String filename) throws FileNotFoundException {
        	   BufferedReader in = new BufferedReader(new FileReader(filename));
        	   String str;
        	   try {
				while((str=in.readLine()) != null) {
					   data.add( Double.parseDouble(str));
				   }
			} catch (NumberFormatException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	N=(int)Math.round(data.get(0));
        	//System.out.println("N"+N);
        	K=data.get(1);
        	System.out.println("K"+K);
        	for(int i=2;i<data.size();i++) {
        		a.add(data.get(i));
        	}
           }
           
           /**
            * first-fit algorithm
            */
           void firstfit(){
        	   l = new double[N];
        	   for(int i=0;i<N;i++) {
        		   for(int j=0;j<N;j++) {
        			   if(a.get(i)<K-l[j]) {
        				   l[j] = l[j]+a.get(i); //once find space, add item into this bin
        				   break;
        			   }
        		   }
        		   
        	   }
        	   b=0;
        	   for(int i=0;i<N;i++) {
        		   if(l[i]>0) {
        			   b++;
        		   }
        	   }
        	   System.out.println("solution from heurstic"+b);
           }
           public static void main(String arg[]) throws FileNotFoundException {
        	   BPproblem exa = new BPproblem();
        	   exa.readData("Falkenauer_u500_10.txt");
        	   exa.creatModel();
        	   long startTime = System.nanoTime();
        	   exa.solve();
        	   long endTime   = System.nanoTime();
               System.out.println("MIP Time"+(endTime - startTime)/1000000000);
               startTime =System.nanoTime();
        	   exa.firstfit();
        	   endTime   = System.nanoTime();
        	   System.out.println("Heuristic Time"+(endTime - startTime)/1000000000);
           }
}

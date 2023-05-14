package logesticHW2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

public class data {
	public int size;
	private double[] xcoord;
	private double[] ycoord;
	public double[][] c;
	public Set<Set<Integer>> subsets;
	private String input;
    public data(String a) {
    	input = a;
    	readdata();
    	//getsubset();
    
    }
void readdata() {
    	
		try {
			FileReader file;
			file = new FileReader(new File(input));
			BufferedReader br = new BufferedReader(file); 
	    	String line;
	    	String[] col;
	    	line = br.readLine();
	    	size = Integer.parseInt(line);
	    	System.out.println(size);
			xcoord = new double[size];
			ycoord = new double[size];
			c = new double[size][size];
	    	for(int i=0;i<size;i++) {
	    		line = br.readLine();
	    		col=line.split("\\s+");
	    		xcoord[i]=Double.parseDouble(col[0]);
	    		//System.out.println("x."+xcoord[i]);
	    		ycoord[i]=Double.parseDouble(col[1]);
	    		//System.out.println("y."+ycoord[i]);
	    	}
	    	for(int i=0;i<size;i++) {
	    	   for(int j=0;j<size;j++) {
	    		   c[i][j]=Math.sqrt((xcoord[i]-xcoord[j])*(xcoord[i]-xcoord[j])+(ycoord[i]-ycoord[j])*(ycoord[i]-ycoord[j]));
	    	   }
	    	}
		} catch (FileNotFoundException e) {
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
	int i=0;
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
		System.out.println("add set"+i);
		i++;
	}
	return powerSet;
}
void getsubset() {
	ArrayList<Integer> set = new ArrayList<Integer>();
	for(int i=0;i<size;i++) {
		set.add(i);
	}
	System.out.println(set);
	System.out.println("subset generating");
	subsets = powerSet(set);
	System.out.println("subset generated");
	
}
void output() {
	/*
	for(int i=0;i<size;i++) {
		for(int j=0;j<size;j++) {
			if(j != i) {
				System.out.println(i+"--"+j+":"+c[i][j]);
			}
		}
	}*/
	double cost=0;
	cost = c[3][4]+c[1][4]+c[0][1]+c[0][9]+c[5][9]+c[5][6]+c[6][2]+c[2][8]+c[8][7]+c[7][3];
	System.out.println("cost:"+cost);
}
public static void main(String[] args)throws Exception{
	data e = new data("TSP_instance_n_10_s_647.dat");
	e.output();
	//System.out.println(e.subsets);
	
}
}

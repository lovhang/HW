import java.util.ArrayList;
import java.util.HashMap;

public class network1 {
	cell[] cl;//cell collection for vehicle    
	cell[] cp;//cell collection for pedestrian
    public HashMap<Integer,cell> Alcell;//all cell collection
	public ArrayList<Integer> Sccell;//source cell collection
	public ArrayList<Integer> Itcell;//intermediate cell collection
	public ArrayList<Integer> Skcell;//sink cell collection
	public HashMap<Integer,cell> Alcelp;//all cell collection for pedestrian
	public ArrayList<Integer> Sccelp;//source cell collection for pedestrian
	public ArrayList<Integer> Itcelp;//intermediate cell collection for pedestrian
	public ArrayList<Integer> Skcelp;//sink cell collection for pedestrian
	public int cell_num=0;
	public int cell_nump=0;
	public network1(){
		cell_num=17;
		cl=new cell[cell_num] ;
		cl[1]=new cell(new int[] {} ,new int[] {2,3},10000,10000);
		cl[2]=new cell(new int[] {1},new int[] {4},1,4);
		cl[3]=new cell(new int[] {1},new int[] {5},2,4);
		cl[4]=new cell(new int[] {2},new int[] {6},2,4);
		cl[5]=new cell(new int[] {3},new int[] {7},2,4);
		cl[6]=new cell(new int[] {4},new int[] {8},1,4);
		cl[7]=new cell(new int[] {5,16},new int[] {9},2,2);
		cl[8]=new cell(new int[] {6},new int[] {10},1,4);
		cl[9]=new cell(new int[] {7},new int[] {11},2,4);
		cl[10]=new cell(new int[] {8},new int[] {12},1,4);
		cl[11]=new cell(new int[] {9},new int[] {13},2,4);
		cl[12]=new cell(new int[] {10},new int[] {13},1,4);
		cl[13]=new cell(new int[] {11,12},new int[] {},10000,10000);
		cl[14]=new cell(new int[] {},new int[] {15},10000,10000);
		cl[15]=new cell(new int[] {14},new int[] {16},1,4);
		cl[16]=new cell(new int[] {15},new int[] {7},1,4);
		 Alcell = new HashMap<Integer,cell>();
		 Sccell = new ArrayList<Integer>();
		 Itcell = new ArrayList<Integer>();
		 Skcell = new ArrayList<Integer>();
		for(int i=1;i<17;i++) {
			Alcell.put(i, cl[i]);
		}
		Sccell.add(1);Sccell.add(14);Skcell.add(13);
		for(int i=2;i<=12;i++) {
			Itcell.add(i);
		}
		Itcell.add(15);Itcell.add(16);
///////////////////////////////pedestrian network///////////////////////////		
	    cell_nump=43;
	    cp=new cell[cell_nump];
	    cp[1]=new cell(new int[] {} ,new int[] {2,3},10000,10000);
	    cp[2]=new cell(new int[] {1},new int[] {4},1,14);		
	    cp[3]=new cell(new int[] {1},new int[] {5},2,14);
	    cp[4]=new cell(new int[] {2},new int[] {6},1,14);
	    cp[5]=new cell(new int[] {3},new int[] {7},2,14);
	    cp[6]=new cell(new int[] {4},new int[] {8},1,14);
	    cp[7]=new cell(new int[] {5},new int[] {9},2,14);
	    cp[8]=new cell(new int[] {6},new int[] {10},2,14);
	    cp[9]=new cell(new int[] {7},new int[] {11},2,14);
	    cp[10]=new cell(new int[] {8},new int[] {12},2,14);
	    cp[11]=new cell(new int[] {9},new int[] {13},2,14);
	    cp[12]=new cell(new int[] {10},new int[] {14},2,14);
	    cp[13]=new cell(new int[] {11},new int[] {15},2,14);
	    cp[14]=new cell(new int[] {12},new int[] {16},1,14);
	    cp[15]=new cell(new int[] {13},new int[] {17},2,12);
	    cp[16]=new cell(new int[] {14},new int[] {18},1,14);
	    cp[17]=new cell(new int[] {15},new int[] {19},2,12);
	    cp[18]=new cell(new int[] {16},new int[] {20},1,14);
	    cp[19]=new cell(new int[] {17},new int[] {21},2,12);
	    cp[20]=new cell(new int[] {18},new int[] {22},1,14);
	    cp[21]=new cell(new int[] {19},new int[] {23},2,14);
	    cp[22]=new cell(new int[] {20},new int[] {24},1,14);
	    cp[23]=new cell(new int[] {21},new int[] {25},2,14);
	    cp[24]=new cell(new int[] {22},new int[] {26},1,14);
	    cp[25]=new cell(new int[] {23},new int[] {27},2,14);
	    cp[26]=new cell(new int[] {24},new int[] {28},1,14);
	    cp[27]=new cell(new int[] {25},new int[] {29},2,14);
	    cp[28]=new cell(new int[] {26},new int[] {30},1,14);
	    cp[29]=new cell(new int[] {27},new int[] {31},2,14);
	    cp[30]=new cell(new int[] {28},new int[] {32},1,14);
	    cp[31]=new cell(new int[] {29},new int[] {35},2,14);
	    cp[32]=new cell(new int[] {30},new int[] {33},1,14);
	    cp[33]=new cell(new int[] {32},new int[] {34},1,14);
	    cp[34]=new cell(new int[] {33},new int[] {35},1,14);
	    cp[35]=new cell(new int[] {34,31} ,new int[] {},10000,10000);
	    cp[36]=new cell(new int[] {} ,new int[] {37},10000,10000);
	    cp[37]=new cell(new int[] {36},new int[] {38},1,14);
	    cp[38]=new cell(new int[] {37},new int[] {39},1,14);
	    cp[39]=new cell(new int[] {38},new int[] {40},1,14);
	    cp[40]=new cell(new int[] {39},new int[] {41},1,14);
	    cp[41]=new cell(new int[] {40},new int[] {42},1,14);
	    cp[42]=new cell(new int[] {41},new int[] {15},1,14);
	    
	    Alcelp = new HashMap<Integer,cell>();
		 Sccelp = new ArrayList<Integer>();
		 Itcelp = new ArrayList<Integer>();
		 Skcelp = new ArrayList<Integer>();
		 
		 for(int i=1;i<cell_nump;i++) {
			 Alcelp.put(i, cp[i]);
		 }
		 Sccelp.add(1);Sccelp.add(36);
		 for(int i=2;i<35;i++) {
			 Itcelp.add(i);
		 }
		 for(int i=37;i<=42;i++) {
			 Itcelp.add(i);
		 }
		 Skcelp.add(35);
	}
}

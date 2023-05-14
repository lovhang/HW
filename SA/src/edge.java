import java.util.ArrayList;

public class edge {
   private ArrayList<Integer> node;
   private int a;
   private int b;
   private double dis;
   public edge(int aa, int bb, double diss) {
	   a=aa;
	   b=bb;
	   dis=diss;
   }
   public void name() {
	   System.out.println(a+"--"+b);
   }
   public int getA() {
	   return a;
   }
   public int getB() {
	   return b;
   }
   public double cost() {
	   return dis;
   }
   public void setdis(double a) {
	   dis = a;
   }
}

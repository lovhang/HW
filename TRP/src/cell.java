/**
 * 
 * @author xiaohang
 * predecessor, sucessor, flow, capacity
 */
public class cell {

	private int[] predecessor;
    private int[] sucessor;
    private double capacity;
    private double flow;
    public cell(int[] predecessor ,int[] sucessor, double flow,double capacity) {
    	this.predecessor = predecessor;
    	this.sucessor = sucessor;
    	this.capacity = capacity;
    	this.flow = flow;
    }
    public int[] getPredecessor(){
    	return predecessor;
    }
    public int[] getSucessor(){
    	return sucessor;
    }
    public double getFlow(){
    	return flow;
    }
    public double getCapacity(){
    	return capacity;
    }
}

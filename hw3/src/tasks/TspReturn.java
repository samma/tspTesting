package tasks;
import java.io.Serializable;
public class TspReturn implements Serializable{

    static final long serialVersionUID = 227L; // Was missing 
	private double lengthOfRoute;
	private int [] route;
    /**
     * 
     * @param taskCoordX where in the complete picture the sub-picture will be placed on the x axis. 
     * @param taskCoordY where in the complete picture the sub-picture will be placed on the y axis.
     * @param MandelbrotResults describes the number of iterations for each pixel in the array that each task returns. This will make up a small part of the complete picture.
     */
    public TspReturn( int [] route, double lengthOfRoute)
    {
    	this.lengthOfRoute = lengthOfRoute;
    	this.route = route;
    }

    public int [] getRoute() { return route; }
    
    public double getRouteLength(){ return lengthOfRoute;}

    
	
}

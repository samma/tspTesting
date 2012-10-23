package tasks;
import java.io.Serializable;
public class MandelbrotReturn implements Serializable{

    static final long serialVersionUID = 227L; // Was missing 
	private int taskCoordX;
	private int taskCoordY;
    private int [][] MandelbrotResults;

    /**
     * 
     * @param taskCoordX where in the complete picture the sub-picture will be placed on the x axis. 
     * @param taskCoordY where in the complete picture the sub-picture will be placed on the y axis.
     * @param MandelbrotResults describes the number of iterations for each pixel in the array that each task returns. This will make up a small part of the complete picture.
     */
    public MandelbrotReturn( int taskCoordX, int taskCoordY, int [][] MandelbrotResults)
    {
    	this.taskCoordY = taskCoordY;
        this.taskCoordX = taskCoordX;
        this.MandelbrotResults = MandelbrotResults;
    }

    public int getTaskCoordX() { return taskCoordX; }
    
    public int getTaskCoordY() { return taskCoordY; }

    public int [][] getMandelbrotResults() { return MandelbrotResults; }
    
	
}

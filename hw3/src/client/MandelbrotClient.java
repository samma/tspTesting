package client;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import system.ComputerImpl;
import system.SpaceImpl;
import tasks.MandelbrotSetTask;
import tasks.MandelbrotReturn;
import tasks.TspTask;

import java.rmi.registry.*;

import api.Result;
import api.Space;

public class MandelbrotClient {
	private static final int N_PIXELS = 1024;
	private static final int ITERATION_LIMIT = 512;
	private static final double CORNER_X = -0.7510975859375;
	private static final double CORNER_Y = 0.1315680625;
	private static final double EDGE_LENGTH = 0.01611;
	private static final int taskDivideNum = 3;
	private static final int numTasks = taskDivideNum*taskDivideNum; 
	private static SpaceImpl localSpace;
	private static ComputerImpl localComputer;
	
	
	public static void main(String[] args) {
		
		
		if (args.length > 1) {
    		if (args[1].equals("standalone")){
    			System.out.println("Running standalone mode");
    			localSpace = new SpaceImpl();
    			localSpace.initLocaly(localSpace);
    			
    			localComputer = new ComputerImpl();
    			localComputer.initLocaly(localComputer);
    		}
    		
    	}
		
		
		if (System.getSecurityManager() == null ){ 
		   System.setSecurityManager(new java.rmi.RMISecurityManager() ); 
		}
		try{
			
			
			
			
			
			String name = "Space";
    		Registry registry = LocateRegistry.getRegistry(args[0]);
    		Space space = (Space) registry.lookup(name);
    		
    		
    		int [][] count = null;
    		System.out.println("Starting Mandelbrotset job");
    		int total = 0;
    		for (int i = 0; i < 5; i++){
    			long start = System.currentTimeMillis();
        		//MandelbrotSetTask mst = new MandelbrotSetTask(CORNER_X, CORNER_Y, EDGE_LENGTH, N_PIXELS, ITERATION_LIMIT, 0, 0);
    			//MandelbrotReturn mrst = mst.execute();
        		//count =  mrst.getMandelbrotResults() ;
        		count = runJob(space);
    			long stop = System.currentTimeMillis();
    			System.out.println("Mandelbrot, " + (i+1) +" try: " +(stop-start) +" milliseconds");
    			total += (stop-start);
    		}
    		System.out.println("Average time: " + total/5);
			


    		
			JLabel mandelbrotLabel = displayMandelbrotSetTaskReturnValue( count );
			JFrame frame = new JFrame( "Result Visualizations" );
			frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			Container container = frame.getContentPane();
			container.setLayout( new BorderLayout() );
			container.add( new JScrollPane( mandelbrotLabel ), BorderLayout.WEST );
			frame.pack();
			frame.setVisible( true );
			space.exit();
		}catch (Exception e) {
			System.err.println("MandelbrotClient exception:");
			e.printStackTrace();
		}
	}

	
    private static int[][] runJob(Space space){
    	
       	try{    		
       		
       		double newEdgeLength = EDGE_LENGTH/taskDivideNum;
			double newCornerX;
			double newCornerY;
			int newPixelCount = 0;
    		for (int i = 0;i<taskDivideNum;i++){
    			for (int j = 0;j<taskDivideNum;j++){
    				newCornerX = CORNER_X+(i*newEdgeLength);
    				newCornerY = CORNER_Y+(j*newEdgeLength);
    				newPixelCount = (int)(N_PIXELS/taskDivideNum);
 
    				space.put(new MandelbrotSetTask(newCornerX, newCornerY, newEdgeLength, newPixelCount, ITERATION_LIMIT, i, taskDivideNum-j-1));
        		}
    		}
    		
        	Result[] results = new Result[numTasks];
        	MandelbrotReturn[] retVals = new MandelbrotReturn [numTasks];

        	for (int i = 0; i < numTasks;i++){
        		results[i] = space.take();
        		retVals[i] = (MandelbrotReturn)results[i].getTaskReturnValue();
        	}
        	
        	
        	int [][] count = new int [N_PIXELS][N_PIXELS];
    		for (int x = 0;x<taskDivideNum;x++){
    			for (int y = 0;y<taskDivideNum;y++){
    				for (int k = 0; k<numTasks;k++){
    					if (retVals[k].getTaskCoordX() == x && retVals[k].getTaskCoordY() == y){
    						for (int g = 0; g<newPixelCount;g++){
    							for (int v = 0; v<newPixelCount;v++){
        							count[(int)(x*newPixelCount)+g][(int)(y*newPixelCount)+v] = retVals[k].getMandelbrotResults()[g][v];
        						}
    						}
    					}
    				}
        		}
    		}
    		return count;
    	
    	}catch (Exception e){
    		System.err.println("TspClient exception:");
    		e.printStackTrace();
    		return null;
    		}
    }

	
	private static JLabel displayMandelbrotSetTaskReturnValue( int[][] counts )
	{
	    Image image = new BufferedImage( N_PIXELS, N_PIXELS, BufferedImage.TYPE_INT_ARGB );
	    Graphics graphics = image.getGraphics();
	    for ( int i = 0; i < counts.length; i++ )
	    for ( int j = 0; j < counts.length; j++ )
	    {
	        graphics.setColor( getColor( counts[i][j] ) );
	        graphics.fillRect(i, j, 1, 1);
	    }
	    ImageIcon imageIcon = new ImageIcon( image );
	    return new JLabel( imageIcon );
	}

	private static Color getColor( int i )
	{
	    if ( i == ITERATION_LIMIT )
	        return Color.BLACK;
	    return Color.getHSBColor((float)i/ITERATION_LIMIT, 1F, 1F);
	}
	

}

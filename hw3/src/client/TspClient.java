package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import api.Space;
import api.Result;
import api.Task;
import system.ComputerImpl;
import system.SpaceImpl;
import tasks.TspReturn;
import tasks.TspTask;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;



public class TspClient {
	private static SpaceImpl localSpace;  // Used for running on one JVM
	private static ComputerImpl localComputer;
	
	private static final int N_PIXELS = 512;
	private static final double[][] towns =
		{
			{ 1, 1 },
			{ 8, 1 },
			{ 8, 8 },
			{ 1, 8 },
			{ 2, 2 },
			{ 7, 2 },
			{ 7, 7 },
			{ 2, 7 },
			{ 3, 3 },
			{ 6, 3 },
			//{ 6, 6 },
			//{ 3, 6 }
		};
	
	private static final double[][] distances = calcAllDistances(towns);
	
	private static final int [] subRoute = {};
	
    public static void main(String args[]) {
        
    	
    	if (args.length > 1) {
    		if (args[1].equals("standalone")){
    			System.out.println("Running standalone mode");
    			localSpace = new SpaceImpl();
    			localSpace.initLocaly(localSpace);
    			
    			localComputer = new ComputerImpl();
    			localComputer.initLocaly(localComputer);
    		}
    		
    	}
    	
    	
    	
    	if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
        	

        	
        	String name = "Space";
    		Registry registry = LocateRegistry.getRegistry(args[0]);
    		Space space = (Space) registry.lookup(name);
		
    		System.out.println("Starting TSP job");
    		int tour[] = new int [towns.length];
    		int total = 0;
    		for (int i = 0; i < 5; i++){
    			long start = System.currentTimeMillis();
        		tour = runJob (space,distances,subRoute);
    			long stop = System.currentTimeMillis();
    			System.out.println("TSP, " + (i+1) +" try: " +(stop-start) +" milliseconds");
    			total += (stop-start);
    		}
    		System.out.println("Average time: " + total/5);
    		       	

            JLabel euclideanTspLabel = displayEuclideanTspTaskReturnValue( towns, tour );
            JFrame frame = new JFrame( "Result Visualizations" );
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
            Container container = frame.getContentPane();
            container.setLayout( new BorderLayout() );
            container.add( new JScrollPane( euclideanTspLabel ), BorderLayout.EAST );
            frame.pack();
            frame.setVisible( true );
            space.exit();
        } catch (Exception e) {
            System.err.println("ComputeTSP exception:");
            e.printStackTrace();
        }
        
        
        
    }    
            
    private static int[] runJob(Space space, double[][] distances, int [] subRoute){
    	
       	try{    		

        	space.put(new TspTask(towns, subRoute, distances));
           	Result results = space.take();
           	return (int[])results.getTaskReturnValue();
    	
    	}catch (Exception e){
    		System.err.println("TspClient exception:");
    		e.printStackTrace();
    		return null;
    		}
    }
    
    private static JLabel displayEuclideanTspTaskReturnValue( double[][] cities, int[] tour )
    {
        System.out.print( "Tour: ");
        for ( int city: tour )
        {
        	System.out.print( city + " ");
        }
        System.out.println("");

        // display the graph graphically, as it were
        // get minX, maxX, minY, maxY, assuming they 0.0 <= mins
        double minX = cities[0][0], maxX = cities[0][0];
        double minY = cities[0][1], maxY = cities[0][1];
        for ( int i = 0; i < cities.length; i++ )
        {
            if ( cities[i][0] < minX ) minX = cities[i][0];
            if ( cities[i][0] > maxX ) maxX = cities[i][0];
            if ( cities[i][1] < minY ) minY = cities[i][1];
            if ( cities[i][1] > maxY ) maxY = cities[i][1];
        }
    	
        // scale points to fit in unit square
        double side = Math.max( maxX - minX, maxY - minY );
        double[][] scaledCities = new double[cities.length][2];
        for ( int i = 0; i < cities.length; i++ )
        {
            scaledCities[i][0] = ( cities[i][0] - minX ) / side;
            scaledCities[i][1] = ( cities[i][1] - minY ) / side;
        }

        Image image = new BufferedImage( N_PIXELS, N_PIXELS, BufferedImage.TYPE_INT_ARGB );
        Graphics graphics = image.getGraphics();

        int margin = 10;
        int field = N_PIXELS - 2*margin;
        // draw edges
        graphics.setColor( Color.BLUE );
        int x1, y1, x2, y2;
        int city1 = tour[0], city2;
        x1 = margin + (int) ( scaledCities[city1][0]*field );
        y1 = margin + (int) ( scaledCities[city1][1]*field );
        for ( int i = 1; i < cities.length; i++ )
        {
            city2 = tour[i];
            x2 = margin + (int) ( scaledCities[city2][0]*field );
            y2 = margin + (int) ( scaledCities[city2][1]*field );
            graphics.drawLine( x1, y1, x2, y2 );
            x1 = x2;
            y1 = y2;
        }
        city2 = tour[0];
        x2 = margin + (int) ( scaledCities[city2][0]*field );
        y2 = margin + (int) ( scaledCities[city2][1]*field );
        graphics.drawLine( x1, y1, x2, y2 );

        // draw vertices
        int VERTEX_DIAMETER = 6;
        graphics.setColor( Color.RED );
        for ( int i = 0; i < cities.length; i++ )
        {
            int x = margin + (int) ( scaledCities[i][0]*field );
            int y = margin + (int) ( scaledCities[i][1]*field );
            graphics.fillOval( x - VERTEX_DIAMETER/2,
                               y - VERTEX_DIAMETER/2,
                              VERTEX_DIAMETER, VERTEX_DIAMETER);
        }
        ImageIcon imageIcon = new ImageIcon( image );
        return new JLabel( imageIcon );
    }
    /**
     * Calculates the distance between two towns from their position 
     * as x and y coordinates on the map.
     * 
     * @param town1X
     * @param town1Y
     * @param town2X
     * @param town2Y
     * @return the length between two towns
     */
    public static double calcOneDistance(double town1X, double town1Y, double town2X, double town2Y){
    	return Math.sqrt((town1X-town2X)*(town1X-town2X) + (town1Y-town2Y)*(town1Y-town2Y));
    }
    
    /**   
	 * This methods simply calculates the distance between each and every
	 * pair of towns on the map. 
	 *
	 * @param towns , the coordinates of all towns on the map
	 * @return a list of distances between every pair of towns
	 */
    public static double[][] calcAllDistances(double[][] towns){
    	int inf = 1000000;
    	double[][] distances = new double[towns.length][towns.length];
    	for (int i = 0; i< towns.length;i++){
    		for (int j = 0; j<towns.length;j++){
    			if (i != j){
    				distances[i][j] = calcOneDistance(towns[i][0],towns[i][1],towns[j][0],towns[j][1]);
    			}
    			else distances[i][j] = inf;
    		}
    	}
    	return distances;
    }
    
}
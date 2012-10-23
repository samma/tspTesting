package tasks;

import api.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.awt.List;
import java.io.Serializable;

import permutation.PermUtil;

/**
 * This class implements a Traveling Salesman Problem solver as a task
 * which fits into the RMI framework implemented in the API.
 * 
 * The solver simply brute forces and finds all possible routes in both 
 * directions and then evaluates them all to find the most efficient one.
 * 
 * @author torgel
 *
 */

public class TspTask implements Task<int[]>, Serializable{

    private static final long serialVersionUID = 227L;
    
    private int [] subRoute;
    
    double [][] distances;
    
    /**
     * Starts the TSP computation
     * 
     * @return an int[] tour that lists the order of the cities of a minimal distance tour. 
     */
    public int [] execute() {
    	return computeRoute(subRoute,distances);
    }
    
    /**
	 * @param towns is the input sequence of the towns in the coordinate system. 
	 * takes a double[][] cities that codes the x and y coordinates of city[i]: 
	 * cities[i][0] is the x-coordinate of city[i] and cities[i][1] is the 
	 * y-coordinate of city[i]. 
	 * @param subRoute is the beginning of the subset of routes that will be searched in
	 * @param distances is the distances between each pair of towns
	 */
    public TspTask(double [][] towns,int [] subRoute, double[][] distances) {
        this.subRoute = subRoute;
        this.distances = distances;
        
    }
    
    /**
     * Finds the shortest route, 1 to N, excludes towns found in subRoute
     * 
     * @param towns is the input sequence of the towns in the coordinate system
     * @param subRoute is the beginning of the subset of routes that will be searched in
     * @param distances is the distances between each pair of towns
     * @return the sequence of towns to visit for the shortest route
     */
public static int[] computeRoute(int [] subRouteIn, double [][] distances){

    	
    	//Block for stupid type conversion
		Integer [] subRoute = new Integer[subRouteIn.length];
		for (int i = 0 ; i < subRoute.length ; i++){
			subRoute[i] = subRouteIn[i];
		}
		
		//The search should start in the last excluded town - startTown
		int startTown = subRoute[subRoute.length-1];
		
		Object [] tempStorage = new Object [distances.length-subRoute.length+1];
		int [] townsToExplore = new int [distances.length-subRoute.length+1];
    	int [] tempTownStorage = new int [distances.length-subRoute.length+1];
		
    	//Removes to subRoutes from the set of routes, the result is the routes we want to explore
    	ArrayList<Integer> townsToExploreList = new ArrayList<Integer>();
    	townsToExploreList.add(startTown);
		for (int j = 0;j<distances.length;j++){
			if (!Arrays.asList(subRoute).contains((int)j)){
				townsToExploreList.add(j);
			}
		}
		
		//More stupid type conversion
		townsToExploreList.toArray(tempStorage);
		for (int i = 0;i<(townsToExplore.length);i++){
			townsToExplore[i] = (Integer)tempStorage[i];
		}

		//This is a type which can be called like permute_next to get another permutation
		PermUtil permute = new PermUtil(townsToExplore);
		
		//this next variable is infinite-ish
		double shortestRouteLength = 100000;
		double temp;
		int [] shortestRoute = tempTownStorage;
		for (int j = 0 ; j <  factorial(townsToExplore.length); j++){
			tempTownStorage = permute.next();
			temp = calcRouteLength(tempTownStorage,distances);
			if (temp < shortestRouteLength){
				shortestRouteLength = temp;
				shortestRoute = tempTownStorage;
			}
		}
		System.out.println(shortestRouteLength);
		return shortestRoute;
		
    }
    
    /**
     * Calculates the length of one route.
     * 
     * @param route gives the sequence of towns to visits.
     * @param distances gives the distances between each town
     * @return
     */
    public static double calcRouteLength(int[] route, double[][] distances){
    	double sum = 0;
    	for (int i = 0;i<route.length-1;i++){
    		sum += distances[route[i]][route[i+1]];
    	}
    	return sum;	
    }
    
    public static int factorial(int n)
    {
        if (n == 0) return 1;
        return n * factorial(n-1);
    }
}
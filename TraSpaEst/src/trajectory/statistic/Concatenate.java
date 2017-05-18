package trajectory.statistic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
public class Concatenate {
	
	/* the threshold of number of trajectory between two points */
	public static final int NUM_OF_TRAJECTORY = 10;
	
	/* read from a matrix which was generated beforehand storing 
	 * the number of trajectories passing from one point to another 
	 * point, in order to reduce the space of the matrix by keeping
	 * only the point IDs having more than a certain number of 
	 * trajectories in between*/
	public static void  buildConnectedList(String fileName, HashSet<Point> pointSet, 
			ArrayList<ArrayList<Integer>> connectedList) throws FileNotFoundException{
		
		FileInputStream inputStreamConnectedList = new FileInputStream(fileName);
		
        @SuppressWarnings("resource")
		Scanner scConnectedList = new Scanner(inputStreamConnectedList, "UTF-8");
        String lineConnectedList = "";
        String SplitBy = ",";
        int lineNum = 0;
        
        while (scConnectedList.hasNextLine()) {
        	
        	/* scan a line */
            lineConnectedList = scConnectedList.nextLine();
            
            /* split a line into items, store into an array */
            String[] lineData = lineConnectedList.split(SplitBy);
            ArrayList<Integer> list = new ArrayList<Integer>();
            
            /* scan the element of a line */
            for(int index = 1; index < lineData.length; index++){
            	/* if the element is larger than the threshold 
            	 * */
            }
        }
		
	}
	
}

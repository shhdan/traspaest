/* ************THIS IS A TEMPORARY CLASS FOR CONCATENATION!**************/

package trajectory.statistic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;


public class PreProcess {
	
	/* the threshold of number of trajectory between two points */
	public static final int NUM_OF_TRAJECTORY = 10;
	
	/* read from a matrix which was generated beforehand storing 
	 * the number of trajectories passing from one point to another 
	 * point, in order to reduce the space of the matrix by keeping
	 * only the point IDs having more than a certain number of 
	 * trajectories in between */
	public static void  generateConnectedList(String fileName, HashSet<Point> pointSet, 
			ArrayList<ArrayList<Integer>> connectedList) throws FileNotFoundException{
		
		FileInputStream inputStreamConnectedList = new FileInputStream(fileName);
		
        @SuppressWarnings("resource")
		Scanner scConnectedList = new Scanner(inputStreamConnectedList, "UTF-8");
        String lineConnectedList = "";
        String SplitBy = ",";
        int lineIndex = 0;
        
        while (scConnectedList.hasNextLine()) {
        	
        	/* scan a line */
            lineConnectedList = scConnectedList.nextLine();
            
            /* split a line into items, store into an array */
            String[] lineData = lineConnectedList.split(SplitBy);
            ArrayList<Integer> pointList = new ArrayList<Integer>();
            
            /* scan the element of a line */
            for(int columnIndex = 1; columnIndex < lineData.length; columnIndex++){
            	/* if the element is larger than the threshold 
            	 * then keep the point ID in the list, and put this
            	 * pair of point IDs into a hash set */
            	if(Integer.parseInt(lineData[columnIndex]) >= NUM_OF_TRAJECTORY){
            		/* add the point pair to the hash set */
            		Point pair = new Point();
            		pair.set_coordinate(0, lineIndex);
            		pair.set_coordinate(1, columnIndex);
            		pointSet.add(pair);
            		/* add the point index to the list */
            		pointList.add(columnIndex);
            	}
            }
            /* line index */
            lineIndex++;
            /* add the list to array */
            connectedList.add(pointList);
        }		
	}
	

}

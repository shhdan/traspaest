package trajectory.statistic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
//import java.util.Stack;

public class ConcatenateUtil {
	
	/* the threshold of number of trajectory between two points */
	//public static final int NUM_OF_TRAJECTORY = 10;
	public static ArrayList<ArrayList<Integer>> connectedList;
	
	/* read the connected list which is generated beforehand 
	 * referring to the PreProcess.java */
	public static void  buildConnectedList(String fileName) 
			throws FileNotFoundException{
		
		FileInputStream inputStreamConnectedList = new FileInputStream(fileName);
		
        @SuppressWarnings("resource")
		Scanner scConnectedList = new Scanner(inputStreamConnectedList, "UTF-8");
        String lineConnectedList = "";
        String SplitBy = ",";
        
        while (scConnectedList.hasNextLine()) {
        	
        	/* scan a line */
            lineConnectedList = scConnectedList.nextLine();
            
            /* split a line into items, store into an array */
            String[] lineData = lineConnectedList.split(SplitBy);
            ArrayList<Integer> pointList = new ArrayList<Integer>();
            
            /* scan the element of a line */
            for(int columnIndex = 0; columnIndex < lineData.length; columnIndex++){           	
            	/* add the point index to the list */
            	pointList.add(Integer.parseInt(lineData[columnIndex]));
            }

            /* add the list to array */
            connectedList.add(pointList);
        }		
	}
	
	public static void getPathEnum(int k, int currentPointId, int destinationPointId,
			ArrayList<Integer> pathEnum, ArrayList<ArrayList<Integer>> result) {
		
		ArrayList<Integer> neighborList = connectedList.get(currentPointId);
		if (k == 0) {
			if (neighborList.contains(destinationPointId)) {
				pathEnum.add(destinationPointId);
				
				/* check whether there is loop in the permutation */
				HashSet<Integer> pathSet = new HashSet<Integer>();
				for(Integer elem : pathEnum){
					pathSet.add(elem);
				}
				
				if(pathSet.size() == pathEnum.size())
					result.add(pathEnum);
			}
			return;
		}
		for (Integer elem : neighborList) {
			
			ArrayList<Integer> newPath = new ArrayList<Integer>(pathEnum);
			newPath.add(elem);
			getPathEnum(k-1, elem, destinationPointId, newPath, result);
		}
	}
	
	
	public static void main(String[] args) {
		FileInputStream inputStreamTrajectoryList;
		connectedList = new ArrayList<>();
		try {
			inputStreamTrajectoryList = new FileInputStream("/home/uqdhe/data/tmp");
			@SuppressWarnings("resource")
			Scanner scTrajectoryList = new Scanner(inputStreamTrajectoryList, "UTF-8");
	        String lineTrajectoryList = "";
	        String SplitBy = ",";
	        
	        while (scTrajectoryList.hasNextLine()) {
	        	
	        	// scan a line 
	            lineTrajectoryList = scTrajectoryList.nextLine();
	            
	            // split a line into items, store into an array 
	            String[] lineData = lineTrajectoryList.split(SplitBy);
	            
	            // new a trajectory 
	            ArrayList<Integer> list = new ArrayList<>();
	            for (String s:lineData) {
	            	int i = Integer.parseInt(s);
	            	list.add(i);
	            }
	            connectedList.add(list);
	        }
	        
	        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
	        ArrayList<Integer> path = new ArrayList<Integer>();
	        int start=2;
	        int dest=4;
	        path.add(start);
	        getPathEnum(3, start, dest, path, result);
	        for (ArrayList<Integer> list: result) {
	        	for (Integer i : list) {
	        		System.out.print(i+",");
	        	}
	        	System.out.println();
	        }
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
}

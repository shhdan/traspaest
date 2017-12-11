package trajectory.statistic;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
//import java.util.Stack;

public class ConcatenateUtil {
	
	/* the threshold of number of trajectory between two points */
	//public static final int NUM_OF_TRAJECTORY = 10;
	public static ArrayList<ArrayList<Integer>> originConnectedList = new ArrayList<ArrayList<Integer>>();
	
	// this is the update transition graph, with some of the edges deleted according to the input OD pair.
	public static ArrayList<ArrayList<Integer>> temporaryConnectedList = new ArrayList<ArrayList<Integer>>();
	public static ArrayList<EMD.Point> pointList = new ArrayList<EMD.Point>();
	public static double DIST_RATIO = 1.001;
	
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
            for(int columnIndex = 1; columnIndex < lineData.length; columnIndex = columnIndex + 2){           	
            	/* add the point index to the list */
            	pointList.add(Integer.parseInt(lineData[columnIndex]));
            }

            /* add the list to array */
            originConnectedList.add(pointList);
        }		
	}
	
	public static void buildPointList(String fileName) 
			throws FileNotFoundException{
		FileInputStream inputStreamPointList = new FileInputStream(fileName);
		
        @SuppressWarnings("resource")
		Scanner scPointList = new Scanner(inputStreamPointList, "UTF-8");
        String linePointList = "";
        String SplitBy = ",";
        
        while (scPointList.hasNextLine()) {
        	
        	/* scan a line */
            linePointList = scPointList.nextLine();
            
            /* split a line into items, store into an array */
            String[] lineData = linePointList.split(SplitBy);
            
            EMD.Point point = new EMD.Point();
            point.latitude = Double.parseDouble(lineData[6])/10000;
            point.longitude = Double.parseDouble(lineData[5])/10000;

            /* add the list to array */
            pointList.add(point);
        }		
	}
	
	public static void getPathEnum(int k, int currentPointId, int destinationPointId,
			ArrayList<Integer> pathEnum, ArrayList<ArrayList<Integer>> result) {
		
		/* get the connected list of current point, which stores all the neighbors of it */
		ArrayList<Integer> neighborList = originConnectedList.get(currentPointId);
		if (k == 0) {
			if (neighborList.contains(destinationPointId)) {
				pathEnum.add(destinationPointId);
				HashSet<Integer> pathSet = new HashSet<Integer>();
				for(Integer elemSet : pathEnum){
					pathSet.add(elemSet);
				}
				
				if(pathSet.size() == pathEnum.size()){
					if(accumulativeSpatialLength(pathEnum.get(0), destinationPointId, pathEnum, DIST_RATIO) && 
							direction(pathEnum.get(0), destinationPointId, pathEnum))
						result.add(pathEnum);
				}
					
			}
			return;
		}
		/* start the recursion */
		for (Integer elem : neighborList) {
			/* new a new path to add the current point into the path */
			ArrayList<Integer> newPath = new ArrayList<Integer>(pathEnum);
			newPath.add(elem);
			
			
			/* check whether there is loop in the permutation */
			HashSet<Integer> pathSet = new HashSet<Integer>();
			for(Integer elemSet : newPath){
				pathSet.add(elemSet);
			}
			
			if(pathSet.size() == newPath.size()){
				if(accumulativeSpatialLength(pathEnum.get(0), destinationPointId, newPath, DIST_RATIO) && 
						direction(pathEnum.get(0), destinationPointId, newPath))
					getPathEnum(k-1, elem, destinationPointId, newPath, result);
			}
		
		}
	}
	
	public static void getPathEnumOld(int k, int currentPointId, int destinationPointId,
			ArrayList<Integer> pathEnum, ArrayList<ArrayList<Integer>> result) {
		
		/* get the connected list of current point, which stores all the neighbors of it */
		ArrayList<Integer> neighborList = originConnectedList.get(currentPointId);
		if (k == 0) {
			if (neighborList.contains(destinationPointId)) {
				pathEnum.add(destinationPointId);
				HashSet<Integer> pathSet = new HashSet<Integer>();
				for(Integer elemSet : pathEnum){
					pathSet.add(elemSet);
				}
				
				if(pathSet.size() == pathEnum.size()){
						result.add(pathEnum);
				}
					
			}
			return;
		}
		/* start the recursion */
		for (Integer elem : neighborList) {
			/* new a new path to add the current point into the path */
			ArrayList<Integer> newPath = new ArrayList<Integer>(pathEnum);
			newPath.add(elem);
			
			
			/* check whether there is loop in the permutation */
			HashSet<Integer> pathSet = new HashSet<Integer>();
			for(Integer elemSet : newPath){
				pathSet.add(elemSet);
			}
			
			if(pathSet.size() == newPath.size()){
					getPathEnumOld(k-1, elem, destinationPointId, newPath, result);
			}
		
		}
	}
	
	public static boolean accumulativeSpatialLength(int origin, int destination,
			ArrayList<Integer> pathEnum, double ratio){
		double ADL = 0.0;
		for(int i = 1; i < pathEnum.size(); i++){
			ADL = ADL + distance(pointList.get(pathEnum.get(i)), pointList.get(pathEnum.get(i-1)));
		}
		double distOD = distance(pointList.get(origin), pointList.get(destination));
		if(ADL/distOD <= ratio)
			return true;
		return false;
	}
	
	public static boolean direction(int origin, int destination, 
			ArrayList<Integer> pathEnum){
		int last = pathEnum.size()-1;
		EMD.Point currentPoint = pointList.get(pathEnum.get(last));
		EMD.Point previousPoint = new EMD.Point();
		if(last < 2)
			previousPoint = pointList.get(origin);
		else
		previousPoint = pointList.get(pathEnum.get(last - 1));
		EMD.Point originPoint = pointList.get(origin);
		EMD.Point destinationPoint = pointList.get(destination);
		EMD.Point projectionPoint = getClosestPointOnSegment(originPoint, destinationPoint, previousPoint);
		double degree1 = calculateDegree(currentPoint, originPoint, destinationPoint);
		if(degree1 >= 90)
			return false;
		double degree2 = calculateDegree(currentPoint, destinationPoint, originPoint);
		if(degree2 >= 90)
			return false;
		double degree3 = calculateDegree(currentPoint, previousPoint, destinationPoint);
		if(degree3 >= 90)
			return false;
		double degree4 = calculateDegree(currentPoint, projectionPoint, destinationPoint);
		if(degree4 >= 90)
			return false;
		
		return true;
	}
	
	public static EMD.Point getClosestPointOnSegment(EMD.Point origin, EMD.Point destination, EMD.Point point)
	  {
	    double xDelta = destination.latitude - origin.latitude;
	    double yDelta = destination.longitude - origin.longitude;

	    if ((xDelta == 0) && (yDelta == 0))
	    {
	      throw new IllegalArgumentException("Segment start equals segment end");
	    }

	    double u = ((point.latitude - origin.latitude) * xDelta + (point.longitude - origin.longitude) * yDelta)
	    		/ (xDelta * xDelta + yDelta * yDelta);

	    EMD.Point closestPoint = new EMD.Point();
	    if (u < 0)
	    {
	      closestPoint = origin;
	    }
	    else if (u > 1)
	    {
	      closestPoint = destination;
	    }
	    else
	    {
	      closestPoint.latitude = Math.round(origin.latitude + u * xDelta);
	      closestPoint.longitude = Math.round(origin.longitude + u * yDelta);
	    }

	    return closestPoint;
	  }
	
	public static double calculateDegree(EMD.Point current, EMD.Point center, EMD.Point previous) {
		  double v1x = current.latitude - center.latitude; 
		  double v1y = current.longitude - center.longitude;

		  //need to normalize:
		  double l1 = Math.sqrt(v1x * v1x + v1y * v1y);
		  v1x /= l1;
		  v1y /= l1;

		  double v2x = previous.latitude - center.latitude;
		  double v2y = previous.longitude - center.longitude;

		  //need to normalize:
		  double l2 = Math.sqrt(v2x * v2x + v2y * v2y);
		  v2x /= l2;
		  v2y /= l2;    

		  //double rad = Math.acos( v1x * v2x + v1y * v2y );
		  double rad = Math.atan2(v2y,v2x) - Math.atan2(v1y,v1x);

		  double degres = Math.toDegrees(rad);
		  return degres;
		}
	
	public static double distance(EMD.Point p1, EMD.Point p2){
		double square = (p1.latitude - p2.latitude) * (p1.latitude - p2.latitude)
				+ (p1.longitude - p2.longitude) * (p1.longitude - p2.longitude);
		return Math.sqrt(square);
	}
	
	
	public static void main(String[] args) throws IOException {
		FileInputStream inputStreamTrajectoryList;
		originConnectedList = new ArrayList<>();
		try {
			inputStreamTrajectoryList = new FileInputStream("/media/dragon_data/uqdhe/"
	        		+ "BeijingFiveDays/mydata/connectedlistod");
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
	            originConnectedList.add(list);
	        }
	        
	        int start=382787;
	        int dest=228884;
	        
	        for(int round = 1; round < 6; round++){
	        	FileWriter file = new FileWriter("/media/dragon_data/uqdhe/"
	            		+ "BeijingFiveDays/mydata/pathlist/" + round);

	    		BufferedWriter outputWriter = new BufferedWriter(file);
	        
	    		ArrayList<ArrayList<Integer>> result = new ArrayList<>();
	    		ArrayList<Integer> path = new ArrayList<Integer>();

	    		path.add(start);
	    		getPathEnum(round, start, dest, path, result);
	    		for (ArrayList<Integer> list: result) {
	    			for (Integer i : list) {
	    				outputWriter.append(i+",");
	    			}
	    			outputWriter.newLine();
	    		}
	    		outputWriter.flush();
	    		outputWriter.close();
	        
	        }
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
}

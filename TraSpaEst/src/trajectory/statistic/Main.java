package trajectory.statistic;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	public static int FILE_NUM = 0;
	public static int ORIGIN = 0;
	public static int DESTINATION = 0;
	public static String FILE_PATH1 = "/media/dragon_data/uqdhe/BeijingFiveDays/mydata/";
	public static String FILE_PATH2 = "/media/bigdata/uqdhe/";
//	public static String FILE_PATH1 = "./";
//	public static String FILE_PATH2 = "./";
	
	public static void main(String[] args) throws IOException {
		
		FileWriter fileLog = new FileWriter(FILE_PATH2
        		+ "mylog1-all-time");

		BufferedWriter outputWriterLog = new BufferedWriter(fileLog);
		

		
		outputWriterLog.append("***** Start the procedure with the construction of trajectory list! *****\n");
		System.out.print("***** Start the procedure with the construction of trajectory list! *****\n");
        long startTime = System.currentTimeMillis();
		ConcatenateUtil.buildPointList(FILE_PATH1 + "roadsegmentmidpoint"); 
		System.out.println("pointlist size = " + ConcatenateUtil.pointList.size());
        Util.trajectoryList = new ArrayList<Trajectory>();
        //Util.buildTrajectoryList("/media/dragon_data/uqdhe/"
        //		+ "BeijingFiveDays/mydata/beijingTrajectory-newnodesequence", 2);
        Util.buildTrajectoryList(FILE_PATH1
        		+ "beijingTrajectory-newnodesequence", 2);
        
        long endTime   = System.currentTimeMillis();
        double totalTime = (double)(endTime - startTime)/1000;
        outputWriterLog.append("The time for trajectory list construction is: " + totalTime + "s! \n");
        outputWriterLog.append("***** Finish the construction of trajectory list and start the construction"
        		+ " of inverted list! *****\n"); 
        System.out.print("The time for trajectory list construction is: " + totalTime + "s! \n");
        System.out.print("***** Finish the construction of trajectory list and start the construction"
        		+ " of inverted list! *****\n"); 
        startTime = System.currentTimeMillis();
        
        Util.invertedIndex = new ArrayList<InvertedList>();
        //Util.buildInvertedIndex("/media/dragon_data/uqdhe/"
        //		+ "BeijingFiveDays/mydata/invertedindex");
        Util.buildInvertedIndex(FILE_PATH1
        		+ "invertedindex", 3);
        
        endTime   = System.currentTimeMillis();
        totalTime = (double)(endTime - startTime)/1000;
        outputWriterLog.append("The time for inverted list construction is: " + totalTime + "s! \n");
        outputWriterLog.append("***** Finish the construction of inverted list and start the construction"
        		+ " of connected list! *****\n");
        System.out.print("The time for inverted list construction is: " + totalTime + "s! \n");
        System.out.print("***** Finish the construction of inverted list and start the construction"
        		+ " of connected list! *****\n");
        startTime = System.currentTimeMillis();

        
//		int[] originset = {78970, 310172, 342987, 97639, 291095};
//		int[] destinationset = {308746, 195742, 244891, 308746, 296552};
		int[] originset = {78970, 310172};
		int[] destinationset = {308746, 195742};
		int[] fileset = {128};

		for(int iteration = 0; iteration < fileset.length; iteration++){
			FILE_NUM = fileset[iteration];
			
        Util.connectedList = new ArrayList<ArrayList<Point>> ();
        Util.buildConnectedList(FILE_PATH2
      		+FILE_NUM+ "/connectedlist-new-" +FILE_NUM);
        ConcatenateUtil.originConnectedList = Util.connectedList;
        
        endTime   = System.currentTimeMillis();
        totalTime = (double)(endTime - startTime)/1000;
        outputWriterLog.append("The time for connected list construction is: " + totalTime + "s! \n");
        outputWriterLog.append("***** Finish the construction of connected list and start the processing! *****\n");
        outputWriterLog.append("***********************************************************\n");
        System.out.print("The time for connected list construction is: " + totalTime + "s! \n");
        System.out.print("***** Finish the construction of connected list and start the processing! *****\n");
        System.out.print("***********************************************************\n");
        startTime = System.currentTimeMillis();
        
        

			
			for(int pair = 0; pair < originset.length; pair++){
				ORIGIN = originset[pair];
				DESTINATION = destinationset[pair];
				
		//int k = 1;
		int origin = ORIGIN;
		int destination = DESTINATION;
		for(int k = 1; k < 4; k++){
			FileWriter file = new FileWriter(FILE_PATH2
	        		+ FILE_NUM + "/output-all-time-" + pair +"-"+ k);

		BufferedWriter outputWriter = new BufferedWriter(file);
		ArrayList<Integer> pathList = new ArrayList<Integer>();
		pathList.add(origin);
		ArrayList<ArrayList<Integer>> targetList = new ArrayList<ArrayList<Integer>>();


		startTime = System.currentTimeMillis();
		
		ConcatenateUtil.getPathEnumOld(k, origin, destination, pathList, targetList);
		
		double ODdist = ConcatenateUtil.distance(ConcatenateUtil.pointList.get(origin), 
				ConcatenateUtil.pointList.get(destination));
		for(int i = 0; i < targetList.size(); i++){
			
			if(i > 1000)
				break;
			
			double ADL = 0;
			int concatenate = targetList.get(i).size();
			int[] path = new int[concatenate];
			int[] intersection = new int[concatenate - 1];
			for(int j = 0; j < targetList.get(i).size(); j++){
				outputWriter.append(targetList.get(i).get(j) + ",");
				path[j] = targetList.get(i).get(j);
				
				if(j < concatenate - 1){
					ADL = ADL + ConcatenateUtil.distance(ConcatenateUtil.pointList.get(targetList.get(i).get(j)), 
						ConcatenateUtil.pointList.get(targetList.get(i).get(j + 1)));
					intersection[j] = Util.invertedIndex.get(targetList.get(i).get(j)).
							check_intersectionTrajectory(Util.invertedIndex.get(targetList.get(i).get(j + 1)));
				}
			}
//			outputWriter.append("\n");
//			}

//			double[] EMD = new double[2];
//			
//			EMD = EMDCalculation.EMDCalculate(origin, destination, path);
//			
//			outputWriter.append(EMD[0] + ",");
//			outputWriter.append(EMD[1] + ",");
//			outputWriter.append(ODdist + ",");
//			outputWriter.append(ADL + "");
//			
//			for(int j = 0; j < intersection.length; j++){
//				outputWriter.append(",");
//				outputWriter.append(intersection[j] +"");
//			}
			
			
			outputWriter.append("\n");
		}
//***************************************************popularity*****************************************		
//		int concatenate = targetList.get(0).size();
//		int[] path = new int[concatenate];
//		long maxPop = 0;
//		int index = 0;
//		for(int i = 0; i < targetList.size(); i++){
//		long Pop = 1;
//
//		int intersection = 1;
//		for(int j = 0; j < targetList.get(i).size(); j++){
//			
//			if(j < concatenate - 1){
//				intersection = Util.invertedIndex.get(targetList.get(i).get(j)).
//						check_intersectionTrajectory(Util.invertedIndex.get(targetList.get(i).get(j + 1)));
//				Pop = Pop * intersection;
//			}
//
//		}
//		if(Pop > maxPop){
//			maxPop = Pop;
//			index = i;
//		}
//		
//		}
//		for(int i = 0; i < concatenate; i++){
//			outputWriter.append(targetList.get(index).get(i) + ",");
//			path[i] = targetList.get(index).get(i);
//		}
//		double[] EMD = new double[2];
//		
//		EMD = EMDCalculation.EMDCalculate(origin, destination, path);
//		
//		outputWriter.append(EMD[0] + ",");
//		outputWriter.append(EMD[1] + ",");
//		outputWriter.append("\n");
		
//***************************************************popularity*****************************************		
		endTime   = System.currentTimeMillis();
        totalTime = (double)(endTime - startTime)/1000;
        outputWriterLog.append("The time for find joint point is: " + totalTime + "s! \n");
        System.out.print("The time for find joint point is: " + totalTime + "s! \n");
		outputWriter.append("*****\n");
		pathList.clear();
		targetList.clear();
		outputWriter.flush();
//		ExpCompare.findJointPointByUnion(pathList, targetList, Util.trajectoryList, Util.invertedIndex, k, origin, destination);
//		
//		endTime   = System.currentTimeMillis();
//        totalTime = (double)(endTime - startTime)/1000;
//        outputWriterLog.append("The time for find joint point by union is: " + totalTime + "s! \n");
//        System.out.print("The time for find joint point by union is: " + totalTime + "s! \n");
//
//		for(int i = 0; i < targetList.size(); i++){
//			
//			for(int j = 0; j < targetList.get(i).size(); j++)
//				outputWriter.append(targetList.get(i).get(j) + "\t");
//			
//			outputWriter.append("\n");
//		}
//		System.out.println("the number of result = " + targetList.size());
//		outputWriter.append("*****\n");
//		startTime = System.currentTimeMillis();
//		
//		pathList.clear();
//		targetList.clear();
//		outputWriter.flush();
        
        //System.out.println("The number of elements in the connected list = " + Util.connectedList.get(38).size());
//        ExpCompare.findJointPointByIntersect(pathList, targetList, Util.invertedIndex, k, origin, destination);
//		endTime   = System.currentTimeMillis();
//        totalTime = (double)(endTime - startTime)/1000;
//        outputWriterLog.append("The time for find joint point by intersect is: " + totalTime + "s! \n");
//        System.out.print("The time for find joint point by intersect is: " + totalTime + "s! \n");
//		
//
//
//		for(int i = 0; i < targetList.size(); i++){
//			
//			for(int j = 0; j < targetList.get(i).size(); j++)
//				outputWriter.append(targetList.get(i).get(j) + "\t");
//			
//			outputWriter.append("\n");
//		}
		
		outputWriter.flush();
		outputWriter.close();
		
		System.out.println("Finish the process of k = " + k);
		outputWriterLog.append("Finish the process of k = " + k +"\n");
		outputWriterLog.flush();
		
		}
		}
			ConcatenateUtil.originConnectedList.clear();
		}
		
		outputWriterLog.flush();
		outputWriterLog.close();
	    System.out.println("This is the end of the program!\n");
        
        
	}

}

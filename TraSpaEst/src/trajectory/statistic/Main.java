package trajectory.statistic;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		System.out.print("***** Start the procedure with the construction of trajectory list! *****\n");
        long startTime = System.currentTimeMillis();
            
        Util.trajectoryList = new ArrayList<Trajectory>();
        Util.buildTrajectoryList("/media/dragon_data/uqdhe/"
        		+ "BeijingFiveDays/mydata/beijingTrajectory-newnodesequence", 2);
        
        long endTime   = System.currentTimeMillis();
        long totalTime = (endTime - startTime)/1000;
        System.out.println("The time for trajectory list construction is: " + totalTime + "s! \n");
        System.out.print("***** Finish the construction of trajectory list and start the construction"
        		+ " of inverted list! *****\n"); 
        
        Util.invertedIndex = new ArrayList<InvertedList>();
        Util.buildInvertedIndex("/media/dragon_data/uqdhe/"
        		+ "BeijingFiveDays/mydata/invertedindex");
        
        endTime   = System.currentTimeMillis();
        totalTime = (endTime - startTime)/1000;
        System.out.println("The time for inverted list construction is: " + totalTime + "s! \n");
        System.out.print("***** Finish the construction of inverted list and start the construction"
        		+ " of connected list! *****\n");
        
        Util.connectedList = new ArrayList<ArrayList<Integer>> ();
        Util.buildConnectedList("/media/dragon_data/uqdhe/"
        		+ "BeijingFiveDays/mydata/connectedlistod");
        
        endTime   = System.currentTimeMillis();
        totalTime = (endTime - startTime)/1000;
        System.out.println("The time for connected list construction is: " + totalTime + "s! \n");
        System.out.print("***** Finish the construction of connected list and start the sub-trajectory"
        		+ " extraction! *****\n");
        
		
		FileInputStream inputStreamConnectedList = new FileInputStream("/media/dragon_data/uqdhe/"
        		+ "BeijingFiveDays/mydata/tmp.txt");

		
        @SuppressWarnings("resource")
		Scanner scConnectedList = new Scanner(inputStreamConnectedList, "UTF-8");
        String lineConnectedList = "";
        String SplitBy = "\t";
        int lineIndex = 0;
        
        while (scConnectedList.hasNextLine()) {
        	
        	System.out.println("this is the " + lineIndex + " line!");
        	
    		FileWriter file = new FileWriter("/media/dragon_data/uqdhe/"
            		+ "BeijingFiveDays/mydata/output-time/" + lineIndex);

    		BufferedWriter outputWriter = new BufferedWriter(file);
        	
        	
        	if(lineIndex % 10 == 0)
        		System.out.println("lineIndex = " + lineIndex);
        	
        	 //scan a line 
            lineConnectedList = scConnectedList.nextLine();
            
            //System.out.println(lineConnectedList);
            
            // split a line into items, store into an array 
            String[] lineData = lineConnectedList.split(SplitBy);
		
		
	        int originPointId = Integer.parseInt(lineData[0]);
	        int destinationPointId = Integer.parseInt(lineData[1]);        
        
	        double distance = 0.0;
	        
	        distance = Util.calculateClipHist(originPointId, destinationPointId, "timePeriod");
	        
	        outputWriter.append("the point pairs : " + lineData[0] + "," + lineData[1] + "\n");
	        outputWriter.append("the test od and clipping distance = " + distance + "\n");
	        
	        for(int i = 1; i < 6; i++){
	        
	        	distance = Util.calculateConcatenateHist(originPointId, destinationPointId, i, "timePeriod");
	        
	        	outputWriter.append("the test od and concatenation distance = " + distance + " when k = "
	        			+ i + "\n");
	        }
	        outputWriter.flush();
	        outputWriter.close();
	        lineIndex++;
        }
        
       
      
/*       int originPointId = 288097;
        int destinationPointId = 336995; 
		// get the sub-trajectories from the origin to the destination 
		ArrayList<Trajectory> subTrajectorySet = new ArrayList<Trajectory>();
		Util.get_ClipSet(subTrajectorySet, originPointId, destinationPointId);
		
		FileWriter file0 = new FileWriter("/media/dragon_data/uqdhe/"
        		+ "BeijingFiveDays/mydata/trajectorylist2/0/1");

		BufferedWriter outputWriter0 = new BufferedWriter(file0);
		
    	for(Trajectory elemtra : subTrajectorySet){
    		for(Point point: elemtra.get_pointArray()){
    			outputWriter0.append(point.get_coordinate(0) + ",");
    		}
    		outputWriter0.newLine();
    	}
    	outputWriter0.flush();
    	outputWriter0.close();
		
		for(int round = 1; round < 6; round++){
			
			// get the concatenation point list 
			ArrayList<ArrayList<Integer>> concatenationPointList = new ArrayList<>();
	        ArrayList<Integer> path = new ArrayList<Integer>();
	        // add the first point to the path 
	        path.add(originPointId);
	        Util.getPathEnum(round, originPointId, destinationPointId, path, concatenationPointList);
	        
	        // get the number of concatenation points
	        long numConPoint = concatenationPointList.size();
	        
	        ArrayList<ArrayList<Integer>> concatenationPointListNew = new ArrayList<>();
	        ArrayList<ArrayList<Integer>> concatenationPointListFinal = new ArrayList<>();
	        
	        if(numConPoint > 1000){
	        	Util.randomPath(1000, concatenationPointList, concatenationPointListNew);
	        	concatenationPointListFinal = concatenationPointListNew;
	        }
	        else
	        	concatenationPointListFinal = concatenationPointList;
	        	
	        
	        System.out.println("the number of paths = " + numConPoint);
	        System.out.println("the number of new paths = " + concatenationPointListNew.size());
	        
	        
	        long numConPointFinal = concatenationPointListFinal.size();
	        
	
	        for(int index = 0; index < numConPointFinal; index++){
	    		FileWriter file = new FileWriter("/media/dragon_data/uqdhe/"
	            		+ "BeijingFiveDays/mydata/trajectorylist2/" + round +"/"+(index+1));

	    		BufferedWriter outputWriter = new BufferedWriter(file);
	        	// get the concatenation trajectories 
	        	ArrayList<Trajectory> concatenationTrajectory = new ArrayList<Trajectory>();
	        	Util.getConcatenationTrajectory(concatenationTrajectory, 
	        			concatenationPointListFinal.get(index));
	        	
	        	for(Trajectory elemtra : concatenationTrajectory){
	        		for(Point point: elemtra.get_pointArray()){
	        			outputWriter.append(point.get_coordinate(0) + ",");
	        		}
	        		outputWriter.newLine();
	        	}
	        	outputWriter.flush();
	        	outputWriter.close();
	
	        }
		}*/
		
			        
	    System.out.println("This is the end of the program!\n");
        
        
	}

}

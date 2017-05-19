package trajectory.statistic;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException {
		
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
        		+ "BeijingFiveDays/mydata/connectedlist");
        
        endTime   = System.currentTimeMillis();
        totalTime = (endTime - startTime)/1000;
        System.out.println("The time for connected list construction is: " + totalTime + "s! \n");
        System.out.print("***** Finish the construction of connected list and start the sub-trajectory"
        		+ " extraction! *****\n");
        
        int originPointId = 258144;
        int destinationPointId = 255392;        
        
        double distance = 0.0;
        
        distance = Util.calculateClipHist(originPointId, destinationPointId, "timePeriod");
  
        System.out.println("the test od and clipping distance = " + distance + "\n");
        
        distance = Util.calculateConcatenateHist(originPointId, destinationPointId, 1, "timePeriod");
        
        System.out.println("the test od and concatenation distance = " + distance + "\n");

	}

}

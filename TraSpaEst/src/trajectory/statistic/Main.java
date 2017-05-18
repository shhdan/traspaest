package trajectory.statistic;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException {
		
		System.out.print("***** Start the procedure with the construction of trajectory list! *****\n");
        long startTime = System.currentTimeMillis();
            
        ArrayList<Trajectory> trajectoryList = new ArrayList<Trajectory>();
        Util.buildTrajectoryList(trajectoryList, "/media/dragon_data/uqdhe/"
        		+ "BeijingFiveDays/mydata/beijingTrajectory-newnodesequence", 2);
        
        long endTime   = System.currentTimeMillis();
        long totalTime = (endTime - startTime)/1000;
        System.out.println("The time for trajectory list construction is: " + totalTime + "s! \n");
        System.out.print("***** Finish the construction of trajectory list and start the construction"
        		+ " of inverted list! *****\n"); 
        
        ArrayList<InvertedList> invertedIndex = new ArrayList<InvertedList>();
        Util.buildInvertedIndex(invertedIndex, "/media/dragon_data/uqdhe/"
        		+ "BeijingFiveDays/mydata/invertedindex");
        
        endTime   = System.currentTimeMillis();
        totalTime = (endTime - startTime)/1000;
        System.out.println("The time for inverted list construction is: " + totalTime + "s! \n");
        System.out.print("***** Finish the construction of inverted list and start the sub-trajectory"
        		+ " extraction! *****\n");
        
        int originPointId = 258144;
        int destinationPointId = 255392;
        
        ArrayList<Trajectory> trajectoryListOD = new ArrayList<Trajectory>();
        Util.get_OriginDestinationSet(trajectoryListOD, originPointId, destinationPointId, 
        				trajectoryList, invertedIndex);
        
        System.out.println("The number of OD trajectories is : " + trajectoryListOD.size());
        
        ArrayList<Trajectory> trajectoryListClip = new ArrayList<Trajectory>();
        Util.get_ClipSet(trajectoryListClip, originPointId, destinationPointId, 
        				trajectoryList, invertedIndex);
        
        System.out.println("The number of Clipping trajectories is : " + trajectoryListClip.size());
        
        double distance = 0.0;
        distance = Util.featureStatistics(trajectoryListOD, trajectoryListClip, "timePeriod");
        
        System.out.println("the test distance = " + distance + "\n");

	}

}

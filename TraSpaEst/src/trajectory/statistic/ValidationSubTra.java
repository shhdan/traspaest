package trajectory.statistic;

import java.util.ArrayList;

public class ValidationSubTra {

	public static double calculateSubTraPercentage(ArrayList<ArrayList<Integer>> targetList, 
			int origin, int destination){
		
		double AVGPercentage = 0;
		
		ArrayList<Trajectory> ODTrajectory = new ArrayList<Trajectory>();
		
		Util.get_SubTrajectorySet(ODTrajectory, origin, destination);
		
		for(int h = 0; h < targetList.size(); h++){
			double percentage = 0;
			ArrayList<Integer> list = targetList.get(h);
			int concatenationNum = list.size();
			for(int i = 0; i < concatenationNum - 1; i++){
				double subPercentage = 1;
				ArrayList<Trajectory> concatenationTrajectory = new ArrayList<Trajectory>();
				Util.get_SubTrajectorySet(concatenationTrajectory, list.get(i), list.get(i + 1));
				double sumTrajNum = concatenationTrajectory.size();
				double subTrajNum = 0;
				for(int j = 0; j < sumTrajNum; j++){
					Trajectory subTrajectory = concatenationTrajectory.get(j);
					for(int k = 0; k < ODTrajectory.size(); k++){
						Trajectory odTrajectory = ODTrajectory.get(j);
						if(isSubTra(odTrajectory, subTrajectory)){
							subTrajNum++;
							break;
						}
					}
				}
				subPercentage = subTrajNum/sumTrajNum;
				percentage = percentage * subPercentage;
			}
			AVGPercentage = AVGPercentage + percentage;
		}
		AVGPercentage = AVGPercentage / targetList.size();
		return AVGPercentage;
	}
	
	public static double calculateSubTraPercentageSingle(ArrayList<Integer> targetList, int origin, 
			int destination){
		double percentage = 1;
		
		ArrayList<Trajectory> ODTrajectory = new ArrayList<Trajectory>();
		
		Util.get_SubTrajectorySet(ODTrajectory, origin, destination);
		
		int concatenationNum = targetList.size();
		for(int i = 0; i < concatenationNum - 1; i++){
			double subPercentage = 1;
			ArrayList<Trajectory> concatenationTrajectory = new ArrayList<Trajectory>();
			Util.get_SubTrajectorySet(concatenationTrajectory, targetList.get(i), targetList.get(i + 1));
			double sumTrajNum = concatenationTrajectory.size();
			double subTrajNum = 0;
			for(int j = 0; j < sumTrajNum; j++){
				Trajectory subTrajectory = concatenationTrajectory.get(j);
				for(int k = 0; k < ODTrajectory.size(); k++){
					Trajectory odTrajectory = ODTrajectory.get(j);
					if(isSubTra(odTrajectory, subTrajectory)){
						subTrajNum++;
						break;
					}
				}
			}
			subPercentage = subTrajNum/sumTrajNum;
			percentage = percentage * subPercentage;
		}
		
		return percentage;
	}
	
	public static boolean isSubTra(Trajectory originTrajectory, Trajectory queryTrajectory){
		if(originTrajectory == null || queryTrajectory == null)
			return false;
		
		int originSize = originTrajectory.get_pointArray().size();
		int querySize = queryTrajectory.get_pointArray().size();
		
		if(querySize > originSize)
			return false;
		
		for(int i = 0; i < originSize - querySize + 1; i++){
			ArrayList<Point> subTrajectory = originTrajectory.get_subTrajectory(i, i + querySize - 1);
			if(equalsTra(subTrajectory, queryTrajectory.get_pointArray()))
				return true;
		}
		
		return false;
	}
	
	public static boolean equalsTra(ArrayList<Point> trajA, ArrayList<Point> trajB){
		if(trajA == null || trajB == null)
			return false;
		
		int trajASize = trajA.size();
		int trajBSize = trajB.size();
		
		if(trajASize != trajBSize)
			return false;
		
		for(int i = 0; i < trajASize ; i++){
			if(trajA.get(i).get_coordinate(0) != trajB.get(i).get_coordinate(0))
				return false;
		}
		return true;
	}
}

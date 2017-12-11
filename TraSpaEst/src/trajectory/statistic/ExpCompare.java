package trajectory.statistic;

import java.util.ArrayList;
import java.util.HashSet;

public class ExpCompare {
	
	public static int NUM_OF_TRAJECTORY = 16;
	
	public static void findJointPointByIntersect(ArrayList<Integer> pathList, ArrayList<ArrayList<Integer>> targetList, 
			ArrayList<InvertedList> invertedIndex, int k, int originPoint, int destinationPoint){
		 
		ArrayList<Integer> candidateList = new ArrayList<Integer>();
		 
		 for(int index = 0; index < invertedIndex.size(); index++){
			 if(index == originPoint)
				 continue;
			 else if(invertedIndex.get(originPoint).check_intersectionTrajectory(invertedIndex.get(index)) >= NUM_OF_TRAJECTORY){
				 candidateList.add(index);
			 }
		 }
		 
		 if(k == 0){
/*			for(int i = 0; i < candidateList.size(); i++){
				 
				int curPoint = candidateList.get(i);
				if(invertedIndex.get(curPoint).check_intersectionTrajectory(invertedIndex.get(destinationPoint)) &&
						 !pathList.contains(curPoint)){
					 pathList.add(curPoint);
					 pathList.add(destinationPoint);
				}

				*/
			if(candidateList.contains(destinationPoint)){
				pathList.add(destinationPoint);
				HashSet<Integer> pathSet = new HashSet<Integer>();
				for(Integer elemSet : pathList){
					pathSet.add(elemSet);
				}
				
				if(pathSet.size() == pathList.size()){
					if(ConcatenateUtil.accumulativeSpatialLength(pathList.get(0), destinationPoint, 
							pathList, ConcatenateUtil.DIST_RATIO) && 
								ConcatenateUtil.direction(pathList.get(0), destinationPoint, pathList))
					targetList.add(pathList);
				}
			}
			
			return;
		 }
		 
		 
			 for(int i = 0; i < candidateList.size(); i++){
				 ArrayList<Integer> newPath = new ArrayList<Integer>(pathList);
				 newPath.add(candidateList.get(i));
				 
				 HashSet<Integer> pathSet = new HashSet<Integer>();
				 for(Integer elem : newPath){
					 pathSet.add(elem);
				 }
				 
				 if(pathSet.size() == newPath.size()){
					 if(ConcatenateUtil.accumulativeSpatialLength(newPath.get(0), destinationPoint, 
							 newPath, ConcatenateUtil.DIST_RATIO) && 
								ConcatenateUtil.direction(newPath.get(0), destinationPoint, newPath))
					 findJointPointByIntersect(newPath, targetList, invertedIndex, k-1, candidateList.get(i), destinationPoint);
				 }
			 }
		 
	}
	
	public static void findJointPointByUnion(ArrayList<Integer> pathList, ArrayList<ArrayList<Integer>> targetList, 
			ArrayList<Trajectory> trajectoryList, ArrayList<InvertedList> invertedIndex, 
			int k, int originPoint, int destinationPoint){
		 
		ArrayList<Integer> candidateList = new ArrayList<Integer>();
		
		InvertedList curInvertedList = new InvertedList();
		curInvertedList = invertedIndex.get(originPoint);
		
		int firstTraj = (int)curInvertedList.get_trajectoryArray().get(0).coordinate[0];
		int pos = (int)curInvertedList.get_trajectoryArray().get(0).coordinate[1];
		getSucceedPoint(candidateList, trajectoryList.get(firstTraj),pos);
		
		//get the union of points that can be reachable from the origin point.
		for(int index = 1; index < curInvertedList.get_trajectoryArray().size(); index = index + 1){
			 
			 ArrayList<Integer> temp1 = new ArrayList<Integer>();
			 ArrayList<Integer> temp2 = new ArrayList<Integer>();
			 int traj1 = (int)curInvertedList.get_trajectoryArray().get(index).coordinate[0];
			 int pos1 = (int)curInvertedList.get_trajectoryArray().get(index).coordinate[1];
//			 int traj2 = (int)curInvertedList.get_trajectoryArray().get(index + 1).coordinate[0];
//			 int pos2 = (int)curInvertedList.get_trajectoryArray().get(index + 1).coordinate[1];

			 getSucceedPoint(temp1, trajectoryList.get(traj1),pos1);
//			 getSucceedPoint(temp2, trajectoryList.get(traj2), pos2);
			 temp2.addAll(candidateList);
//			 System.out.println("index = " + index);
//			 for(int i = 0; i < temp1.size(); i++)
//				 System.out.print(temp1.get(i) + ",");
//			 System.out.print("\n");
//			 ArrayList<Integer> temp3 = new ArrayList<Integer>();
//			 unionPoint(temp2, temp1, candidateList);
//			 ArrayList<Integer> temp4 = new ArrayList<Integer>();
//			 temp4.addAll(candidateList);
			 candidateList.clear();
			 unionPoint(candidateList, temp1, temp2);
//			 unionPoint(candidateList, temp3, temp4);
		}
		
//		System.out.println("the number of elements in the candidate list = " + candidateList.size());
		 
		if(k == 0){
			if(candidateList.contains(destinationPoint)){
				//pathList.add(originPoint);
				pathList.add(destinationPoint);
				HashSet<Integer> pathSet = new HashSet<Integer>();
				for(Integer elemSet : pathList){
					pathSet.add(elemSet);
				}
				
				if(pathSet.size() == pathList.size())
					targetList.add(pathList);
			}
			return;
		 }
		 
			 for(int i = 0; i < candidateList.size(); i++){
				 ArrayList<Integer> newPath = new ArrayList<Integer>(pathList);
				 newPath.add(candidateList.get(i));
				 
				 HashSet<Integer> pathSet = new HashSet<Integer>();
				 for(Integer elem : newPath){
					 pathSet.add(elem);
				 }
				 
				 if(pathSet.size() == newPath.size()){
					 findJointPointByUnion(newPath, targetList, trajectoryList, invertedIndex, k-1, 
							 candidateList.get(i), destinationPoint);
				 }
			 }
		 
	}
	
	/*Get the union of two lists.*/
	public static void unionPoint(ArrayList<Integer> targetList, ArrayList<Integer> oneList, ArrayList<Integer> twoList){
		HashSet<Integer> unionSet = new HashSet<Integer>();
		
		for(Integer elem : oneList){
			unionSet.add(elem);
		}
		
		for(Integer elem : twoList){
			unionSet.add(elem);
		}
		
		for(Integer elem : unionSet){
			targetList.add(elem);
		}
	}
	
	/*Get the intersection of two list.*/
	public static void intersectPoint(ArrayList<Integer> targetList, ArrayList<Integer> oneList, ArrayList<Integer> twoList){
		
		HashSet<Integer> unionSet = new HashSet<Integer>();
		
		for(Integer elem : oneList){
			unionSet.add(elem);
		}
		
		for(Integer elem : twoList){
			if(unionSet.contains(elem) && !targetList.contains(elem))
				targetList.add(elem);
		}
		
	}
	
	/*Get the succeeding points from a trajectory.*/
	public static void getSucceedPoint(ArrayList<Integer> targetList, Trajectory traj, int position){
		if(position >= traj.get_pointArray().size() - 1){
			//System.out.println("Warning: The given position is larger than the size of given trajectory or it is the last element of the trajectory!");
			//System.out.println("position = " + position + " trajectory.size = " + traj.get_pointArray().size()
//					+ " trajid = " + traj.get_trajectoryId());
			return;
		}
		
		int size = traj.get_pointArray().size();
		
		for(int i = position + 1; i < size; i++){
			targetList.add((int)traj.get_pointArray().get(i).get_coordinate(0));
		}
	}

}

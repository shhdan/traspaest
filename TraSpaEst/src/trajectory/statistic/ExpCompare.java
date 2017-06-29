package trajectory.statistic;

import java.util.ArrayList;
import java.util.HashSet;

public class ExpCompare {
	
	public void findJointPointByIntersect(ArrayList<Integer> pathList, ArrayList<ArrayList<Integer>> targetList, 
			ArrayList<InvertedList> invertedIndex, int k, int originPoint, int destinationPoint){
		 
		ArrayList<Integer> candidateList = new ArrayList<Integer>();
		 
		 for(int index = 0; index < invertedIndex.size(); index++){
			 if(index == originPoint)
				 continue;
			 else if(invertedIndex.get(originPoint).check_intersectionTrajectory(invertedIndex.get(index))){
				 candidateList.add(index);
			 }
		 }
		 
		 if(k == 1){
			for(int i = 0; i < candidateList.size(); i++){
				 
				int curPoint = candidateList.get(i);
				if(invertedIndex.get(curPoint).check_intersectionTrajectory(invertedIndex.get(destinationPoint)) &&
						 !pathList.contains(curPoint)){
					 pathList.add(curPoint);
					 pathList.add(destinationPoint);
				}
				HashSet<Integer> pathSet = new HashSet<Integer>();
				for(Integer elemSet : pathList){
						pathSet.add(elemSet);
				}
				
				if(pathSet.size() == pathList.size())
					targetList.add(pathList);
			 }
			 return;
		 }
		 
		 else {
			 for(int i = 0; i < candidateList.size(); i++){
				 ArrayList<Integer> newPath = new ArrayList<Integer>(pathList);
				 newPath.add(candidateList.get(i));
				 
				 HashSet<Integer> pathSet = new HashSet<Integer>();
				 for(Integer elem : newPath){
					 pathSet.add(elem);
				 }
				 
				 if(pathSet.size() == newPath.size()){
					 findJointPointByIntersect(newPath, targetList, invertedIndex, k-1, candidateList.get(i), destinationPoint);
				 }
			 }
		 }
	}
	
	public void findJointPointByUnion(ArrayList<Integer> pathList, ArrayList<ArrayList<Integer>> targetList, 
			ArrayList<Trajectory> trajectoryList, ArrayList<InvertedList> invertedIndex, 
			int k, int originPoint, int destinationPoint){
		 
		ArrayList<Integer> candidateList = new ArrayList<Integer>();
		
		InvertedList curInvertedList = new InvertedList();
		curInvertedList = invertedIndex.get(originPoint);
		
		//get the union of points that can be reachable from the origin point.
		for(int index = 0; index < curInvertedList.get_trajectoryArray().size() - 1; index = index + 2){
			 
			 ArrayList<Integer> temp1 = new ArrayList<Integer>();
			 ArrayList<Integer> temp2 = new ArrayList<Integer>();
			 int traj1 = (int)curInvertedList.get_trajectoryArray().get(index).coordinate[0];
			 int pos1 = (int)curInvertedList.get_trajectoryArray().get(index).coordinate[1];
			 int traj2 = (int)curInvertedList.get_trajectoryArray().get(index + 1).coordinate[0];
			 int pos2 = (int)curInvertedList.get_trajectoryArray().get(index + 1).coordinate[1];

			 getSucceedPoint(temp1, trajectoryList.get(traj1),pos1);
			 getSucceedPoint(temp2, trajectoryList.get(traj2), pos2);
			 
			 ArrayList<Integer> temp3 = new ArrayList<Integer>();
			 unionPoint(temp3, temp1, temp2);
			 ArrayList<Integer> temp4 = new ArrayList<Integer>();
			 temp4.addAll(candidateList);
			 candidateList.clear();
			 unionPoint(candidateList, temp3, temp4);
		}
		 
		if(k == 0){
			if(candidateList.contains(destinationPoint)){
				pathList.add(originPoint);
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
		 
		 else {
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
	}
	
	/*Get the union of two lists.*/
	public void unionPoint(ArrayList<Integer> targetList, ArrayList<Integer> oneList, ArrayList<Integer> twoList){
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
	public void intersectPoint(ArrayList<Integer> targetList, ArrayList<Integer> oneList, ArrayList<Integer> twoList){
		
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
	public void getSucceedPoint(ArrayList<Integer> targetList, Trajectory traj, int position){
		if(position >= traj.get_pointArray().size()){
			System.out.println("Error: The given position is larger than the size of given trajectory!");
			return;
		}
		
		int size = traj.get_pointArray().size();
		for(int i = position; i < size; i++){
			targetList.add(traj.get_pointArray().get(i).id);
		}
	}

}

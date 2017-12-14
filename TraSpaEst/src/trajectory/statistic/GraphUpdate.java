package trajectory.statistic;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class GraphUpdate {
	
	// this is the update transition graph, with some of the edges deleted according to the input OD pair.
	public static ArrayList<ArrayList<Point>> temporaryConnectedList = new ArrayList<ArrayList<Point>>();
	public static ArrayList<ArrayList<Point>> originConnectedList = new ArrayList<ArrayList<Point>>();
	public static ArrayList<ArrayList<Point>> invertedConnectedList  = new ArrayList<ArrayList<Point>>();
//	public static int START = 0;
//	public static int END = 2000;
	public static int FILE = 1;
	public static int LAMDA = 4;
	
	static void initList(String fileName, ArrayList<ArrayList<Point>> list) throws FileNotFoundException{
		
		FileInputStream inputStreamConnectedList = new FileInputStream(fileName);
		
        @SuppressWarnings("resource")
		Scanner scConnectedList = new Scanner(inputStreamConnectedList, "UTF-8");
        String lineConnectedList = "";
        String SplitBy = ",";
        
        //debug
        int check = 0;
        //debug
        
        while (scConnectedList.hasNextLine()) {
        	
        	if(check % 10000 == 0){
        		System.out.println("finish the line " + check);
        	}
        	
        	/* scan a line */
            lineConnectedList = scConnectedList.nextLine();
            
            /* split a line into items, store into an array */
            String[] lineData = lineConnectedList.split(SplitBy);
            
            /* initialize the element list, each list contains a sequence of points*/
            ArrayList<Point> elemList = new ArrayList<Point>();
            
            for(int i = 1; i < lineData.length; i = i + 1){
            	Point item =  new Point();
            	item.set_coordinate(0, Integer.parseInt(lineData[i]));
            	item.set_coordinate(1, Integer.parseInt(lineData[i + 1]));
            	elemList.add(item);
            	//elemList.add(Integer.parseInt(lineData[i]));
            }
            
            list.add(elemList);
            
            //debug
            check++;
            //debug
        }
	}
	
	public static int binarySearch(ArrayList<Point> pointlist, int point){
		
		//debug
		//System.out.println("Here comes to binary search!");
		//debug
		
		int high = pointlist.size() - 1;
		int low = 0;
		
		while(low <= high){
			int mid = (high + low)/2;
			
			if(point < pointlist.get(mid).get_coordinate(0))
				high = mid - 1;
			else if(point > pointlist.get(mid).get_coordinate(0))
				low = mid + 1;
			else {
				 return (int) pointlist.get(mid).coordinate[1];
			}
		}
		
		//debug
		//System.out.println("Here finish to binary search!");
		//debug
		
		return -1;
		

	}
	
	public static int binarySearchPosition(ArrayList<Point> pointlist, int point){
		
		//debug
		//System.out.println("Here comes to binary search position!");
		//debug
		
		int high = pointlist.size() - 1;
		int low = 0;
		//int position = -1;
		while(low <= high){
			int mid =(high + low)/2; 
			
			if(point < pointlist.get(mid).get_coordinate(0))
				high = mid - 1;
			else if(point > pointlist.get(mid).get_coordinate(0))
				low = mid + 1;
			else {
				return mid;
			}
		}
		return -1;
	}
	
	
	/* eliminate the redundant edges in the transition graph, 
	 * which will update the connected list and the inverted connected list*/
	static void graphReduction(){
		for(int i = 0; i < originConnectedList.size(); i++){
			
			//debug
			if(i % 1000 == 0)
				System.out.println("now finish the processing of line " + i);
			//debug
			
//			if(i < START)
//				continue;
//			if(i >= END)
//				break;
			
			int pCurrent = i;
//			ArrayList<Point> pCurrentTraList = Util.invertedIndex.get(pCurrent).get_trajectoryArray();
			ArrayList<Point> outNeighborList = originConnectedList.get(pCurrent);
			ArrayList<Point> inNeighborList = invertedConnectedList.get(pCurrent);
			
			for(int j = 0; j < outNeighborList.size(); j++){
				
				//debug
				//System.out.println("j = " + j);
				//System.out.println("in neighbor list size = " + inNeighborList.size());
				//debug
				
				int pOut = (int) outNeighborList.get(j).get_coordinate(0);
				int numTraOut = (int) outNeighborList.get(j).get_coordinate(1);
				
				for(int k = 0; k < inNeighborList.size(); k++){
					
					//debug
					//System.out.println("k = " + k);
					//debug
					
					int numTraIn = (int) inNeighborList.get(k).get_coordinate(1);
					
					if(numTraIn == numTraOut){
						
						int pIn = (int) inNeighborList.get(k).get_coordinate(0);
						
						//debug
						//System.out.println("pCurrent = " + pCurrent);
						//System.out.println("pIn = " + pIn);
						//System.out.println("pOut = " + pOut);
						//System.out.println("numTraIn = " + numTraIn);
						//debug
						
						ArrayList<Point> inNeighborOutList = originConnectedList.get(pIn);
						//Point searchPoint = new Point();
						//binarySearch(inNeighborOutList, pOut, searchPoint);
						//System.out.println("search point id = " + searchPoint.coordinate[0]);
						int numTraSearch = binarySearch(inNeighborOutList, pOut);
						if(numTraSearch == numTraIn){
							
							//debug
							//System.out.println("here comes to the second step");
							//System.out.println("search point id = " + searchPoint.coordinate[0]);
							//debug
							
//							ArrayList<Point> inTraList = new ArrayList<Point>();
//							ArrayList<Point> pInTraList = Util.invertedIndex.get(pIn).get_trajectoryArray();
//							ArrayList<Point> outTraList = new ArrayList<Point>();
//							ArrayList<Point> pOutTraList = Util.invertedIndex.get(pOut).get_trajectoryArray();
//							Util.intersectionTrajectory(inTraList, pInTraList, pCurrentTraList);
//							Util.intersectionTrajectory(outTraList, pCurrentTraList, pOutTraList);
//							int l = 0;
//							for(l = 0; l < numTraIn; l++){
//								if(inTraList.get(l).coordinate[0] != outTraList.get(l).coordinate[0]){
//									
//									//debug
//									//System.out.println("This point is insufficient!");
//									//debug
//									
//									l = -1;
//									break;
//								}
//							}
//							if(l != -1){
								
								//debug
								//System.out.println("This point is sufficient!");
								//debug
								
								int inOutPosition = binarySearchPosition(originConnectedList.get(pIn), pCurrent);
								originConnectedList.get(pIn).remove(inOutPosition);
								invertedConnectedList.get(pCurrent).remove(k);
								k--;
							//}
						}
					}
				}
				
			}
		}
	}
	
	// update the temporal connected list by eliminate the edges from OD trajectory
	public static void graphUpdateByODTrajectory(int origin, int destination){
		// reset the temporary connected list
		temporaryConnectedList.clear();
		Collections.copy(temporaryConnectedList, originConnectedList);
		
		// get the OD trajectories
		ArrayList<Trajectory> subTrajectoryList = new ArrayList<Trajectory>();
		Util.get_SubTrajectorySet(subTrajectoryList, origin, destination);
		
		// for each trajectory, delete the edge from the connected list
		for(int i = 0; i < subTrajectoryList.size(); i++){
			// get the trajectory
			Trajectory traj = subTrajectoryList.get(i);
			for(int j = 0; j < traj.get_pointArray().size(); j++){
				// get the current point from the trajectory, 
				// delete the edge connecting all the succeeding points from the trajectory
				int start = traj.get_pointArray().get(j).get_coordinate(0);
				for(int k = j + 1; k < traj.get_pointArray().size(); k++){
					int end = traj.get_pointArray().get(k).get_coordinate(0);
					int position = binarySearchPosition(temporaryConnectedList.get(start), end);
					if(position != -1){
						// get the current value of the edge sum
						int currentVal = temporaryConnectedList.get(start).get(position).get_coordinate(1);
						// if the current value of edge sum is smaller than the threshold
						// delete current connected edge from the connected list
						if(currentVal <= LAMDA + 1){
							temporaryConnectedList.get(start).remove(position);
						}
						else
							temporaryConnectedList.get(start).get(position).set_coordinate(1, currentVal - 1);
					}
				}
			}
		}		
	}
	
	public static void main(String[] args) throws IOException {
		//generateConnectedList("/home/uqdhe/workspace/TraSpaEst/odpairmatrix", "/home/uqdhe/"
        //		+ "workspace/TraSpaEst/connectedlist");
        		
        initList("/media/bigdata/uqdhe/"+FILE+"/connectedlist-"+FILE, originConnectedList);
        //initList("./connectedlist", connectedList);
        System.out.println("Finish the building of connected list!");
        initList("/media/bigdata/uqdhe/"+FILE+"/invertedconnectedlist-"+FILE, invertedConnectedList);
        //initList("./invertedconnectedlist", invertedConnectedList);
        System.out.println("Finish the building of inverted connected list!");
		
		//double averageDegree = scanConnectedList("/media/dragon_data/uqdhe/"
        //		+ "BeijingFiveDays/mydata/connectedlist");
		//System.out.println("the average degree = " + averageDegree + "\n");
		
		//scanResultList("/media/dragon_data/uqdhe/BeijingFiveDays/mydata/output-node/", 
		//		"/media/dragon_data/uqdhe/BeijingFiveDays/mydata/output-stat-node");
//        Util.buildInvertedIndex("/media/dragon_data/uqdhe/BeijingFiveDays/mydata/invertedindex", 3);
        //Util.buildInvertedIndex("./invertedindex", 3);
        //Util.buildInvertedIndex("/media/bigdata/uqdhe/invertedindex-tmp", 2);
        //System.out.println("check the inverted list: " + Util.invertedIndex.get(4).get_trajectoryArray().get(0).coordinate[0]);
        System.out.println("Finish the building of inverted list!");
        graphReduction();
        
        FileWriter file = new FileWriter("/media/bigdata/uqdhe/"+FILE+"/invertedconnectedlist-new-"+FILE);
        //FileWriter file = new FileWriter("./invertedconnectedlist-tmp");

		BufferedWriter outputWriter = new BufferedWriter(file);
        
        //for(int i = START; i < END; i++){
		for(int i = 0; i < invertedConnectedList.size(); i++){
        	outputWriter.append(i + "");
        	for(int j = 0; j < invertedConnectedList.get(i).size(); j++){
        		outputWriter.append(",");
        		outputWriter.append(invertedConnectedList.get(i).get(j).coordinate[0] + ",");
        		outputWriter.append(invertedConnectedList.get(i).get(j).coordinate[1] + "");
        	}
        	outputWriter.append("\n");
        }
        outputWriter.flush();
        outputWriter.close();
		System.out.println("This is the end of the program!");
		
		
	}

}

package trajectory.statistic;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class GraphUpdate {
	
	public static ArrayList<ArrayList<Point>> connectedList = new ArrayList<ArrayList<Point>>();
	public static ArrayList<ArrayList<Point>> invertedConnectedList  = new ArrayList<ArrayList<Point>>();
//	public static int START = 0;
//	public static int END = 2000;
	public static int FILE = 1;
	
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
            
            for(int i = 1; i < lineData.length; i = i + 2){
            	Point item =  new Point();
            	item.set_coordinate(0, Integer.parseInt(lineData[i]));
            	item.set_coordinate(1, Integer.parseInt(lineData[i + 1]));
            	elemList.add(item);
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
			int mid = low + (high - low)/2;
			
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
			int mid = low + (high - low)/2; 
			
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
		for(int i = 0; i < connectedList.size(); i++){
			
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
			ArrayList<Point> outNeighborList = connectedList.get(pCurrent);
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
						
						ArrayList<Point> inNeighborOutList = connectedList.get(pIn);
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
								
								int inOutPosition = binarySearchPosition(connectedList.get(pIn), pCurrent);
								connectedList.get(pIn).remove(inOutPosition);
								invertedConnectedList.get(pCurrent).remove(k);
								k--;
							//}
						}
					}
				}
				
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		//generateConnectedList("/home/uqdhe/workspace/TraSpaEst/odpairmatrix", "/home/uqdhe/"
        //		+ "workspace/TraSpaEst/connectedlist");
        		
        initList("/media/bigdata/uqdhe/"+FILE+"/connectedlist-"+FILE, connectedList);
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

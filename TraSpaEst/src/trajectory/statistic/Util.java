package trajectory.statistic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.FileNotFoundException;
import java.io.FileInputStream;

public class Util {
	
	/* if the length of sub-trajectory is larger than noise-threshold times of average
	 * length, it is regarded as a noise sub-trajectory*/
	public static final int NOISE_THRESHOLD = 5; 
	public static final int DURATION_PARAMETER = 10;
    public static final int SECOND = 60;
    public static final int MINUTE = 60;
    public static final int HOUR = 24;
	
	/* compare the accessed nodes in two trajectory sets with histograms */
	public static double featureStatistics(ArrayList<Trajectory> 
			trajectoryListA, ArrayList<Trajectory> trajectoryListB, String option){
		
		double histDistance = 0.0;
		
		HashMap<Long, Integer> statNodeA = new HashMap<Long, Integer>();
		HashMap<Long, Integer> statNodeB = new HashMap<Long, Integer>();
		
		int optionInt = 0;
		
		if (option.equals("node"))
			optionInt = 1;
		else if (option.equals("length"))
			optionInt = 2;
		else if (option.equals("timeDuration"))
			optionInt = 3;
		else if (option.equals("timePeriod"))
			optionInt = 4;
		
		switch(optionInt){
		
			case 1 : // node
				/* statistics for trajectory list A */
				setHaspMapTrajectoryListByPoint(trajectoryListA, statNodeA);
				
				/* statistics for trajectory list B */
				setHaspMapTrajectoryListByPoint(trajectoryListB, statNodeB);
				break;
			
			case 2 : // length
				/* statistics for trajectory list A */
				setHaspMapTrajectoryListByLength(trajectoryListA, statNodeA);
				
				/* statistics for trajectory list B */
				setHaspMapTrajectoryListByLength(trajectoryListB, statNodeB);
				break;
				
			case 3 : // time duration
				/* statistics for trajectory list A */
				setHaspMapTrajectoryListByDuration(trajectoryListA, statNodeA);
				
				/* statistics for trajectory list B */
				setHaspMapTrajectoryListByDuration(trajectoryListB, statNodeB);
				break;
				
			case 4 : // time period
				/* statistics for trajectory list A */
				setHaspMapTrajectoryListByPeriod(trajectoryListA, statNodeA);
				
				/* statistics for trajectory list B */
				setHaspMapTrajectoryListByPeriod(trajectoryListB, statNodeB);
				break;
				
			default:
				System.out.println("Error: [Util::featureStatistics()] "
						+ "The option is not eligible!\n");
				break;
		}
		
		/* union the key of two hash maps */		
		HashMap<Long, Integer> unionNode = new HashMap<Long, Integer>();
		addTwoHashSet(statNodeA, statNodeB, unionNode);
		
		/* initialize two histograms */
		Histogram histA = new Histogram(unionNode.size());
		Histogram histB = new Histogram(unionNode.size());
		
		/* set the histograms with values in the hash maps */
		setHistByHashMap(statNodeA, unionNode, histA);
		setHistByHashMap(statNodeB, unionNode, histB);
		
		histDistance = histA.distanceHistogram(histB);

		return histDistance;
	}
	
	/* add two hash set into one set by the keys */
	public static void addTwoHashSet(HashMap<Long, Integer> hashMapA, 
			HashMap<Long, Integer> hashMapB, HashMap<Long, Integer> hashMapUnion){
		
		Set<Long> setA = hashMapA.keySet();
		Set<Long> setB = hashMapB.keySet();
		Set<Long> union = new HashSet<Long>(setA);
		union.addAll(setB);
		
		int setIndex = 0;
		for(long elem: union){
			hashMapUnion.put(elem, setIndex);
			setIndex++;
		}
		
	}
	
	/* fill the histogram with the hash map */
	public static void setHistByHashMap(HashMap<Long, Integer> originHashMap, 
			HashMap<Long, Integer> unitonHashMap, Histogram hist){
		
		Iterator<Entry<Long, Integer>> iter = originHashMap.entrySet().iterator();
		
        while (iter.hasNext()) {
        	
			Map.Entry<Long, Integer> pair = (Map.Entry<Long, Integer> ) iter.next();			
        	int index = unitonHashMap.get(pair.getKey());        	
        	hist.addDataOccurrence(index, (long)pair.getValue());
        }
	}
	
	/* set the hash map for a trajectory list, the key is the nDimension
	 * field in a point of trajectory; the key can be the node or the time */
	public static void setHaspMapTrajectoryListByPoint(ArrayList<Trajectory> trajectoryList,
			HashMap<Long, Integer> hashMap){
		
		for(int index = 0; index < trajectoryList.size(); index++){
			/* get the trajectory */
			ArrayList<Point> trajectory = trajectoryList.get(index).get_pointArray();
			
			for(int i = 0; i < trajectory.size(); i++){
				long node = trajectory.get(i).get_coordinate(0);
				setHashMap(hashMap, node);
			}
		}
		
	}
	
	/* set the hash map for a trajectory list, the key is the length of each trajectory */
	public static void setHaspMapTrajectoryListByLength(ArrayList<Trajectory> trajectoryList,
			HashMap<Long, Integer> hashMap){
		
		for(int index = 0; index < trajectoryList.size(); index++){
			/* get the trajectory */
			ArrayList<Point> trajectory = trajectoryList.get(index).get_pointArray();
			
			long length = trajectory.size();
			
			setHashMap(hashMap, length);
			
		}
		
	}
	
	/* set the hash map for a trajectory list, the key is the duration of each trajectory */
	public static void setHaspMapTrajectoryListByDuration(ArrayList<Trajectory> trajectoryList,
			HashMap<Long, Integer> hashMap){
		
		for(int index = 0; index < trajectoryList.size(); index++){
			/* get the trajectory */
			ArrayList<Point> trajectory = trajectoryList.get(index).get_pointArray();
			
			int length = trajectory.size();
			
			long duration = trajectory.get(length - 1).get_coordinate(1) - 
					trajectory.get(0).get_coordinate(1);
			
			setHashMap(hashMap, duration/10);
			
		}
		
	}
	
	/* set the hash map for a trajectory list, the key is the nDimension
	 * field in a point of trajectory; the key can be the node or the time */
	public static void setHaspMapTrajectoryListByPeriod(ArrayList<Trajectory> trajectoryList,
			HashMap<Long, Integer> hashMap){
		
		for(int index = 0; index < trajectoryList.size(); index++){
			/* get the trajectory */
			ArrayList<Point> trajectory = trajectoryList.get(index).get_pointArray();
			
			for(int i = 0; i < trajectory.size(); i++){
				long time = trajectory.get(i).get_coordinate(1);
				long period =(long) (time/(SECOND * MINUTE)) % HOUR;
				setHashMap(hashMap, period);
			}
		}
		
	}
	
	/* this hash map is a counter for the string field 
	 * if there is already a mapped string then the 
	 * counter increase by 1, otherwise new a counter
	 * for that string */
    public static void setHashMap(HashMap<Long, Integer> hashMap, long metadata){
		if (hashMap.containsKey(metadata)) {
			hashMap.put(metadata, hashMap.get(metadata)+1);
		} 
		else {
			hashMap.put(metadata, new Integer(1));
		}
	}
	
	
	/* get clipping trajectory set with given origin and destination points */
	public static void get_ClipSet(ArrayList<Trajectory> trajectoryClipList, 
			int originPointId, int destinationPointId, ArrayList<Trajectory> 
			trajectoryList, ArrayList<InvertedList> invertedIndex){
		
		
		/* get the intersection trajectory from two inverted lists */
		ArrayList<Point> intersectionTrajectory = new ArrayList<Point>();
		intersectionTrajectory(intersectionTrajectory, originPointId, 
				destinationPointId, invertedIndex);
		
		/* extract the sub-trajectories from trajectory list */
		extractSubTrajectoryClipList(trajectoryClipList, intersectionTrajectory, 
				trajectoryList);
		
		
	}
	
	/* get origin-destination trajectory set with given origin and destination points */
	public static void  get_OriginDestinationSet(ArrayList<Trajectory> trajectoryODList,
			int originPointId, int destinationPointId, ArrayList<Trajectory> 
			trajectoryList, ArrayList<InvertedList> invertedIndex){
		
		/* get the intersection trajectory from two inverted lists */
		ArrayList<Point> intersectionTrajectory = new ArrayList<Point>();
		intersectionTrajectory(intersectionTrajectory, originPointId, 
				destinationPointId, invertedIndex);
		
		/* extract the sub-trajectories from trajectory list */
		extractSubTrajectoryODList(trajectoryODList, intersectionTrajectory, 
				trajectoryList);
		
	}
	
	/* extract a sub-trajectory list with given intersection trajectory list 
	 * this sub-trajectory is a set of clipping trajectories*/
	public static void  extractSubTrajectoryClipList(ArrayList<Trajectory> 
			subTrajectoryList, ArrayList<Point> intersectionTrajectory, 
			ArrayList<Trajectory> trajectoryList){
		
		/* FIXME: by now the trajectory id is the same as
		 *  the index in the trajectory list*/
		
		/* FIXME: set a noise filter threshold */
		int averageLength = 0;
		
		for(int index = 0; index < intersectionTrajectory.size(); index++){
			
			Trajectory subTrajectory = new Trajectory();
			
			/* get information from intersection list */
			int trajectoryIndex = (int)intersectionTrajectory.get(index).get_coordinate(0);
			int startPosition = (int)intersectionTrajectory.get(index).get_coordinate(1);
			int endPosition = (int)intersectionTrajectory.get(index).get_coordinate(2);
			
			/* calculate the current averageLength of trajectory */
			averageLength = (averageLength * index + 
					trajectoryList.get(trajectoryIndex).get_pointArray().size())/(index + 1);
			
			/* FIXME: if the length of sub-trajectory is larger than noise-threshold times 
			 *  of average length, it is regarded as a noise sub-trajectory*/
			if(trajectoryList.get(trajectoryIndex).get_pointArray().size() 
					> NOISE_THRESHOLD * averageLength){
				averageLength = (averageLength * (index + 1) -
						trajectoryList.get(trajectoryIndex).get_pointArray().size())/index;
				continue;
			}
			
			if(startPosition != 0 || endPosition != 
					trajectoryList.get(trajectoryIndex).get_pointArray().size() - 1){
				/* set sub-trajectory id */
				subTrajectory.set_trajectoryId(trajectoryIndex);
				
				/* set sub-trajectory point list */
				subTrajectory.set_pointArray(trajectoryList.get(trajectoryIndex)
						.get_subTrajectory(startPosition, endPosition));
				
				/* add sub-trajectory to the result list */
				subTrajectoryList.add(subTrajectory);
			}
		}
		
	}
	
	/* extract a sub-trajectory list with given intersection trajectory list 
	 * this sub-trajectory is a set of origin-destination trajectories*/
	public static void extractSubTrajectoryODList(ArrayList<Trajectory> 
			subTrajectoryList, ArrayList<Point> intersectionTrajectory, 
			ArrayList<Trajectory> trajectoryList){
		
		/* FIXME: by now the trajectory id is the same as
		 *  the index in the trajectory list*/
		for(int index = 0; index < intersectionTrajectory.size(); index++){
			
			Trajectory subTrajectory = new Trajectory();
			
			/* get information from intersection list */
			int trajectoryIndex = (int)intersectionTrajectory.get(index).get_coordinate(0);
			int startPosition = (int)intersectionTrajectory.get(index).get_coordinate(1);
			int endPosition = (int)intersectionTrajectory.get(index).get_coordinate(2);
			
			if(startPosition == 0 && endPosition == 
					trajectoryList.get(trajectoryIndex).get_pointArray().size() - 1){
				/* set sub-trajectory id */
				subTrajectory.set_trajectoryId(trajectoryIndex);
				
				/* set sub-trajectory point list */
				subTrajectory.set_pointArray(trajectoryList.get(trajectoryIndex)
						.get_subTrajectory(startPosition, endPosition));
				
				/* add sub-trajectory to the result list */
				subTrajectoryList.add(subTrajectory);
			}
		}
		
	}
	
	/* extract a sub-trajectory list with given intersection trajectory list 
	 * this sub-trajectory does not concern whether a sub-trajectory is od or clipping*/
	public static void extractSubTrajectoryList(ArrayList<Trajectory> subTrajectoryList, 
			ArrayList<Point> intersectionTrajectory, ArrayList<Trajectory> trajectoryList){
		
		/* FIXME: by now the trajectory id is the same as
		 *  the index in the trajectory list*/
		for(int index = 0; index < intersectionTrajectory.size(); index++){
			
			Trajectory subTrajectory = new Trajectory();
			
			/* get information from intersection list */
			int trajectoryIndex = (int)intersectionTrajectory.get(index).get_coordinate(0);
			int startPosition = (int)intersectionTrajectory.get(index).get_coordinate(1);
			int endPosition = (int)intersectionTrajectory.get(index).get_coordinate(2);
			
			/* set sub-trajectory id */
			subTrajectory.set_trajectoryId(trajectoryIndex);
			
			/* set sub-trajectory point list */
			subTrajectory.set_pointArray(trajectoryList.get(trajectoryIndex)
					.get_subTrajectory(startPosition, endPosition));
			
			/* add sub-trajectory to the result list */
			subTrajectoryList.add(subTrajectory);
		}
		
	}
	
	
	
	/* build the trajectory list by reading trajectory file */
	public static void buildTrajectoryList(ArrayList<Trajectory> trajectoryList, 
			String trajectoryFile, int pointDimension) throws FileNotFoundException{
		
		FileInputStream inputStreamTrajectoryList = new FileInputStream(trajectoryFile);
		
        @SuppressWarnings("resource")
		Scanner scTrajectoryList = new Scanner(inputStreamTrajectoryList, "UTF-8");
        String lineTrajectoryList = "";
        String SplitBy = ",";
        
        while (scTrajectoryList.hasNextLine()) {
        	
        	/* scan a line */
            lineTrajectoryList = scTrajectoryList.nextLine();
            
            /* split a line into items, store into an array */
            String[] lineData = lineTrajectoryList.split(SplitBy);
            
            /* new a trajectory */
            Trajectory trajectoryItem = new Trajectory();
            /* set trajectory id */
            trajectoryItem.set_trajectoryId(Integer.parseInt(lineData[0]));
            
            /* construct a trajectory */
            for(int i = 1; i < lineData.length - 1; i = i + pointDimension){
            	
            	/* new the item of the trajectory
            	 * Point: pointId, time-stamp */
                Point point = new Point(pointDimension);
                
                for(int j = 0; j < pointDimension; j++)
                	point.set_coordinate(j, Long.parseLong(lineData[i + j]));

                trajectoryItem.addPointToArray(point);
            }
            
            /* add the trajectory into the trajectory list */
            trajectoryList.add(trajectoryItem);            
        }

	}
	
	/* build the inverted index by reading the inverted-index file */
	public static void buildInvertedIndex(ArrayList<InvertedList> invertedIndex, 
			String invertedIndexFile) throws FileNotFoundException {
		
        FileInputStream inputStreamInvertedIndex = 
        		new FileInputStream(invertedIndexFile);
        
		@SuppressWarnings("resource")
		Scanner scInvertedIndex = new Scanner(inputStreamInvertedIndex, "UTF-8");
        String lineInvertedIndex = "";
        String SplitBy = ",";
        
        while (scInvertedIndex.hasNextLine()) {
        	
        	/* scan a line */
            lineInvertedIndex = scInvertedIndex.nextLine();
            
            /* split a line into items, store into an array */
            String[] lineData = lineInvertedIndex.split(SplitBy);
            
            /* new an inverted list */
            InvertedList invertedlistItem = new InvertedList();
            
            /* set inverted list id */
            invertedlistItem.set_invertedlistID(Integer.parseInt(lineData[0]));
            
            /* construct the inverted list */
            for(int i = 1; i < lineData.length - 1; i = i + 3){
            	
            	/* new the item of the inverted list
            	 * Point: trajectoryId, position */
                Point trajectoryPoint = new Point();
                
                trajectoryPoint.set_coordinate(0, Long.parseLong(lineData[i]));
                trajectoryPoint.set_coordinate(1, Long.parseLong(lineData[i + 1]));
                
                invertedlistItem.addTrajectoryToArray(trajectoryPoint);
            }
            
            /* add the inverted list into the inverted index */
            invertedIndex.add(invertedlistItem);
        }

	}
	
	/* get intersection trajectory with two point ids */
	public static void intersectionTrajectory(ArrayList<Point> intersectionList, 
			long originPointId, long destinationPointId, 
			ArrayList<InvertedList> invertedIndex){
		
		/* FIXME: at this stage, the point id is the same
		 * as the point index */
		ArrayList<Point> originList = new ArrayList<Point>();
		originList = invertedIndex.get((int)originPointId).get_trajectoryArray();
		
		ArrayList<Point> destinationList = new ArrayList<Point>();
		destinationList = invertedIndex.get((int)destinationPointId)
				.get_trajectoryArray();
		
		/* get intersection by two inverted lists */
		intersectionTrajectory(intersectionList, originList, destinationList);

	}	
	
	/* get intersection trajectories with two inverted lists 
	 * the element in each inverted list is in form of
	 * Point: trajectoryId, position
	 * the element in the result intersection list is as follow
	 * Point: trajectoryId, start position, end position */
    public static void intersectionTrajectory(ArrayList<Point> intersectionList, 
    		ArrayList<Point> originList, ArrayList<Point> destinationList) {
    	
        int originLength = originList.size();
        int destinationLength = destinationList.size();
        
        /* check whether given lists have element */
        if (originLength == 0 || destinationLength == 0){
        	System.out.println("Warning: [Util::intersectionTrajectory()] "
        			+ "At least one of the given lists is null!");
        	return;
        }
        
        /* check whether there is intersection by comparing the starting and ending */
        if (originList.get(originLength - 1).get_coordinate(0) 
        		< destinationList.get(0).get_coordinate(0)
                || destinationList.get(destinationLength - 1).get_coordinate(0) 
                < originList.get(0).get_coordinate(0)){
        	System.out.println("Warning: [Util::intersectionTrajectory()] "
        			+ "There is no intersection!");
        	return;
        }
        
        int originIndex = 0;
        int destinationIndex = 0;
        
        long currentOriginId;
        long currentDestinationId;
        
        /* begin to compare the trajectory id in each lists */
        while(originIndex < originLength && destinationIndex < destinationLength){
            
            Point intersect = new Point(3);
            
            /* set the current trajectory id */
            currentOriginId = originList.get(originIndex).get_coordinate(0);
            currentDestinationId = destinationList.get(destinationIndex)
            		.get_coordinate(0);
            
            /* if the current origin id is larger then the current destination id
             * the destination id increases and update the current destination id */
            while((destinationIndex < destinationLength - 1) && 
            		(currentOriginId > currentDestinationId)){
                destinationIndex++;
                currentDestinationId = destinationList.get(destinationIndex)
                		.get_coordinate(0);
            }
            
            /**/
            if (currentOriginId > currentDestinationId){
                destinationIndex++;
                continue;
            }
            
            if (currentOriginId < currentDestinationId){
                originIndex++;
                continue;
            }
            
            /* when the trajectories ids are the same */
            else if (currentOriginId == currentDestinationId){
            	
                if (originList.get(originIndex).get_coordinate(1) < 
                		destinationList.get(destinationIndex).get_coordinate(1)){
                	
                    intersect.set_coordinate(0, currentOriginId);
                    intersect.set_coordinate(1, originList.get(originIndex)
                    		.get_coordinate(1));
                    intersect.set_coordinate(2, destinationList.get(destinationIndex)
                    		.get_coordinate(1));
                    intersectionList.add(intersect);
                }
                
                originIndex++;
                destinationIndex++;
            }
        }
              
    }

}
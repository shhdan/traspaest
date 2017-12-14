package trajectory.statistic;

import java.util.ArrayList;

public class Trajectory {
	
	private int trajectoryId;
    private ArrayList<Point> pointArray;
//    private ArrayList<Point> linkArray;
    
    /* initialization */
    public Trajectory() {	
		trajectoryId = -1;
		pointArray = new ArrayList<Point>();		
    }
    
    /* add point to the trajectory list */
    public void addPointToArray(Point point) {
		pointArray.add(point);
    }
    
    /* set trajectory id */
    public void set_trajectoryId(int id) {
		this.trajectoryId = id;
    }
	
    /* get trajectory id */
    public int get_trajectoryId() {
		return trajectoryId;
    }						
	
    /* get the whole trajectory list */
    public ArrayList<Point> get_pointArray() {
		return pointArray;
    }

    /* set the whole trajectory list */
    public void set_pointArray(ArrayList<Point> pointArray) {
		this.pointArray = pointArray;
    }
    
    /* get sub-trajectory by starting and ending index , the ending index point is included*/
    public ArrayList<Point> get_subTrajectory(int start, int end){
    	ArrayList<Point> subTrajectory = new ArrayList<Point>();
    	
    	/* check whether the index is reasonable */
    	if(start < 0 || end > this.get_pointArray().size()){
    		System.out.println("Error: [Trajectory::get_subTrajectory()] "
    				+ "The given index is out of range!");
    		return subTrajectory;
    	}
    	
    	for(int index = start ; index <= end; index++){
    		subTrajectory.add(this.get_pointArray().get(index));
    	}
    	
    	return subTrajectory;
    }

    
    
}

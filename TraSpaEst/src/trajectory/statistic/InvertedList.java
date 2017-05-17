package trajectory.statistic;

import java.util.ArrayList;

public class InvertedList {

	private int invertedlistID;
    private ArrayList<Point> trajectoryArray; // 2D-Point: trajectoryID, position
    
    /* initialization */
    public InvertedList(){
        invertedlistID = -1;
        trajectoryArray = new ArrayList<Point>();
    }
    
    /* add point to the inverted list */
    public void addTrajectoryToArray(Point point) {
		trajectoryArray.add(point);
    }
    
    /* set inverted list id, usually refers to the point id */
    public void set_invertedlistID(int id) {
		this.invertedlistID = id;
    }
	
    /* get inverted list id */
    public int get_invertedlistID() {
		return invertedlistID;
    }						
	
    /* get the whole inverted list */
    public ArrayList<Point> get_trajectoryArray() {
		return trajectoryArray;
    }

    /* set the whole inverted list */
    public void set_trajectoryArray(ArrayList<Point> trajectoryArray) {
		this.trajectoryArray = trajectoryArray;
    }
}

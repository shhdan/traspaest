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
    
    public boolean check_intersectionTrajectory(InvertedList otherList) {
        
        int Olength = this.get_trajectoryArray().size();
        int Dlength = otherList.get_trajectoryArray().size();
        
        if (Olength == 0 || Dlength == 0){
            return false;
        }
        
        if (this.get_trajectoryArray().get(Olength - 1).get_coordinate(0) < otherList.get_trajectoryArray().get(0).get_coordinate(0)
                || otherList.get_trajectoryArray().get(Dlength - 1).get_coordinate(0) < this.get_trajectoryArray().get(0).get_coordinate(0)){
            return false;
        }
        
        int Olist_index = 0;
        int Dlist_index = 0;
        
        long currentOID;
        long currentDID;
        
        while(Olist_index < Olength && Dlist_index < Dlength){

            
            Point intersect = new Point(3);
            
            currentOID = this.get_trajectoryArray().get(Olist_index).get_coordinate(0);
            currentDID = otherList.get_trajectoryArray().get(Dlist_index).get_coordinate(0);
            
            while((Dlist_index < Dlength - 1) && (currentOID > currentDID)){
                Dlist_index++;
                currentDID =otherList.get_trajectoryArray().get(Dlist_index).get_coordinate(0);
            }
            
            if (currentOID > currentDID){
                Dlist_index++;
                continue;
            }
            
            if (currentOID < currentDID){
                Olist_index++;
                continue;
            }
            
            else if (currentOID == currentDID){
                if (this.get_trajectoryArray().get(Olist_index).get_coordinate(1) < otherList.get_trajectoryArray().get(Dlist_index).get_coordinate(1)){
                    intersect.set_coordinate(0, currentOID);
                    intersect.set_coordinate(1, this.get_trajectoryArray().get(Olist_index).get_coordinate(1));
                    intersect.set_coordinate(2, otherList.get_trajectoryArray().get(Dlist_index).get_coordinate(1));
                    //this.intersectionTrajectory.add(intersect);
                    return true;
                }
                Olist_index++;
                Dlist_index++;
            }
        }
        
        //this.nintersection = this.intersectionTrajectory.size();  
        return true;
    }
    
public void find_intersectionTrajectory(InvertedList otherList, ArrayList<Point> resultTrajectory) {
        
        int Olength = this.get_trajectoryArray().size();
        int Dlength = otherList.get_trajectoryArray().size();
        
        if (Olength == 0 || Dlength == 0){
            return;
        }
        
        if (this.get_trajectoryArray().get(Olength - 1).get_coordinate(0) < otherList.get_trajectoryArray().get(0).get_coordinate(0)
                || otherList.get_trajectoryArray().get(Dlength - 1).get_coordinate(0) < this.get_trajectoryArray().get(0).get_coordinate(0)){
            return;
        }
        
        int Olist_index = 0;
        int Dlist_index = 0;
        
        long currentOID;
        long currentDID;
        
        while(Olist_index < Olength && Dlist_index < Dlength){

            
            Point intersect = new Point(3);
            
            currentOID = this.get_trajectoryArray().get(Olist_index).get_coordinate(0);
            currentDID = otherList.get_trajectoryArray().get(Dlist_index).get_coordinate(0);
            
            while((Dlist_index < Dlength - 1) && (currentOID > currentDID)){
                Dlist_index++;
                currentDID =otherList.get_trajectoryArray().get(Dlist_index).get_coordinate(0);
            }
            
            if (currentOID > currentDID){
                Dlist_index++;
                continue;
            }
            
            if (currentOID < currentDID){
                Olist_index++;
                continue;
            }
            
            else if (currentOID == currentDID){
                if (this.get_trajectoryArray().get(Olist_index).get_coordinate(1) < otherList.get_trajectoryArray().get(Dlist_index).get_coordinate(1)){
                    intersect.set_coordinate(0, currentOID);
                    intersect.set_coordinate(1, this.get_trajectoryArray().get(Olist_index).get_coordinate(1));
                    intersect.set_coordinate(2, otherList.get_trajectoryArray().get(Dlist_index).get_coordinate(1));
                    //this.intersectionTrajectory.add(intersect);
                    resultTrajectory.add(intersect);
                }
                Olist_index++;
                Dlist_index++;
            }
        }
        
    }
}

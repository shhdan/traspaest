package trajectory.statistic;

import java.util.ArrayList;

public class Grid {
	
	int ncol; // number of columns
    int nrow; // number of row
    
    double csize; // size of the cell, the height/width of each cell
    MBR extent; // entire extent of the Grid
    
    /*
    compute the cell where the given point belongs to
    return the cell ID
    this is the cell id when ncol = 4 nrow = 3
    8  9  10 11
    4  5  6  7
    0  1  2  3
   */
   int getCell(double x, double y){
       int cid = 0;
       int col = (int) ((x - extent.minx)/csize);
       int row = (int) ((y - extent.miny)/csize);
       cid = row * ncol + col;
       return cid;
   }
   
   public static void build(ArrayList<RoadSegment> roadSegmentList, double cellsize){
	   
   }

}

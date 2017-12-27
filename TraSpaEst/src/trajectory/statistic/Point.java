package trajectory.statistic;

//import rtree.IndexablePoint;

public class Point{

	private int nDimensions; 	// the number of dimensions of a point
	int[] coordinate;	    // the coordinate of a point
	
	/* default constructor which shall be never used, 
	 * we can use the following constructor instead
	 */
	public Point() {		
		nDimensions = 2;
		coordinate = new int[nDimensions];
		coordinate[0] = coordinate[1] = 0;
	}
	
	/* initialization point with the number of dimension, 
	 * each dimension has a value 0
	 */
	public Point(int Dimensions) {
		nDimensions = Dimensions;
		coordinate = new int[nDimensions];
		for( int i=0; i < nDimensions; i++ ) {
			coordinate[i] = 0;
		}
	}
	
	
	/* return the coordinate according to the dimension 'nth'
	 * @param nth #dimension
	 * @return
	 */
	public int get_coordinate(int nth) {
		
		return coordinate[nth];
	}
	
	public int get_nDimensions() {
		return nDimensions;
	}
	
	
	/* set the coordinate according to the dimension
	 * @param nth dimension
	 * @param value value
	 */
	public void set_coordinate(int nth, int value) {
		this.coordinate[nth] = value;
	}
	
	/* compare two points equals or not */
	public boolean equals (Point point){
		for (int i = 0; i < nDimensions; i++){
			if(this.get_coordinate(i) != point.get_coordinate(i)){
				return false;
			}
		}
		return true;
	}
}

package trajectory.statistic;

public class Point {

	private int nDimensions; 	// the number of dimensions of a point
	long[] coordinate;	    // the coordinate of a point
	
	/* default constructor which shall be never used, 
	 * we can use the following constructor instead
	 */
	public Point() {		
		nDimensions = 2;
		coordinate = new long[nDimensions];
		coordinate[0] = coordinate[1] = 0;
	}
	
	/* initialization point with the number of dimension, 
	 * each dimension has a value 0
	 */
	public Point(int Dimensions) {
		nDimensions = Dimensions;
		coordinate = new long[nDimensions];
		for( int i=0; i < nDimensions; i++ ) {
			coordinate[i] = 0;
		}
	}
	
	
	/* return the coordinate according to the dimension 'nth'
	 * @param nth #dimension
	 * @return
	 */
	public long get_coordinate(int nth) {
		
		return coordinate[nth];
	}
	
	public int get_nDimensions() {
		return nDimensions;
	}
	
	
	/* set the coordinate according to the dimension
	 * @param nth dimension
	 * @param value value
	 */
	public void set_coordinate(int nth, long value) {
		this.coordinate[nth] = value;
	}
}

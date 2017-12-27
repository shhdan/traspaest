package trajectory.statistic;

import java.util.ArrayList;

public class Grid {

	int ncol; // number of columns
	int nrow; // number of row

	double csize; // size of the cell, the height/width of each cell
	MBR extent; // entire extent of the Grid

	// MultiMap<Integer, Integer> leftBoundIntersect;
	// MultiMap<Integer, Integer> rightBoundIntersect;

	MultiMap<Integer, Integer> nodes;

	/*
	 * compute the cell where the given point belongs to return the cell ID this
	 * is the cell id when ncol = 4 nrow = 3 8 9 10 11 4 5 6 7 0 1 2 3
	 */
	int getCell(double x, double y) {
		int cid = 0;
		int col = (int) ((x - extent.minx) / csize);
		int row = (int) ((y - extent.miny) / csize);
		cid = row * ncol + col;
		return cid;
	}

	/*
	 * Build the grid index for road segments, where each grid store the road
	 * segments intersecting with their boundary
	 */
	public void build(ArrayList<Point> pointList, double cellsize) {
		if (pointList.size() < 4)
			return;

		// compute the overall extent of the grid space
		for (int i = 0; i < pointList.size(); i++) {
			extent.unionWithPoint(pointList.get(i));
		}

		// set the grid size
		ncol = (int) Math.ceil(extent.width() / csize);
		nrow = (int) Math.ceil(extent.height() / csize);

		if (nodes.size() > 0)
			nodes.clear();

		for (int i = 0; i < pointList.size(); i++) {
			int nodeId = getCell(pointList.get(i).get_coordinate(0), pointList
					.get(i).get_coordinate(1));
			
			nodes.putDistinct(nodeId, i);
		}

	}

	public int rightOf(int cid) {
		int rst = cid + 1;
		return (rst % ncol == 0) ? -1 : rst;
	}

	public int leftOf(int cid) {
		int rst = cid - 1;
		return (cid % ncol == 0) ? -1 : rst;
	}

	public int upperOf(int cid) {
		int rst = cid + ncol;
		return (rst >= nrow * ncol) ? -1 : rst;
	}

	public int belowOf(int cid) {
		int rst = cid - ncol;
		return (rst < 0) ? -1 : rst;
	}

}

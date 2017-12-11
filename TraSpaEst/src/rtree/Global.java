package rtree;

public class Global {
	
	public static long MYINFTY = 2147483647;
	public static int rtreeNode_dim = 2;
	public static int axisIndex = 0;
	public static int FanoutRatio = 4;
	
	public static boolean CompRtreeNodeLess(RtreeNode _u1, RtreeNode _u2){
		int c1 = (_u1.mbr.valueList[axisIndex + rtreeNode_dim] + _u1.mbr.valueList[axisIndex]);
		int c2 = (_u2.mbr.valueList[axisIndex + rtreeNode_dim] + _u2.mbr.valueList[axisIndex]);
		return c1 < c2;
	}
	
	public static boolean CompElementLess(IndexablePoint _p1, IndexablePoint _p2){
		IndexablePoint p1 = _p1;
		IndexablePoint p2 = _p2;
		return p1.coords[axisIndex] < p2.coords[axisIndex];
	}

}

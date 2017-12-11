package rtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class RtreeNode {

	/*
	*  For an internal node, listPtr is the pointer of the child node list.
	*  For a leaf node, listPtr is the pointer of the element list.
	*/
	//private String[] listPtr;
	private ArrayList<IndexablePoint> listPtr;
	private ArrayList<RtreeNode> listChildNode;
	private int listLeafSize;
	private int listChildSize;

	public Rectangle mbr = new Rectangle();
	public RtreeNode parent;

	public RtreeNode()
	{
		// TODO Auto-generated constructor stub
		//listPtr = null;
		listPtr =  new ArrayList<IndexablePoint>();
		listChildNode = new ArrayList<RtreeNode>();
		listLeafSize = 0;
		listChildSize = 0;
		parent = null;
	}

	public void close(){}

	public final int addElement(IndexablePoint _elem, int _dim)
	{

		IndexablePoint p = _elem;
		Rectangle rect = new Rectangle();
		rect.valueList = new int[2 * _dim];
		for (int i = 0; i < _dim; i++)
		{
			rect.valueList[i] = p.coords[i];
			rect.valueList[i + _dim] = p.coords[i];
		}
		// Update the MBR for the parent node.
		if (this.listLeafSize == 0)
		{
			this.mbr.setByRectangle(rect, _dim);
		}
		else
		{
			this.mbr.enlarge(rect, _dim);
		}
		//this.listPtr[listSize++] = _elem;
		this.listPtr.add(_elem);
		listLeafSize = listPtr.size();

		rect.releaseSpace();
		//
		return listLeafSize;
	}
	public final int deleteElement(IndexablePoint _elem)
	{
		if (listLeafSize == 0)
		{
			// TODO Error message
			return listLeafSize;
		}
		for (int i = 0; i < listLeafSize; i++)
		{
			if (listPtr.get(i) == _elem)
			{
				// Move the last child node to index i and remove the last one.
				//listPtr.get(i) = listPtr.get(listSize - 1);
				//listPtr[listSize - 1] = null;
				listPtr.remove(i);
				listLeafSize--;

				// delete this element
				// TODO here need to release the space of the element.
				//			delete _elem;
				//			_elem = NULL;
				break;
			}
		}
		return listLeafSize;
	}

	public final int addChildNode(RtreeNode _child, int _dim)
	{
		// Update the MBR for the parent node.
		if (this.listChildSize == 0)
		{
			this.mbr.setByRectangle(_child.mbr, _dim);
		}
		else
		{
			this.mbr.enlarge(_child.mbr, _dim);
		}
		//this.listPtr[listSize++] = (String)_child;
		this.listChildNode.add(_child);
		_child.parent = this;
		listChildSize = listChildNode.size();
		return listChildSize;
	}
	public final int deleteChildNode(RtreeNode _child)
	{
		if (listChildSize == 0)
		{
			// TODO Error message
			return listChildSize;
		}
		for (int i = 0; i < listChildSize; i++)
		{
			if (listChildNode.get(i) == _child)
			{
				// Move the last child node to index i and remove the last one.
				//listPtr[i] = listPtr[listSize - 1];
				//listPtr[listSize - 1] = null;
				listChildNode.remove(i);
				listChildSize--;

				// delete this child node
				_child.close();
				if (_child != null)
				{
					_child.close();
				}
				_child = null;
				break;
			}
		}
		return listChildSize;
	}
	public final int getListChildSize()
	{
		return listChildSize;
	}
	
	public final int getListLeafSize(){
	    return listLeafSize;
	}
//	public final String[] getListPtr()
//	{
//		return listPtr;
//	}


	//int RtreeNode::getListSize() {
	//	return listSize;
	//}
	//
	//char** RtreeNode::getListPtr() {
	//	return listPtr;
	//}

//	public final void initialize(boolean _isLeaf, int _dim, int _branchFactor, int _leafCapacity)
//	{
//		if (listPtr != null)
//		{
//			 TODO Error detected.
//		}
//		if (_isLeaf)
//		{
//			listPtr = new byte[_leafCapacity][];
//		}
//		else
//		{
//			listPtr = new byte[_branchFactor][];
//		}
//		listSize = 0;
//	}

	public final RtreeNode chooseBestChildNode(Rectangle _rect, int _dim)
	{
		int bestIndex = -1;
		long bestValue = Global.MYINFTY;
		long p = 0;

		int childNodeNum = listChildSize;
		ArrayList<RtreeNode> childList = listChildNode;
		for (int i = 0; i < childNodeNum; ++i)
		{
			p = childList.get(i).mbr.perimeterIncrement(_rect, _dim);
			if (bestValue > p)
			{
				bestValue = p;
				bestIndex = i;
			}
			else if (bestValue == p && bestIndex >= 0)
			{
				long p1 = childList.get(i).mbr.computePerimeter(_dim);
				long p2 = childList.get(bestIndex).mbr.computePerimeter(_dim);
				if (p1 < p2)
				{
					// If the increment of perimeter are the same, then pick the one whose perimeter is smaller.
					bestValue = p;
					bestIndex = i;
				}
			}
		}
		return childList.get(bestIndex);
	}
	
	public final RtreeNode splitNode(RtreeNode _node, int _dim)
	{
		int childNodeNum = listChildSize + 1;
		int half = childNodeNum / 2;
		int rest = childNodeNum - half;
		long p1 = 0;
		long p2 = 0;
		long bestValue = Global.MYINFTY;
		int bestIndex = -1;

		//String[] tempList = new byte[childNodeNum];
		ArrayList<RtreeNode> tempList = new ArrayList<RtreeNode>();
		
		for (int i = 0; i < childNodeNum; i++)
		{
			tempList.add(listChildNode.get(i));
		}
		//tempList[listSize] = _toInsPtr.argValue;
		tempList.add(_node);

		// Set the dimensionality of the rtree nodes.
		Global.rtreeNode_dim = _dim;
		// Set the compare function pointer.

		// Find the best split axis.
		for (int i = 0; i < _dim; i++)
		{
			// sort the child nodes on each dimension coordinate.
			Global.axisIndex = i;
			//sort(tempList, tempList + childNodeNum, comp);
			Collections.sort(tempList, new Comparator<RtreeNode>(){
				public int compare(RtreeNode u1, RtreeNode u2){
					int c1 = (u1.mbr.valueList[Global.axisIndex + Global.rtreeNode_dim] + u1.mbr.valueList[Global.axisIndex]);
					int c2 = (u2.mbr.valueList[Global.axisIndex + Global.rtreeNode_dim] + u2.mbr.valueList[Global.axisIndex]);
					return c2 - c1;
				}
			});
			p1 = this.computeGroupPerimeterNode(tempList, 0, half, _dim);
			p2 = this.computeGroupPerimeterNode(tempList, half, rest, _dim);
			if (p1 + p2 < bestValue)
			{
				// The smaller value the better.
				bestValue = p1 + p2;
				bestIndex = Global.axisIndex;
			}
		}

		// Split this node by bestIndex.
		Global.axisIndex = bestIndex;
		//sort(tempList, tempList + childNodeNum, comp);
		Collections.sort(tempList, new Comparator<RtreeNode>(){
			public int compare(RtreeNode u1, RtreeNode u2){
				int c1 = (u1.mbr.valueList[Global.axisIndex + Global.rtreeNode_dim] + u1.mbr.valueList[Global.axisIndex]);
				int c2 = (u2.mbr.valueList[Global.axisIndex + Global.rtreeNode_dim] + u2.mbr.valueList[Global.axisIndex]);
				return c2 - c1;
			}
		});

		// Create a new node u.
		RtreeNode u = new RtreeNode();
		u.parent = this.parent;


			// Reset this node.
			listChildSize = 0;
			this.listChildNode = null;
			for (int i = 0; i < half; ++i)
			{
				this.addChildNode((tempList.get(i)), _dim);
			}
			for (int i = half; i < childNodeNum; ++i)
			{
				u.addChildNode((tempList.get(i)), _dim);
			}

		return u;
	}

	public final RtreeNode splitLeaf(IndexablePoint _elem, int _dim)
	{
		int childNodeNum = listLeafSize + 1;
		int half = childNodeNum / 2;
		int rest = childNodeNum - half;
		long p1 = 0;
		long p2 = 0;
		long bestValue = Global.MYINFTY;
		int bestIndex = -1;

		//String[] tempList = new byte[childNodeNum];
		ArrayList<IndexablePoint> tempList = new ArrayList<IndexablePoint>();
		
		for (int i = 0; i < childNodeNum; i++)
		{
			tempList.add(listPtr.get(i));
		}
		//tempList[listSize] = _toInsPtr.argValue;
		tempList.add(_elem);

		// Set the dimensionality of the rtree nodes.
		Global.rtreeNode_dim = _dim;
		// Set the compare function pointer.

		// Find the best split axis.
		for (int i = 0; i < _dim; i++)
		{
			// sort the child nodes on each dimension coordinate.
			Global.axisIndex = i;
//			sort(tempList, tempList + childNodeNum, comp);
			Collections.sort(tempList, new Comparator<IndexablePoint>(){
				public int compare(IndexablePoint p1, IndexablePoint p2){
					return p2.coords[Global.axisIndex] - p1.coords[Global.axisIndex];
				}
			});
			p1 = this.computeGroupPerimeterLeaf(tempList, 0, half, _dim);
			p2 = this.computeGroupPerimeterLeaf(tempList, half, rest, _dim);
			if (p1 + p2 < bestValue)
			{
				// The smaller value the better.
				bestValue = p1 + p2;
				bestIndex = Global.axisIndex;
			}
		}

		// Split this node by bestIndex.
		Global.axisIndex = bestIndex;
		//sort(tempList, tempList + childNodeNum, comp);

		Collections.sort(tempList, new Comparator<IndexablePoint>(){
			public int compare(IndexablePoint p1, IndexablePoint p2){
				return p2.coords[Global.axisIndex] - p1.coords[Global.axisIndex];
			}
		});
		// Create a new node u.
		RtreeNode u = new RtreeNode();
//		u.initialize(_isLeaf, _dim, _branchFactor, _leafCapacity);
		u.parent = this.parent;

			// Reset this node.
			listLeafSize = 0;
			this.listPtr = null;
			for (int i = 0; i < half; ++i)
			{
				this.addElement(tempList.get(i), _dim);
			}

			for (int i = half; i < childNodeNum; ++i)
			{
				u.addElement(tempList.get(i), _dim);
			}

		
		return u;
	}
	
	private long computeGroupPerimeterNode(ArrayList<RtreeNode> _list, int _start, int _length, int _dim)
	{
		if (_start < 0 || _length < 1)
		{
			// TODO Error message.
			return -1;
		}

		long rtn = 0;
		int[] valueList = new int[_dim * 2];
		//int[] minList = valueList;
		//int[] maxList = &(valueList[_dim]);
		int[] minList = new int[_dim];
		int[] maxList = new int[_dim];

			ArrayList<RtreeNode> childList = _list;
			for (int k = 0; k < _dim * 2; k++)
			{
				valueList[k] = childList.get(_start).mbr.valueList[k];
			}
			
			for(int k = 0; k < _dim; k++){
				minList[k] = valueList[k];
				maxList[k] = valueList[k + _dim];
			}

			int end = _start + _length;
			for (int j = _start + 1; j < end; ++j)
			{
				for (int k = 0; k < _dim; ++k)
				{
					if (childList.get(j).mbr.valueList[k] < minList[k])
					{
						minList[k] = childList.get(j).mbr.valueList[k];
					}
					if (childList.get(j).mbr.valueList[k + _dim] > maxList[k])
					{
						maxList[k] = childList.get(j).mbr.valueList[k + _dim];
					}
				}
			}


		// Compute the perimeter of the mbr defined by minList and maxList.
		for (int i = 0; i < _dim; i++)
		{
			rtn += (maxList[i] - minList[i]);
		}
		rtn *= 2;

		valueList = null;

		return rtn;
	}
	
	private long computeGroupPerimeterLeaf(ArrayList<IndexablePoint> _list, int _start, int _length, int _dim)
	{
		if (_start < 0 || _length < 1)
		{
			// TODO Error message.
			return -1;
		}

		long rtn = 0;


		int[] minList = new int[_dim];
		int[] maxList = new int[_dim];

		// This is a leaf node.
			ArrayList<IndexablePoint> pointList = _list;
			
			
			for (int k = 0; k < _dim; k++)
			{
				minList[k] = pointList.get(_start).coords[k];
				maxList[k] = pointList.get(_start).coords[k];
			}

			int end = _start + _length;
			for (int j = _start + 1; j < end; ++j)
			{
				for (int k = 0; k < _dim; ++k)
				{
					if (pointList.get(j).coords[k] < minList[k])
					{
						minList[k] = pointList.get(j).coords[k];
					}
					if (pointList.get(j).coords[k] > maxList[k])
					{
						maxList[k] = pointList.get(j).coords[k];
					}
				}
			}

		// Compute the perimeter of the mbr defined by minList and maxList.
		for (int i = 0; i < _dim; i++)
		{
			rtn += (maxList[i] - minList[i]);
		}
		rtn *= 2;

		return rtn;
	}

	public final ArrayList<IndexablePoint> getListPtr() {
		// TODO Auto-generated method stub
		return this.listPtr;
	}

	public final ArrayList<RtreeNode> getListChildNode() {
		// TODO Auto-generated method stub
		return this.listChildNode;
	}
    

}

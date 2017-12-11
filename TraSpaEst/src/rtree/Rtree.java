package rtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Rtree {
	
	static class heapElement{
		int lev;
		RtreeNode ptr;
		IndexablePoint pt;
		double dist;
	}
	
	static class QueElement{
		RtreeNode node;
		int lev;
	}
	
	protected RtreeNode root; // Leaf nodes are at level-0 and elements are at level-(-1).
	protected int rootLev;

	protected int dim;
	protected int branchFactor;
	protected int leafCapacity;

	private void insertNode_SubRoutine(RtreeNode _subroot, int _subrootLevel, RtreeNode _toInsNode, int _targetLevel)
	{
		if (_subroot == null)
		{
			System.out.print("Error in Rtree::InsertNode_SubRoutine: subroot is NULL!\n");
			return;
		}
		if (_toInsNode == null)
		{
			System.out.print("Error in Rtree::InsertNode_SubRoutine: toInsNode is NULL!\n");
			return;
		}

		if (_targetLevel >= _subrootLevel)
		{
			System.out.print("Error in Rtree::InsertNode_SubRoutine: targetLevel >= subroot->level!\n");
			return;
		}

		if (_subrootLevel == _targetLevel + 1)
		{
			if (_subroot.getListChildSize() + 1 > branchFactor)
			{
				// this subroot overflows after this insertion. It needs to split.
				this.overflowHandler(_subroot, _toInsNode);
			}
			else
			{
				_subroot.addChildNode(_toInsNode, dim);
			}
		}
		else
		{
			// Enlarge the mbr of subroot.
			_subroot.mbr.enlarge(_toInsNode.mbr, dim);
			RtreeNode bestChild = _subroot.chooseBestChildNode(_toInsNode.mbr, dim);
			this.insertNode_SubRoutine(bestChild, _subrootLevel - 1, _toInsNode, _targetLevel);
		}
	}

	/*
	*  Private functions.
	*/
	private int insertElement_SubRoutine(RtreeNode _subroot, int _subrootLevel, IndexablePoint _elem, Rectangle _elem_rect)
	{
		if (_subroot == null || _elem == null)
		{
			System.out.print("Error in Rtree::insertElement_SubRoutine: _subroot or _elem is NULL!\n");
			return 1;
		}
		// Version before Jan 8, 2017
		if (_subrootLevel == 0)
		{
			// subroot is a leaf node.
			if (_subroot.getListLeafSize() + 1 > leafCapacity)
			{
				// the _subroot will overflow after inserting this element.
				RtreeNode u = _subroot.splitLeaf(_elem, dim);
				this.insertNode_SubRoutine(_subroot.parent, 1, u, 0);
			}
			else
			{
				_subroot.addElement(_elem, dim);
			}
			return 0;
		}
		// subroot is an internal node.
		_subroot.mbr.enlarge(_elem_rect, dim);
		RtreeNode bestChild = _subroot.chooseBestChildNode(_elem_rect, dim);
		int rtn = this.insertElement_SubRoutine(bestChild, _subrootLevel - 1, _elem, _elem_rect);
		return rtn;
	}

	private RtreeNode findLeafForDel_SubRoutine(RtreeNode _subroot, int _subrootLevel, IndexablePoint _elem, Rectangle _elem_rect)
	{
		if (_subroot == null)
		{
			return null;
		}
		if (_subrootLevel == 0)
		{
			// subroot is a leaf node.
			int elemNum = _subroot.getListLeafSize();
			ArrayList<IndexablePoint> elemList = _subroot.getListPtr();
			for (int i = 0; i < elemNum; i++)
			{
				if (elemList.get(i) == _elem)
				{
					return _subroot;
				}
			}
		}
		else
		{
			RtreeNode rtn = null;
			int childNum = _subroot.getListChildSize();
			ArrayList<RtreeNode> childList = _subroot.getListChildNode();
			for (int i = 0; i < childNum; ++i)
			{
				if (childList.get(i).mbr.contain(_elem_rect, dim))
				{
					// the mbr of this child node fully contains the mbr of _elem.
					rtn = this.findLeafForDel_SubRoutine(childList.get(i), _subrootLevel - 1, _elem, _elem_rect);
					if (rtn != null)
					{
						return rtn;
					}
				}
			}
		}
		return null;
	}

	private void overflowHandler(RtreeNode _overflowNode, RtreeNode _toInsNode)
	{
		RtreeNode u = _overflowNode.splitNode(_toInsNode, dim);

		if (_overflowNode.parent != null)
		{
			if (_overflowNode.parent.getListChildSize() + 1 > branchFactor)
			{
				// the parent node overflows after inserting u.
				this.overflowHandler(_overflowNode.parent, u);
			}
			else
			{
				_overflowNode.parent.addChildNode(u, dim);
			}
		}
		else
		{
			// overflowNode is the root of this tree.
			// then create a new root for this tree.
			RtreeNode r = new RtreeNode();
//			r.initialize(false, dim, branchFactor, leafCapacity);
			r.addChildNode(_overflowNode, dim);
			r.addChildNode(u, dim);

			this.root = r;
			rootLev++;
		}
	}
	private void underflowHandler(RtreeNode _underflowNode, int _nodeLevel)
	{
		if (_underflowNode == null)
		{
			return;
		}
		RtreeNode parent = _underflowNode.parent;
		if (parent == null)
		{
			// underflowNode is the root of this tree.
			int num = _underflowNode.getListChildSize();
			if (num > 1)
			{
				// underflowNode is a legal root.
				return;
			}
			else
			{
				if (num == 1)
				{
					if (rootLev > 0)
					{
						// underflowNode is an illegal root.
						// Let the only child node be the root.
						this.root = root.getListChildNode().get(0);
						this.root.parent = null;
						rootLev = rootLev - 1;
						//_underflowNode.releaseSpace();
						if (_underflowNode != null)
						{
							_underflowNode.close();
						}
						_underflowNode = null;
					}
				}
				else
				{
					this.root = null;
					rootLev = 0;
//					_underflowNode.releaseSpace();
					if (_underflowNode != null)
					{
						_underflowNode.close();
					}
					_underflowNode = null;
				}
			}
		}
		else
		{
			// underflowNode is not the root of this tree.


			if (_nodeLevel > 0)
			{
				// Get all the child nodes of underflowNode.
				int reInsNum = _underflowNode.getListChildSize();

				ArrayList<RtreeNode> listPtr = _underflowNode.getListChildNode();
				ArrayList<RtreeNode> reInsList = new ArrayList<RtreeNode>();
				for (int i = 0; i < reInsNum; i++)
				{
					reInsList.add(listPtr.get(i));
				}

				// Delete underflowNode from its parent.
				if (parent.deleteChildNode(_underflowNode) < branchFactor / Global.FanoutRatio)
				{
					// parent underflows after deleting underflowNode.
					// Handle the underflowing event of parent.
					this.underflowHandler(parent, _nodeLevel + 1);
				}
				
				// Reinsert all the child nodes of underflowNode.
				for (int i = 0; i < reInsNum; i++)
				{
					this.insertNode(reInsList.get(i), _nodeLevel - 1);
				}
			}
			else
			{
				// Get all the child nodes of underflowNode.
				int reInsNum = _underflowNode.getListChildSize();
				ArrayList<IndexablePoint> listPtr = _underflowNode.getListPtr();
				ArrayList<IndexablePoint> reInsList = new ArrayList<IndexablePoint>();
				for (int i = 0; i < reInsNum; i++)
				{
					reInsList.add(listPtr.get(i));
				}
				// Reinsert all the elements of underflowNode.
				for (int i = 0; i < reInsNum; i++)
				{
					//				// === Codes for debug.
					//				if(((Point*)(reInsList[i]))->id == -1){
					//					printf("time to break.\n");
					//				}
					//				// ===
					this.insertElement(reInsList.get(i));
				}
			}
		}
	}


	/*
	*  Count the total number of elements in the specified subtree.
	*/

	/*
	*  Count the total number of elements in the specified subtree.
	*/
	private int countElementInSubtree(RtreeNode _subroot, int _subrootLevel)
	{
		int cnt = 0;
		if (_subrootLevel == 0)
		{
			// This is a leaf node.
			cnt += _subroot.getListLeafSize();
			return cnt;
		}
		// This is an internal node.
		int childNum = _subroot.getListChildSize();
		ArrayList<RtreeNode> childList = _subroot.getListChildNode();
		for (int i = 0; i < childNum; ++i)
		{
			cnt += countElementInSubtree(childList.get(i), _subrootLevel - 1);
		}
		return cnt;
	}

//	**************************************************/
	/*
	*  Construct leaf level by STR algorithm.
	*  Parameter List:
	*  	_leafLevel:			the target place to store the pointers of the leaf nodes.
	*  	_dimLev:			which dimension we used for sorting in the current recursion.
	*/

	/***************************************************************************/

	public Rtree()
	{
		// TODO Auto-generated constructor stub
		root = null;
		rootLev = 0;
		dim = -1;
		branchFactor = 12;
		leafCapacity = 12;
	}
	public Rtree(int _dim, int _branchFactor, int _leafCapacity)
	{
		root = null;
		rootLev = 0;
		dim = _dim;
		branchFactor = _branchFactor;
		leafCapacity = _leafCapacity;
	}
	public void close(){}

	public final void initialize(int _dim, int _branchFactor, int _leafCapacity)
	{
		root = null;
		rootLev = 0;
		dim = _dim;
		branchFactor = _branchFactor;
		leafCapacity = _leafCapacity;
	}
	public final void constructEmptyTree(int _dim, int _branchFactor, int _leafCapacity)
	{
		if (root != null)
		{
			// TODO error message
			// The tree is not empty!
			System.out.print("Error in Rtree::constructRtree: The tree is not empty!\n");
		}
		dim = _dim;
		branchFactor = _branchFactor;
		leafCapacity = _leafCapacity;
		rootLev = 0;
		root = new RtreeNode();
//		root.initialize(true, dim, branchFactor, leafCapacity);
	}

	public final RtreeNode constructRtree(ArrayList<IndexablePoint> _elementList, int _elemNum, int _dim, int _branchFactor, int _leafCapacity)
	{
	    if (root != null)
		{
			System.out.print("Error in Rtree::constructRtree: The tree is not empty!\n");
			return null;
		}

		dim = _dim;
		branchFactor = _branchFactor;
		leafCapacity = _leafCapacity;
		rootLev = 0;

		int num = _elemNum;
		if (num == 0)
		{
			// TODO error message.
			System.out.print("Error in Rtree::ConstructRtree: the query list is empty!\n");
			return null;
		}
		else if (num <= leafCapacity)
		{
			root = new RtreeNode();
//			root.initialize(true, dim, branchFactor, leafCapacity);
			for (int i = 0; i < num; i++)
			{
				root.addElement(_elementList.get(i), dim);
			}
			return root;
		}

		ArrayList<RtreeNode> treeNodeList = new ArrayList<RtreeNode>();
		ArrayList<RtreeNode> parentList = new ArrayList<RtreeNode>();

		ArrayList<IndexablePoint> ptList = new ArrayList<IndexablePoint>();
		for (int i = 0; i < num; i++)
		{
			ptList.add(_elementList.get(i));
		}

		/*************** Sort the Zorder directly by the coordinates of queries ****************/
		//	rtree_dim = dim;
		//	sort(ptList, ptList + num, compPtByZorder);
		//sort(ptList, ptList + num, new PointZorderSorter(dim));
		Collections.sort(ptList, new Comparator<IndexablePoint>(){
			public int compare(IndexablePoint p1, IndexablePoint p2){
				int pos = 0;
				int curMax = 0;
				int temp = 0;
				for(int i = 0; i < dim; i++){
					temp = p1.coords[i] ^ p2.coords[i];
					if(curMax < temp && curMax < (temp ^ curMax)){
						pos = i;
						curMax = temp;
					}
				}
				return p2.coords[pos] - p1.coords[pos];
			}
		});

		int leafNum = (int) Math.ceil(num * 1.0 / leafCapacity);
		
		int cnt = 0;
		int tempNum = leafCapacity;
		// Construct the leaf level.
		for (int i = 0; i < leafNum; i++)
		{
			//treeNodeList.set(i, new RtreeNode());
			RtreeNode tmpNode = new RtreeNode();
			//treeNodeList.get(i).initialize(true, dim, branchFactor, leafCapacity);
			if (num < cnt + leafCapacity)
			{
				tempNum = num - cnt;
			}
			for (int j = 0; j < tempNum; j++)
			{

				//treeNodeList.get(i).addElement(ptList.get(cnt), dim);
			    tmpNode.addElement(ptList.get(cnt), _dim);
				cnt++;
			}
			treeNodeList.add(tmpNode);
		}

		int level = 0;
		int treeNodeNum = 0;
		int parentNum = 0;
		int rest = 0;
		int index = 0;

		while (treeNodeList.size() > 1)
		{

			treeNodeNum = treeNodeList.size();
			parentNum = (int) Math.ceil(treeNodeNum * 1.0 / branchFactor);

			// The size of the last group of tree nodes.
			rest = treeNodeNum - branchFactor * (treeNodeNum / branchFactor);

			for (int i = 0; i < treeNodeNum - rest; i += branchFactor)
			{
				index = i / branchFactor;
				//parentList.set(index, new RtreeNode());
				RtreeNode tmpNode = new RtreeNode();
				//parentList.get(index).initialize(false, dim, branchFactor, leafCapacity);

				for (int j = 0; j < branchFactor; ++j)
				{
					tmpNode.addChildNode(treeNodeList.get(i + j), dim);
				}
				parentList.add(tmpNode);
			}

			if (rest > 0)
			{
				// Add the rest tree nodes to parent.
				//parentList.set(parentNum - 1, new RtreeNode());
			    RtreeNode tmpNode = new RtreeNode();
				//parentList.get(parentNum - 1).initialize(false, dim, branchFactor, leafCapacity);
				for (int i = treeNodeNum - rest; i < treeNodeNum; ++i)
				{
					tmpNode.addChildNode(treeNodeList.get(i), dim);
				}
				parentList.add(tmpNode);
			}
			//tangible.VectorHelper.swap(treeNodeList, parentList);
			treeNodeList.clear();
			treeNodeList.addAll(parentList);
			parentList.clear();
			level++;
		}
		rootLev = level;
		root = treeNodeList.get(0);

		return root;
	}


	public final int insertNode(RtreeNode _toInsNode, int _targetLevel)
	{
		if (root == null)
		{
			if (dim == -1)
			{
				System.out.print("Error in Rtree::insertNode: dim = -1! Please initialize the rtree first!\n");
				return 1;
			}

			root = _toInsNode;
			return 0;
		}
		if (rootLev == _targetLevel)
		{
			// In this case, we need to create a new root.
			RtreeNode u = new RtreeNode();
//			u.initialize(false, dim, branchFactor, leafCapacity);
			u.addChildNode(this.root, dim);
			u.addChildNode(_toInsNode, dim);

			this.root = u;
			rootLev = _targetLevel + 1;
		}
		else
		{
			this.insertNode_SubRoutine(this.root, rootLev, _toInsNode, _targetLevel);
		}
		return 0;
	}
	public final int insertElement(IndexablePoint _elem)
	{
		if (root == null)
		{
			if (dim == -1)
			{
				System.out.print("Error in Rtree::insertElement: dim = -1! Please initialize the rtree first!\n");
				return 1;
			}
			root = new RtreeNode();
//			root.initialize(true, dim, branchFactor, leafCapacity);
			root.addElement(_elem, dim);
			return 0;
		}
		if (rootLev == 0)
		{
			if (root.getListLeafSize() + 1 > leafCapacity)
			{
				// the root will overflow after inserting this element.
				RtreeNode u = root.splitLeaf(_elem, dim);
				RtreeNode r = new RtreeNode();
//				r.initialize(false, dim, branchFactor, leafCapacity);
				r.addChildNode(root, dim);
				r.addChildNode(u, dim);

				root = r;
				rootLev++;
			}
			else
			{
				root.addElement(_elem, dim);
			}
			return 0;
		}

		IndexablePoint p = _elem;
		Rectangle rect = new Rectangle();
		rect.valueList = new int[2 * dim];
		for (int i = 0; i < dim; i++)
		{
			rect.valueList[i] = p.coords[i];
			rect.valueList[i + dim] = p.coords[i];
		}
		int rtn = this.insertElement_SubRoutine(root, rootLev, _elem, rect);

		// release space
		rect.releaseSpace();
		return rtn;
	}
	public final int deleteElement(IndexablePoint _elem)
	{
		if (_elem == null || root == null)
		{
			System.out.print("Error in Rtree::deleteElement: _elem or root is NULL!\n");
			return 1;
		}

		IndexablePoint p = _elem;
		Rectangle rect = new Rectangle();
		rect.setByPoint(p.coords, dim);
		RtreeNode targetLeaf = this.findLeafForDel_SubRoutine(root, rootLev, _elem, rect);

		// === Codes for debug.
		if (targetLeaf == null)
		{
			System.out.print("Error in Rtree::deleteElement: _elem deletion failed!\n");
		}
		// ===

		rect.releaseSpace();

		if (targetLeaf != null)
		{
			if (targetLeaf.deleteElement(_elem) < leafCapacity / Global.FanoutRatio)
			{
				// targetLeaf underflow after deleting _elem.
				this.underflowHandler(targetLeaf, 0);
			}
			return 0;
		}
		return 1;
	}

	public final void findKNN(IndexablePoint _elem, int _topK, ArrayList<IndexablePoint> _targetPlace, int _resultCnt)
	{
		if (root == null)
		{
			_resultCnt = 0;
			return;
		}
		PriorityQueue<heapElement> minHeap = new PriorityQueue<heapElement>((int)Global.MYINFTY, new Comparator<heapElement>(){
			public int compare(heapElement e1, heapElement e2){
				return (int) (e1.dist - e2.dist);
			}
		});
		_resultCnt = 0;

		IndexablePoint pt = _elem;
		heapElement e = new heapElement();
		e.lev = rootLev;
		e.ptr = root;
		e.pt = null;
		e.dist = root.mbr.computeMinDistToPt(pt.coords, dim);
		minHeap.offer(e);

		int temp_num = 0;
		RtreeNode temp_ptr = null;
		ArrayList<RtreeNode> temp_list = new ArrayList<RtreeNode>();
		ArrayList<IndexablePoint> tempLeafList = new ArrayList<IndexablePoint>();
		heapElement min_e = new heapElement();

		while (minHeap.size() > 0 && _resultCnt < _topK)
		{
			min_e = minHeap.poll();
			//minHeap.pop();

			if (min_e.lev == -1)
			{
				// this is a leaf entry, i.e., a point.
				_targetPlace.add(min_e.pt);
				_resultCnt = _targetPlace.size();
			}
			else
			{
				// this is a tree node.

				// === Codes for debug.
//				GlobalMembers.VisitedNodeNum++;
//				GlobalMembers.VisitedNodeNumList[min_e.lev]++;
				// ===

				temp_ptr = (RtreeNode)min_e.ptr;
				temp_num = temp_ptr.getListChildSize();
				temp_list = temp_ptr.getListChildNode();
				tempLeafList = temp_ptr.getListPtr();
				e.lev = min_e.lev - 1;
				if (min_e.lev > 0)
				{
					for (int i = 0; i < temp_num; i++)
					{
						e.ptr = temp_list.get(i);
						e.dist = ((RtreeNode)(e.ptr)).mbr.computeMinDistToPt(pt.coords, dim);
						minHeap.offer(e);
					}
				}
				else
				{
					for (int i = 0; i < temp_num; i++)
					{
						e.pt = tempLeafList.get(i);
						// Add points to heap according to selection criteria.
						e.dist = pt.computeDist(((IndexablePoint)e.pt).coords, dim);
						//					e.dist = pt->getDist((IndexablePoint*) (e.ptr), dim);
						minHeap.offer(e);
					}
				}
			}
		}
	}
	public final IndexablePoint findNN(IndexablePoint _elem)
	{
		ArrayList<IndexablePoint> target = new ArrayList<IndexablePoint>();
		int cnt = 0;
		int tempRef_cnt = 0;
		findKNN(_elem, 1, target, tempRef_cnt);
		cnt = tempRef_cnt;
		if(cnt != 1)
			System.out.println("Warning: the findNN cann't work normally!");
		return target.get(0);
	}

	/*
	*  Return the number of points in ball B(_elem, _radius).
	*/

	/*
	*  Return the number of points in ball B(_elem, _radius).
	*/
	public final int rangeCntQuery(IndexablePoint _elem, double _radius)
	{
		if (root == null)
		{
			System.out.print("Warning in Rtree::rangeQuery: The root is NULL!\n");
			return 0;
		}
		ArrayList<RtreeNode> childList = new ArrayList<RtreeNode>();
		int childNum = 0;
		ArrayList<IndexablePoint> ptList = new ArrayList<IndexablePoint>();

		IndexablePoint q = _elem;
		int ptNum = 0;
		int state = 0;
		double sqrRadius = _radius * _radius;
		double sqrDist = 0;
		int cnt = 0;

		Queue<QueElement> que = new LinkedList<QueElement>();

		QueElement curElement = new QueElement();
		curElement.node = root;
		curElement.lev = rootLev;
		que.add(curElement);
		while (!que.isEmpty())
		{
			curElement = que.peek();
			que.poll();
			if (curElement.lev == 0)
			{
				// the current node is a leaf node.
				ptList = curElement.node.getListPtr();
				ptNum = curElement.node.getListLeafSize();
				for (int i = 0; i < ptNum; i++)
				{
					sqrDist = q.computeSqrDist(ptList.get(i).coords, dim);
					if (sqrDist <= sqrRadius)
					{
						cnt++;
					}
				}
			}
			else
			{
				// the current node is an internal node.
				childList = curElement.node.getListChildNode();
				childNum = curElement.node.getListChildSize();
				for (int i = 0; i < childNum; i++)
				{
					state = childList.get(i).mbr.stateWithSphere(q.coords, _radius, dim);
					if (state == 0)
					{
						// intersect, then add this node to queue.
					    QueElement temp = new QueElement();
						temp.node = childList.get(i);
						temp.lev = curElement.lev - 1;
						que.add(temp);
					}
					else if (state == 1)
					{
						// fully inside, count the total number of points in the subtree of this node.
						cnt += countElementInSubtree(childList.get(i), curElement.lev - 1);
					}
				}
			}
		}
		return cnt;
	}



	public final int rangeQuery(IndexablePoint _elem, double _radius, ArrayList<IndexablePoint> _targetPlace)
	{
		if (root == null)
		{
			System.out.print("Warning in Rtree::rangeQuery: The root is NULL!\n");
			return 0;
		}
		// Clear the targetPlace.
		_targetPlace.clear();

		ArrayList<RtreeNode> childList = new ArrayList<RtreeNode>();
		int childNum = 0;
		ArrayList<IndexablePoint> ptList = new ArrayList<IndexablePoint>();
		int ptNum = 0;
		int state = 0;
		double sqrRadius = _radius * _radius;
		double sqrDist = 0;

		Queue<QueElement> que = new LinkedList<QueElement>();
		QueElement temp = new QueElement();
		QueElement curElement = new QueElement();
		curElement.node = root;
		curElement.lev = rootLev;
		que.offer(curElement);
		while (!que.isEmpty())
		{
			curElement = que.peek();
			que.poll();

			if (curElement.lev == 0)
			{
				// the current node is a leaf node.
				ptList = curElement.node.getListPtr();
				ptNum = curElement.node.getListLeafSize();
				for (int i = 0; i < ptNum; i++)
				{
					sqrDist = _elem.computeSqrDist(ptList.get(i).coords, dim);
					if (sqrDist <= sqrRadius)
					{
						_targetPlace.add(ptList.get(i));
					}
				}
			}
			else
			{
				// the current node is an internal node.
				childList = curElement.node.getListChildNode();
				childNum = curElement.node.getListChildSize();
				for (int i = 0; i < childNum; i++)
				{
					state = childList.get(i).mbr.stateWithSphere(_elem.coords, _radius, dim);
					if (state != -1)
					{
						// intersect or fully inside, then add this node to queue.
						temp.node = childList.get(i);
						temp.lev = curElement.lev - 1;
						que.offer(temp);
					}
				}
			}
		}
		return 0;
	}

	/*
	*  Return the farthest point in B(_q, _radius).
	*/

	/*
	*  Return the farthest point in B(_q, _radius).
	*/
	public final ArrayList<IndexablePoint> rangeFarthestQuery(IndexablePoint _elem, double _radius)
	{
		if (root == null)
		{
			System.out.print("Warning in Rtree::rangeFarthestQuery: The root is NULL!\n");
			return null;
		}

		ArrayList<IndexablePoint> targetPoint = new ArrayList<IndexablePoint>();

		IndexablePoint _q = _elem;

		ArrayList<RtreeNode> childList = new ArrayList<RtreeNode>();
		int childNum = 0;
		ArrayList<IndexablePoint> ptList = new ArrayList<IndexablePoint>();
		int ptNum = 0;
		int state = 0;
		double sqrRadius = _radius * _radius;
		double sqrDist = 0;
		double curMax = 0;

		Queue<QueElement> que = new LinkedList<QueElement>();
		QueElement temp = new QueElement();
		QueElement curElement = new QueElement();
		curElement.node = root;
		curElement.lev = rootLev;
		que.offer(curElement);
		while (!que.isEmpty())
		{
			curElement = que.peek();
			que.poll();
			if (curElement.lev == 0)
			{
				// the current node is a leaf node.F
				ptList = curElement.node.getListPtr();
				ptNum = curElement.node.getListLeafSize();
				for (int i = 0; i < ptNum; i++)
				{
					sqrDist = _q.computeSqrDist(ptList.get(i).coords, dim);
					if (sqrDist <= sqrRadius  && curMax < sqrDist)
					{
						targetPoint.add(ptList.get(i));
						curMax = sqrDist;
					}
				}
			}
			else
			{
				// the current node is an internal node.
				childList = curElement.node.getListChildNode();
				childNum = curElement.node.getListChildSize();
				for (int i = 0; i < childNum; i++)
				{
					state = childList.get(i).mbr.stateWithSphere(_q.coords, _radius, dim);
					if (state != -1)
					{
						// intersect or fully inside, then add this node to queue.
						temp.node = childList.get(i);
						temp.lev = curElement.lev - 1;
						que.offer(temp);
					}
				}
			}
		}
		return targetPoint;
	}

	public final int getDim()
	{
		return dim;
	}
	public final boolean isValid()
	{
		return root != null;
	}

	// Functions for debug.
	public final int getHeight()
	{
		return rootLev + 1;
	}
}

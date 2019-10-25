// Peter Kwak
// Dr. Anjum Chida
// CS 3345.001
// Fall 2019
// Project 4: Implement a simplified red-black tree

/**
 * The task of this project is to implement in Java a red-black tree data structure. 
 * However, the tree is a simplified version – it only supports insertion, not deletion.
 * @author KwakP
 *
 */
public class RedBlackTree<E extends Comparable<E>>
{
	private static final boolean RED = false;
	private static final boolean BLACK = true;
	private Node<E> root;
	
	/**
	 *  Returns a string representing the pre-order traversal of this tree
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		toString(root, sb);
		return sb.toString().trim();
	}
	
	private void toString(Node<E> curNode, StringBuffer sb)
	{
		if (curNode != null)
		{
			// preorder traversal
			sb.append(curNode.toString());
			sb.append(" ");
			toString(curNode.leftChild, sb);
			toString(curNode.rightChild, sb);
		}
	}
	
	/**
	 *  returns whether the tree contains any element that compares equal to the 
	 *  given object using the compareTo method of the object.
	 * @param object object to search for
	 * @return	true is object is found
	 */
	public boolean contains(Comparable<E> object)
	{
		// Do not throw an error for null objects
		if (object == null)
		{
			return false;
		}
		
		// Call recursive helper method to find object
		return contains(root, object);
	}
	
	private boolean contains(Node<E> curNode, Comparable<E> object)
	{
		// If we reach a null node, stop the search
		if (curNode == null)
		{
			return false;
		}
		
		int c = object.compareTo(curNode.element);
		if (c > 0)
		{
			// The search object is greater than curNode, so traverse right
			return contains(curNode.rightChild, object);
		}
		else if (c < 0)
		{
			// The search object is less than curNode, so traverse left
			return contains(curNode.leftChild, object);
		}
		
		// Otherwise, the curNode matches the object
		return true;
	}
	
	/**
	 * inserts the given element into the tree at the correct position, 
	 * and then rebalances the tree if necessary
	 * @param element the element to insert
	 * @return true if the tree was changed
	 * @throws NullPointerException
	 */
	public boolean insert(E element) throws NullPointerException
	{
		if (element == null)
		{
			throw new NullPointerException();
		}
		
		// Check whether the element already exists in the tree
		if (contains(element))
		{
			return false;
		}
		
		if (root == null)
		{
			root = new Node<E>(element, null, BLACK);
		}
		else
		{
			// Call helper method to insert the element
			insert(root, null, element);
			//setColor(root, BLACK);
		}
		
		return true;
	}
	
	private void insert(Node<E> curNode, Node<E> parent, E element)
	{
		if (curNode == null)
		{
			return;
		}
		
		if (element.compareTo(curNode.element) > 0)
		{
			// The element to insert is greater than curNode so traverse right
			if (curNode.rightChild == null)
			{
				curNode.rightChild = new Node<E>(element, curNode, RED);
				curNode.rightChild.parent = curNode;
				balance(curNode.rightChild);
			}
			else
			{
				insert(curNode.rightChild, curNode, element);
			}
		}
		else
		{
			// Otherwise, the element to insert is less than curNode so traverse left
			if (curNode.leftChild == null)
			{
				curNode.leftChild = new Node<E>(element, curNode, RED);
				curNode.leftChild.parent = curNode;
				balance(curNode.leftChild);
			}
			else
			{
				insert(curNode.leftChild, curNode, element);
			}
		}
	}
	
	/**
	 * Balances the tree at the grandchild and recursively balances ancestors
	 * @param gc grandchild node
	 */
	private void balance(Node<E> gc)
	{
		if (gc == null)
		{
			return;
		}
		
		// Check whether the current node is the root
		if (gc == root)
		{
			// The root is always black
			setColor(gc, BLACK);
		}
		else if (gc.parent != root) // If the cur node is the child of the root, no more balancing is needed
		{
			Node<E> parent = gc.parent;
			// Check whether there is a need to rebalance (double red edges)
			if (gc.color == RED && parent.color == RED)
			{
				// Since we are depth at least 2, the grandparent exists and the uncle exists (may be null)
				Node<E> uncle = getUncle(gc);
				Node<E> gp = parent.parent;
				
				if (isRightChild(gc) && isRightChild(parent)) // right-right
				{
					if (color(uncle) == RED) // recolor
					{
						setColor(uncle, BLACK);
						setColor(gp, RED);
						setColor(parent, BLACK);
						
						// percolate the balancing upwards
						balance(gp);
					}
					else // restructure
					{
						rotateLeft(gp);
						setColor(gp, RED);
						setColor(parent, BLACK);
					}
				}
				else if (!isRightChild(gc) && !isRightChild(parent)) // left-left
				{
					if (color(uncle) == RED) // recolor
					{
						setColor(uncle, BLACK);
						setColor(gp, RED);
						setColor(parent, BLACK);
					
						// percolate the balancing upwards
						balance(gp);
					}
					else // restructure
					{
						rotateRight(gp);
						setColor(gp, RED);
						setColor(parent, BLACK);
					}
				}
				else if (!isRightChild(gc) && isRightChild(parent)) // right-left
				{
					if (color(uncle) == RED) // recolor
					{
						setColor(uncle, BLACK);
						setColor(gp, RED);
						setColor(parent, BLACK);
						
						// percolate the balancing upwards
						balance(gp);
					}
					else // restructure
					{
						// do double rotate
						rotateRight(parent);
						rotateLeft(gp);
						// recolor
						setColor(gp, RED);
						setColor(gc, BLACK);
					}
				}
				else if (isRightChild(gc) && !isRightChild(parent)) // left-right
				{
					if (color(uncle) == RED) // recolor
					{
						setColor(uncle, BLACK);
						setColor(gp, RED);
						setColor(parent, BLACK);
						
						// percolate the balancing upwards
						balance(gp);
					}
					else // restructure
					{
						// do double rotate
						rotateLeft(parent);
						rotateRight(gp);
						// recolor
						setColor(gp, RED);
						setColor(gc, BLACK);
					}
				}
			}
		}
	}
	
	private void rotateLeft(Node<E> gp)
	{
		// restructure children pointers
		Node<E> rChild = gp.rightChild;
		gp.rightChild = rChild.leftChild;
		rChild.leftChild = gp;
		if (gp.parent != null)
		{
			if (isRightChild(gp))
			{
				setRight(gp.parent, rChild);
			}
			else
			{
				setLeft(gp.parent, rChild);
			}
		}
		
		// restructure parent pointers
		rChild.parent = gp.parent;
		gp.parent = rChild;
		setParent(gp.rightChild, gp);
		
		// udpate the root
		if (gp == root)
			root = rChild;
	}
	
	private void rotateRight(Node<E> gp)
	{
		// restructure children pointers
		Node<E> lChild = gp.leftChild;
		gp.leftChild = lChild.rightChild;
		lChild.rightChild = gp;
		if (gp.parent != null)
		{
			if (isRightChild(gp))
			{
				setRight(gp.parent, lChild);
			}
			else
			{
				setLeft(gp.parent, lChild);
			}
		}
		
		// restructure parent pointers
		lChild.parent = gp.parent;
		gp.parent = lChild;
		setParent(gp.leftChild, gp);
		
		// update the root
		if (gp == root)
			root = lChild;
	}
	
	private Node<E> getUncle(Node<E> x)
	{
		Node<E> parent = x.parent;
		if (isRightChild(parent))
		{
			return parent.parent.leftChild;
		}
		return parent.parent.rightChild;
	}
	
	private boolean isRightChild(Node<E> x)
	{
		return x.parent.rightChild == x;
	}
	
	private void setRight(Node<E> x, Node<E> right)
	{
		if (x != null)
			x.rightChild = right;
	}
	
	private void setLeft(Node<E> x, Node<E> left)
	{
		if (x != null)
			x.leftChild = left;
	}
	
	private void setParent(Node<E> x, Node<E> p)
	{
		if (x != null)
			x.parent = p;
	}
	
	private boolean color(Node<E> x)
	{
		// Null nodes are black
		if (x == null)
			return BLACK;
		return x.color;
	}
	
	private void setColor(Node<E> x, boolean color)
	{
		if (x != null)
			x.color = color;
	}
	
	/**
	 * DEBUG. Determines whether the current state of the tree is valid. Should always return true.
	 * @return true if there are no double red edges and the black depth of null nodes is the same
	 */
	public boolean validate()
	{
		// the root must be black
		if (color(root) == RED)
			return false;
		return validate(root, 0) != -1;
	}
	
	private int validate(Node<E> curNode, int blackDepth)
	{
		// return the black depth at null nodes
		if (curNode == null)
		{
			return blackDepth;
		}
		
		// check for double red
		if (color(curNode) == RED && (color(curNode.rightChild) == RED || color(curNode.leftChild) == RED))
		{
			return -1;
		}
		
		// Recursive check subtrees
		int newBlackDepth = blackDepth + (color(curNode) == BLACK ? 1 : 0);
		int depth1 = validate(curNode.leftChild, newBlackDepth);
		int depth2 = validate(curNode.rightChild, newBlackDepth);
		
		// If depths are unequal, this is invalid
		if (depth1 != depth2)
			return -1;
		return depth1;
	}
	
	private static class Node<E>
	{
		E element;
		Node<E> leftChild;
		Node<E> rightChild;
		Node<E> parent;
		boolean color;
		
		public Node(E element, Node<E> parent, boolean color)
		{
			this.element = element;
			this.parent = parent;
			this.color = color;
		}
		
		public String toString()
		{
			if (color == RED)
				return "*" + element.toString();
			else
				return element.toString();
		}
	}
}

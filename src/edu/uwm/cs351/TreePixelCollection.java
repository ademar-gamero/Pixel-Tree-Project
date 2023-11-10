package edu.uwm.cs351;

import java.awt.Point;
import java.util.AbstractCollection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

//import edu.uwm.cs351.DynamicRaster.Node;

//import edu.uwm.cs351.DynamicRaster.Node;


/**
 * An extensible Raster that satisfies {@link java.util.Collection} 
 * and uses binary search trees internally.
 */
public class TreePixelCollection extends AbstractCollection<Pixel> implements Cloneable
	// TODO: We need to implement something so that super.clone will work.
{
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);

	private static boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	private static class Node {
		Pixel data;
		Node left, right;
		Node next;
		Node(Pixel a) { data = a; }
		
		@Override //implementation
		public String toString() {
			return "Node(" + data + ")"; 
		}
	}
	// TODO: You will need to add a 'next" field to the node class
	
	// TODO: Declare the private fields needed given the BST data structure
	Node dummy;
	int size;
	int version;
	// TODO: define private getter for model field "root"
	private Node getRoot() {
		return dummy.right;
	}

	/** Compare two points in column-major order.
	 * @param p1 first point, must not be null
	 * @param p2 second point, must not be null
	 * @return whether the first point comes before the second in
	 * column-major order (first column, then row).
	 */
	private static boolean comesBefore(Point p1, Point p2) {
		return p1.x < p2.x || (p1.x == p2.x && p1.y < p2.y);		
	}
	
	/**
	 * Return the number of nodes in a subtree that has no cycles.
	 * @param r root of the subtree to count nodes in, may be null
	 * @return number of nodes in subtree
	 */
	private static int countNodes(Node r) {
		if (r == null) return 0;
		return 1 + countNodes(r.left) + countNodes(r.right);
	}
	
	/**
	 * Find the node that has the point (if acceptEqual) or the first thing
	 * after it.  Return that node.  Return the alternate if everything in the subtree
	 * comes before the given point.
	 * @param r subtree to look into, may be null
	 * @param target point to look for, must not be null
	 * @param acceptEqual whether we accept something with this point.  Otherwise, only
	 * Pixels after the point are accepted.
	 * @param alt what to return if no node in subtree is acceptable.
	 * @return node that has the first element equal (if acceptEqual) or after
	 * the point.
	 */
	private static Node nextInTree(Node r, Point target, boolean acceptEqual, Node alt) {
		if (r == null) return alt;
		Point p = r.data.loc();
		if (p.equals(target) && acceptEqual) return r;
		if (comesBefore(target, p)) return nextInTree(r.left, target, acceptEqual, r);
		return nextInTree(r.right, target, acceptEqual, alt);
	}

	/**
	 * Return whether all the data in nodes in the subtree are non-null
	 * and in the given range, and also in their respective subranges.
	 * If there is a problem, one problem should be reported.
	 * @param r root of subtree to check, may be null
	 * @param lo exclusive lower bound, may be null (no lower bound)
	 * @param hi exclusive upper bound, may be null (no upper bound)
	 * @return whether any problems were found.
	 * If a problem was found, it has been reported.
	 */
	private static boolean allInRange(Node r, Point lo, Point hi) {
		if (r == null) return true;
		if (r.data == null) return report("found null data in tree");
		Point pt = r.data.loc();
		if (lo != null && !comesBefore(lo, pt)) return report("Out of bound: " + r.data + " is not >= " + lo);
		if (hi != null && !comesBefore(pt, hi)) return report("Out of bound: " + r.data + " is not < " + hi);
		return allInRange(r.left, lo, pt) && allInRange(r.right, pt, hi);
	}
	
	private static Node firstInTree(Node r) {
		Node first = r;
		for(Node s = r; s != null; s = s.left) {
			first = s;
		}
		return first; // TODO: non-recursive is fine
	}
	
	private boolean wellFormed() {
		// TODO: Read Homework description
		
		//1. check dummy
		if(dummy == null) return report("dummy can not be null");
		if(dummy.data != null) return report("dummy can not be null");
		if(dummy.left != null) return report ("dummy left node is not null");
		
		//2.check range
		if(allInRange(getRoot(),null,null)==false) return false;
		
		//3. check size
		int s = countNodes(getRoot());
		if (s != size) return report("our size is " + size + " but our actual size is " + s);
		
		//tortoise and hare cycle check
		if (dummy.next != null) {
			Node slow = dummy.next;
			Node fast = dummy.next.next;
			while (fast != null) {
				if (slow == fast) return report("Found cycle in list");
				slow = slow.next;
				fast = fast.next;
				if (fast != null) fast = fast.next;
			}
		}
		
		// linked list size check
		int count = 0;
		if(dummy.next != null) {
		for(Node start = dummy.next; start != null;start = start.next) {
			if(start != null)++count;
			}
		}
		if (count != size)return report("linked list size incorrect");
		
		
		
		//3. Next pointer
	
		if(dummy.next != firstInTree(getRoot()))return report("dummy.next does not equal first in tree");
		if(dummy.next != null) {
		for(Node start = dummy.next; start.next != null; start= start.next) {
			Node after = nextInTree(getRoot(),start.data.loc(),false,null);
			if (!start.next.equals(after))return report("next pointer is incorrect");
			}
		}
		
		// If no problems discovered, return true
		return true;
	}

	// This is only for testing the invariant.  Do not change!
	private TreePixelCollection(boolean testInvariant) { }

	/**
	 * Create an empty raster.
	 */
	public TreePixelCollection() {
		this.dummy = new Node(null);
		this.size = 0;
		this.version = 0;
		// TODO: Implement the main constructor
	}

	/** Get a pixel from the raster.
	 * @param x pixel from the left (zero based), must not be negative
	 * @param y pixel from the top (zero based), must not be negative
	 * @return the pixel at x,y, or null if no pixel.
	 */
	public Pixel getPixel(int x, int y) {
		assert wellFormed():"invariant broken before getPixel";
		if(x < 0)throw new IllegalArgumentException("x cant be negative");
		if(y < 0)throw new IllegalArgumentException("y cant be negative");
		Point p = new Point(x,y);
		Node n = nextInTree(getRoot(),p,true,null);
		if(n != null && n.data.loc().equals(p))return n.data;
		// TODO: Copy from Homework #8, but use root model field
		return null;
	}

	/**
	 * Insert a node into the subtree and return the (modified) subtree.
	 * @param r root of subtree, may be null
	 * @param p pixel to add to tree, must not be null and must not be in tree
	 * @param before the last node before the ones in the subtree, never null
	 * @return root of new subtree
	 */
	private Node doAdd(Node r, Pixel p, Node before) {
		// TODO: recommended
		// Very similar to the recursive doAdd done in lecture,
		// but we add the "before" parameter to recursive calls,
		// and then use it when we hit a null: "before" will be the node
		// before where we need to be in the linked list.
		if(r == null) {
			r = new Node(p);
			r.next = before.next;
			before.next = r;
			if (before.equals(dummy)){
				Node roo = before.right;
				roo.left = r;
			}
			else if(comesBefore(before.data.loc(),r.data.loc())) {
				before.right = r;
			}
			else {
				before.left = r;
			}
			return r;
		}
		if(comesBefore(p.loc(),r.data.loc())== true) {
			r.left = doAdd(r.left, p,before);
		}
		else {
			r.right = doAdd(r.right,p,r);
		}
		
		return r;
	}
	
	@Override // implementation
	/**
	 * Set a pixel in the raster.  Return whether a change was made.
	 * If a pixel with the same coordinate was in the raster,
	 * then the new pixel replaces this one.
	 * @param p pixel to add, must not be null
	 * @return whether a change was made to a pixel.
	 */
	public boolean add(Pixel element)
	{
		assert wellFormed() : "invariant failed at start of add";
		boolean result = true;
		if(element.loc().x < 0|| element.loc().y < 0)throw new IllegalArgumentException("cant add a negative loc to the raster");
		Node n = nextInTree(getRoot(),element.loc(),true,null);
		if(n != null) {
		if(n.data.color().equals(element.color())&& n.data.loc().equals(element.loc())) {
			assert wellFormed() : "invariant failed at end of add";
			return false;
		}
			if(n.data.loc().equals(element.loc())) {
			n.data = element;
			assert wellFormed() : "invariant failed at end of add";
			return true;
			}
		}
		
		if (getRoot() != null) {
		dummy.right = doAdd(getRoot(), element, dummy);
		}
		else {
			Node s = new Node(element);
			dummy.right = s;
			dummy.next = s;
		}
		size++;
		version++;
		// TODO: First see if there is a node already with same point,
		// otherwise use the helper method
		assert wellFormed() : "invariant failed at end of add";
		return result;
	}

	/**
	 * Remove the node from the BST that has a pixel with the given point.
	 * This helper method will update size and version if a node is removed,
	 * otherwise not.
	 * @param r root of subtree to remove from, may be null
	 * @param pt point to look for, must not be null
	 * @param before last node before all nodes in subtree, must not be null
	 * @return new subtree (without node with given point), may be null
	 */
	private Node doRemove(Node r, Point pt, Node before) {
		
		if(r == null)return r;
		
		if(r.data.loc().equals(pt)) {
			
			if(r.right == null && r.left == null) {
				before.next = r.next;
				r = null;
				return r;
			}
			if(r.right == null) {
				
				r = r.left;
				r.next = r.next.next;
				return r;
			}
			if(r.left == null) {
				before.next = r.next;
				r = r.right;
				
				return r;
			}
			Node s =r.right;
			while(s.left != null) {
				s = s.left;	
			}
			r.data = s.data;
			r.right = doRemove(r.right,s.data.loc(), r);
		}
		
		if(comesBefore(pt,r.data.loc())== true) {
			r.left = doRemove(r.left, pt,before);
		}
		else {
			r.right = doRemove(r.right,pt,r);
		}
		
		
		// TODO: implement this helper method
//		Node l = r.left;
//		Node right = r.right;
//		Node nxt = nextInTree(getRoot(),pt,false,null);
//		before.next = r.next;
//		r = nxt;
//		if(r!= null) {
//		r.right = right;
//		r.left = r.left;
//		}
//		nxt = null;
		return r;
	}
	
	/**
	 * Remove the pixel, if any, at the given coordinates.
	 * Returns whether there was a pixel to remove.
	 * @param x x-coordinate, must not be negative
	 * @param y y-coordinate, must not be negative
	 * @return whether anything was removed.
	 */
	public boolean clearAt(int x, int y) {
		assert wellFormed() : "invariant broken in clearAt";
		// conveniently getPixel checks the arguments for us.
		if (getPixel(x,y) == null) return false; 
		Point delete = new Point(x,y);
		//Node del = nextInTree(getRoot(),delete,true,null);
		
		dummy.right = doRemove(getRoot(),delete,dummy);
		size--;
		version++;
		// TODO: use helper method
		assert wellFormed() : "invariant broken by clearAt";
		return true;
	}

	// TODO: Some Collection overridings.
	// Make sure to comment reasons for any overrides.
	
	
	@Override //effeciency(no longer need a loop)
	public void clear() {
		assert wellFormed():"invariant broken before clear";
		size = 0;
		version++;
		dummy.right = null;
		dummy.next = null;
		assert wellFormed():"invariant broken in clear";
	} 
	
	/**
	 * Clone the given subtree
	 * @param r subtree to clone, may be null
	 * @return cloned subtree
	 */
	private Node doClone(Node r) {
		return null; // TODO: Similar to but easier than Homework #8
	}
	
	/**
	 * Link up the nodes in the subtree and return the first
	 * one (or the "after" if there are no nodes).
	 * This will set the "next" fields in all the nodes
	 * of the subtree.
	 * @param r subtree to work on
	 * @param after node closest after the subtree
	 * @return the first node in subtree (or the after node if none)
	 */
	private Node doLink(Node r, Node after) {
		return null; // TODO
	}
	
	@Override // decorate (we use the superclass implementation, but do more)
	public TreePixelCollection clone() {
	    assert wellFormed() : "Invariant broken in clone";
		TreePixelCollection result;
		try {
			result = (TreePixelCollection) super.clone();
		} catch(CloneNotSupportedException ex) {
			throw new IllegalStateException("did you forget to implement Cloneable?");
		}
		// TODO: Work to do.
		// 1. Create new tree (as in Homework #8)
		// 2. Link together all the nodes in the result.
		assert result.wellFormed() : "invariant faield for new clone";
		return result;
	}

	private class MyIterator implements Iterator<Pixel>
	{
		Node precursor;
		boolean hasCurrent;
		int colVersion;
		
		// TODO define getCursor() for model field 'cursor'
		private Node getCursor() {
			return precursor.next;
		}
		private boolean wellFormed() {
			// First check outer invariant, and if that fails don't proceed further
			if(TreePixelCollection.this.wellFormed()==false)return false;

			// Next, if the versions don't match, pretend there are no problems.
			
			if(colVersion != version) return true;
			//precursor check
			
			if (precursor == null)return report("precursor can not be null");
			if(precursor.data == null && !precursor.equals(dummy))return report("precusor data null but not dummy");
			if(precursor.data != null) {
			Node pre = nextInTree(getRoot(),precursor.data.loc(),true,null);
			if(pre == null ||(!pre.equals(precursor)))return report("cursor not in tree");
			}
			if (hasCurrent == true && precursor.next == null)return report("no current");
			// (Any problems could be due to being stale, which is not our fault.)
			// Then check the remaining parts of the invariant.  (See Homework description.)
			return true;
		}
		
		MyIterator(boolean unused) {} // do not change this iterator
		
		MyIterator() {
			colVersion = version;
			hasCurrent = false;
			precursor = dummy;
			// Implement this constructor.  Don't forget to assert the invariant
		}
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			assert wellFormed():"invariant broken before hasNext";
			if (colVersion != version) throw new ConcurrentModificationException("version and colVersion dont line up in hasNext()"); 
		
			if(precursor.next == null) {
				return false;
			}
			hasCurrent = true;
			return true;
		}
		@Override
		public Pixel next() {
			// TODO Auto-generated method stub
			assert wellFormed():"invariant broken before next";
			if(hasNext()==false)throw new NoSuchElementException("no current");
			precursor = getCursor();
			hasCurrent = false;
			return precursor.data;
		}
		
		
		@Override
		public void remove() {
			return;
		}
		// TODO iterator methods
	}

	/**
	 * Class for internal testing.
	 * Do not use in client/application code.
	 * Do not change anything in this class.
	 */
	public static class Spy {
		/**
		 * Return the sink for invariant error messages
		 * @return current reporter
		 */
		public Consumer<String> getReporter() {
			return reporter;
		}

		/**
		 * Change the sink for invariant error messages.
		 * @param r where to send invariant error messages.
		 */
		public void setReporter(Consumer<String> r) {
			reporter = r;
		}

		private static Pixel a = new Pixel(0, 0);
		
		/**
		 * Class of nods for testing purposes.
		 */		
		public class Node extends TreePixelCollection.Node {
			public Node(Pixel d, Node n1, Node n2, Node n3) {
				super(a);
				data = d;
				left = n1;
				right = n2;
				next = n3;
			}
			public void setLeft(Node l) {
				left = l;
			}
			public void setRight(Node r) {
				right = r;
			}
			public void setNext(Node n) {
				next = n;
			}
		}

		/**
		 * Create a node for testing.
		 * @param a pixel, may be null
		 * @param l left subtree, may be null
		 * @param r right subtree, may be null
		 * @param n next node, may be null
		 * @return newly created test node
		 */	
		public Node newNode(Pixel a, Node l, Node r, Node n) {
			return new Node(a, l, r, n);
		}

		/**
		 * Create an instance of the ADT with give data structure.
		 * This should only be used for testing.
		 * @param d data array
		 * @param s size
		 * @param v current version
		 * @return instance of the ADT with the given field values.
		 */
		public TreePixelCollection create(Node r, int s, int v) {
			TreePixelCollection result = new TreePixelCollection(false);
			result.dummy = r;
			result.size = s;
			result.version = v;
			return result;
		}

		/**
		 * Create an iterator for testing purposes.
		 * @param outer outer object to create iterator for
		 * @param p precursor of iterator
		 * @param c whether the iterator has a current
		 * @param cv version of collection this iterator is for
		 * @return iterator to the raster
		 */
		public Iterator<Pixel> newIterator(TreePixelCollection outer, Node p, boolean c, int cv) {
			MyIterator result = outer.new MyIterator(false);
			result.precursor = p;
			result.hasCurrent = c;
			result.colVersion = cv;
			return result;
		}
		
		/**
		 * Return whether the wellFormed routine returns true for the argument
		 * @param s transaction seq to check.
		 * @return
		 */
		public boolean wellFormed(TreePixelCollection s) {
			return s.wellFormed();
		}

		/**
		 * Return whether the wellFormed routine returns true for the argument
		 * @param s transaction seq to check.
		 * @return
		 */
		public boolean wellFormed(Iterator<Pixel> it) {
			MyIterator myit = (MyIterator)it;
			return myit.wellFormed();
		}

	}

	@Override
	public Iterator<Pixel> iterator() {
		// TODO Auto-generated method stub
		return new MyIterator();
	}
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}
}

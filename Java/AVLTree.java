//Written by Van Euclid Dy
//University of Utah CS

package AVLTree;

public class AVLTree <AnyType extends Comparable<? super AnyType>> {

	TreeNode root;

	class TreeNode<AnyType> { //Tree Node has a left, and right pointer w/ data and counter
		TreeNode left = null, right = null;
		AnyType data = null;
		int height = 0;

		/**
		 * TreeNode constructor
		 * @param data data to be stored
		 */
		TreeNode(AnyType data) {
			this.data = data;
		}
	}

	AVLTree() {
		root = null; 
	}

	void insert(AnyType data) {
		root = insert(root, data);
	}

	private TreeNode insert(TreeNode<AnyType> n, AnyType data) {
		if(n == null) 
			n = new TreeNode<AnyType>(data);
		else if(data.compareTo(n.data) < 0) { //compares size then moves left or right
			n.left = insert(n.left, data);
		}
		else if(data.compareTo(n.data) >= 0) {//Handles duplicate here, if duplicate go right.
			n.right = insert(n.right, data);
		}
		n = check_balance(n);
		return n;
	}

	void remove(AnyType data) {
		if (root == null) return;
		else root = remove(root, data);
	}

	private TreeNode remove(TreeNode<AnyType> n, AnyType data) {
		int c = data.compareTo(n.data);
		if (c < 0) {
			// parent.left = child (which may be different)
			n.left = remove(n.left, data);
		} else if (c > 0) {
			n.right = remove(n.right, data);
		} else if (c == 0) {
			if (n.left == null) {
				// no left child, return right child
				return n.right;
			} else if (n.right == null) {
				// no right child, return left child
				return n.left;
			} else {
				// two children, copy the minimum in the right subtree 
				// then set the right child to the return value of remove n.dat on the right subtree
				n.data = findMin(n.right).data;
				n.right = remove(n.right, n.data);
			}
		}
		n = check_balance(n);
		return n;
	}

	TreeNode<AnyType> findMin(TreeNode n) {
		if(n.left == null) return n;
		return findMin(n.left);
	}

	TreeNode check_balance(TreeNode<AnyType> n) {
		int nright, nleft;
		if(n.right == null) nright = -1;
		else nright = n.right.height;
		if(n.left == null) nleft = -1;
		else nleft = n.left.height;

		n.height = 1 + Math.max(nleft, nright);
		if(Math.abs(nleft - nright) > 1) {
			n = rotate(n, nleft, nright);
		}
		return n;
	}

	TreeNode rotate(TreeNode n, int nleft, int nright) {
		if(nleft - nright > 1 ) {
			if(n.left.left == null) nleft = -1;
			else nleft = n.left.left.height; 
			if(n.left.right == null) nright = -1;
			else nright = n.left.right.height;
			if(nright - nleft > 0){
				n = doubleRotateLeft(n);
				System.out.println(n.right.data);
				n.height++;
				n.left.height--;
				n.right.height-=2;
			}
			else {
				n = rotateLeft(n);
				n.right.height-=2;
			}
		}
		if(nright - nleft > 1) {
			if(n.right.left == null) nleft = -1;
			else nleft = n.right.left.height; 
			if(n.right.right == null) nright = -1;
			else nright = n.right.right.height;
			if(nleft - nright > 0) { 
				n = doubleRotateRight(n);
				n.height++;
				n.right.height--;
				n.left.height-=2;
			}
			else {
				n = rotateRight(n);
				n.left.height-=2;
			}
		}

		return n;
	}

	TreeNode rotateLeft(TreeNode n2) {
		TreeNode n1 = n2.left;
		n2.left = n1.right;
		n1.right = n2;
		return n1;
	}

	TreeNode rotateRight(TreeNode n1) {
		TreeNode n2 = n1.right;
		n1.right = n2.left;
		n2.left = n1;
		return n2;
	}

	TreeNode doubleRotateLeft(TreeNode n3) {
		n3.left = rotateRight(n3.left);
		return rotateLeft(n3);
	}

	TreeNode doubleRotateRight(TreeNode n1) {
		n1.right = rotateLeft( n1.right);
		return rotateRight(n1);
	}

	void levelOrder() 
	{ //gives list in order from top to bottom by level
		levelOrder(root);
	}

	void levelOrder(TreeNode<AnyType> n) {
		ListQue queue = new ListQue<TreeNode>();
		queue.enque(n);
		while(!queue.isEmpty()) {  //Traverses until the listqueue is empty
			TreeNode current = (TreeNode) queue.deque();
			if(current != null) {
				queue.enque(current.left);
				queue.enque(current.right);
				System.out.println(current.data + " " + current.height);
			}
		}
	}
}
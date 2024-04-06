package resources.BPTree;

import java.util.*;

public class TestBPTree {

	public static void main(String[] args) {
		BPTree<Integer> tree = new BPTree<Integer>(15);
		// Scanner sc = new Scanner(System.in);
		// int i=0;
		// while(i<9) 
		// {
		// 	int x = sc.nextInt();
		// 	if(x == -1)
		// 		break;
		// 	tree.insert(x, null);
		// 	i++;
		// 	System.out.println(tree.toString());
		// }
		// System.out.println(tree.insertRef(107, 15, "p", true));
		// tree.insert(107, new Ref("p0.ser", 0));
		// System.out.println(tree.insertRef(109, 15, "p", false));
		// tree.insert(109, tree.insertRef(109, 15, "p", false));
		// tree.insert(109, tree.insertRef(109, 15, "p", false));
		// tree.insert(110, tree.insertRef(110, 15, "p", false));
		// System.out.println("OVERFLOW" + tree.getLastLeaf().getOverflow(1));
		// tree.insert(3, new Ref("p1", 0));
		// tree.insert(3, new Ref("p1", 1));
		// System.out.println(tree.insertRef(2, 2, "p",false));
		// System.out.println(tree);
		// tree.insert(2, tree.insertRef(2, 2, "p",false));
		// System.out.println(tree.searchGreaterthan(2));
		// ArrayList x = new ArrayList();
		// x.add(new Ref("p0.ser", 1));
		// x.add(new Ref("p0.ser", 2));
		// x.add(new Ref("p0.ser", 3));
		// x.add(new Ref("p0.ser", 4));
		// x.add(new Ref("p1", 0));
		// x.add(new Ref("p1", 1));
		// x.add(new Ref("p2", 0));
		// System.out.println(tree.getLastLeaf().overflow);
		// System.out.println("OVERFLOW" + tree.getLastLeaf().getOverflow(0));
		// System.out.println("OVERFLOW" + tree.getLastLeaf().getOverflow(1));
		// System.out.println("OVERFLOW" + tree.getLastLeaf().getOverflow(2));
		// tree.updateRef(x, 0, tree.getLastLeaf());
		// System.out.println("OVERFLOW" + tree.getLastLeaf().getOverflow(0));
		// System.out.println("OVERFLOW" + tree.getLastLeaf().getOverflow(1));
		// System.out.println("OVERFLOW" + tree.getLastLeaf().getOverflow(2));
		// System.out.println(tree.getLastLeaf().overflow);
		//  System.out.println(tree);
//		tree.insert(100, tree.insertRef(100, 15, "p",false));
//		System.out.println(tree.getLastLeaf().overflow);
		tree.insert(1, new Ref("p0.ser", 0));
		tree.insert(17, new Ref("p0.ser", 1));
		tree.insert(17, new Ref("p0.ser", 2));
		tree.insert(18, new Ref("p0.ser", 3));
		tree.insert(19, new Ref("p0.ser", 4));
		tree.insert(20, new Ref("p0.ser", 5));
		tree.insert(21, new Ref("p0.ser", 6));
		tree.insert(22, new Ref("p0.ser", 7));
		tree.insert(23, new Ref("p0.ser", 8));
		tree.insert(23, new Ref("p0.ser", 9));
		tree.insert(25, new Ref("p0.ser", 10));
		ArrayList x = new ArrayList();
		x.add(new Ref("p0.ser", 100));
		//x.add(new Ref("p0.ser", 2));
		ArrayList y = new ArrayList();
//		y.add(new Ref("p0.ser", ));
		y.add(new Ref("beta3 17", 2));

// 		tree.insert(100, new Ref("p0.ser", 100));
// 		tree.updateRefNonKey(x, y);
// 		System.out.println(tree);

// 		// tree.delete(21, new Ref("p0.ser", 6));

// 		// tree.insert(4, new Ref());
// 		for (int i = 0; i < tree.getLastLeaf().numberOfKeys; i++) {
// 			System.err.println(tree.getLastLeaf().getRecord(i));
// 		}
// //		System.err.println(tree.searchDuplicates(17));
// //		Ref x = tree.searchDuplicates(17).get(1);
// //		System.out.println(x);
// 		tree.delete(200, new Ref("p0.ser", 8));
// 		System.err.println(tree);
// 		for (int i = 0; i < tree.getLastLeaf().numberOfKeys; i++) {
// 			System.err.println(tree.getLastLeaf().getRecord(i));
// 		}
// 		System.err.println("test itt" + tree.searchDuplicates(22));
// 		System.out.println("deletee" + tree.deleteAllRefs(null, 25));
// 		System.out.println("deletee" + tree.delete(23, new Ref("p0.ser", 9)));
// 		System.out.println("deletee" + tree.delete(23, new Ref("p0.ser", 8)));

// //		System.out.println(tree.insertRef(26, 10, "p", false));
// //		tree.insert(26, new Ref("p1.ser", 0));
// //		System.out.println(tree.insertRef(27, 10, "p", false));
// //		System.out.println(tree.searchDuplicates(3));
// //		tree.insert(1, null);
// //		tree.insert(10, new Ref("5", 33));
// //		tree.insert(2, null);
// //		tree.insert(11, new Ref("2", 33));
// //		tree.insert(3, null);
// //		tree.insert(4, new Ref("1", 2));
// //		tree.insert(4, new Ref("2", 2));
// //		tree.insert(4, new Ref("4", 4));
// //		tree.insert(8, new Ref("1", 33));
// //		tree.insert(5, new Ref("1", 39));
// //		tree.insert(7, null);
// 		// when we enter a new page we have to make h=0
// 		System.out.println(tree.toString());

// //		tree.printingRef(4);
// //		tree.delete(4, new Ref("4", 4));
// //		tree.delete(4, new Ref("2", 2));
// //		tree.delete(4, new Ref("1", 2));
// //		System.out.println(tree);
// //		tree.printingRef(4);
// //		tree.delete(3, null);
// //		System.out.println(tree);
// //		System.out.println(tree.searchGreaterthan(3));
// //		System.out.println(tree.searchGreaterEqual(5));
// 		// System.out.println(tree.insertRef(4));
// //		tree.insert(3, new Ref("1", 2));
// //		BPTreeLeafNode node = tree.searchGreaterthan(3);
// //		System.out.println(tree);
// //		ArrayList x = new ArrayList();
// //		x.add(new Ref("2", 2));
// //		x.add(new Ref("4", 4));
// //		x.add(new Ref("1", 33));
// //		Ref xx = node.getRecord(0);
// //		tree.updateRef(x, 0, node);
// //		System.out.println("the ref is" + xx);
// //		System.out.println(tree);
// //		for(int i=0;i<x.size();i++)
// //			System.out.println(x.get(i));
// 		// x=new ArrayList();
// 		// x.add(new Ref("1",2));
// 		// x.add(new Ref("2",2));
// 		// .add(new Ref("4",4));
// 		// tree.deleteAllRefs(x,4);
// 		// System.out.println(tree);
// //		tree.delete(4, new Ref("1",2));
// 		// tree.delete(10, null);
// //		System.out.println(tree);
// //		tree.delete(4, new Ref("6",2));
// //		System.out.println(tree);
// //		System.out.println(tree.searchNodeRef(4, new Ref("6",2))+"node to be deleted");
// //		System.out.println(tree.wasLeft +"left");
// //		System.out.println(tree.deleteL(4, new Ref("6",2)));
// //		System.out.println(tree);
// //		System.out.println(tree.searchNodeRef(4, new Ref("6",2))+"after deleted");
// //		tree.delete(8, null);
// //		System.out.println(tree);
// //		ArrayList<Ref> out = tree.searchDuplicates(4);
// //		if (out == null)
// //			System.out.println("null");
// //		else
// //			for (int i = 0; i < out.size(); i++)
// //				System.out.println(out.get(i));
// //		tree.insert(1, null);
// //		tree.insert(2,null);
// //		tree.insert(3, null);
// //		tree.insert(4, new Ref("1",2));
// //		tree.insert(4, null);
// //		System.out.println(tree);
// 		// System.out.println(tree.searchNodeRef(4,new Ref("1",2)));

// 		// tree.delete(4,new Ref("1",2));
// 		// System.out.println(tree);

// //		tree.delete(7);
// //		System.out.println(tree.toString());

// //		i=0;
// //		while(i<3) 
// //		{
// //			int x = sc.nextInt();
// //			if(x == -1)
// //				break;
// //			tree.delete(x);
// //			System.out.println(tree.toString());
// //			i++;
// //		}
// //		sc.close();
// //		tree.delete(5);
// 		// tree.delete(4);
// //		tree.delete(11);
// 		// System.out.println(tree.toString());
// //		tree.update(4,90,null);
// //		System.out.println(tree.toString());

	}
}
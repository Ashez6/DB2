import java.io.*;
import java.util.*;

import ds.bplus.bptree.BPlusTree;
import ds.bplus.util.InvalidBTreeStateException;
@SuppressWarnings({"rawtypes","unchecked",})
public class Test {


	public static int firstGreater(Vector<Object> v, int target) {
        Object[] arr=v.toArray();
		int left = 0;
        int right = arr.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            // If current element is greater than or equal to target,
            // move to the left half
            if ((int)arr[mid] >= target) {
                result = mid;
                right = mid - 1;
            } 
            // If current element is less than target, move to the right half
            else {
                left = mid + 1;
            }
        }

        // If result is still -1, it means no element greater than target is found
        // Otherwise, result contains the index of the first element greater than or equal to target
        return result;
    }
    public static void main(String args[]) {
       Hashtable htblColNameType = new Hashtable( );
			htblColNameType.put("id", "java.lang.Integer");
			htblColNameType.put("name", "java.lang.String");
			htblColNameType.put("gpa", "java.lang.Double");


			Hashtable htblColNameValue = new Hashtable( );
			htblColNameValue.put("id", Integer.valueOf( 2343230 ));
			htblColNameValue.put("name", new String("Ahmed Noor" ) );
			htblColNameValue.put("gpa", Double.valueOf( 0.95 ) );

			Hashtable tup1 = new Hashtable( );
			tup1.put("id", Integer.valueOf( 0 ));
			tup1.put("name", new String("Aby" ) );
			tup1.put("gpa", Double.valueOf( 0.95 ) );

			Hashtable tup2 = new Hashtable( );
			tup2.put("id", Integer.valueOf( 2 ));
			tup2.put("name", new String("bobs" ) );
			tup2.put("gpa", Double.valueOf( 0.95 ) );

			Hashtable tup3 = new Hashtable( );
			tup3.put("id", Integer.valueOf( 3 ));
			tup3.put("name", 1 );
			tup3.put("gpa", Double.valueOf( 0.95 ) );

			Hashtable tup4 = new Hashtable( );
			tup4.put("id", Integer.valueOf( 4 ));
			tup4.put("name", new String("ehab" ) );
			tup4.put("gpa", Double.valueOf( 0.95 ) );

			Hashtable tup5 = new Hashtable( );
			tup5.put("id", Integer.valueOf( 5 ));
			tup5.put("name", new String("zeina" ) );
			tup5.put("gpa", Double.valueOf( 0.95 ) );

			Hashtable tup6 = new Hashtable( );
			tup6.put("id", Integer.valueOf( 6 ));
			tup6.put("name", new String("hazem" ) );
			tup6.put("gpa", Double.valueOf( 0.95 ) );


			Hashtable tup7 = new Hashtable( );
			tup7.put("id", Integer.valueOf( 7 ));
			tup7.put("name", new String("mohamed" ) );
			tup7.put("gpa", Double.valueOf( 0.95 ) );


			Hashtable tup8 = new Hashtable( );
			tup8.put("id", Integer.valueOf( 8 ));
			tup8.put("name", new String("ahmed" ) );
			tup8.put("gpa", Double.valueOf( 0.95 ) );

			DBApp d = new DBApp();
			try {
				BPlusTree b=new BPlusTree();
				b.printTree();
				b.insertKey(1, "aby", false);
				b.insertKey(4, "ehab", false);
				b.insertKey(7, "bobs", false);
				b.insertKey(3, "zeina", false);
				b.printTree();
				BPlusTree b2=new BPlusTree();
				b2.insertKey(1, "sam", false);
				b2.insertKey(4, "hazem", false);
				

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidBTreeStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
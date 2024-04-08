import java.io.*;
import java.util.*;

import resources.BPTree.*;
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
			tup1.put("id", Integer.valueOf( 1 ));
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
			Hashtable tup6a = new Hashtable( );
			tup6a.put("id", Integer.valueOf( 9 ));
			tup6a.put("name", new String("hazem" ) );
			tup6a.put("gpa", Double.valueOf( 2 ) );
			Hashtable tup6b = new Hashtable( );
			tup6b.put("id", Integer.valueOf( 10 ));
			tup6b.put("name", new String("hazem" ) );
			tup6b.put("gpa", Double.valueOf( 1 ) );

			Hashtable tup7 = new Hashtable( );
			tup7.put("id", Integer.valueOf( 7 ));
			tup7.put("name", new String("mohamed" ) );
			tup7.put("gpa", Double.valueOf( 0.95 ) );


			Hashtable tup8 = new Hashtable( );
			tup8.put("id", Integer.valueOf( 8 ));
			tup8.put("name", new String("sam" ) );
			tup8.put("gpa", Double.valueOf( 0.95 ) );

			DBApp d = new DBApp();
			try {
				d.createTable("City", "id", htblColNameType);
				d.createIndex("City", "name", "NameTree");
				Table t=d.loadTableFromDisk("City");
				BPTree b=d.loadTree("CityNameTree");
				d.insertIntoTable("City", tup8);
				t=d.loadTableFromDisk("City");
				b=d.loadTree("CityNameTree");
				//b.insert(8, b.insertRef(8,4,"City",false));
				System.out.println(t);
				System.out.println(b);
				d.insertIntoTable("City", tup1);
				t=d.loadTableFromDisk("City");
				b=d.loadTree("CityNameTree");
				//b.insert(1, b.insertRef(1,4,"City",true));
				System.out.println(t);
				System.out.println(b);
				d.insertIntoTable("City", tup6);
				t=d.loadTableFromDisk("City");
				b=d.loadTree("CityNameTree");
				//Ref Rtup6 =  b.insertRef(6,4,"City",false);
				//b.insert(6, Rtup6);
				System.out.println(t);
				System.out.println(b);
				d.insertIntoTable("City", tup4);
				t=d.loadTableFromDisk("City");
				b=d.loadTree("CityNameTree");
				//b.insert(4, b.insertRef(4,4,"City",false));
				System.out.println(t);
				System.out.println(b);
				d.insertIntoTable("City", tup2);
				t=d.loadTableFromDisk("City");
				b=d.loadTree("CityNameTree");
				//b.insert(2, b.insertRef(2,4,"City",false));
				System.out.println(t);
				System.out.println(b);
				d.insertIntoTable("City", tup5);
				t=d.loadTableFromDisk("City");
				b=d.loadTree("CityNameTree");
				//b.insert(5, b.insertRef(5,4,"City",false));
				System.out.println(t);
				System.out.println(b);
				
				d.insertIntoTable("City", tup6a);
				t=d.loadTableFromDisk("City");
				b=d.loadTree("CityNameTree");
				//Ref Rtup6 =  b.insertRef(6,4,"City",false);
				//b.insert(6, Rtup6);
				// System.out.println(t);
				// System.out.println(b);
				// d.insertIntoTable("City", tup7);
				// t=d.loadTableFromDisk("City");
				// b=d.loadTree("CityNameTree");
				//b.insert(8, b.insertRef(8,4,"City",false));
				System.out.println(t);
				System.out.println(b);
				d.insertIntoTable("City", tup6b);
				t=d.loadTableFromDisk("City");
				b=d.loadTree("CityNameTree");
				//Ref Rtup6 =  b.insertRef(6,4,"City",false);
				//b.insert(6, Rtup6);
				System.out.println(t);
				System.out.println(b);

				System.out.println(b.searchDuplicates("Aby"));
				System.out.println(b.searchDuplicates("bobs"));
				System.out.println(b.searchDuplicates("ehab"));
				//System.out.println(b.searchDuplicates("mohamed"));
				System.out.println(b.searchDuplicates("sam"));
				System.out.println(b.searchDuplicates("hazem"));
				System.out.println(b.searchDuplicates("zeina"));

				Hashtable tup9 = new Hashtable( );
				tup9.put("name", new String("hazem" ) );
				tup9.put("gpa", Double.valueOf( 2 ) );
				d.deleteFromTable("City", tup9);
				t=d.loadTableFromDisk("City");
				b=d.loadTree("CityNameTree");
				System.out.println(t);
				System.out.println(b);

				System.out.println(b.searchDuplicates("Aby"));
				System.out.println(b.searchDuplicates("bobs"));
				System.out.println(b.searchDuplicates("ehab"));
				System.out.println(b.searchDuplicates("hazem"));
				System.out.println(b.searchDuplicates("mohamed"));
				System.out.println(b.searchDuplicates("sam"));
				System.out.println(b.searchDuplicates("zeina"));

				//System.out.println(b.searchDuplicates("hoba3"));

			} catch ( DBAppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
	}
}
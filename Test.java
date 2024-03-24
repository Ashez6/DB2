import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
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
			tup3.put("name", new String("sam" ) );
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
			// try {
			// 	d.createTable("Ashez", "id", htblColNameType);
			// } catch (DBAppException e) {
			// 	// TODO Auto-generated catch block
			// 	e.printStackTrace();
			// }
			// try {
			// 	d.deleteTableFile("Ashez.class");
			// } catch (DBAppException e) {
			// 	// TODO Auto-generated catch block
			// 	e.printStackTrace();
			// }
			try {
				d.insertIntoTable("Ashez.class", tup6);
			} catch (DBAppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(d.loadTableFromDisk("Ashez.class").loadPageFromFile("Ashez1.class"));
			System.out.println(d.loadTableFromDisk("Ashez.class").loadPageFromFile("Ashez2.class"));
			System.out.println(d.loadTableFromDisk("Ashez.class").loadPageFromFile("Ashez3.class"));
			
	}
}
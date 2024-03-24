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

			DBApp d = new DBApp();
			// try {
			// 	d.createTable("aby", "id", htblColNameType);
			// } catch (DBAppException e) {
			// 	// TODO Auto-generated catch block
			// 	e.printStackTrace();
			// }
			// try {
			// 	d.deleteTableFile("aby.class");
			// } catch (DBAppException e) {
			// 	// TODO Auto-generated catch block
			// 	e.printStackTrace();
			// }
			// try {
			// 	d.insertIntoTable("aby.class", htblColNameValue);
			// } catch (DBAppException e) {
			// 	// TODO Auto-generated catch block
			// 	e.printStackTrace();
			// }
			System.out.println(d.loadTableFromDisk("aby.class").loadPageFromFile("aby1.class").toString());
	}
}
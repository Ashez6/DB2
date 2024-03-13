import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
public class Test {


    public static void main(String args[]) {
       Hashtable htblColNameType = new Hashtable( );
			htblColNameType.put("id", "java.lang.Integer");
			htblColNameType.put("name", "java.lang.String");
			htblColNameType.put("gpa", "java.lang.Double");


			Hashtable htblColNameValue = new Hashtable( );
			htblColNameValue.put("id", Integer.valueOf( 2343432 ));
			htblColNameValue.put("name", new String("Ahmed Noor" ) );
			htblColNameValue.put("gpa", Double.valueOf( 0.95 ) );

			DBApp d = new DBApp();


		// try {
		// 	d.createTable("aby", "id", htblColNameType);
		// } catch (DBAppException e) {
		// 	throw new RuntimeException(e);
		// }

		try {
			d.insertIntoTable("aby", htblColNameValue);
		} catch (DBAppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// try {
		// 	d.createIndex("ashez", "gpa", "GPAindex");
		// } catch (DBAppException e) {
		// 	throw new RuntimeException(e);
		// }


	}
}
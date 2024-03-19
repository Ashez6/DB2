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
			// Table t=d.loadTableFromDisk("NotZomix.class");
			// t.getPageNames().remove("NotZomix1.class");
			// System.out.println(t.getPageNames());
			// d.saveTableToDisk(t);
			// try {
			// 	d.createTable("Zomix", "id", htblColNameType);
			// } catch (DBAppException e) {
			// 	// TODO Auto-generated catch block
			// 	e.printStackTrace();
			// }
			// Table t=d.loadTableFromDisk("Zomix.class");
			// t.createPage();
			// t.createPage();
			// t.createPage();
			// d.saveTableToDisk(t);
			try {
				d.deleteTableFile("Zomix.class");
			} catch (DBAppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


	}
}
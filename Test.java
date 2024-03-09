import java.util.Hashtable;
import java.util.Vector;
public class Test {


    public static void main(String args[]) {
       Hashtable htblColNameType = new Hashtable( );
			htblColNameType.put("id", "java.lang.Integer");
			htblColNameType.put("name", "java.lang.String");
			htblColNameType.put("gpa", "java.lang.double");
			

			Hashtable htblColNameValue = new Hashtable( );
			htblColNameValue.put("id", new Integer( 2343432 ));
			htblColNameValue.put("name", new String("Ahmed Noor" ) );
			htblColNameValue.put("gpa", new Double( 0.95 ) );

            Page p = new Page("aby");
        p.insert(htblColNameValue);
       
        Table t= new Table("test", "id", htblColNameType);
        t.createPage();
        System.out.println(p.N);
        

    }
}
import java.util.Vector;
public class Test {


    public static void main(String args[]) {
        //testing if shifting is handled in deletion automatically (it is)
            // Creating an empty Vector
            Vector<String> vec_tor = new Vector<String>();

            // Use add() method to add elements in the Vector
            vec_tor.add("Geeks");
            vec_tor.add("for");
            vec_tor.add("Geeks");
            vec_tor.add("10");
            vec_tor.add("20");

            // Output the Vector
            System.out.println("Vector: " + vec_tor);

            // Remove the head using remove()
            Boolean rem_ele;

            rem_ele = vec_tor.remove("10");
            // Print the removed element
            if (rem_ele)
                System.out.println("Geeks"
                        + " found and removed.");
            else
                System.out.println("Geeks"
                        + " not found or removed.");

            rem_ele = vec_tor.remove("500");
            // Print the removed element
            if (rem_ele)
                System.out.println("500"
                        + " found and removed.");
            else
                System.out.println("500"
                        + " not found or removed.");

            // Print the final Vector
            System.out.println("Final Vector: " + vec_tor);
        }


}
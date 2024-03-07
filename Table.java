import java.io.*;
import java.util.*;

public class Table implements Serializable{
    String name;
    ArrayList<String> PageNames = new ArrayList<>();
    Page CurrentPage;

    public Table(String s){

    }

    public void loadPageToMemory(String s){
        try {
         FileInputStream fileIn = new FileInputStream(s);
         ObjectInputStream in = new ObjectInputStream(fileIn);
         CurrentPage = (Page) in.readObject();
         in.close();
         fileIn.close();
      } catch (IOException i) {
         i.printStackTrace();
         return;
      } catch (ClassNotFoundException c) {
         System.out.println("Page class not found");
         c.printStackTrace();
         return;
      }
    }

    public void savePageToDisk(String s){
        try {
         FileOutputStream fileOut = new FileOutputStream(s);
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(CurrentPage);
         out.close();
         fileOut.close();
      } catch (IOException i) {
         i.printStackTrace();
         return;
    }
}

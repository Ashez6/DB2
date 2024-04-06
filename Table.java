import java.io.*;
import java.util.*;

public class Table implements Serializable{
    String name;
    String cKey;
    Vector<String> pageNames = new Vector<String>();
    int NPages;


    public Table(String name,String cKey){
        this.name=name;
        this.cKey=cKey;
    }

    public String getName(){
        return name;
    }

    public String getCKey(){
        return cKey;
    }

    public int getNPages(){
        return NPages;
    }

    public Vector<String> getPageNames(){
        return pageNames;
    }

    public void setName(String s){
        this.name=s;
    }

    public void setNPages(int n){
        this.NPages=n;
    }

    public void setPageNames(Vector<String> v){
        this.pageNames=v;
    }

    public void createPage(){
        NPages++;
        String pname=name + "" + NPages + ".class";
        pageNames.add(pname);
        Page p=new Page(pname);
        savePageToFile(p);
    }

    public void deletePage(String s){
        pageNames.remove(s);
        File file = new File(s);
        if (file.exists()) {
            file.delete();
        } else {
            System.out.println("Page file not found: " + s);
        }
        
    }

    
    public Page loadPageFromFile(String s){
        Page p=null;
        try {
         FileInputStream fileIn = new FileInputStream(s);
         ObjectInputStream in = new ObjectInputStream(fileIn);
         p = (Page) in.readObject();
         in.close();
         fileIn.close();
      } catch (IOException i) {
         i.printStackTrace();
      } catch (ClassNotFoundException c) {
         System.out.println("Page class not found");
         c.printStackTrace();
      }
      return p;
    }

    public void savePageToFile(Page p){
        try {
         FileOutputStream fileOut = new FileOutputStream(p.name);
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         out.writeObject(p);
         out.close();
         fileOut.close();
      } catch (IOException i) {
         i.printStackTrace();
         return;
      }
    }

    public String toString(){
        String s="";
        for(String name:pageNames){
            Page p=loadPageFromFile(name);
            s+=p.toString()+"\n";
        }
        return s;
    }

}

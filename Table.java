import resources.BPTree.BPTree;

import java.io.*;
import java.util.*;

public class Table implements Serializable{
    
    private static final long serialVersionUID = 1L;
    String name;
    String cKey;
    Vector<String> pageNames = new Vector<String>();
    Vector<Object> pageMin = new Vector<Object>();
    Vector<Object> pageMax = new Vector<Object>();
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

    public Vector<Object> getMinVector(){
        return pageMin;
    }

    public Vector<Object> getMaxVector(){
        return pageMax;
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

    public void setMinVector(Vector<Object> v){
        this.pageMin=v;
    }

    public void setMaxVector(Vector<Object> v){
        this.pageMax=v;
    }
    
    public void setPageMin(int page,Object value){
        try{
            pageMin.set(page, value);
        }
        catch ( ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
            pageMin.insertElementAt(value, page);
        }
    }

    public void setPageMax(int page,Object value){
        try{
            pageMax.set(page, value);
        }
        catch ( ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
            pageMax.insertElementAt(value, page);
        }
    }
    

    public void createPage(){
        NPages++;
        String pname=name + "" + NPages + ".class";
        pageNames.add(pname);
        Page p=new Page(pname);
        savePageToFile(p);
    }

    public void deletePage(String s){
        pageMin.remove(pageNames.indexOf(s));
        pageMax.remove(pageNames.indexOf(s));
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

    public ArrayList<BPTree> getBPTrees(){
        ArrayList<BPTree> result = new ArrayList<>();

        FileReader fr;
        try {
            fr = new FileReader("metadata.csv");
            BufferedReader br = new BufferedReader(fr);
            String line ;
            while((line = br.readLine()) != null){
                String[] arr= line.split(",");
                if((arr[0]).equals(this.name)){

                    if(!arr[4].equals("null")){
                        result.add(loadTree(name + arr[4]));
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public BPTree loadTree(String s){
        BPTree t=null;
        try {
            FileInputStream fileIn = new FileInputStream(s+".class");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            t = (BPTree) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Tree not found");
            c.printStackTrace();
        }
        return t;
    }

    public String toString(){
        String s="";
        int i=0;
        for(String name:pageNames){
            Page p=loadPageFromFile(name);
            s+=p.toString()+" Min:"+pageMin.get(i)+" Max:"+pageMax.get(i)+"\n";
            i++;
        }
        return s;
    }

}

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

public class Page implements Serializable {
    String name;
    int N;
    Vector<Object> tuples = new Vector<>(N);

    public Page(String name) {
        this.name = name;
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("resources/DBApp.config")) {
            properties.load(input);
            N = Integer.parseInt(properties.getProperty("MaximumRowsCountinPage"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName(){
        return name;
    }

    public int getN(){
        return N;
    }

    public Vector<Object> getTuples(){
        return tuples;
    }

    public void setName(String s){
        this.name=s;
    }


    public void insert(Object x) {
        if (tuples.size() != N) {
            tuples.add(x);
        }
    }

    public void delete(Object x) {
        tuples.remove(x);
    }

    public boolean isEmpty() {
        return tuples.isEmpty();
    }

    public boolean isFull() {
        return tuples.size() == N;
    }

    public int linearSearch(String key,Hashtable<String,Object> toInsert){
        for (int i = 0; i < tuples.size(); i++) {
            Hashtable tuple = (Hashtable) tuples.elementAt(i);
            String s=tuple.get(key).toString();
            String s1=toInsert.get(key).toString();
            if(s.compareTo(s1)>=0){
                return i;
            }  
        }
        return -1;
    }

    @Override
    public String toString() {
        String r = "";
        for (int i = 0; i < tuples.size(); i++) {
            Hashtable tuple = (Hashtable) tuples.elementAt(i);
            if (i == tuples.size() - 1) {
                r += "" + this.printTuple(tuple);
            } else {
                r += "" + this.printTuple(tuple) + ",";
            }
        }
        return r;
    }

    public String printTuple(Hashtable ht) {
        Object[] arr = ht.values().toArray();
        String s = "";
        for (int i = arr.length - 1; i >= 0; i--) {
            s += "" + arr[i];
            if (i != 0) {
                s += ",";
            }
        }
        return s;
    }

}

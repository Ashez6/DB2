import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

public class Page implements Serializable{
    String name;
    int N=200 ;
    Vector<Object> v = new Vector<>(N);


    public Page(String name){
        this.name=name;
    }

    public void insert(Object x) {
        if (v.size() != N) {
            v.add(x);
        }
    }

    public void delete(Object x) {
        v.remove(x);
    }

    public boolean isEmpty(){
        return v.isEmpty() ;
    }
    public boolean isFull(){
        return v.size() == N;
    }


    @Override
    public String toString() {
        String r = "";
        for (int i = 0; i < v.size() ; i++) {
            Hashtable tuple = (Hashtable)v.elementAt(i);
            if(i == v.size() - 1){
                r += "" + this.printTuple(tuple) ;
            }
            else {
                r += "" + this.printTuple(tuple) + ",";
            }
        }
        return r;
    }

    public String printTuple(Hashtable ht){
        Object[] arr= ht.values().toArray();
        String s="";
        for(int i=arr.length-1;i>=0;i--){
            s+= ""+arr[i];
            if(i!=0){
                s+=","; 
            }
        }
        return s;
    }

    

        
    
}

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

    public boolean empty(){
        return v.isEmpty() ;
    }
    public boolean isFull(){
        return v.size() == N;
    }


    @Override
    public String toString() {
        String r = "";
        for (int i = 0; i < v.size() ; i++) {
            if(i == v.size() - 1){
                r += "" + v.elementAt(i).toString() ;
            }
            else {
                r += "" + v.elementAt(i).toString() + ",";
            }
        }
        return r;
    }

        
    
}

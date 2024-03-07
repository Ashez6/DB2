import java.io.*;
import java.util.*;

public class Page {
    int size = 200;
    Vector<Row> v = new Vector<>(size);


    public Page(){

    }

    public void insert(Row x){
        if(v.size() == size){
            //TODO check if we need to throw an exception or not
        }
        else {
            v.add(x);
        }
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


    public static void main(String[] args) {
        Page p = new Page();
        p.insert(new Row("Ahmed", 20, "Zamalek"));

        System.out.println(p);
    }
}

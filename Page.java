import java.io.*;
import java.util.*;

public class Page {
    int size = 200;
    Vector<Row> v = new Vector<>(size);


    public Page(){

    }

    public void insert(Object x){

    }



    @Override
    public String toString() {
        String r = "";
        for (int i = 0; i < v.size() ; i++) {
            if(i == v.size() - 1 ){
                r += "" + v.elementAt(v.size() - 1);
                break;
            }
            else {
                r += "" + v.elementAt(i) + ",";
            }
        }
        return r;
    }


    public static void main(String[] args) {
        Page p = new Page();
        System.out.println(p);
    }
}

import java.io.Serializable;
import java.util.*;

public class Page implements Serializable{
    int N = 200;
    Vector<Row> v = new Vector<>(N);


    public Page(){

    }

    public void insert(Row x) {
        if (v.size() != N) {
            v.add(x);
        }
    }

    public void delete(Row x) {
        v.remove(x);
    }

    public boolean empty(){
        return v.isEmpty() ;
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

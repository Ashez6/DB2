import java.util.Hashtable;

public class Tuple {
    Hashtable ht;

    public Tuple(Hashtable ht){
        this.ht = ht;
    }

    @Override
    public String toString() {
        return printTuple(ht);
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

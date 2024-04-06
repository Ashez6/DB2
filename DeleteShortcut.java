public class DeleteShortcut {
    public static void main(String[] args) throws DBAppException {
        DBApp d = new DBApp();
        d.deleteTableFile("City");
    }
}

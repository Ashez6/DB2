import resources.BPTree.*;
//import Main.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class DBAppTest {
    private static DBApp engine;
    private static String newTableName;
    private static final String id = "id";
    private static final String name = "name";
    private static final String gpa = "gpa";
    private static final String TEST_NAME = "Abdo";
    private static final double TEST_GPA = 1.8;
    private static final String STRING_DATA_TYPE_NAME = "java.lang.String";
    private static final String INTEGER_DATA_TYPE_NAME = "java.lang.Integer";
    private static final String DOUBLE_DATA_TYPE_NAME = "java.lang.Double";

    private static void generateNewTableName() {
        int randomNumber1 = (int) (Math.random() * 100000) + 1;
        int randomNumber2 = (int) (Math.random() * 100000) + 1;
        StringBuilder s = new StringBuilder();
        newTableName = s.append("aa").append(randomNumber1).append(randomNumber2).toString();
        while (engine.getMyTables().contains(newTableName)) {
            randomNumber1 = (int) (Math.random() * 100000) + 1;
            randomNumber2 = (int) (Math.random() * 100000) + 1;
            s.setLength(0);
            newTableName = s.append(randomNumber1).append(randomNumber2).toString();
        }
    }

    private static void createTable() throws DBAppException {
        Hashtable<String, String> htblColNameType = createHashtable(INTEGER_DATA_TYPE_NAME,
                STRING_DATA_TYPE_NAME, DOUBLE_DATA_TYPE_NAME);

        engine.createTable(newTableName, id, htblColNameType);
    }

    @BeforeEach
    void setEnvironment() throws DBAppException{
        engine = new DBApp();
        engine.init();
        generateNewTableName();
        createTable();
    }

    @Test
    void testCreateTable_AlreadyExistingName_ShouldFailCreation() throws DBAppException {
        // Given
        Hashtable<String, String> htblColNameType = new Hashtable<>();
        htblColNameType.put("id", "java.lang.String");
        htblColNameType.put("courseName", "java.lang.String");
        // When
        Exception exception = assertThrows(DBAppException.class, () ->
                engine.createTable(newTableName, "id", htblColNameType)
        );
        // Then
        assertEquals("Table name already in use.", exception.getMessage());
    }

    @Test
    void testCreateTable_InvalidPrimaryKeyColumn_ShouldFailCreation() throws DBAppException {
        // Given
        Hashtable<String, String> htblColNameType = new Hashtable<>();
        htblColNameType.put("id", "java.lang.String");
        htblColNameType.put("courseName", "java.lang.String");
        // When
        Exception exception = assertThrows(DBAppException.class, () ->
                engine.createTable("newTable", "price", htblColNameType)
        );
        // Then
        assertEquals("Clustering key column not found.", exception.getMessage());
    }

    @Test
    void testCreateTable_InvalidDataType_ShouldFailCreation() throws DBAppException {
        // Given
        Hashtable<String, String> htblColNameType = new Hashtable<>();
        htblColNameType.put("id", "java.lang.Byte");
        htblColNameType.put("courseName", "java.lang.String");
        // When
        Exception exception = assertThrows(DBAppException.class, () ->
                engine.createTable("newTable", "id", htblColNameType)
        );
        // Then
        assertEquals("Invalid column datatype.", exception.getMessage());
    }


    @Test
    void testInsertIntoTable_OneTuple_ShouldInsertSuccessfully()
            throws DBAppException, ClassNotFoundException, IOException {
        // Given
        Hashtable<String, Object> htblColNameValue = createRow(1, TEST_NAME, TEST_GPA);

        // When
        engine.insertIntoTable(newTableName, htblColNameValue);

        // Then
        Table table = engine.loadTableFromDisk(newTableName);
        assertEquals(1, Objects.requireNonNull(table).getPageNames().size());
        Page page = table.loadPageFromFile(table.getPageNames().get(0));
        assertEquals(1, page.getTuples().size());
    }


//    @Test
//    void testInsertIntoTable_MissingColumn_ShouldInsertSuccessfully()
//            throws DBAppException, ClassNotFoundException, IOException {
//        // Given
//        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
//        htblColNameValue.put(gpa, TEST_GPA);
//        htblColNameValue.put(id, 5);
//
//        // When
//        engine.insertIntoTable(newTableName, htblColNameValue);
//
//        // Then
//        Table table = engine.loadTableFromDisk(newTableName);
//        assertEquals(1,table.getPageNames().size());
//        Page page = table.loadPageFromFile(table.getPageNames().get(0));
//        assertEquals(1, page.getTuples().size());
//    }


    @Test
    void testInsertIntoTable_ManyTuples_ShouldInsertSuccessfully() throws DBAppException {

        for (int i = 1; i < 300; i++) {
            // Given
            Hashtable<String, Object> htblColNameValue = createRow(i, TEST_NAME, TEST_GPA);

            // When
            engine.insertIntoTable(newTableName, htblColNameValue);
        }
        // Then
        Table table = engine.loadTableFromDisk(newTableName);
        assertEquals(2, table.getPageNames().size());
        Page page = table.loadPageFromFile(table.getPageNames().get(1));
        assertEquals(99, page.getTuples().size());
        page = table.loadPageFromFile(table.getPageNames().get(0));
        assertTrue(page.isFull());
    }

    @Test
    void testInsertIntoTable_InsertingLastRecordIntoFullPage_ShouldInsertSuccessfully() throws DBAppException {
        // Given
        for (int i = 2; i < 402; i += 2) {
            Hashtable<String, Object> htblColNameValue = createRow(i, TEST_NAME, TEST_GPA);
            engine.insertIntoTable(newTableName, htblColNameValue);
        }

        // When
        Hashtable<String, Object> htblColNameValue = createRow(399, TEST_NAME, TEST_GPA);
        engine.insertIntoTable(newTableName, htblColNameValue);

        // Then
        Table table = engine.loadTableFromDisk(newTableName);
        String so = table.getCKey();
        assertEquals(2, table.getPageNames().size());
        Page page = table.loadPageFromFile(table.getPageNames().get(0));
        assertTrue(page.isFull());
        assertEquals(399,((Hashtable) page.getTuples().get(199)).get(so));
    }

    @Test
    void testInsertIntoTable_InsertingRecordShiftingTwoPages_ShouldInsertSuccessfully() throws DBAppException {
        // Given
        for (int i = 2; i <= 802; i += 2) {
            Hashtable<String, Object> htblColNameValue = createRow(i, TEST_NAME, TEST_GPA);
            engine.insertIntoTable(newTableName, htblColNameValue);
        }

        // When
        Hashtable<String, Object> htblColNameValue = createRow(399, TEST_NAME, TEST_GPA);
        engine.insertIntoTable(newTableName, htblColNameValue);

        // Then
        Table table = engine.loadTableFromDisk(newTableName);
        String so = table.getCKey();


        assertEquals(3, table.getPageNames().size());
        Page page = table.loadPageFromFile(table.getPageNames().get(0));

        assertEquals(399,((Hashtable) page.getTuples().get(199)).get(so));
        page = table.loadPageFromFile(table.getPageNames().get(1));
        assertEquals(400,((Hashtable) page.getTuples().get(0)).get(so));
        page = table.loadPageFromFile(table.getPageNames().get(2));
        assertEquals(800,((Hashtable) page.getTuples().get(0)).get(so));
    }

    @Test
    void testInsertIntoTable_RepeatedPrimaryKey_ShouldFailInsert()
            throws DBAppException {
        // Given
        insertRow(1);
        Hashtable<String, Object> htblColNameValue = createRow(1, "moham", TEST_GPA);

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.insertIntoTable(newTableName, htblColNameValue);
        });

        // Then
        String expectedMessage = "identical primary key insertion";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage,outputMessage);
    }

    @Test
    void testInsertIntoTable_InvalidDataType_ShouldFailInsertion() throws DBAppException {
        // Given
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(name, "Foo");
        htblColNameValue.put(gpa, "boo");
        htblColNameValue.put(id, 55);

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.insertIntoTable(newTableName, htblColNameValue);
        });

        // Then
        String expectedMessage = "Invalid input datatypes.";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage,outputMessage);
    }

    @Test
    void testInsertIntoTable_MissingPrimaryKey_ShouldFailInsert() throws DBAppException {
        // Given
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(gpa, TEST_GPA);
        htblColNameValue.put(name, TEST_NAME);

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.insertIntoTable(newTableName, htblColNameValue);
        });

        // Then
        String expectedMessage = "All columns should have a not null value.";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage,outputMessage);
    }

    @Test
    void testInsertIntoTable_InvalidTableName_ShouldFailInsertion() {
        // Given
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(name, "Foo");
        htblColNameValue.put(gpa, TEST_GPA);
        htblColNameValue.put(id, 55);

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.insertIntoTable("someRandomTableName", htblColNameValue);
        });

        // Then
        String expectedMessage = "Table does not exist.";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage,outputMessage);
    }

    @Test
    void testInsertIntoTable_ExtraColumn_ShouldFailInsertion() {
        // Given
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(name, "Foo");
        htblColNameValue.put("salary", 10000);
        htblColNameValue.put(gpa, TEST_GPA);
        htblColNameValue.put(id, 3);

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.insertIntoTable(newTableName, htblColNameValue);
        });

        // Then
        String expectedMessage = "Invalid column name.";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage,outputMessage);
    }

    @Test
    void testUpdateTable_ValidInput_ShouldUpdateSuccessfully()
            throws DBAppException, ClassNotFoundException, IOException {
        // Given
        insertRow(1);
        String updatedName = "moham";
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(name, updatedName);

        // When
        engine.updateTable(newTableName, "1", htblColNameValue);

        // Then
        Table tito = engine.loadTableFromDisk(newTableName);
        Page page = tito.loadPageFromFile(tito.getPageNames().get(0));
        Hashtable<String, Object> updated = (Hashtable<String, Object>) page.getTuples().get(0);
        assertEquals(updatedName,updated.get(name));
    }

    @Test
    void testUpdateTable_PrimaryKeyUpdate_ShouldFailUpdate()
            throws DBAppException, ClassNotFoundException, IOException {
        // Given
        insertRow(1);
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(id, 2);

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.updateTable(newTableName, "1", htblColNameValue);
        });

        // Then
        String expectedMessage = "Clustering key should not be updated";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage, outputMessage);
    }

    @Test
    void testUpdateTable_ExtraInput_ShouldFailUpdate() throws DBAppException {
        // Given
        insertRow(1);
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(name, "Foo");
        htblColNameValue.put(gpa, 1.8);
        htblColNameValue.put("University", "GUC");

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.updateTable(newTableName, "0", htblColNameValue);
        });

        // Then
        String expectedMessage = "Column(s) does not exist.";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage, outputMessage);
    }

    @Test
    void testUpdateTable_InvalidDataType_ShouldFailUpdate() throws DBAppException {
        // Given
        insertRow(1);
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(gpa, "Foo");

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.updateTable(newTableName, "1", htblColNameValue);
        });

        // Then
        String expectedMessage = "Invalid input datatypes.";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage, outputMessage);
    }

    @Test
    void testUpdateTable_InvalidTableName_ShouldFailUpdate() throws DBAppException {
        // Given
        insertRow(1);
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(gpa, 1.8);

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.updateTable("randomName", "1", htblColNameValue);
        });

        // Then
        String expectedMessage = "Table does not exist.";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage, outputMessage);
    }

    @Test
    void testDeleteFromTable_OneTuple_ShouldDeleteSuccessfully()
            throws DBAppException, ClassNotFoundException, IOException, InterruptedException {
        // Given
        insertRow(1);
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(id, 1);

        // When
        engine.deleteFromTable(newTableName, htblColNameValue);

        // Then
        Table table = engine.loadTableFromDisk(newTableName);
        assertTrue(table.getPageNames().isEmpty());
    }

    @Test
    void testDeleteFromTable_ManyTuplesDeleteOne_ShouldDeleteSuccessfully()
            throws DBAppException, ClassNotFoundException, IOException {
        // Given
        for (int i = 0; i < 100; i++)
            insertRow(i);
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(name, TEST_NAME);
        htblColNameValue.put(id, 0);

        // When
        engine.deleteFromTable(newTableName, htblColNameValue);

        // Then
        Table table = engine.loadTableFromDisk(newTableName);
        int counter = 0;
        for (int i = 0; i < table.getPageNames().size(); i++) {
            Page p = table.loadPageFromFile(table.getPageNames().get(i));
            counter += p.getTuples().size(); 
        }
        assertEquals(99,counter); 
    }

    @Test
    void testDeleteFromTable_ManyTuplesDeleteAll_ShouldDeleteSuccessfully()
            throws DBAppException, ClassNotFoundException, IOException {
        // Given
        for (int i = 0; i < 100; i++)
            insertRow(i);
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(name, TEST_NAME);

        // When
        engine.deleteFromTable(newTableName, htblColNameValue);

        // Then
        Table table = engine.loadTableFromDisk(newTableName);
        assertTrue(table.getPageNames().isEmpty());
    }

    @Test
    void testDeleteFromTable_InvalidColumnName_ShouldFailDelete()
            throws DBAppException, ClassNotFoundException, IOException {
        // Given
        insertRow(1);
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put("middle_name", "Mohamed");

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.deleteFromTable(newTableName, htblColNameValue);
        });

        // Then
        String expectedMessage = "Column(s) does not exist.";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage, outputMessage);
    }

    @Test
    void testDeleteFromTable_InvalidDataType_ShouldFailDelete() throws DBAppException {
        // Given
        insertRow(1);
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(id, 1);
        htblColNameValue.put("gpa", "Foo");

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.deleteFromTable(newTableName, htblColNameValue);
        });

        // Then
        String expectedMessage = "Invalid input datatypes.";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage, outputMessage);
    }

    @Test
    void testDeleteFromTable_InvalidTable_ShouldFailDelete()
            throws DBAppException, ClassNotFoundException, IOException, InterruptedException {
        // Given
        insertRow(1);
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(id, 1);

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.deleteFromTable("randomTableName", htblColNameValue);
        });

        // Then
        String expectedMessage = "Table does not exist.";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage, outputMessage);
    }

    @Test
    void testCreateIndex_ValidInput_ShouldCreateSuccessfully() throws DBAppException {
        // Given
        insertRow(1);

        // When
        engine.createIndex(newTableName, gpa, gpa+"Index");

        // Then
        Table table = engine.loadTableFromDisk(newTableName);
        assertEquals(1,table.getBPTrees().get(0).getRoot().getNumberOfKeys());
    }

    @Test
    void testCreateIndex_RepeatedIndex_ShouldFailCreation() throws DBAppException {
        // Given
        engine.createIndex(newTableName, gpa, gpa+"Index");

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.createIndex(newTableName, name, gpa+"Index");
        });

        // Then
        String expectedMessage = "Index name already exists.";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage, outputMessage);

    }

    @Test
    void testCreateIndex_InvalidTableName_ShouldFailCreation() throws DBAppException {

        // Given

        // When
        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.createIndex("Foo", gpa, gpa);
        });

        // Then
        String expectedMessage = "Table does not exist.";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage, outputMessage);
    }

    @Test
    void testInsertionIntoIndex_ValidInput_ShouldInsertIntoIndex() throws DBAppException {
        // Given
        engine.createIndex(newTableName, gpa, gpa+"Index.ser");
        Table table = engine.loadTableFromDisk(newTableName);
        int oldSize = table.getBPTrees().get(0).getRoot().getNumberOfKeys();

        // When
        insertRow(1);

        // Then
        table = engine.loadTableFromDisk(newTableName);
        int newSize = table.getBPTrees().get(0).getRoot().getNumberOfKeys();
        assertEquals(oldSize+1, newSize);
    }

    //Comment if your BPTree doesn't contain these functions/add them to yours
    @Test
    void testUpdateTable_ValidInput_ShouldUpdateIndex() throws DBAppException {
        // Given
        engine.createIndex(newTableName, gpa, gpa+"Index.ser");
        insertRow(3);
        Table table = engine.loadTableFromDisk(newTableName);
        boolean oldValue = ((table.getBPTrees().get(0)).search(TEST_GPA) != null);

        // When
        Hashtable<String, Object> updateTable = new Hashtable<>();
        updateTable.put("gpa", 0.7);
        engine.updateTable(newTableName, "3", updateTable);

        // Then
        table = engine.loadTableFromDisk(newTableName);
        boolean oldValueCheck = ((table.getBPTrees().get(0)).search(TEST_GPA) != null);
        boolean newValueCheck = ((table.getBPTrees().get(0)).search(0.7) != null);
        assertTrue(oldValue);
        assertFalse(oldValueCheck);
        assertTrue(newValueCheck);
    }

    @Test
    void testSelectFromTable_TwoORTerms_ShouldSelectSixTuples() throws DBAppException {
        // Given
        for (int i = 1; i <= 10; i++)
            insertRow(i);

        // When
        SQLTerm[] sqlTerms = new SQLTerm[2];
        sqlTerms[0] = new SQLTerm(newTableName, id, ">", 5);
        sqlTerms[1] = new SQLTerm(newTableName, id, "<", 2);
        String[] strArrOperator = new String[] { "OR" };

        // Then
        Iterator it = engine.selectFromTable(sqlTerms, strArrOperator);
        assertEquals(6,getIteratorSize(it));
    }

    @Test
    void testSelectFromTable_TwoANDTerms_ShouldSelectZeroTuples() throws DBAppException {
        // Given
        for (int i = 1; i <= 10; i++)
            insertRow(i);

        // When
        SQLTerm[] sqlTerms = new SQLTerm[2];
        sqlTerms[0] = new SQLTerm(newTableName, id, ">", 5);
        sqlTerms[1] = new SQLTerm(newTableName, id, "<", 2);
        String[] strArrOperator = new String[] { "AND" };

        // Then
        Iterator it = engine.selectFromTable(sqlTerms, strArrOperator);
        assertEquals(0, getIteratorSize(it));
    }

    @Test
    void testSelectWithIndex_ThreeANDTermsGreaterThan_ShouldSelectFiveTuples() throws DBAppException {
        // Given
        for (int i = 1; i <= 10; i++)
            insertRow(i);
        engine.createIndex(newTableName, gpa, gpa+"Index");
        // When
        SQLTerm[] sqlTerms = new SQLTerm[3];
        sqlTerms[0] = new SQLTerm(newTableName, id, ">", 5);
        sqlTerms[1] = new SQLTerm(newTableName, name, "=", TEST_NAME);
        sqlTerms[2] = new SQLTerm(newTableName, gpa, "=", TEST_GPA);
        String[] strArrOperator = new String[] { "AND", "AND" };

        // Then
        Iterator it = engine.selectFromTable(sqlTerms, strArrOperator);
        assertEquals(5, getIteratorSize(it));
    }

    @Test
    void testSelectWithIndex_ThreeANDTermsNotEqual_ShouldSelectNineTuples() throws DBAppException {
        // Given
        for (int i = 1; i <= 10; i++)
            insertRow(i);
        engine.createIndex(newTableName, gpa, gpa+"Index");

        // When
        SQLTerm[] sqlTerms = new SQLTerm[3];
        sqlTerms[0] = new SQLTerm(newTableName, id, "!=", 5);
        sqlTerms[1] = new SQLTerm(newTableName, name, "=", TEST_NAME);
        sqlTerms[2] = new SQLTerm(newTableName, gpa, "=", TEST_GPA);
        String[] strArrOperator = new String[] { "AND", "AND" };

        // Then
        Iterator it = engine.selectFromTable(sqlTerms, strArrOperator);
        assertEquals(9, getIteratorSize(it));
    }

    @Test
    void testSelectWithIndex_ThreeANDTermsLessThanOrEqual_ShouldSelectSixTuples() throws DBAppException {
        // Given
        for (int i = 1; i <= 10; i++)
            insertRow(i);
        engine.createIndex(newTableName, gpa, gpa+"Index");

        // When
        SQLTerm[] sqlTerms = new SQLTerm[3];
        sqlTerms[0] = new SQLTerm(newTableName, id, "<=", 6);
        sqlTerms[1] = new SQLTerm(newTableName, name, "=", TEST_NAME);
        sqlTerms[2] = new SQLTerm(newTableName, gpa, "=", TEST_GPA);
        String[] strArrOperator = new String[] { "AND", "AND" };

        // Then
        Iterator it = engine.selectFromTable(sqlTerms, strArrOperator);
        assertEquals(6,getIteratorSize(it));
    }

    @Test
    void testSelectWithIndex_FourTermsAndAtEnd_ShouldSelectFiveTuples() throws DBAppException {
        // Given
        for (int i = 1; i <= 10; i++)
            insertRow(i);
        engine.createIndex(newTableName, gpa, gpa+"Index");

        // When
        SQLTerm[] sqlTerms = new SQLTerm[4];

        sqlTerms[0] = new SQLTerm(newTableName, id, "=", 5);
        sqlTerms[1] = new SQLTerm(newTableName, id, "<=", 6);
        sqlTerms[2] = new SQLTerm(newTableName, name, "=", TEST_NAME);
        sqlTerms[3] = new SQLTerm(newTableName, gpa, "=", TEST_GPA);
        String[] strArrOperator = new String[] { "XOR" ,"AND", "AND" };

        // Then
        Iterator it = engine.selectFromTable(sqlTerms, strArrOperator);
        assertEquals(5,getIteratorSize(it));
    }

    @Test
    void testSelectFromTable_TwoXORTerms_ShouldSelectFiveTuples() throws DBAppException {
        // Given
        for (int i = 1; i <= 10; i++)
            insertRow(i);

        // When
        SQLTerm[] sqlTerms = new SQLTerm[2];
        sqlTerms[0] = new SQLTerm(newTableName, id, ">", 5);
        sqlTerms[1] = new SQLTerm(newTableName, name, "=", "yehia");
        String[] strArrOperator = new String[] { "XOR" };

        // Then
        Iterator it = engine.selectFromTable(sqlTerms, strArrOperator);
        assertEquals(5,getIteratorSize(it));
    }

    @Test
    void testSelectFromTable_WrongNumberOfOperators_ShouldFailSelection() throws DBAppException {
        // Given
        for (int i = 1; i <= 10; i++)
            insertRow(i);

        // When
        SQLTerm[] sqlTerms = new SQLTerm[2];
        sqlTerms[0] = new SQLTerm(newTableName, id, ">", 5);
        sqlTerms[1] = new SQLTerm(newTableName, name, "=", "yehia");
        String[] strArrOperator = new String[] { "XOR", "AND" };

        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.selectFromTable(sqlTerms, strArrOperator);
        });

        // Then
        String expectedMessage = "incompatible length of terms with operators";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage,outputMessage);
    }

    @Test
    void testSelectFromTable_UnknownArrOperator_ShouldFailSelection() throws DBAppException {
        // Given
        for (int i = 1; i <= 10; i++)
            insertRow(i);

        // When
        SQLTerm[] sqlTerms = new SQLTerm[2];
        sqlTerms[0] = new SQLTerm(newTableName, id, ">", 5);
        sqlTerms[1] = new SQLTerm(newTableName, name, "=", "yehia");
        String[] strArrOperator = new String[] { "NOT" };

        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.selectFromTable(sqlTerms, strArrOperator);
        });

        // Then
        String expectedMessage = "The only supported logical operators are the AND, OR and XOR :(";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage,outputMessage);
    }

    @Test
    void testSelectFromTable_UnknownOperator_ShouldFailSelection() throws DBAppException {
        // Given
        for (int i = 1; i <= 10; i++)
            insertRow(i);

        // When
        SQLTerm[] sqlTerms = new SQLTerm[2];
        sqlTerms[0] = new SQLTerm(newTableName, id, ">", 5);
        sqlTerms[1] = new SQLTerm(newTableName, name, "<>", "yehia");
        String[] strArrOperator = new String[] { "AND" };

        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.selectFromTable(sqlTerms, strArrOperator);
        });

        // Then
        String expectedMessage = "Unsupported operator";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage,outputMessage);
    }

    @Test
    void testSelectFromTable_InvalidColumn_ShouldFailSelection() throws DBAppException {
        // Given
        for (int i = 1; i <= 10; i++)
            insertRow(i);

        // When
        SQLTerm[] sqlTerms = new SQLTerm[2];
        sqlTerms[0] = new SQLTerm(newTableName, id, ">", 5);
        sqlTerms[1] = new SQLTerm(newTableName, "salary", "=", "yehia");
        String[] strArrOperator = new String[] { "AND" };

        Exception exception = assertThrows(DBAppException.class, () -> {
            engine.selectFromTable(sqlTerms, strArrOperator);
        });

        // Then
        String expectedMessage = "The selected column does not exist";
        String outputMessage = exception.getMessage();
        assertEquals(expectedMessage,outputMessage);
    }


    private int getIteratorSize(Iterator it) {
        int ret = 0;
        while (it.hasNext()) {
            ret++;
            it.next();
        }
        return ret;
    }

    private static void insertRow(int id) throws DBAppException {

        Hashtable<String, Object> htblColNameValue = createRow(id, TEST_NAME, TEST_GPA);

        engine.insertIntoTable(newTableName, htblColNameValue);
    }

    private static Hashtable<String, String> createHashtable(String value1, String value2, String value3) {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put(id, value1);
        hashtable.put(name, value2);
        hashtable.put(gpa, value3);
        return hashtable;
    }

    private static Hashtable<String, Object> createRow(int idInput, String nameInput, double gpaInput) {
        Hashtable<String, Object> htblColNameValue = new Hashtable<>();
        htblColNameValue.put(id, idInput);
        htblColNameValue.put(name, nameInput);
        htblColNameValue.put(gpa, gpaInput);
        return htblColNameValue;
    }

	/*
	@AfterEach
	void deleteCreatedTable() throws DBAppException {
		Table table = engine.loadTableFromDisk(newTableName);
		FileDeleter.deleteFile(table, FileType.TABLE);
	}
	*/
}

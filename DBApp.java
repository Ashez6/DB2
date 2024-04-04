
/** * @author Wael Abouelsaadat */




import java.io.*;
import java.util.*;

import ds.bplus.bptree.BPlusTree;
import ds.bplus.util.InvalidBTreeStateException;

@SuppressWarnings({"rawtypes","unchecked"})
public class DBApp {



	public DBApp( ){

	}

	// this does whatever initialization you would like
	// or leave it empty if there is no code you want to
	// execute at application startup
	public void init( ){


	}


	// following method creates one table only
	// strClusteringKeyColumn is the name of the column that will be the primary
	// key and the clustering column as well. The data type of that column will
	// be passed in htblColNameType
	// htblColNameValue will have the column name as key and the data
	// type as value

	public void createTable(String strTableName,
							String strClusteringKeyColumn,
							Hashtable<String,String> htblColNameType)
			throws DBAppException{
		
		File dupCheck = new File(strTableName+".class");
		if(dupCheck.exists()){
			throw new DBAppException("Table name already in use.");
		}
		Table t=new Table(strTableName, strClusteringKeyColumn);
		saveTableToDisk(t);

		// Writing to the CSV file
		FileWriter csvFile;
		try {
			csvFile = new FileWriter("metadata.csv", true);

			BufferedWriter bw = new BufferedWriter(csvFile);
			Object[] colName =  htblColNameType.keySet().toArray();
			Object[] colTypes =  htblColNameType.values().toArray();
			//TableName,ColumnName, ColumnType, ClusteringKey, IndexName, IndexType
			for (int i = 0; i < colName.length; i++) {
				bw.write(strTableName + ",");
				bw.write(colName[i].toString() + ",");
				bw.write(colTypes[i].toString() + ",");
				if (strClusteringKeyColumn.equals(colName[i].toString())){
					bw.write("True,");
				}
				else {
					bw.write("False,");
				}

				bw.write("null,");
				bw.write("null");

				bw.newLine();
			}
			bw.close();
		}  catch (IOException e) {
			throw new RuntimeException(e);
		}

		//throw new DBAppException("not implemented yet");
	}


	// following method creates a B+tree index
	public void createIndex(String   strTableName,
							String   strColName,
							String   strIndexName) throws DBAppException{

		File mData = new File("metadata.csv");
		try {
			File tmpFile = new File("tempFile.csv");
			FileReader fr = new FileReader(mData);
			BufferedReader br = new BufferedReader(fr);
			FileWriter fw = new FileWriter(tmpFile);
			BufferedWriter bw = new BufferedWriter(fw);

			String line;
			while((line = br.readLine()) != null){

				String[] lineValues = line.split(",");

				if(lineValues[1].equals(strColName) && lineValues[0].equals(strTableName)){
					lineValues[4] = strIndexName;
					lineValues[5] = "B+tree";
					String newLine = String.join(",", lineValues );
					bw.write(newLine);
					bw.newLine();
				}
				else {
					bw.write(line);
					bw.newLine();
				}
			}

			br.close();
			bw.close();

			if(!mData.delete()){
				throw new DBAppException("meta data not deleted");
			}
			if(!tmpFile.renameTo(mData)){
				throw new DBAppException("temp file not renamed");
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			BPlusTree b=new BPlusTree();
			Table t=loadTableFromDisk(strTableName);
			for(String page:t.getPageNames()){
				Page p=t.loadPageFromFile(page+".class");
				for(Object o:p.getTuples()){
					Hashtable ht=(Hashtable)o;
					Object key=ht.get(strColName);
					b.insertKey(key, page, true);
				}
			}
			saveTree(b, strTableName+strIndexName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidBTreeStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	// following method inserts one row only.
	// htblColNameValue must include a value for the primary key
	public void insertIntoTable(String strTableName,
								Hashtable<String,Object>  htblColNameValue) throws DBAppException{
		Vector<String> indexName=new Vector<String>();
		Vector<String> indexColumn=new Vector<String>();
		List<Object> colNames = Arrays.asList(htblColNameValue.keySet().toArray());
		List<Object> colValues = Arrays.asList(htblColNameValue.values().toArray());
		FileReader fr;
		try {
			fr = new FileReader("metadata.csv");
			BufferedReader br = new BufferedReader(fr);
			String line ;
			while((line = br.readLine()) != null){
				String[] arr= line.split(",");
				if((arr[0]+".class").equals(strTableName)){
					if(colNames.contains(arr[1])){
						int index=colNames.indexOf(arr[1]);
						String type=colValues.get(index).getClass().getName();
						if(!type.equals(arr[2])){
							throw new DBAppException("Invalid input datatypes.");
						}
						if(!arr[4].equals("null")){
							indexName.add(arr[4]);
							indexColumn.add(arr[1]);
						}
					}
					else{
						throw new DBAppException("All columns should have a not null value.");
					}
				}
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		//TODO binary search insertion using clustering key
		String pageName=linearInsert(strTableName, htblColNameValue);
		for(int i=0;i<indexName.size();i++){
			BPlusTree b=loadTree(strTableName+indexName.get(i));
			try {
				b.insertKey(htblColNameValue.get(indexColumn.get(i)), pageName, true);
			} catch (NumberFormatException  | IllegalStateException | IOException | InvalidBTreeStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			saveTree(b, strTableName+indexName.get(i));
		}
		

		//throw new DBAppException("not implemented yet");
	}

	public String linearInsert(String strTableName,
		Hashtable<String,Object>  htblColNameValue){
		Table t = loadTableFromDisk(strTableName);
		
		if(t.getNPages()==0){
			t.createPage();
			Page p=t.loadPageFromFile(t.getPageNames().lastElement());
			p.insert(htblColNameValue);
			t.savePageToFile(p);
			saveTableToDisk(t);
			return p.name;
		}
		String targetPage=null;
		String ckey=t.getCKey();
		Object value=htblColNameValue.get(ckey);
		for (int i = 0; i < t.getPageNames().size(); i++) {
            String pagename = t.getPageNames().elementAt(i);
            Page p =t.loadPageFromFile(pagename);
            if(p.getInsertLoc(ckey, value)!=-1){
                targetPage=pagename;
				break;
            }
        }
		Page p;
		if(targetPage==null){
			targetPage=t.getPageNames().lastElement();
			p =t.loadPageFromFile(targetPage);
			if(p.isFull()){
				t.createPage();
				p=t.loadPageFromFile(t.getPageNames().lastElement());
			}
			p.insert(htblColNameValue);
			t.savePageToFile(p);
		    saveTableToDisk(t);
			return p.name;
		}
		p =t.loadPageFromFile(targetPage);

		if(!p.isFull()){
			int index=p.getInsertLoc(ckey, value);
			Vector<Object> v=p.getTuples();
		    v.insertElementAt(htblColNameValue, index);
		    p.setTuples(v);
		}
		else{
			int currPage=t.getPageNames().indexOf(p.getName());
			if(t.getPageNames().lastElement().equals(p.getName())){
				t.createPage();
			}
			String tempName=t.getPageNames().get(++currPage);
			Page tempPage=t.loadPageFromFile(tempName);
			Object last=p.getLastTuple();
			int index=p.getInsertLoc(ckey, value);
			p.delete(last);
			Vector<Object> v=p.getTuples();
		    v.insertElementAt(htblColNameValue, index);
		    p.setTuples(v);
			Object first=last;
			while(tempPage.isFull()){
				last=tempPage.getLastTuple();
				tempPage.delete(last);
				v=tempPage.getTuples();
				v.insertElementAt(first, 0);
				tempPage.setTuples(v);
				t.savePageToFile(tempPage);
				first=last;
				if(t.getPageNames().lastElement().equals(tempPage.getName())){
					t.createPage();
				}
				tempPage=t.loadPageFromFile(t.getPageNames().get(++currPage));
			}
			v=tempPage.getTuples();
			v.insertElementAt(first, 0);
			tempPage.setTuples(v);
			t.savePageToFile(tempPage);
		}
		t.savePageToFile(p);
		saveTableToDisk(t);
		return p.name;

	}
	// following method updates one row only
	// htblColNameValue holds the key and new value
	// htblColNameValue will not include clustering key as column name
	// strClusteringKeyValue is the value to look for to find the row to update.
	public void updateTable(String strTableName,
							String strClusteringKeyValue,
							Hashtable<String,Object> htblColNameValue   )  throws DBAppException{
		Vector<String> indexName=new Vector<String>();
		Vector<String> indexColumn=new Vector<String>();
		List<Object> colNames = Arrays.asList(htblColNameValue.keySet().toArray());
		List<Object> colValues = Arrays.asList(htblColNameValue.values().toArray());
		FileReader fr;
		try {
			fr = new FileReader("metadata.csv");
			BufferedReader br = new BufferedReader(fr);
			String line ;
			while((line = br.readLine()) != null){
				String[] arr= line.split(",");
				if((arr[0]+".class").equals(strTableName)){
					int index=colNames.indexOf(arr[1]);
					if(index==-1){
						continue;
					}
					if(!arr[4].equals("null")){
						indexName.add(arr[4]);
						indexColumn.add(arr[1]);
					}
					String type=colValues.get(index).getClass().getName();
					if(!type.equals(arr[2])){
						throw new DBAppException("Invalid input datatypes.");
					}
				}
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		Table t=loadTableFromDisk(strTableName);
		String ckey=t.getCKey();
		//TODO binary search update using clustering key
		Page p=null;
		Vector<Object> oldKeys=new Vector<>();
		outerloop : for (int i = 0; i < t.getPageNames().size(); i++) {
			String pagename = t.getPageNames().elementAt(i);
			p =t.loadPageFromFile(pagename);
			Vector<Object> v=p.getTuples();
			for(int j=0;j<v.size();j++){
				Hashtable<String,Object> ht=(Hashtable<String,Object>)v.elementAt(j);
				if(strClusteringKeyValue.equals(ht.get(ckey).toString())){
					for(String s:indexColumn){
						oldKeys.add(ht.get(s));
					}
					for(int k=0;k<colNames.size();k++){
						ht.put(colNames.get(k).toString(),colValues.get(k));
					}
					v.setElementAt(ht, j);
					p.setTuples(v);
					break outerloop;
				}
			}
		}
		t.savePageToFile(p);
		for(int i=0;i<indexName.size();i++){
			BPlusTree b=loadTree(strTableName+indexName.get(i));
			try {
				b.deleteKey(oldKeys.get(i), false);
				b.insertKey(htblColNameValue.get(indexColumn.get(i)), p.name, true);
			} catch (NumberFormatException  | IllegalStateException | IOException | InvalidBTreeStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			saveTree(b, strTableName+indexName.get(i));
		}
		
		//throw new DBAppException("not implemented yet");
	}


	// following method could be used to delete one or more rows.
	// htblColNameValue holds the key and value. This will be used in search
	// to identify which rows/tuples to delete.
	// htblColNameValue enteries are ANDED together
	public void deleteFromTable(String strTableName,
								Hashtable<String,Object> htblColNameValue) throws DBAppException{
		Table t=loadTableFromDisk(strTableName);
		Vector<String> pages=t.getPageNames();
		Page p=null;
		Iterator<String> pItr=pages.iterator();
		Iterator<Object> tItr;
		List<Object> colNames = Arrays.asList(htblColNameValue.keySet().toArray());
		List<Object> colValues = Arrays.asList(htblColNameValue.values().toArray());
		Vector<String> indexName=new Vector<String>();
		Vector<String> indexColumn=new Vector<String>();
		
		FileReader fr;
		try {
			fr = new FileReader("metadata.csv");
			BufferedReader br = new BufferedReader(fr);
			String line ;
			while((line = br.readLine()) != null){
				String[] arr= line.split(",");
				if((arr[0]+".class").equals(strTableName)){
					int index=colNames.indexOf(arr[1]);
					if(index==-1){
						continue;
					}
					if(!arr[4].equals("null")){
						indexName.add(arr[4]);
						indexColumn.add(arr[1]);
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while(pItr.hasNext()){
			p=t.loadPageFromFile(pItr.next());
			Vector<Object> Tuples=p.getTuples();
			tItr=Tuples.iterator();
			while(tItr.hasNext()){
				boolean flag=true;
				Hashtable ht=(Hashtable)tItr.next();
				for(int i=0;i<colNames.size();i++){
					String key=colNames.get(i).toString();
					if(!colValues.get(i).equals(ht.get(key))){
						flag=false;
						break;
					}
				}
				if(flag){
					for(int i=0;i<indexName.size();i++){
						BPlusTree b=loadTree(strTableName+indexName.get(i));
						try {
							b.deleteKey(ht.get(indexColumn.get(i)), false);
						} catch (NumberFormatException  | IllegalStateException | IOException | InvalidBTreeStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						saveTree(b, strTableName+indexName.get(i));
					}
					tItr.remove();
				}
			}
			p.setTuples(Tuples);
			if(p.isEmpty()){
				t.deletePage(p.getName());
			}
			else{
				t.savePageToFile(p);
			}
		}
		saveTableToDisk(t);
		//throw new DBAppException("not implemented yet");
	}


	public Iterator selectFromTable(SQLTerm[] arrSQLTerms,
									String[]  strarrOperators) throws DBAppException{

		return null;
	}

	public Table loadTableFromDisk(String s){
		Table t=null;
		try {
			FileInputStream fileIn = new FileInputStream(s+".class");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			t = (Table) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			System.out.println("Table class not found");
			c.printStackTrace();
		}
		return t;
	}

	public void saveTableToDisk(Table t){
		try {
			FileOutputStream fileOut = new FileOutputStream(t.name+".class");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(t);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
			return;
		}
	}

	public void deleteTableFile(String s) throws DBAppException{
		File file = new File(s+".class");
		if (file.exists()) {
			Table t=loadTableFromDisk(s);
			Vector<String> temp=new Vector<String>();
			Vector<String> trees=new Vector<String>();
			for(String name : t.getPageNames()){
				temp.add(name);
			}
			for(String name : temp){
				t.deletePage(name);
			}
            file.delete();
			try {
				File metadata = new File("metadata.csv");
				FileReader fr = new FileReader(metadata);
				BufferedReader br = new BufferedReader(fr);

				String line;
				File tmpFile = null;
				while((line = br.readLine()) != null){
					String[] lineValues = line.split(",");
					if((lineValues[0] + ".class").equals(s+".class")){
						if(!lineValues[4].equals("null")){
							trees.add(lineValues[4]);
						}
						tmpFile = deleteLine(metadata, lineValues[0]);
					}
				}
				br.close();


				if(!metadata.delete()){
					throw new DBAppException("meta data not deleted");
				}
				if(!tmpFile.renameTo(metadata)){
					throw new DBAppException("temp file not renamed");
				}


			} catch (IOException e) {
				e.printStackTrace();
			}
			for(String tree:trees){
				file=new File(tree);
				file.delete();
			}
		} else {
			throw new DBAppException("Table file not found: " + s);
		}



	}

	public BPlusTree loadTree(String s){
		BPlusTree t=null;
		try {
			FileInputStream fileIn = new FileInputStream(s+".class");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			t = (BPlusTree) in.readObject();
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

	public void saveTree(BPlusTree t,String indexName){
		try {
			FileOutputStream fileOut = new FileOutputStream(indexName+".class");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(t);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
			return;
		}
	}




	// helper method to delete lines
	public File deleteLine(File f, String l) {
		File tempFile = new File("myTempFile.csv");

		try {

			BufferedReader reader = new BufferedReader(new FileReader(f));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			String currentLine;

			while ((currentLine = reader.readLine()) != null) {
				String[] lineValues2 = currentLine.split(",");

				if ((lineValues2[0]).equals(l)) continue;
				writer.write(currentLine);
				writer.newLine();
			}
			reader.close();
			writer.close();

			return tempFile;
		}
		catch (IOException e){
			throw new RuntimeException();
		}
	}

	public static void main( String[] args ){

		try{
			String strTableName = "Student";
			DBApp	dbApp = new DBApp( );

			Hashtable htblColNameType = new Hashtable( );
			htblColNameType.put("id", "java.lang.Integer");
			htblColNameType.put("name", "java.lang.String");
			htblColNameType.put("gpa", "java.lang.double");
			dbApp.createTable( strTableName, "id", htblColNameType );
			dbApp.createIndex( strTableName, "gpa", "gpaIndex" );

			Hashtable htblColNameValue = new Hashtable( );
			htblColNameValue.put("id", Integer.valueOf( 2343432 ));
			htblColNameValue.put("name", new String("Ahmed Noor" ) );
			htblColNameValue.put("gpa", Double.valueOf( 0.95 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id", Integer.valueOf( 453455 ));
			htblColNameValue.put("name", new String("Ahmed Noor" ) );
			htblColNameValue.put("gpa",Double.valueOf( 0.95 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id", Integer.valueOf( 5674567 ));
			htblColNameValue.put("name", new String("Dalia Noor" ) );
			htblColNameValue.put("gpa", Double.valueOf( 1.25 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id",Integer.valueOf( 23498 ));
			htblColNameValue.put("name", new String("John Noor" ) );
			htblColNameValue.put("gpa", Double.valueOf( 1.5 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id", Integer.valueOf( 78452 ));
			htblColNameValue.put("name", new String("Zaky Noor" ) );
			htblColNameValue.put("gpa", Double.valueOf( 0.88 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			SQLTerm[] arrSQLTerms;
			arrSQLTerms = new SQLTerm[2];
			arrSQLTerms[0]._strTableName =  "Student";
			arrSQLTerms[0]._strColumnName=  "name";
			arrSQLTerms[0]._strOperator  =  "=";
			arrSQLTerms[0]._objValue     =  "John Noor";

			arrSQLTerms[1]._strTableName =  "Student";
			arrSQLTerms[1]._strColumnName=  "gpa";
			arrSQLTerms[1]._strOperator  =  "=";
			arrSQLTerms[1]._objValue     =  Double.valueOf( 1.5 );

			String[]strarrOperators = new String[1];
			strarrOperators[0] = "OR";
			// select * from Student where name = "John Noor" or gpa = 1.5;
			Iterator resultSet = dbApp.selectFromTable(arrSQLTerms , strarrOperators);
		}
		catch(Exception exp){
			exp.printStackTrace( );
		}
	}

}

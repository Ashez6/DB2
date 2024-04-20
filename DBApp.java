
/** * @author Wael Abouelsaadat */




import java.io.*;
import java.util.*;

import resources.BPTree.*;

@SuppressWarnings({"rawtypes","unchecked"})
public class DBApp {
	ArrayList<String> myTables = new ArrayList<>();

	ArrayList<Ref> oldrefs=new ArrayList<Ref>();
	ArrayList<Ref> newrefs=new ArrayList<Ref>();
	public static  File f;

	public DBApp( ){
		init();
	}

	public ArrayList<String> getMyTables() {
		return myTables;
	}

	public void setMyTables(ArrayList<String> myTables) {
		this.myTables = myTables;
	}


	// this does whatever initialization you would like
	// or leave it empty if there is no code you want to
	// execute at application startup
	public void init( ){
		f = new File("metadata.csv");
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

		Object[] arr=htblColNameType.values().toArray();
		for(Object s:arr){
			if(!s.toString().equals("java.lang.Integer") && !s.toString().equals("java.lang.Double")
					&& !s.toString().equals("java.lang.String")){
				throw new DBAppException("Invalid column datatype.");
			}
		}

		Object[] arr2=htblColNameType.keySet().toArray();
		boolean flag=false;
		for(int i=0;i<arr2.length;i++){
			if(arr2[i].toString().equals(strClusteringKeyColumn)){
				flag=true;
			}
			for(int j=i+1;j<arr2.length;j++){
				if(arr2[i].equals(arr2[j])){
					throw new DBAppException("Duplicate column names in table.");
				}
			}
		}

		if(!flag){
			throw new DBAppException("Clustering key column not found.");
		}

		Table t=new Table(strTableName, strClusteringKeyColumn);
		saveTableToDisk(t);

		// Writing to the CSV file
		FileWriter csvFile;
		try {
			csvFile = new FileWriter(f, true);

			myTables.add(strTableName);

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


	}


	// following method creates a B+tree index
	public void createIndex(String   strTableName,
							String   strColName,
							String   strIndexName) throws DBAppException{

		boolean tableExist=false;
		boolean columnExist=false;
		String s="";

		String datatype="";
		try {
			//File tmpFile = new File("tempFile.csv");
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			// FileWriter fw = new FileWriter("metadata.csv");
			// BufferedWriter bw = new BufferedWriter(fw);

			String line;
			while((line = br.readLine()) != null){

				String[] lineValues = line.split(",");

				if(lineValues[0].equals(strTableName)){
					tableExist=true;
				}

				if(lineValues[1].equals(strColName) && lineValues[0].equals(strTableName)){
					columnExist=true;
					if(!lineValues[4].equals("null")){
						throw new DBAppException("There is an existing index on this column.");
					}
					lineValues[4] = strIndexName;
					lineValues[5] = "B+tree";
					datatype=lineValues[2];
					String newLine = String.join(",", lineValues );
					s+=newLine+"\n";

					// bw.write(newLine);
					// bw.newLine();
				}
				else {
					s+=line+"\n";
					// bw.write(line);
					// bw.newLine();
				}
			}
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(s);

			br.close();
			bw.close();

			// if(!mData.delete()){
			// 	throw new DBAppException("meta data not deleted");
			// }
			// if(!tmpFile.renameTo(mData)){
			// 	throw new DBAppException("temp file not renamed");
			// }

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if(!tableExist){
			throw new DBAppException("Table does not exist.");
		}

		if(!columnExist){
			throw new DBAppException("Column does not exist.");
		}

		File index =new File(strTableName+strIndexName+".class");
		if(index.exists()){
			throw new DBAppException("Index name already exists.");
		}

		Properties properties = new Properties();
		int n=0;
		try (FileInputStream input = new FileInputStream("resources/DBApp.config")) {
			properties.load(input);
			n = Integer.parseInt(properties.getProperty("MaximumKeysinNode"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		BPTree b;
		switch(datatype){
			case "java.lang.String": b=new BPTree<String>(n);
				break;
			case "java.lang.Integer": b=new BPTree<Integer>(n);
				break;
			case "java.lang.Double": b=new BPTree<Double>(n);
				break;
			default: throw new DBAppException("Unsupported datatype.");
		}

		Table t=loadTableFromDisk(strTableName);
		for(String page:t.getPageNames()){
			Page p=t.loadPageFromFile(page);
			for(int i=0;i<p.getTuples().size();i++){
				Hashtable ht=(Hashtable)p.getTuples().get(i);
				b.insert((Comparable)ht.get(strColName), new Ref(page,i));
			}
		}
		saveTree(b, strTableName+strIndexName);


	}


	// following method inserts one row only.
	// htblColNameValue must include a value for the primary key
	public void insertIntoTable(String strTableName,
								Hashtable<String,Object>  htblColNameValue) throws DBAppException{

		Vector<String> indexName=new Vector<String>();
		Vector<String> indexColumn=new Vector<String>();
		List<Object> colNames = Arrays.asList(htblColNameValue.keySet().toArray());
		List<Object> colValues = Arrays.asList(htblColNameValue.values().toArray());
		int len=0;
		boolean tableExist=false;

		FileReader fr;
		try {
			fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line ;
			while((line = br.readLine()) != null){
				String[] arr= line.split(",");
				if((arr[0]).equals(strTableName)){
					tableExist=true;
					if(colNames.contains(arr[1])){
						int index=colNames.indexOf(arr[1]);
						len++;
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

		if(!tableExist){
			throw new DBAppException("Table does not exist.");
		}

		if(colNames.size()!=len){
			throw new DBAppException("Invalid column name.");
		}
		Table t = loadTableFromDisk(strTableName);

		int num1, num2;
		num1 = BinaryPageSearch(t.pageMin,t.pageMax, htblColNameValue.get(t.getCKey()));
		if (num1 != -1){
			Page p = t.loadPageFromFile(t.getPageNames().get(num1));
			num2 = p.BinaryTupleSearch(t.getCKey(), htblColNameValue.get(t.getCKey()));
			if (num2 != -1)
				throw new DBAppException("identical primary key insertion");
		}

		Ref r=insertHelper(strTableName, htblColNameValue);

		for(int i=0;i<indexName.size();i++){
			BPTree b=loadTree(strTableName+indexName.get(i));
			b.updateInsertRefNonKey(oldrefs, newrefs);
			b.insert((Comparable)htblColNameValue.get(indexColumn.get(i)), r);
			saveTree(b, strTableName+indexName.get(i));
		}


	}

	public Ref insertHelper(String strTableName,
							Hashtable<String,Object>  htblColNameValue){

		Table t = loadTableFromDisk(strTableName);
		String ckey =t.getCKey();
		Object value=htblColNameValue.get(ckey);

		if(t.getNPages()==0){
			t.createPage();
			Page p=t.loadPageFromFile(t.getPageNames().lastElement());
			p.insert(htblColNameValue);
			Vector<Object> pMin=new Vector<>();
			pMin.add(value);
			Vector<Object> pMax=new Vector<>();
			pMax.add(value);
			t.setMinVector(pMin);
			t.setMaxVector(pMax);
			t.savePageToFile(p);
			saveTableToDisk(t);
			return new Ref(p.getName(),0);
		}

		int targetpage= BinaryPageSearch(t.getMinVector(), t.getMaxVector(), value);
		Page p=t.loadPageFromFile(t.getPageNames().get(targetpage));
		int index=p.getInsertLoc(ckey, value);

		Vector<Object> pMin=t.getMinVector();
		Vector<Object> pMax=t.getMaxVector();

		Properties properties = new Properties();
		int n=0;
		try (FileInputStream input = new FileInputStream("resources/DBApp.config")) {
			properties.load(input);
			n = Integer.parseInt(properties.getProperty("MaximumRowsCountinPage"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (index == n){
			t.createPage();
			p=t.loadPageFromFile(t.getPageNames().lastElement());
			p.insert(htblColNameValue);
			pMin.add(value);
			pMax.add(value);
			t.setMinVector(pMin);
			t.setMaxVector(pMax);
			t.savePageToFile(p);
			saveTableToDisk(t);
			return new Ref(p.getName(),0);
		}

		if(!p.isFull()){
			Vector<Object> v=p.getTuples();
			for(int i=index;i<p.getTuples().size();i++){
				oldrefs.add(new Ref(p.getName(), i));
			}
			v.insertElementAt(htblColNameValue, index);
			p.setTuples(v);
			for(int i=index+1;i<p.getTuples().size();i++){
				newrefs.add(new Ref(p.getName(), i));
			}

			if(((Comparable)value).compareTo(pMin.get(targetpage))<0){
				t.setPageMin(targetpage, value);
			}
			else if(((Comparable)value).compareTo(pMax.get(targetpage))>0){
				t.setPageMax(targetpage, value);
			}
		}
		else{
			int currPage = targetpage;
			boolean flag = false;
			if(t.getPageNames().lastElement().equals(p.getName())){
				t.createPage();
				flag = true;
			}
			String tempName=t.getPageNames().get(++currPage);
			Page tempPage=t.loadPageFromFile(tempName);
			Object last=p.getLastTuple();
			for(int i = index;i < p.getTuples().size();i++){
				oldrefs.add(new Ref(p.getName(), i));
			}
			Vector<Object> v = p.getTuples();
			v.insertElementAt(htblColNameValue, index);
			p.setTuples(v);
			p.delete(last);
			for(int i = index+1;i < p.getTuples().size();i++){
				newrefs.add(new Ref(p.getName(), i));
			}
			newrefs.add(new Ref(tempName, 0));

			Object newMax=((Hashtable)p.getLastTuple()).get(ckey);
			t.setPageMax(currPage-1, newMax);
			Object first=last;
			Object newMin=((Hashtable)first).get(ckey);

			while(tempPage.isFull()){
				last=tempPage.getLastTuple();
				for(int i=0;i<tempPage.getTuples().size();i++){
					oldrefs.add(new Ref(tempPage.getName(), i));
				}
				v=tempPage.getTuples();
				v.insertElementAt(first, 0);
				tempPage.setTuples(v);
				tempPage.delete(last);

				for(int i=1;i<tempPage.getTuples().size();i++){
					newrefs.add(new Ref(tempPage.getName(), i));
				}
				newrefs.add(new Ref(tempName, 0));
				t.setPageMin(currPage, newMin);
				t.savePageToFile(tempPage);
				newMax=((Hashtable)p.getLastTuple()).get(ckey);
				t.setPageMax(currPage, newMax);
				first=last;
				newMin=((Hashtable)first).get(ckey);
				if(t.getPageNames().lastElement().equals(tempPage.getName())){
					t.createPage();
					flag=true;
				}
				tempPage=t.loadPageFromFile(t.getPageNames().get(++currPage));
			}
			v=tempPage.getTuples();
			for(int i=0;i<tempPage.getTuples().size();i++){
				oldrefs.add(new Ref(tempPage.getName(), i));
			}
			v.insertElementAt(first, 0);
			tempPage.setTuples(v);
			for(int i=1;i<tempPage.getTuples().size();i++){
				newrefs.add(new Ref(tempPage.getName(), i));
			}
			t.setPageMin(currPage, newMin);
			if(flag){
				t.setPageMax(currPage, newMin);
			}
			t.savePageToFile(tempPage);
		}
		t.savePageToFile(p);
		saveTableToDisk(t);
		return new Ref(p.getName(),index);

	}

	public int BinaryPageSearch(Vector<Object> pMin,Vector<Object> pMax,Object o){
		int max=pMin.size()-1;
		int min=0;
		int mid;
		while(min<=max){
			if(min==max){
				return min;
			}
			mid=(max+min)/2;
			Comparable low = (Comparable) pMin.elementAt(mid);
			Comparable high = (Comparable) pMax.elementAt(mid);
			if((low).compareTo(o)<=0 && (high).compareTo(o)>=0)
				return mid;
			else if((low).compareTo(o)>0 )
				max=mid-1;
			else
				min=mid+1;
		}
		return -1;
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
		boolean[] colExist=new boolean[colNames.size()];
		List<Object> colValues = Arrays.asList(htblColNameValue.values().toArray());
		String checktype=null;
		boolean tableExist=false;

		FileReader fr;
		try {
			fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line ;
			while((line = br.readLine()) != null){
				String[] arr= line.split(",");
				if((arr[0]).equals(strTableName)){
					tableExist=true;
					if (colNames.contains(arr[1]) && arr[3].equals("True"))
						throw new DBAppException("Clustering key should not be updated");

					if(colNames.contains(arr[1])){
						colExist[colNames.indexOf(arr[1])]=true;
					}

					if(arr[3].equals("True")){
						checktype=arr[2];
					}
					int index=colNames.indexOf(arr[1]);
					if(index==-1){
						continue;
					}
					if(!arr[4].equals("null")){
						indexName.add(arr[4]);
						indexColumn.add(arr[1]);
					}

					try{
						String type=colValues.get(index).getClass().getName();
						if(!type.equals(arr[2])){
							throw new DBAppException("Invalid input datatypes.");
						}
					}
					catch(NullPointerException e){
						throw new DBAppException("All columns should have a not null value.");
					}

				}
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}


		if(!tableExist){
			throw new DBAppException("Table does not exist.");
		}

		for(boolean b:colExist){
			if(b!=true){
				throw new DBAppException("Column(s) does not exist.");
			}
		}

		Table t=loadTableFromDisk(strTableName);
		String ckey=t.getCKey();

		//get target page
		int target;
		Object o=strClusteringKeyValue;
		if(checktype.equals("java.lang.Integer")){
			o=Integer.parseInt(strClusteringKeyValue);
		}else if (checktype.equals("java.lang.Double")){
			o=Double.parseDouble(strClusteringKeyValue);
		}
		target=BinaryPageSearch(t.getMinVector(), t.getMaxVector(), o);
		if(target==-1){
			throw new DBAppException("Clustering key value not found.");
		}
		Page p=t.loadPageFromFile(t.getPageNames().get(target));

		Vector<Object> oldKeys=new Vector<>();
		Vector<Ref> Refs=new Vector<>();

		Vector<Object> v=p.getTuples();
		int j=p.BinaryTupleSearch(ckey, o);
		if(j==-1){
			throw new DBAppException("Clustering key value not found.");
		}
		Hashtable<String,Object> ht=(Hashtable<String,Object>)v.elementAt(j);
		for(String s:indexColumn){
			oldKeys.add(ht.get(s));
			Refs.add(new Ref(p.getName(), j));
		}
		for(int k=0;k<colNames.size();k++){
			ht.put(colNames.get(k).toString(),colValues.get(k));
		}
		v.setElementAt(ht, j);
		p.setTuples(v);

		t.savePageToFile(p);
		for(int i=0;i<indexName.size();i++){
			BPTree b=loadTree(strTableName+indexName.get(i));
			b.update((Comparable)oldKeys.get(i),(Comparable)htblColNameValue.get(indexColumn.get(i)), Refs.get(i), Refs.get(i));
			saveTree(b, strTableName+indexName.get(i));
		}
		saveTableToDisk(t);
	}


	// following method could be used to delete one or more rows.
	// htblColNameValue holds the key and value. This will be used in search
	// to identify which rows/tuples to delete.
	// htblColNameValue enteries are ANDED together
	public void deleteFromTable(String strTableName,
								Hashtable<String,Object> htblColNameValue) throws DBAppException{

		boolean tableExist=false;
		List<Object> colNames = Arrays.asList(htblColNameValue.keySet().toArray());
		boolean[] colExist=new boolean[colNames.size()];
		List<Object> colValues = Arrays.asList(htblColNameValue.values().toArray());
		Vector<String> indexName=new Vector<String>();
		Vector<String> indexColumn=new Vector<String>();
		Vector<String> allColName=new Vector<String>();
		Vector<String> allIndexName=new Vector<String>();

		FileReader fr;
		try {
			fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			String line ;
			while((line = br.readLine()) != null){
				String[] arr= line.split(",");
				if((arr[0]).equals(strTableName)){
					tableExist=true;

					if(colNames.contains(arr[1])){
						colExist[colNames.indexOf(arr[1])]=true;
					}

					if(!arr[4].equals("null")){
						allColName.add(arr[1]);
						allIndexName.add(arr[4]);

						int index=colNames.indexOf(arr[1]);
						if(index==-1){
							continue;
						}

						indexName.add(arr[4]);
						indexColumn.add(arr[1]);
					}

					int index=colNames.indexOf(arr[1]);
					if(index!=-1){
						String type=colValues.get(index).getClass().getName();
						if(!type.equals(arr[2])){
							throw new DBAppException("Invalid input datatypes.");
						}
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(!tableExist){
			throw new DBAppException("Table does not exist.");
		}

		for(boolean b:colExist){
			if(!b){
				throw new DBAppException("Column(s) does not exist.");
			}
		}


		Table t=loadTableFromDisk(strTableName);

		if(colNames.isEmpty()){
			for (int i = 0; i < t.getPageNames().size(); i++)
				t.deletePage(t.getPageNames().get(i));

			t.NPages = 0;
			saveTableToDisk(t);
			return;
		}
		String ckey=t.cKey;
		Vector<String> pages=t.getPageNames();
		Page p;
		Iterator<String> pItr=pages.iterator();
		Iterator<Object> tItr;

		if(allColName.containsAll(colNames)){
			Set<Ref> intersection =null;
			boolean first=true;
			for(int i=0;i<indexName.size();i++){
				Object key=htblColNameValue.get(indexColumn.get(i));
				BPTree b=loadTree(strTableName+indexName.get(i));
				ArrayList a=b.searchDuplicates((Comparable)key);
				Set<Ref> set = new HashSet<>(a);
				if(first){
					intersection=new HashSet<>(set);
					first=false;
				}
				else{
					intersection.retainAll(set);
				}
			}
			Vector<Ref> refs= new Vector(intersection);
			Vector<Ref> refs2= new Vector(intersection);
			for(Ref r:refs){
				r.setTableName(strTableName);
			}
			Collections.sort(refs);

			for(int i=refs.size()-1;i>=0;i--){
				Ref r=refs.get(i);
				String s=r.getPage();
				int index=r.getIndexInPage();
				p=t.loadPageFromFile(s);
				Hashtable ht=(Hashtable)p.getTuples().get(index);
				//remove from all trees
				for(int j=0;j<allColName.size();j++){
					Object key=ht.get(allColName.get(j));
					BPTree b=loadTree(strTableName+allIndexName.get(j));
					b.delete((Comparable)key, r);
					for(int k=index+1;k<p.getTuples().size();k++){
						oldrefs.add(new Ref(p.getName(), k));
						newrefs.add(new Ref(p.getName(), k-1));
					}

					b.updateDeleteRefNonKey(oldrefs, newrefs);

					saveTree(b, strTableName+allIndexName.get(j));
				}

			}

			for(int i=0;i<refs2.size();i++){
				Ref r=refs2.get(i);
				String s=r.getPage();
				int index=r.getIndexInPage();
				p=t.loadPageFromFile(s);
				p.remove(index);
				for(int j=i+1;j<refs2.size();j++){
					Ref r1=refs2.get(j);
					String s1=r1.getPage();
					int index1=r1.getIndexInPage();
					if(s.equals(s1) && index1>index){
						refs2.set(j, new Ref(s1, index1-1));
					}
				}


				if(p.isEmpty()){
					t.deletePage(p.getName());
				}
				else{
					t.setPageMax(t.pageNames.indexOf(p.getName()),((Hashtable)p.getLastTuple()).get(ckey));
					t.setPageMin(t.pageNames.indexOf(p.getName()),((Hashtable)p.getFirstTuple()).get(ckey));
					t.savePageToFile(p);
				}
			}


		}
		else{
			while(pItr.hasNext()){
				p=t.loadPageFromFile(pItr.next());
				Vector<Object> Tuples=p.getTuples();
				tItr=Tuples.iterator();
				int j=-1;
				int del=0;
				int size=p.getTuples().size();
				while(tItr.hasNext()){
					boolean flag=true;
					Hashtable ht=(Hashtable)tItr.next();
					j++;
					for(int i=0;i<colNames.size();i++){
						String key=colNames.get(i).toString();
						if(!colValues.get(i).equals(ht.get(key))){
							flag=false;
							break;
						}
					}
					if(flag){
						for(int i=0;i<allColName.size();i++){
							BPTree b=loadTree(strTableName+allIndexName.get(i));
							b.delete((Comparable)ht.get(allColName.get(i)), new Ref(p.getName(),j-del));
							for(int k=j-del+1;k<size;k++){
								oldrefs.add(new Ref(p.getName(), k));
								newrefs.add(new Ref(p.getName(), k-1));
							}
							b.updateDeleteRefNonKey(oldrefs, newrefs);

							saveTree(b, strTableName+allIndexName.get(i));
						}

						tItr.remove();
						size--;
						del++;
					}
				}

				p.setTuples(Tuples);

				if(p.isEmpty()){
					pItr.remove();
					File file = new File(p.getName());
					if (file.exists()) {
						file.delete();
					} else {
						System.out.println("Page file not found: " + p.getName());
					}
				}
				else{
					t.setPageMax(t.pageNames.indexOf(p.getName()),((Hashtable)p.getLastTuple()).get(ckey));
					t.setPageMin(t.pageNames.indexOf(p.getName()),((Hashtable)p.getFirstTuple()).get(ckey));
					t.savePageToFile(p);
				}
			}

		}
		saveTableToDisk(t);
	}


	public Iterator selectFromTable(SQLTerm[] arrSQLTerms,
									String[]  strarrOperators) throws DBAppException{

		if(strarrOperators != null){
			if(arrSQLTerms.length != strarrOperators.length + 1){
				throw new DBAppException("incompatible length of terms with operators");
			}
		}

		int nterms=arrSQLTerms.length;
		Vector<Hashtable>[] results=new Vector[nterms];

		for(int i=0;i<nterms;i++){
			results[i] = new Vector<>();
			String indexName=null;
			FileReader fr;
			boolean columnExist = false;
			try {
				fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String line ;
				while((line = br.readLine()) != null){
					String[] arr= line.split(",");
					if((arr[0]).equals(arrSQLTerms[i]._strTableName) && (arr[1]).equals(arrSQLTerms[i]._strColumnName)){
						columnExist = true;
						String type=arrSQLTerms[i]._objValue.getClass().getName();
						if(!type.equals(arr[2])){
							throw new DBAppException("Invalid search datatypes.");
						}
						if(!arr[4].equals("null")){
							indexName=arr[4];
						}

						break;
					}
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!columnExist){
				throw new DBAppException("The selected column does not exist");
			}

			Comparable key=(Comparable)arrSQLTerms[i]._objValue;
			Table t=loadTableFromDisk(arrSQLTerms[i]._strTableName);
			if(indexName!=null && !arrSQLTerms[i]._strOperator.equals("!=")){
				BPTree b=loadTree(arrSQLTerms[i]._strTableName+indexName);
				Vector<Ref> refs=new Vector();
				BPTreeLeafNode l;
				BPTreeLeafNode limit;
				switch (arrSQLTerms[i]._strOperator) {
					case ">":
						l=b.searchGreaterthan(key);
						if(l != null) {
							for (int j = 0; j < l.getNumberOfKeys(); j++) {
								if (l.getKey(j).compareTo(key) > 0) {
									refs.add(l.getRecord(j));

									if (l.getOverflow(j) != null) refs.addAll(l.getOverflow(j));
								}
							}
							l = l.getNext();
						}
						while(l!=null){
							for (int j = 0; j < l.getNumberOfKeys(); j++) {
								refs.add(l.getRecord(j));
								if(l.getOverflow(j) != null) refs.addAll(l.getOverflow(j));
							}
							l=l.getNext();
						}
						break;

					case ">=":
						l=b.searchGreaterEqual(key);
						if(l != null) {
							for (int j = 0; j < l.getNumberOfKeys(); j++) {
								if (l.getKey(j).compareTo(key) >= 0) {
									refs.add(l.getRecord(j));
									if (l.getOverflow(j) != null) refs.addAll(l.getOverflow(j));
								}
							}
							l = l.getNext();
						}
						while(l!=null){
							for (int j = 0; j < l.getNumberOfKeys(); j++) {
								refs.add(l.getRecord(j));
								if(l.getOverflow(j) != null) refs.addAll(l.getOverflow(j));
							}
							l=l.getNext();
						}
						break;

					case "<":
						l=b.searchMinNode();
						limit=b.searchGreaterEqual(key);

						if(limit != null) {
							while (!l.equals(limit)) {
								for (int j = 0; j < l.getNumberOfKeys(); j++) {
									refs.add(l.getRecord(j));
									if (l.getOverflow(j) != null) refs.addAll(l.getOverflow(j));
								}
								l = l.getNext();
							}

							for (int j = 0; j < limit.getNumberOfKeys(); j++) {
								if (limit.getKey(j).compareTo(key) < 0) {
									refs.add(limit.getRecord(j));
									if (l.getOverflow(j) != null) refs.addAll(l.getOverflow(j));
								}
							}
						}
						break;

					case "<=":
						l=b.searchMinNode();
						limit=b.searchGreaterthan(key);

						if(limit != null){
							while(!limit.equals(l)){
								for (int j = 0; j < l.getNumberOfKeys(); j++) {
									refs.add(l.getRecord(j));
									if(l.getOverflow(j) != null) refs.addAll(l.getOverflow(j));
								}
								l=l.getNext();
							}
							for (int j = 0; j < limit.getNumberOfKeys(); j++) {
								if (limit.getKey(j).compareTo(key) <= 0 ) {
									refs.add(limit.getRecord(j));
									if(l.getOverflow(j) != null) refs.addAll(l.getOverflow(j));
								}
							}
						}
						else{
							while(l != null){
								for (int j = 0; j < l.getNumberOfKeys(); j++) {
									refs.add(l.getRecord(j));
									if(l.getOverflow(j) != null) refs.addAll(l.getOverflow(j));
								}
								l=l.getNext();
							}
						}


						break;

					case "=":
						refs.addAll(b.searchDuplicates(key));
						break;

					default: throw new DBAppException("Unsupported operator");
				}

				for(Ref r:refs){
					Page p=t.loadPageFromFile(r.getPage());

					Hashtable ht=(Hashtable)p.getTuples().get(r.getIndexInPage());
					results[i].add(ht);
				}

			}
			else{
				for(String pname:t.pageNames){
					Page p=t.loadPageFromFile(pname);
					for(Object o : p.getTuples()){
						Hashtable ht=(Hashtable)o;
						Comparable val=(Comparable)ht.get(arrSQLTerms[i]._strColumnName);
						switch (arrSQLTerms[i]._strOperator) {
							case ">":
								if(val.compareTo(key)>0)
									results[i].add(ht);
								break;

							case ">=":
								if(val.compareTo(key)>=0)
									results[i].add(ht);
								break;

							case "<":
								if(val.compareTo(key)<0)
									results[i].add(ht);
								break;

							case "<=":
								if(val.compareTo(key)<=0)
									results[i].add(ht);
								break;

							case "=":
								if(val.compareTo(key)==0)
									results[i].add(ht);
								break;

							case "!=":
								if(val.compareTo(key)!=0)
									results[i].add(ht);
								break;

							default: throw new DBAppException("Unsupported operator");
						}


					}
				}
			}

		}

		LinkedHashSet<Hashtable> output = new LinkedHashSet<>(results[0]);

		if(strarrOperators != null) {
			for (int i = 0; i < strarrOperators.length; i++) {

				LinkedHashSet<Hashtable> operand = new LinkedHashSet<>((results[i + 1]));


				switch (strarrOperators[i]) {
					case "OR":
						output.addAll(operand);
						break;

					case "AND":
						output.retainAll(operand);
						break;

					case "XOR":
						// in A and not in B
						Set<Hashtable> differenceSet1;
						differenceSet1 = new HashSet<>(output);
						differenceSet1.removeAll(operand);

						// in B and not in A
						Set<Hashtable> differenceSet2;
						differenceSet2 = new HashSet<>(operand);
						differenceSet2.removeAll(output);

						// Union difference 1 and difference 2 and place in output

						differenceSet1.addAll(differenceSet2);
						output = new LinkedHashSet<>(differenceSet1);

						break;
					default: throw new DBAppException("The only supported logical operators are the AND, OR and XOR :(");
				}
			}
		}
		Iterator oi = output.iterator();
		ArrayList<String> al = new ArrayList<>();
		while(oi.hasNext()){
			Tuple t = new Tuple((Hashtable) oi.next());
			al.add(t.toString());
		}

		return al.iterator();
	}



	public LinkedHashSet sortSet(LinkedHashSet<Hashtable> lhs, Comparable clusteringKey){

		Hashtable[] arr = (Hashtable[]) lhs.toArray();

		if (arr == null || arr.length <= 1) {
			return lhs;
		}
		int n = arr.length;
		boolean swapped;

		for (int i = 0; i < n - 1; i++) {
			swapped = false;
			for (int j = 0; j < n - i - 1; j++) {
				if (((Comparable)arr[j].get(clusteringKey)).compareTo(arr[j + 1].get(clusteringKey)) > 0) {
					// Swap arr[j] and arr[j + 1]
					Hashtable temp = arr[j];
					arr[j] = arr[j + 1];
					arr[j + 1] = temp;
					swapped = true;
				}
			}
			if (!swapped) {
				break; // If no swaps occurred, the array is already sorted
			}
		}

		return new LinkedHashSet<>(Arrays.asList(arr));
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
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);

				myTables.remove(s);

				String line;
				//File tmpFile = null;
				while((line = br.readLine()) != null){
					String[] lineValues = line.split(",");
					if((lineValues[0]).equals(s)){
						if(!lineValues[4].equals("null")){
							trees.add(s+lineValues[4]+".class");
						}
						deleteLine(lineValues[0]);
					}
				}
				br.close();


				// if(!metadata.delete()){
				// 	throw new DBAppException("meta data not deleted");
				// }
				// if(!tmpFile.renameTo(metadata)){
				// 	throw new DBAppException("temp file not renamed");
				// }


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

	public BPTree loadTree(String s){
		BPTree t=null;
		try {
			FileInputStream fileIn = new FileInputStream(s+".class");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			t = (BPTree) in.readObject();
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

	public void saveTree(BPTree t,String indexName){
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
	public void deleteLine(String l) {
		//File tempFile = new File("myTempFile.csv");
		String s="";

		try {

			BufferedReader reader = new BufferedReader(new FileReader(f));

			String currentLine;

			while ((currentLine = reader.readLine()) != null) {
				String[] lineValues2 = currentLine.split(",");

				if ((lineValues2[0]).equals(l)) continue;
				s+=currentLine+"\n";
				// writer.write(currentLine);
				// writer.newLine();
			}
			reader.close();
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(s);

			writer.close();


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
			htblColNameType.put("gpa", "java.lang.Double");
			dbApp.createTable( strTableName, "id", htblColNameType );
			dbApp.createIndex( strTableName, "gpa", "gpaIndex" );

			Hashtable htblColNameValue = new Hashtable( );
			htblColNameValue.put("id", Integer.valueOf( 2343432 ));
			htblColNameValue.put("name", new String("Ahmed Noor" ) );
			htblColNameValue.put("gpa", Double.valueOf( 1.5 ) );
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
			htblColNameValue.put("gpa", Double.valueOf( 2 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			htblColNameValue.clear( );
			htblColNameValue.put("id", Integer.valueOf( 78452 ));
			htblColNameValue.put("name", new String("Zaky Noor" ) );
			htblColNameValue.put("gpa", Double.valueOf( 0.88 ) );
			dbApp.insertIntoTable( strTableName , htblColNameValue );

			SQLTerm[] arrSQLTerms;
			arrSQLTerms = new SQLTerm[2];
			arrSQLTerms[0]=new SQLTerm();
			arrSQLTerms[1]=new SQLTerm();
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
			while(resultSet.hasNext()){
				System.out.println(resultSet.next());
			}
		}
		catch(Exception exp){
			exp.printStackTrace( );
		}
	}

}

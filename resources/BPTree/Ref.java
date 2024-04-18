package resources.BPTree;

import java.io.Serializable;

@SuppressWarnings("rawtypes")
public class Ref implements Comparable, Serializable {

	/**
	 * This class represents a pointer to the record. It is used at the leaves of
	 * the B+ tree
	 */
	private static final long serialVersionUID = 1L;
	private int indexInPage;
	private String pageName;
	public String tablename;

	public Ref(String pageName, int indexInPage) {
		this.pageName = pageName;
		this.indexInPage = indexInPage;
	}

	/**
	 * @return the page at which the record is saved on the hard disk
	 */
	public String getPage() {
		return pageName;
	}

	/**
	 * @return the index at which the record is saved in the page
	 */
	public int getIndexInPage() {
		return indexInPage;
	}

	public String getFileName() {
		return this.pageName;

	}

	public Boolean isEqual(Ref ref) {
		if (this.pageName.equals(ref.pageName) && this.indexInPage == ref.indexInPage)
			return true;

		return false;
	}

	public String toString() {
		String s = "";
		s += "PageName:" + this.getFileName() + "  RowIndex:" + this.getIndexInPage();
		return s;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		System.out.println("byeeee");
		int p1=Integer.parseInt(pageName.substring(tablename.length(),pageName.length()-6));
		int p2=Integer.parseInt(((Ref)o).pageName.substring(tablename.length(),pageName.length()-6));
		if(p1>p2){
			return 1;
		}else if(p1<p2){
			return -1;
		}else{
			if(indexInPage>((Ref)o).indexInPage){
				return 1;
			}else if(indexInPage<((Ref)o).indexInPage){
				return -1;
			}else{
				return 0;
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(pageName.equals(((Ref)o).pageName)){
			if(indexInPage==((Ref)o).indexInPage){
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		Integer i=indexInPage;
		return pageName.hashCode()+i.hashCode();
	}

	public String getTableName(){
		return tablename;
	}

	public void setTableName(String s){
		this.tablename= s;
	}
}
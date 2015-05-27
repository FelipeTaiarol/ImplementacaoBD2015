public class IndexData {
	
	private int indexKey;
	
	private TableDataBlock tableDataBlock;
	
	public IndexData(int indexKey, TableDataBlock tableDataBlock) {
		super();
		this.indexKey = indexKey;
		this.tableDataBlock = tableDataBlock;
	}
	
	public int getIndexKey() {
		return indexKey;
	}
	
	public void setIndexKey(int indexKey) {
		this.indexKey = indexKey;
	}
	
	public TableDataBlock getTableDataBlock() {
		return tableDataBlock;
	}
	
	public void setTableDataBlock(	TableDataBlock tableDataBlock) {
		this.tableDataBlock = tableDataBlock;
	}
	
	@Override
	public String toString() {
		return "[indexKey=" + indexKey + ", tableDataBlock="
				+ tableDataBlock.getId() + "]";
	}
	
}

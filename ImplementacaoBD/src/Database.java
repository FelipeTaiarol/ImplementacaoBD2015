import java.util.List;

public class Database {
	
	private IndexDataBlock bTreeRoot;
	private DataBlock firstDatablock;
	private int maxRecordsPerTableDataBlock = 4;
	private int maxIndexPerIndexDataBlock = 4;
	private int lastDatablockId = 1;
	
	public static void main(String[] args) {
		Database db = new Database();
		db.insertCustomer(3, "Joao");
		db.insertCustomer(4, "Maria");
		db.insertCustomer(1, "Felipe");
		db.insertCustomer(2, "Ana");
		db.insertCustomer(5, "Jorge");
		
		db.printDataFile();
	}
	
	private void split(	IndexLeafDataBlock leftDataBlock,
						IndexData overflowIndex) {
		IndexBranchDatablock branchDataBlock = new IndexBranchDatablock(
																		getNextDataBlockId());
		
		leftDataBlock.addIndexRecord(overflowIndex);
		
		int middle = maxIndexPerIndexDataBlock / 2;
		
		List<IndexData> leftList = leftDataBlock.getIndexRecords()
												.subList(0, middle);
		
		List<IndexData> rightList = leftDataBlock.getIndexRecords()
													.subList(	middle,
																maxIndexPerIndexDataBlock + 1);
		leftDataBlock.setIndexRecords(leftList);
		
		IndexLeafDataBlock rightDataBlock = new IndexLeafDataBlock(
																	getNextDataBlockId());
		
		rightDataBlock.setIndexRecords(rightList);
		
		branchDataBlock.setKey(rightList.get(0).getIndexKey());
		branchDataBlock.setLeftDataBlock(leftDataBlock);
		branchDataBlock.setRightDataBlock(rightDataBlock);
		
		getLastDataBlock().setNext(branchDataBlock);
		branchDataBlock.setNext(rightDataBlock);
		
	}
	
	public void insertCustomer(	int code, String name) {
		TableDataBlock dataBlock = getAvailableTableDataBlock();
		
		CustomerData data = new CustomerData(code, name);
		dataBlock.getRecords().add(data);
		
		createIndex(code, dataBlock);
	}
	
	private void createIndex(	int key, TableDataBlock tableDataBlock) {
		
		IndexData indexData = new IndexData(key, tableDataBlock);
		
		addToLeafDataBlock(indexData);
		
	}
	
	private void addToLeafDataBlock(IndexData indexData) {
		
		if (bTreeRoot == null) {
			bTreeRoot = new IndexLeafDataBlock(getNextDataBlockId());
			getLastDataBlock().setNext(bTreeRoot);
			((IndexLeafDataBlock) bTreeRoot).addIndexRecord(indexData);
			return;
		}
		
		if (bTreeRoot instanceof IndexLeafDataBlock) {
			IndexLeafDataBlock aux = (IndexLeafDataBlock) bTreeRoot;
			if (aux.getNumberOfRecords() >= maxIndexPerIndexDataBlock) {
				split(aux, indexData);
			} else {
				((IndexLeafDataBlock) bTreeRoot).addIndexRecord(indexData);
			}
		}
		
	}
	
	private TableDataBlock getAvailableTableDataBlock() {
		
		// se nenhum datablock foi criado ainda
		if (firstDatablock == null) {
			firstDatablock = new TableDataBlock(getNextDataBlockId());
			return (TableDataBlock) firstDatablock;
		}
		
		TableDataBlock tableDataBlock = getLastTableDataBlock();
		
		// se nenhum tabledatablock foi criado ainda cria um.
		if (tableDataBlock == null) {
			tableDataBlock = new TableDataBlock(getNextDataBlockId());
			getLastDataBlock().setNext(tableDataBlock);
		}
		
		// se o table datablock sendo considerado ja estiver cheio cria outro.
		if (tableDataBlock.getRecords().size() >= maxRecordsPerTableDataBlock) {
			tableDataBlock = new TableDataBlock(getNextDataBlockId());
			getLastDataBlock().setNext(tableDataBlock);
		}
		
		return tableDataBlock;
	}
	
	private TableDataBlock getLastTableDataBlock() {
		DataBlock dataBlock = firstDatablock;
		TableDataBlock tableDataBlock = null;
		do {
			if (dataBlock instanceof TableDataBlock) {
				tableDataBlock = (TableDataBlock) dataBlock;
			}
			dataBlock = dataBlock.getNext();
		} while (dataBlock != null);
		
		return tableDataBlock;
	}
	
	private DataBlock getLastDataBlock() {
		DataBlock dataBlock = firstDatablock;
		while (dataBlock.getNext() != null) {
			dataBlock = dataBlock.getNext();
		}
		return dataBlock;
		
	}
	
	private int getNextDataBlockId() {
		return lastDatablockId++;
	}
	
	public void printDataFile() {
		System.out.println(" -- Data File -- ");
		
		DataBlock dataBlock = firstDatablock;
		
		do {
			System.out.println(dataBlock);
		} while ((dataBlock = dataBlock.getNext()) != null);
		
		System.out.println("-- End --");
		
	}
}

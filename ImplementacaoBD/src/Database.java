import java.util.LinkedList;
import java.util.List;

public class Database {
	
	private IndexDataBlock bTreeRoot;
	private DataBlock firstDatablock;
	private int maxRecordsPerTableDataBlock = 4;
	private int maxIndexPerIndexDataBlock = 4;
	private int lastDatablockId = 1;
	
	public static void main(String[] args) {
		Database db = new Database();
		db.insertCustomer(37, "Joao");
		db.insertCustomer(50, "p8");
		db.insertCustomer(13, "Jorge");
		db.insertCustomer(61, "p6");
		db.insertCustomer(23, "p7");
		db.insertCustomer(17, "Ana");
		db.insertCustomer(32, "Maria");
		db.insertCustomer(8, "Felipe");
		// db.insertCustomer(9, "p9");
		//
		db.printDataFile();
	}
	
	/**
	 * faz o split do leaf data block em dois e retorna o branch datablock node
	 * que aponta pra eles.
	 * 
	 * @param leftDataBlock
	 * @param overflowIndex
	 * @return
	 */
	private BranchDataBlockNode splitLeaf(	IndexLeafDataBlock leftDataBlock,
											IndexData overflowIndex) {
		
		leftDataBlock.addIndexRecord(overflowIndex);
		
		int middle = maxIndexPerIndexDataBlock / 2;
		
		List<IndexData> leftList = leftDataBlock.getIndexRecords()
												.subList(0, middle);
		
		// se nao fizer isso vai dar um erro bizarro na hora de iterar na
		// sublist
		leftList = new LinkedList<IndexData>(leftList);
		
		List<IndexData> rightList = leftDataBlock.getIndexRecords()
													.subList(	middle,
																maxIndexPerIndexDataBlock + 1);
		// se nao fizer isso vai dar um erro bizarro na hora de iterar na
		// sublist
		rightList = new LinkedList<IndexData>(rightList);
		
		leftDataBlock.setIndexRecords(leftList);
		
		IndexLeafDataBlock rightDataBlock = new IndexLeafDataBlock(
																	getNextDataBlockId());
		
		rightDataBlock.setIndexRecords(rightList);
		
		BranchDataBlockNode node = new BranchDataBlockNode(
															rightList.get(0)
																		.getIndexKey(),
															leftDataBlock,
															rightDataBlock);
		
		getLastDataBlock().setNext(rightDataBlock);
		
		return node;
	}
	
	public void insertCustomer(	int code, String name) {
		TableDataBlock dataBlock = getAvailableTableDataBlock();
		
		CustomerData data = new CustomerData(code, name);
		dataBlock.getRecords().add(data);
		
		createIndex(code, dataBlock);
	}
	
	private void createIndex(	int key, TableDataBlock tableDataBlock) {
		
		IndexData indexData = new IndexData(key, tableDataBlock);
		
		addToIndexDataBlock(indexData);
		
	}
	
	/**
	 * adicinona o index no leaf datablock. se ele estiver cheio faz o split e
	 * retorna o branch data block.
	 * 
	 * @param leaf
	 * @param indexData
	 * @return
	 */
	private BranchDataBlockNode addToLeafDataBlock(	IndexLeafDataBlock leaf,
													IndexData indexData) {
		
		if (leaf.getNumberOfRecords() >= maxIndexPerIndexDataBlock) {
			return splitLeaf(leaf, indexData);
		} else {
			leaf.addIndexRecord(indexData);
		}
		
		return null;
	}
	
	private void addToIndexDataBlock(	IndexData indexData) {
		
		if (bTreeRoot == null) {
			bTreeRoot = new IndexLeafDataBlock(getNextDataBlockId());
			getLastDataBlock().setNext(bTreeRoot);
			((IndexLeafDataBlock) bTreeRoot).addIndexRecord(indexData);
			return;
		}
		
		// se o root é um Leaf adiciona nele. Caso de overflow
		// 'addToLeafDataBlock' fara o split de leaf e retornara o branch data
		// block node que aponta para os leafs criados no split. Nesse caso um
		// branch é criado, o node é adicionado a ele e ele passa a ser o root
		if (bTreeRoot instanceof IndexLeafDataBlock) {
			IndexLeafDataBlock aux = (IndexLeafDataBlock) bTreeRoot;
			
			BranchDataBlockNode branchNode = addToLeafDataBlock(aux, indexData);
			
			if (branchNode != null) {
				IndexBranchDatablock branch = new IndexBranchDatablock(
																		getNextDataBlockId());
				getLastDataBlock().setNext(branch);
				
				branch.addNode(branchNode);
				
				this.bTreeRoot = branch;
			}
			
			return;
		}
		
		if (bTreeRoot instanceof IndexBranchDatablock) {
			IndexBranchDatablock branch = (IndexBranchDatablock) bTreeRoot;
			
			this.addToBranchDataBlock(branch, indexData);
		}
		
	}
	
	private void addToBranchDataBlock(	IndexBranchDatablock branch,
										IndexData indexData) {
		
		BranchDataBlockNode branchNode = branch.getNodeForKey(indexData.getIndexKey());
		
		DataBlock dataBlock = null;
		
		// decide para qual lado da branch ir.
		if (indexData.getIndexKey() < branchNode.getKey()) {
			dataBlock = branchNode.getLeftDataBlock();
		} else {
			dataBlock = branchNode.getRightDataBlock();
		}
		
		// se o lado for uma folha, adiciona na folha.
		if (dataBlock instanceof IndexLeafDataBlock) {
			IndexLeafDataBlock leaf = (IndexLeafDataBlock) dataBlock;
			
			this.addToBranchLeaf(branch, leaf, indexData);
		}
		
	}
	
	private void addToBranchLeaf(	IndexBranchDatablock branch,
									IndexLeafDataBlock leaf, IndexData indexData) {
		BranchDataBlockNode node = addToLeafDataBlock(leaf, indexData);
		
		// ao adicionar na folha pode ser que um split aconteca, nesse caso
		// adiciona o novo branchNode no branch.
		if (node != null)
			this.addNodeToBranch(branch, node);
	}
	
	private IndexBranchDatablock addNodeToBranch(	IndexBranchDatablock branch,
													BranchDataBlockNode node) {
		
		branch.addNode(node);
		
		return null;
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
			if (this.bTreeRoot == dataBlock)
				System.out.print("ROOT ");
			System.out.println(dataBlock);
		} while ((dataBlock = dataBlock.getNext()) != null);
		
		System.out.println("-- End --");
		
	}
}

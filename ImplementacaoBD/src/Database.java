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
		db.insertCustomer(62, "Felipe");
		db.insertCustomer(63, "p9");
		db.insertCustomer(51, "p51");
		db.insertCustomer(52, "p52");
		db.insertCustomer(53, "p53");
		db.insertCustomer(64, "p64");
		db.insertCustomer(65, "p65");
		db.insertCustomer(54, "p54");
		db.insertCustomer(55, "p55");
		
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
			
			IndexBranchDatablock newBranch = this.addToBranchDataBlock(	branch,
																		indexData);
			// se new branch nao for null significa que um split de branch
			// aconteceu e por isso a referencia no root precisa ser atualizada.
			if (newBranch != null) {
				this.bTreeRoot = newBranch;
			}
		}
		
	}
	
	private IndexBranchDatablock addToBranchDataBlock(	IndexBranchDatablock branch,
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
			
			return this.addToBranchLeaf(branch, leaf, indexData);
		} else {
			// TODO e se for um branch ?
			return null;
		}
		
	}
	
	private IndexBranchDatablock addToBranchLeaf(	IndexBranchDatablock branch,
													IndexLeafDataBlock leaf,
													IndexData indexData) {
		BranchDataBlockNode node = addToLeafDataBlock(leaf, indexData);
		
		// ao adicionar na folha pode ser que um split aconteca, nesse caso
		// adiciona o novo branchNode no branch.
		if (node != null) {
			IndexBranchDatablock newBranch = this.addNodeToBranch(branch, node);
			return newBranch;
		}
		
		return null;
		
	}
	
	/**
	 * adiciona node no branch. se um split de branch for necessario faz o split
	 * e retorna um novo branch
	 * 
	 * @param branch
	 * @param node
	 * @return
	 */
	private IndexBranchDatablock addNodeToBranch(	IndexBranchDatablock branch,
													BranchDataBlockNode node) {
		
		if (branch.getNumberOfNodes() < this.maxIndexPerIndexDataBlock) {
			branch.addNode(node);
		} else {
			return splitBranch(branch, node);
		}
		
		return null;
	}
	
	private IndexBranchDatablock splitBranch(	IndexBranchDatablock leftBranch,
												BranchDataBlockNode overflowNode) {
		int middle = maxIndexPerIndexDataBlock / 2;
		
		// adiciona o overflow
		leftBranch.addNode(overflowNode);
		
		BranchDataBlockNode middleNode = leftBranch.getNodes().get(middle);
		
		// separa em duas listas. nenhuma delas possui o node do meio.
		List<BranchDataBlockNode> leftNodes = leftBranch.getNodes()
														.subList(0, middle);
		
		leftNodes = new LinkedList<BranchDataBlockNode>(leftNodes);
		
		List<BranchDataBlockNode> rightNodes = leftBranch.getNodes()
															.subList(	middle + 1,
																		this.maxIndexPerIndexDataBlock + 1);
		
		rightNodes = new LinkedList<BranchDataBlockNode>(rightNodes);
		
		leftBranch.setNodes(leftNodes);
		
		IndexBranchDatablock rightBranch = new IndexBranchDatablock(
																	getNextDataBlockId());
		rightBranch.setNodes(rightNodes);
		
		getLastDataBlock().setNext(rightBranch);
		
		// o branch node do meio aponta para os branch data blocks
		middleNode.setLeftDataBlock(leftBranch);
		middleNode.setRightDataBlock(rightBranch);
		
		IndexBranchDatablock newBranch = new IndexBranchDatablock(
																	getNextDataBlockId());
		
		rightBranch.setNext(newBranch);
		
		newBranch.addNode(middleNode);
		
		return newBranch;
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
			if (dataBlock instanceof TableDataBlock)
				continue;
			if (this.bTreeRoot == dataBlock)
				System.out.print("ROOT ");
			System.out.println(dataBlock);
		} while ((dataBlock = dataBlock.getNext()) != null);
		
		dataBlock = firstDatablock;
		
		do {
			if (!(dataBlock instanceof TableDataBlock))
				continue;
			if (this.bTreeRoot == dataBlock)
				System.out.print("ROOT ");
			System.out.println(dataBlock);
		} while ((dataBlock = dataBlock.getNext()) != null);
		
		System.out.println("-- End --");
		
	}
}

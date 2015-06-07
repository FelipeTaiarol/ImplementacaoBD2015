import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Database {
	
	private IndexDataBlock bTreeRoot;
	private DataBlock firstDatablock;
	private int maxRecordsPerTableDataBlock = 4;
	private int maxIndexPerIndexDataBlock = 4;
	private int lastDatablockId = 1;
	
	public static void main(String[] args) {
		Database db = new Database();
		
		// // adicionando primeiros clientes
		// db.insertCustomer(37, "Joao");
		// db.insertCustomer(50, "p8");
		// db.insertCustomer(13, "Jorge");
		// db.insertCustomer(61, "p6");
		//
		// // split de folha
		// db.insertCustomer(23, "p7");
		//
		// // mais alguns clientes
		// db.insertCustomer(17, "Ana");
		// db.insertCustomer(32, "Maria");
		// db.insertCustomer(62, "Felipe");
		//
		// // split de folha dentro de um branch.
		// db.insertCustomer(63, "p9");
		//
		// // mais alguns clientes
		// db.insertCustomer(51, "p51");
		// db.insertCustomer(52, "p52");
		//
		// // split de folha do node do meio de um branch.
		// db.insertCustomer(53, "p53");
		// db.insertCustomer(64, "p64");
		//
		// // splir de folha de um node do lado direito.
		// db.insertCustomer(65, "p65");
		//
		// // mais alguns cleintes
		// db.insertCustomer(54, "p54");
		//
		// // split de branch
		// db.insertCustomer(55, "p55");
		//
		// // mais alguns clientes, agora tendo que andar nos branchs
		// // para encontrar a leaf.
		// db.insertCustomer(38, "p38");
		// db.insertCustomer(39, "p39");
		//
		// // split de folha de de novo.
		// db.insertCustomer(40, "p40");
		//
		// // mais um split de folha
		// db.insertCustomer(14, "p14");
		//
		// // mais alguns clientes.
		// db.insertCustomer(12, "p12");
		// db.insertCustomer(11, "p11");
		//
		// // split de branch, sendo que o branch esta dentro de outro branch.
		// db.insertCustomer(10, "p10");
		//
		// // enchendo a tree.
		// db.insertCustomer(66, "p66");
		// db.insertCustomer(67, "p67");
		// db.insertCustomer(69, "p68");
		// db.insertCustomer(70, "p68");
		// db.insertCustomer(71, "p68");
		// db.insertCustomer(72, "p68");
		// db.insertCustomer(73, "p68");
		// db.insertCustomer(74, "p68");
		// db.insertCustomer(75, "p68");
		// db.insertCustomer(76, "p68");
		// db.insertCustomer(77, "p68");
		// db.insertCustomer(78, "p68");
		// db.insertCustomer(79, "p68");
		// db.insertCustomer(80, "p68");
		// db.insertCustomer(81, "p68");
		// db.insertCustomer(82, "p68");
		// db.insertCustomer(83, "p68");
		// db.insertCustomer(84, "p68");
		// db.insertCustomer(85, "p68");
		// db.insertCustomer(86, "p68");
		// db.insertCustomer(87, "p68");
		// db.insertCustomer(88, "p68");
		// db.insertCustomer(89, "p68");
		//
		// // split de branch de uma branch intermediaria.
		// db.insertCustomer(90, "p68");
		
		db.insertCustomer(1, "Felipe");
		db.insertRandom(50);
		
		db.printDataFile();
		
		CustomerData c1 = db.selectByCodCliente(1);
		System.out.println(c1);
		
		c1 = db.selectByName("Felipe");
		System.out.println(c1);
		
	}
	
	public void insertRandom(	int total) {
		int max = 10000;
		int min = 2;
		
		Random rand = new Random();
		
		for (int i = 0; i < total; i++) {
			int randomNum = rand.nextInt((max - min) + 1) + min;
			int code = randomNum;
			String name = "Customer_" + randomNum;
			this.insertCustomer(code, name);
		}
	}
	
	public CustomerData selectByName(	String name) {
		return fullScan(firstDatablock, name);
	}
	
	public CustomerData fullScan(	DataBlock dataBlock, String name) {
		
		if (dataBlock == null)
			return null;
		
		if (dataBlock instanceof TableDataBlock) {
			TableDataBlock table = (TableDataBlock) dataBlock;
			CustomerData customerdData = table.getCustomerByName(name);
			if (customerdData != null) {
				return customerdData;
			} else {
				return fullScan(dataBlock.getNext(), name);
			}
			
		} else {
			return fullScan(dataBlock.getNext(), name);
		}
	}
	
	public CustomerData selectByCodCliente(	int code) {
		
		TableDataBlock table = this.findTableDatablock(bTreeRoot, code);
		
		if (table == null)
			return null;
		else
			return table.getCustomerByCode(code);
	}
	
	private TableDataBlock findTableDatablock(	IndexDataBlock dataBlock,
												int code) {
		
		if (dataBlock instanceof IndexLeafDataBlock) {
			IndexLeafDataBlock leaf = (IndexLeafDataBlock) dataBlock;
			
			TableDataBlock tableDataBlock = leaf.getByKey(code);
			
			return tableDataBlock;
		}
		
		if (dataBlock instanceof IndexBranchDatablock) {
			IndexBranchDatablock branch = (IndexBranchDatablock) dataBlock;
			
			BranchDataBlockNode branchNode = branch.getNodeForKey(code);
			
			IndexDataBlock aux = null;
			
			// decide para qual lado da branch ir.
			if (code < branchNode.getKey()) {
				aux = (IndexDataBlock) branchNode.getLeftDataBlock();
			} else {
				aux = (IndexDataBlock) branchNode.getRightDataBlock();
			}
			
			return this.findTableDatablock(aux, code);
		}
		
		return null;
		
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
			
			BranchDataBlockNode newNode = this.addToLeafDataBlock(	leaf,
																	indexData);
			
			// ao adicionar na folha pode ser que um split de folha aconteca,
			// nesse caso adiciona o novo branchNode no branch.
			if (newNode != null) {
				this.addNodeToBranch(branch, newNode);
			}
			
		} else {
			// se o datablock nao for um Leaf navegar na tree até econtrar um.
			IndexBranchDatablock otherBranch = (IndexBranchDatablock) dataBlock;
			
			this.addToBranchDataBlock(otherBranch, indexData);
		}
		
	}
	
	/**
	 * adiciona node no branch. se um split de branch for necessario faz o split
	 */
	private void addNodeToBranch(	IndexBranchDatablock branch,
									BranchDataBlockNode node) {
		
		if (branch.getNumberOfNodes() < this.maxIndexPerIndexDataBlock) {
			branch.addNode(node);
		} else {
			BranchDataBlockNode newNode = splitBranch(branch, node);
			
			// se a branch que foi separda era o root agora a nova
			// branch vai passar a ser o root
			if (branch == bTreeRoot) {
				// um novo branch data block é criado para guardar o
				// node.
				IndexBranchDatablock newBranch = new IndexBranchDatablock(
																			getNextDataBlockId());
				getLastDataBlock().setNext(newBranch);
				newBranch.addNode(newNode);
				this.bTreeRoot = newBranch;
			} else {
				// caso contrario tenta adicionar na parent
				IndexBranchDatablock parentBranch = branch.getParent();
				this.addNodeToBranch(parentBranch, newNode);
			}
			
		}
		
	}
	
	private BranchDataBlockNode splitBranch(IndexBranchDatablock leftBranch,
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
		
		// tem que usar addNode
		leftBranch.setNodes(new LinkedList<BranchDataBlockNode>());
		for (BranchDataBlockNode branchDataBlockNode : leftNodes) {
			leftBranch.addNode(branchDataBlockNode);
		}
		
		IndexBranchDatablock rightBranch = new IndexBranchDatablock(
																	getNextDataBlockId());
		// tem que usar addNode
		rightBranch.setNodes(new LinkedList<BranchDataBlockNode>());
		for (BranchDataBlockNode branchDataBlockNode : rightNodes) {
			rightBranch.addNode(branchDataBlockNode);
		}
		
		getLastDataBlock().setNext(rightBranch);
		
		// o branch node do meio aponta para os branch data blocks
		middleNode.setLeftDataBlock(leftBranch);
		middleNode.setRightDataBlock(rightBranch);
		
		return middleNode;
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
			if (!(dataBlock instanceof IndexBranchDatablock))
				continue;
			if (this.bTreeRoot == dataBlock)
				System.out.print("ROOT ");
			System.out.println(dataBlock);
		} while ((dataBlock = dataBlock.getNext()) != null);
		
		dataBlock = firstDatablock;
		do {
			if (!(dataBlock instanceof IndexLeafDataBlock))
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

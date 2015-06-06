import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class IndexBranchDatablock extends IndexDataBlock {
	
	private IndexBranchDatablock parent;
	
	public IndexBranchDatablock(int id) {
		super(id);
	}
	
	private List<BranchDataBlockNode> nodes = new LinkedList<BranchDataBlockNode>();
	
	public List<BranchDataBlockNode> getNodes() {
		return nodes;
	}
	
	public void setNodes(	List<BranchDataBlockNode> nodes) {
		this.nodes = nodes;
	}
	
	public IndexBranchDatablock getParent() {
		return parent;
	}
	
	public void setParent(	IndexBranchDatablock parent) {
		this.parent = parent;
	}
	
	public void addNode(BranchDataBlockNode node) {
		nodes.add(node);
		
		setParent(node);
		sortNodes();
		updateReferences();
	}
	
	private void updateReferences() {
		for (int i = 0; i < nodes.size() - 1; i++) {
			BranchDataBlockNode firstNode = nodes.get(i);
			BranchDataBlockNode secondNode = nodes.get(i + 1);
			
			secondNode.setLeftDataBlock(firstNode.getRightDataBlock());
		}
	}
	
	private void sortNodes() {
		Collections.sort(this.nodes, new Comparator<BranchDataBlockNode>() {
			
			@Override
			public int compare(	BranchDataBlockNode o1, BranchDataBlockNode o2) {
				return Integer.valueOf(o1.getKey()).compareTo(o2.getKey());
			}
		});
	}
	
	private void setParent(	BranchDataBlockNode node) {
		if (node.getLeftDataBlock() instanceof IndexBranchDatablock) {
			((IndexBranchDatablock) node.getLeftDataBlock()).setParent(this);
		}
		if (node.getRightDataBlock() instanceof IndexBranchDatablock) {
			((IndexBranchDatablock) node.getRightDataBlock()).setParent(this);
		}
	}
	
	// retorna o node que tem uma key menor, se nao houver um retorna o ultimo
	public BranchDataBlockNode getNodeForKey(	int key) {
		
		BranchDataBlockNode node = null;
		for (BranchDataBlockNode branchDataBlockNode : nodes) {
			if (key < branchDataBlockNode.getKey())
				return branchDataBlockNode;
		}
		
		return nodes.get(nodes.size() - 1);
	}
	
	@Override
	public String toString() {
		
		String str = "Branch  "
				+ this.getId()
				+ (this.getParent() == null ? " NO_PARENT" : " PARENT: "
						+ this.getParent().getId()) + "\n";
		for (BranchDataBlockNode branchDataBlockNode : nodes) {
			str += " -- " + branchDataBlockNode.toString() + "\n";
		}
		return str + "\n";
	}
	
	public int getNumberOfNodes() {
		return this.nodes.size();
	}
	
}

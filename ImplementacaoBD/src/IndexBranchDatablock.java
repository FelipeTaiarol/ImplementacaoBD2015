import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class IndexBranchDatablock extends IndexDataBlock {
	
	public IndexBranchDatablock(int id) {
		super(id);
	}
	
	private List<BranchDataBlockNode> nodes = new LinkedList<BranchDataBlockNode>();
	
	public void addNode(BranchDataBlockNode node) {
		nodes.add(node);
		
		Collections.sort(this.nodes, new Comparator<BranchDataBlockNode>() {
			
			@Override
			public int compare(	BranchDataBlockNode o1, BranchDataBlockNode o2) {
				return Integer.valueOf(o1.getKey()).compareTo(o2.getKey());
			}
		});
		
		for (int i = 0; i < nodes.size() - 1; i++) {
			BranchDataBlockNode firstNode = nodes.get(i);
			BranchDataBlockNode secondNode = nodes.get(i + 1);
			
			secondNode.setLeftDataBlock(firstNode.getRightDataBlock());
		}
	}
	
	public BranchDataBlockNode getNodeForKey(	int key) {
		
		BranchDataBlockNode node = null;
		for (BranchDataBlockNode branchDataBlockNode : nodes) {
			if (key < branchDataBlockNode.getKey())
				node = branchDataBlockNode;
		}
		if (node == null) {
			return nodes.get(nodes.size() - 1);
		} else {
			return node;
		}
	}
	
	@Override
	public String toString() {
		
		String str = "Branch  " + this.getId() + "\n";
		for (BranchDataBlockNode branchDataBlockNode : nodes) {
			str += " -- " + branchDataBlockNode.toString();
		}
		return str + "\n";
	}
	
}

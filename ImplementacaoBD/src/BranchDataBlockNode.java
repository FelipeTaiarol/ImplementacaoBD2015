public class BranchDataBlockNode {
	
	private int key;
	private DataBlock leftDataBlock;
	private DataBlock rightDataBlock;
	
	public BranchDataBlockNode(int key, DataBlock leftDataBlock,
								DataBlock rightDataBlock) {
		super();
		this.key = key;
		this.leftDataBlock = leftDataBlock;
		this.rightDataBlock = rightDataBlock;
	}
	
	public int getKey() {
		return key;
	}
	
	public void setKey(	int key) {
		this.key = key;
	}
	
	public DataBlock getLeftDataBlock() {
		return leftDataBlock;
	}
	
	public void setLeftDataBlock(	DataBlock leftDataBlock) {
		this.leftDataBlock = leftDataBlock;
	}
	
	public DataBlock getRightDataBlock() {
		return rightDataBlock;
	}
	
	public void setRightDataBlock(	DataBlock rightDataBlock) {
		this.rightDataBlock = rightDataBlock;
	}
	
	@Override
	public String toString() {
		return "BranchDataBlockNode [key=" + key + ", leftDataBlock="
				+ leftDataBlock.getId() + ", rightDataBlock="
				+ rightDataBlock.getId() + "]";
	}
	
}

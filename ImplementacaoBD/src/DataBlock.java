public class DataBlock {
	
	private int id;
	private DataBlock next;
	
	public DataBlock(int id) {
		super();
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(	int id) {
		this.id = id;
	}
	
	public DataBlock getNext() {
		return next;
	}
	
	public void setNext(DataBlock next) {
		this.next = next;
	}
	
}

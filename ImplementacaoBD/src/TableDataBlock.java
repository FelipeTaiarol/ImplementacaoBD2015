import java.util.LinkedList;
import java.util.List;

public class TableDataBlock extends DataBlock {
	private List<CustomerData> records = new LinkedList<CustomerData>();
	
	public TableDataBlock(int id) {
		super(id);
	}
	
	public List<CustomerData> getRecords() {
		return records;
	}
	
	public void setRecords(	List<CustomerData> records) {
		this.records = records;
	}
	
	@Override
	public String toString() {
		
		String recordsString = "";
		for (CustomerData indexData : records) {
			recordsString += " -- " + indexData.toString() + "\n";
		}
		
		return "TableDataBlock " + this.getId() + " \n" + recordsString;
	}
}

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
	
	public CustomerData getCustomerByCode(	int code) {
		for (CustomerData customerData : records) {
			if (customerData.getCode() == code)
				return customerData;
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		
		String recordsString = "";
		for (CustomerData indexData : records) {
			recordsString += " -- " + indexData.toString() + "\n";
		}
		
		return "TableDataBlock " + this.getId() + " \n" + recordsString;
	}
	
	public CustomerData getCustomerByName(	String name) {
		for (CustomerData customerData : records) {
			if (customerData.getName().equals(name))
				return customerData;
		}
		return null;
	}
}

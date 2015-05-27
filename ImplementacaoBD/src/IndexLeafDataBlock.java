import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class IndexLeafDataBlock extends IndexDataBlock {
	private List<IndexData> indexRecords = new LinkedList<IndexData>();
	
	public IndexLeafDataBlock(int id) {
		super(id);
	}
	
	public void addIndexRecord(	IndexData indexData) {
		indexRecords.add(indexData);
		Collections.sort(indexRecords, new Comparator<IndexData>() {
			@Override
			public int compare(	IndexData o1, IndexData o2) {
				return Integer.valueOf(o1.getIndexKey())
								.compareTo(o2.getIndexKey());
			}
		});
	}
	
	public List<IndexData> getIndexRecords() {
		return indexRecords;
	}
	
	public void setIndexRecords(List<IndexData> indexRecords) {
		this.indexRecords = indexRecords;
	}
	
	public int getNumberOfRecords() {
		return indexRecords.size();
	}
	
	@Override
	public String toString() {
		
		String indexRecordsString = "";
		for (IndexData indexData : indexRecords) {
			indexRecordsString += " -- " + indexData.toString() + "\n";
		}
		
		return "IndexDataBlock " + this.getId() + " \n" + indexRecordsString;
	}
}

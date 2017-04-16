package au.com.greater.processor;

import org.springframework.batch.item.ItemProcessor;

import au.com.greater.model.TransactionRecordItem;

/**
 * @author Ashish Gajabi
 * Placeholder class for future use
 */
public class TransactionItemProcessor implements
		ItemProcessor<au.com.greater.model.TransactionRecordItem, au.com.greater.model.TransactionRecordItem> {

	@Override
	public TransactionRecordItem process(TransactionRecordItem item) throws Exception {
		return item;
	}

}

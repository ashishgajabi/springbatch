package au.com.greater.reader;

import java.math.BigDecimal;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import au.com.greater.model.TransactionRecordItem;

public class CSVFieldSetMapper implements FieldSetMapper<TransactionRecordItem> {

	@Override
	public TransactionRecordItem mapFieldSet(FieldSet fieldSet) throws BindException {
		TransactionRecordItem transactionRecordItem = new TransactionRecordItem();
		transactionRecordItem.setAccountNumber(fieldSet.readBigDecimal(0).intValueExact());
		Double amt = fieldSet.readBigDecimal(1).doubleValue();
		if(amt > 0) {
			transactionRecordItem.setCreditAmount(new BigDecimal(amt));
		} else {
			transactionRecordItem.setDebitAmount(new BigDecimal(amt));
		}		
		return transactionRecordItem;
	}

}

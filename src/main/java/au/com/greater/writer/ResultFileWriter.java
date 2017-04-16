package au.com.greater.writer;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Value;

import au.com.greater.model.TransactionRecordItem;

public class ResultFileWriter
		implements FlatFileHeaderCallback, FlatFileFooterCallback, ItemWriter<TransactionRecordItem>, ItemStream {

	private BigDecimal totalCredit = BigDecimal.ZERO;
	private BigDecimal totalDebit = BigDecimal.ZERO;
	private Set<Integer> numberOfAccounts = new HashSet<Integer>();
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	@Value("#{jobParameters['SKIP_COUNT']}")
	public String skipCount;
	
	@Value("#{jobParameters['fileName']}")
	public String fileName;

	private FlatFileItemWriter<TransactionRecordItem> delegate;

	@Override
	public void writeHeader(Writer writer) throws IOException {
		writer.write("File Processed: "+fileName);
	}

	@Override
	public void writeFooter(Writer writer) throws IOException {
		writer.write("Total Number Accounts: " + numberOfAccounts.size()+LINE_SEPARATOR);
		writer.write("Total Credits        : " + NumberFormat.getCurrencyInstance(Locale.US).format(totalCredit)+LINE_SEPARATOR);
		writer.write("Total Debits         : " + NumberFormat.getCurrencyInstance(Locale.US).format(totalDebit.negate())+LINE_SEPARATOR);
		writer.write("Skipped Transactions: "+skipCount);
	}

	@Override
	public void write(List<? extends TransactionRecordItem> items) throws Exception {
		items.forEach((e) -> numberOfAccounts.add(e.getAccountNumber()));

		items.forEach((e) -> {
			if (e.getCreditAmount() != null) {
				totalCredit = totalCredit.add(e.getCreditAmount());
			}
		});

		items.forEach((e) -> {
			if (e.getDebitAmount() != null) {
				totalDebit = totalDebit.add(e.getDebitAmount());
			}
		});
	}

	public void setDelegate(FlatFileItemWriter<TransactionRecordItem> delegate) {
		this.delegate = delegate;
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		this.delegate.open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		this.delegate.update(executionContext);
	}

	@Override
	public void close() throws ItemStreamException {
		this.delegate.close();
	}

}

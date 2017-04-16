package au.com.greater.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import au.com.greater.model.TransactionRecordItem;

public class TransactionItemSkipListener implements StepExecutionListener, SkipListener<TransactionRecordItem, TransactionRecordItem> {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		// DO Nothing
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println("Toal Skipped Transactions: " + stepExecution.getSkipCount());
		stepExecution.getJobParameters().getParameters().put("SKIP_COUNT", new JobParameter(""+stepExecution.getSkipCount()));
		//don't change status of job execution, keep as it is by returning null
		return null;
	}

	private int skipCount = 0;
	
	@Override
	public void onSkipInRead(Throwable t) {
		skipCount = skipCount + 1;		
	}

	@Override
	public void onSkipInWrite(TransactionRecordItem item, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSkipInProcess(TransactionRecordItem item, Throwable t) {
		// TODO Auto-generated method stub
		
	}

}

package au.com.greater.model;

import java.math.BigDecimal;

public class TransactionRecordItem {

	private Integer accountNumber;

	private BigDecimal CreditAmount;
	
	private BigDecimal DebitAmount;

	public Integer getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Integer accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getCreditAmount() {
		return CreditAmount;
	}

	public void setCreditAmount(BigDecimal creditAmount) {
		CreditAmount = creditAmount;
	}

	public BigDecimal getDebitAmount() {
		return DebitAmount;
	}

	public void setDebitAmount(BigDecimal debitAmount) {
		DebitAmount = debitAmount;
	}


}

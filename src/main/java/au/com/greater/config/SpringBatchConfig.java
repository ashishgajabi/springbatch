package au.com.greater.config;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import au.com.greater.listener.TransactionItemSkipListener;
import au.com.greater.model.TransactionRecordItem;
import au.com.greater.processor.TransactionItemProcessor;
import au.com.greater.reader.CSVFieldSetMapper;
import au.com.greater.reader.FileSkipperPolicy;
import au.com.greater.writer.ResultFileWriter;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

	@Value("#{systemEnvironment['TRANSACTION_PROCESSING']}")
	private String path;

	private final String INPUT_FOLDER = "pending";
	private final String OUTPUT_FOLDER = "processed";
	private final String SEPARATOR = File.separator;
	private final String OUTPUT_FILE_NAME = "finance_customer_transactions_report-";

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Bean
	ItemReader<TransactionRecordItem> csvFileReader() {
		FlatFileItemReader<TransactionRecordItem> fileReader = new FlatFileItemReader<>();
		//TODO File Name needs to be taken from jobParameters - to be enhanced
		fileReader.setResource(new FileSystemResource(path + SEPARATOR + INPUT_FOLDER + SEPARATOR + "report.csv"));
		fileReader.setLinesToSkip(1);
		fileReader.setLineMapper(createLineMapper());
		return fileReader;
	}

	private FieldSetMapper<TransactionRecordItem> createFieldMapper() {
		CSVFieldSetMapper cSVFieldSetMapper = new CSVFieldSetMapper();
		return cSVFieldSetMapper;
	}

	private LineMapper<TransactionRecordItem> createLineMapper() {
		DefaultLineMapper<TransactionRecordItem> lineMapper = new DefaultLineMapper<>();
		lineMapper.setLineTokenizer(CreateLineToekniser());
		lineMapper.setFieldSetMapper(createFieldMapper());
		return lineMapper;

	}

	private LineTokenizer CreateLineToekniser() {
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		//delimitedLineTokenizer.setDelimiter(",");
		delimitedLineTokenizer.setNames(new String[] { "accountNumber", "Amount" });
		return delimitedLineTokenizer;
	}

	@StepScope
	ItemWriter<TransactionRecordItem> csvFileWriter() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		FlatFileItemWriter<TransactionRecordItem> fileWriter = new FlatFileItemWriter<>();

		ResultFileWriter customFileWriter = new ResultFileWriter();
		fileWriter.setResource(new FileSystemResource(path + SEPARATOR + OUTPUT_FOLDER + SEPARATOR + OUTPUT_FILE_NAME
				+ dateFormat.format(new Date()) + ".txt"));
		customFileWriter.setDelegate(fileWriter);
		fileWriter.setFooterCallback(customFileWriter);
		fileWriter.setHeaderCallback(customFileWriter);
		return customFileWriter;
	}

	@Bean
	public Job importUserJob() {
		return jobBuilderFactory
				.get("importTransactionsJob")
				.incrementer(new RunIdIncrementer())
				.flow(step1())
				.end()
				.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.<TransactionRecordItem, TransactionRecordItem>chunk(5000)
				.reader((csvFileReader()))
				//processor created and configured for future use
				.processor(new TransactionItemProcessor())
				.faultTolerant()
				.skipPolicy(new FileSkipperPolicy())
				.skipLimit(100)
				.writer(csvFileWriter())
				.listener(new TransactionItemSkipListener())
				.build();
	}
}

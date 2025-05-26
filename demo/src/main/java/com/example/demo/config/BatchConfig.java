package com.example.demo.config;

import com.example.demo.pojo.dto.GoodsDTO;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.builder.*;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    private final DataSource dataSource;
    private JobRepository jobRepository;

    @Autowired
    public BatchConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    public void setJobRepository(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public Job importGoodsJob() {
        return new JobBuilder("importGoodsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(importGoodsStep())
                .validator(importJobParametersValidator())
                .build();
    }

    @Bean
    public Step importGoodsStep() {
        return new StepBuilder("importGoodsStep", jobRepository)
                .<GoodsDTO, GoodsDTO>chunk(100, transactionManager(dataSource))
                .reader(goodsCsvReader(null))
                .processor(goodsProcessor())
                .writer(goodsInsertWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<GoodsDTO> goodsCsvReader(
            @Value("#{jobParameters['inputFile']}") String inputFile) {

        return new FlatFileItemReaderBuilder<GoodsDTO>()
                .name("goodsCsvReader")
                .resource(new ClassPathResource(inputFile != null ? inputFile : "data/data.csv"))
                .delimited()
                .names("goodsId", "type", "goodsName", "price", "inventory","salenum")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(GoodsDTO.class);
                }})
                .build();
    }

    @Bean
    public ItemProcessor<GoodsDTO, GoodsDTO> goodsProcessor() {
        return item -> {
            if (item.getPrice() != null && item.getPrice().doubleValue() < 0) {
                item.setPrice(null);
            }
            return item;
        };
    }

    @Bean
    public JdbcBatchItemWriter<GoodsDTO> goodsInsertWriter() {
        return new JdbcBatchItemWriterBuilder<GoodsDTO>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO goods (goods_id, type, goodsname, price, inventory, salenum) " +
                        "VALUES (:goodsId, :type, :goodsName, :price, :inventory, :salenum)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job updateGoodsJob() {
        return new JobBuilder("updateGoodsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(updateGoodsStep())
                .build();
    }

    @Bean
    public Step updateGoodsStep() {
        return new StepBuilder("updateGoodsStep", jobRepository)
                .<GoodsDTO, GoodsDTO>chunk(50, transactionManager(dataSource))
                .reader(goodsCsvReader(null))
                .processor(goodsUpdateProcessor())
                .writer(goodsUpdateWriter())
                .build();
    }

    @Bean
    public ItemProcessor<GoodsDTO, GoodsDTO> goodsUpdateProcessor() {
        return item -> {
            if (item.getGoodsId() == null) {
                throw new IllegalArgumentException("更新操作需要商品ID");
            }
            return item;
        };
    }

    @Bean
    public JdbcBatchItemWriter<GoodsDTO> goodsUpdateWriter() {
        return new JdbcBatchItemWriterBuilder<GoodsDTO>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("UPDATE goods SET type = :type, goodsname = :goodsnName, price = :price, " +
                        "inventory = :inventory, salenum = :salenum WHERE goods_id = :goodsId")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job deleteGoodsJob() {
        return new JobBuilder("deleteGoodsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(deleteGoodsStep())
                .build();
    }

    @Bean
    public Step deleteGoodsStep() {
        return new StepBuilder("deleteGoodsStep", jobRepository)
                .<GoodsDTO, GoodsDTO>chunk(20, transactionManager(dataSource))
                .reader(goodsDeleteReader())
                .processor(goodsDeleteProcessor())
                .writer(goodsDeleteWriter())
                .build();
    }

    @Bean
    public ItemReader<GoodsDTO> goodsDeleteReader() {
        return new FlatFileItemReaderBuilder<GoodsDTO>()
                .name("goodsDeleteReader")
                .resource(new ClassPathResource("data/delete.csv"))
                .delimited()
                .names("goodsId")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(GoodsDTO.class);
                }})
                .build();
    }

    @Bean
    public ItemProcessor<GoodsDTO, GoodsDTO> goodsDeleteProcessor() {
        return item -> {
            if (item.getGoodsId() == null) {
                throw new IllegalArgumentException("删除操作需要商品ID");
            }
            return item;
        };
    }

    @Bean
    public JdbcBatchItemWriter<GoodsDTO> goodsDeleteWriter() {
        return new JdbcBatchItemWriterBuilder<GoodsDTO>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("DELETE FROM goods WHERE goods_id = :goodsId")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job queryGoodsJob() {
        return new JobBuilder("queryGoodsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(queryGoodsStep())
                .build();
    }

    @Bean
    public Step queryGoodsStep() {
        return new StepBuilder("queryGoodsStep", jobRepository)
                .<GoodsDTO, GoodsDTO>chunk(100, transactionManager(dataSource))
                .reader(goodsDbReader(null))
                .writer(goodsCsvWriter(null))
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<GoodsDTO> goodsDbReader(
            @Value("#{jobParameters['status']}") String status) {

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("status", status != null ? status : "ACTIVE");

        return new JdbcPagingItemReaderBuilder<GoodsDTO>()
                .name("goodsDbReader")
                .dataSource(dataSource)
                .pageSize(100)
                .queryProvider(createQueryProvider())
                .parameterValues(parameterValues)
                .rowMapper((rs, rowNum) -> {
                    GoodsDTO goods = new GoodsDTO();
                    goods.setGoodsId(rs.getInt("goods_id"));
                    goods.setType(rs.getString("type"));
                    goods.setGoodsName(rs.getString("goodsname"));
                    goods.setPrice(rs.getDouble("price"));
                    goods.setInventory(rs.getInt("inventory"));
                    goods.setSalenum(rs.getInt("salenum"));
                    return goods;
                })
                .build();
    }

    private PagingQueryProvider createQueryProvider() {
        try {
            SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
            factory.setDataSource(dataSource);
            factory.setSelectClause("SELECT goods_id, type, goodsname, price, inventory, salenum");
            factory.setFromClause("FROM goods");
            factory.setWhereClause("WHERE status = :status");

            Map<String, Order> sortKeys = new HashMap<>();
            sortKeys.put("goods_id", Order.ASCENDING);
            factory.setSortKeys(sortKeys);

            return factory.getObject();
        } catch (Exception e) {
            throw new RuntimeException("创建查询提供者失败", e);
        }
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<GoodsDTO> goodsCsvWriter(
            @Value("#{jobParameters['outputFile']}") String outputFile) {

        BeanWrapperFieldExtractor<GoodsDTO> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"goodsId", "type", "goodsName", "price", "inventory", "salenum"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<GoodsDTO> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<GoodsDTO>()
                .name("goodsCsvWriter")
                .resource(new FileSystemResource(
                        outputFile != null ? outputFile : "output/exported-goods.csv"))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("ID,类型,名称,价格,库存,已售"))
                .build();
    }

    @Bean
    public JobParametersValidator importJobParametersValidator() {
        return new JobParametersValidator() {
            @Override
            public void validate(JobParameters parameters) throws JobParametersInvalidException {
                if (!parameters.getParameters().containsKey("inputFile")) {
                    throw new JobParametersInvalidException("导入作业需要 inputFile 参数");
                }
            }
        };
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("batch-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.afterPropertiesSet();
        return executor;
    }
}
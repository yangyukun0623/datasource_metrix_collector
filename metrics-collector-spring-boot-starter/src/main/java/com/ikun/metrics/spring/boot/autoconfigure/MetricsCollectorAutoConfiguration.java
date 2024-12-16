package com.ikun.metrics.spring.boot.autoconfigure;

import com.ikun.metrics.transaction.config.TransactionCollectProperties;
import com.ikun.metrics.transaction.holder.TransactionMetricsHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnSingleCandidate(DataSource.class)
public class MetricsCollectorAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MetricsCollectorAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "metrics.collector.druid")
    public TransactionCollectProperties transactionCollectProperties() {
        return new TransactionCollectProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public TransactionMetricsHolder transactionMetricsHolder(TransactionCollectProperties properties, DataSource dataSource) {
        log.info(" init transaction metrics holder {}", TransactionMetricsHolder.class.getName());
        return new TransactionMetricsHolder(properties, dataSource);
    }


}

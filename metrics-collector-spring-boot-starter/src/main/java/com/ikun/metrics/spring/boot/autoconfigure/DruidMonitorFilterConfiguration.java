package com.ikun.metrics.spring.boot.autoconfigure;

import com.alibaba.druid.pool.DruidDataSource;
import com.ikun.metrics.transaction.collector.DruidTransactionNoneCommitCollector;
import com.ikun.metrics.transaction.holder.TransactionMetricsHolder;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@AutoConfigureAfter(MetricsCollectorAutoConfiguration.class)
@ConditionalOnClass(DruidDataSource.class)
public class DruidMonitorFilterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(DruidDataSource.class)
    public DruidTransactionNoneCommitCollector druidTransactionNoneCommitCollector(DruidDataSource dataSource, TransactionMetricsHolder holder) {
        DruidTransactionNoneCommitCollector collector = new DruidTransactionNoneCommitCollector(holder);
        dataSource.setProxyFilters(Collections.singletonList(collector));
        return collector;
    }
}

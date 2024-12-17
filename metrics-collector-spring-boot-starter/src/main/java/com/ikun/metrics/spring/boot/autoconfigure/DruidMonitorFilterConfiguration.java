package com.ikun.metrics.spring.boot.autoconfigure;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.pool.DruidAbstractDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.ikun.metrics.transaction.collector.DruidTransactionNoneCommitCollector;
import com.ikun.metrics.transaction.holder.TransactionMetricsHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

@Configuration
@AutoConfigureAfter(MetricsCollectorAutoConfiguration.class)
public class DruidMonitorFilterConfiguration implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(DruidMonitorFilterConfiguration.class);

    private final ObjectProvider<TransactionMetricsHolder> holderObjectProvider;
    private final DataSource dataSource;

    public DruidMonitorFilterConfiguration(ObjectProvider<TransactionMetricsHolder> holderObjectProvider, DataSource dataSource) {
        this.holderObjectProvider = holderObjectProvider;
        this.dataSource = dataSource;
    }

    @Override
    public void afterPropertiesSet() {
        TransactionMetricsHolder metricsHolder = holderObjectProvider.getIfAvailable();
        if (metricsHolder != null) {
            DruidTransactionNoneCommitCollector collector = new DruidTransactionNoneCommitCollector(metricsHolder);
            setProxyFilters(dataSource, collector);
        }
    }

    private void setProxyFilters(DataSource dataSource, Filter filter) {
        if (dataSource instanceof DruidDataSource) {
            ((DruidDataSource) dataSource).setProxyFilters(Collections.singletonList(filter));
            resetDruidDataSourceFilterChain(dataSource);
        }
        else if (dataSource instanceof AbstractRoutingDataSource) {
            Map<Object, DataSource> resolvedDataSources = ((AbstractRoutingDataSource) dataSource).getResolvedDataSources();
            resolvedDataSources.values().forEach(v -> setProxyFilters(v, filter));
        }
    }

    private void resetDruidDataSourceFilterChain(DataSource dataSource) {
        if (dataSource instanceof DruidDataSource) {
            Class<? extends DataSource> sourceClass = dataSource.getClass();
            try {
                Field filterChain = sourceClass.getDeclaredField("filterChain");
                filterChain.setAccessible(true);
                filterChain.set(dataSource, null);
            } catch (NoSuchFieldException e) {
                log.warn("field filterChain not exist class {}", sourceClass.getName(), e);
            } catch (IllegalAccessException e) {
                log.warn("field filterChain cannot access in class {}", sourceClass.getName(), e);
            }
        }
    }
}

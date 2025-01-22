package rebue.robotech.beansearcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.zhxu.bs.dialect.Dialect;
import rebue.robotech.beansearcher.convertor.EnumToByteConvertor;
import rebue.robotech.beansearcher.convertor.PgGeometryToGeometryConvertor;
import rebue.robotech.beansearcher.operator.Length;

@Configuration(proxyBeanMethods = false)
public class BeanSearcherConfig {
    @Bean
    public EnumToByteConvertor enumToByteConvertor() {
        return new EnumToByteConvertor();
    }

    @Bean
    public PgGeometryToGeometryConvertor pgGeometryToGeometryConvertor() {
        return new PgGeometryToGeometryConvertor();
    }

    @Bean
    public Length lengthFunSqlInterceptor(Dialect dialect) {
        return new Length(dialect);
    }
}

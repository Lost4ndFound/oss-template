package cn.huadingyun.goods.oss.config;

import cn.huadingyun.goods.oss.rule.OssRule;
import cn.huadingyun.goods.oss.rule.PmsRule;
import cn.huadingyun.goods.oss.props.OssProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(
    proxyBeanMethods = false
)
@EnableConfigurationProperties({OssProperties.class})
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean({OssRule.class})
    public OssRule ossRule() {
        return new PmsRule();
    }
}
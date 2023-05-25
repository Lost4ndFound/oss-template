package cn.huadingyun.goods.oss.config;

import cn.huadingyun.goods.oss.rule.OssRule;
import cn.huadingyun.goods.oss.props.OssProperties;
import cn.huadingyun.goods.oss.template.MinioTemplate;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: lsw
 * @date: 2022/12/6 13:47
 */
@Configuration(
        proxyBeanMethods = false
)
@ConditionalOnClass({MinioClient.class})
@EnableConfigurationProperties({OssProperties.class})
@ConditionalOnProperty(
        value = {"oss.name"},
        havingValue = "minio"
)
public class MinioConfiguration {

    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public MinioConfiguration(final OssProperties ossProperties, final OssRule ossRule) {
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @Bean
    @ConditionalOnMissingBean({MinioClient.class})
    public MinioClient minioClient() {
        try {
            return MinioClient.builder().endpoint(this.ossProperties.getEndpoint()).credentials(this.ossProperties.getAccessKey(), this.ossProperties.getSecretKey()).build();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @ConditionalOnBean({MinioClient.class})
    @ConditionalOnMissingBean({MinioTemplate.class})
    public MinioTemplate minioTemplate(MinioClient minioClient) {
        return new MinioTemplate(minioClient, this.ossProperties, this.ossRule);
    }

}

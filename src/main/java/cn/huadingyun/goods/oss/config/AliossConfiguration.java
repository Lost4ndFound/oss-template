package cn.huadingyun.goods.oss.config;

import cn.huadingyun.goods.oss.props.OssProperties;
import cn.huadingyun.goods.oss.rule.OssRule;
import cn.huadingyun.goods.oss.template.AliossTemplate;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: lsw
 * @date: 2022/12/6 13:46
 */
@Configuration(
        proxyBeanMethods = false
)
@EnableConfigurationProperties({OssProperties.class})
@ConditionalOnClass({OSSClient.class})
@ConditionalOnProperty(
        value = {"oss.name"},
        havingValue = "alioss"
)
public class AliossConfiguration {

    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public AliossConfiguration(final OssProperties ossProperties, final OssRule ossRule) {
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @Bean
    @ConditionalOnMissingBean({OSSClient.class})
    public OSSClient ossClient() {
        ClientConfiguration conf = new ClientConfiguration();
        conf.setMaxConnections(1024);
        conf.setSocketTimeout(50000);
        conf.setConnectionTimeout(50000);
        conf.setConnectionRequestTimeout(1000);
        conf.setIdleConnectionTime(60000L);
        conf.setMaxErrorRetry(5);
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(this.ossProperties.getAccessKey(), this.ossProperties.getSecretKey());
        return new OSSClient(this.ossProperties.getEndpoint(), credentialsProvider, conf);
    }

    @Bean
    @ConditionalOnBean({OSSClient.class})
    @ConditionalOnMissingBean({AliossTemplate.class})
    public AliossTemplate aliossTemplate(OSSClient ossClient) {
        return new AliossTemplate(ossClient, this.ossProperties, this.ossRule);
    }

}

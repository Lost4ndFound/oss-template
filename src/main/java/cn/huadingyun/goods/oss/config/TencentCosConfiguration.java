package cn.huadingyun.goods.oss.config;

import cn.huadingyun.goods.oss.props.OssProperties;
import cn.huadingyun.goods.oss.rule.OssRule;
import cn.huadingyun.goods.oss.template.TencentCosTemplate;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: lsw
 * @date: 2022/12/6 13:48
 */
@Configuration(
        proxyBeanMethods = false
)
@ConditionalOnClass({COSClient.class})
@EnableConfigurationProperties({OssProperties.class})
@ConditionalOnProperty(
        value = {"oss.name"},
        havingValue = "tencentcos"
)
public class TencentCosConfiguration {

    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public TencentCosConfiguration(final OssProperties ossProperties, final OssRule ossRule) {
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @Bean
    @ConditionalOnMissingBean({COSClient.class})
    public COSClient ossClient() {
        COSCredentials credentials = new BasicCOSCredentials(this.ossProperties.getAccessKey(), this.ossProperties.getSecretKey());
        Region region = new Region(this.ossProperties.getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setMaxConnectionsCount(1024);
        clientConfig.setSocketTimeout(50000);
        clientConfig.setConnectionTimeout(50000);
        clientConfig.setConnectionRequestTimeout(1000);
        return new COSClient(credentials, clientConfig);
    }

    @Bean
    @ConditionalOnBean({COSClient.class})
    @ConditionalOnMissingBean({TencentCosTemplate.class})
    public TencentCosTemplate tencentCosTemplate(COSClient cosClient) {
        return new TencentCosTemplate(cosClient, this.ossProperties, this.ossRule);
    }

}

package cn.huadingyun.goods.oss.config;

import cn.huadingyun.goods.oss.rule.OssRule;
import cn.huadingyun.goods.oss.props.OssProperties;
import cn.huadingyun.goods.oss.template.QiniuTemplate;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
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
@ConditionalOnClass({Auth.class, UploadManager.class, BucketManager.class})
@EnableConfigurationProperties({OssProperties.class})
@ConditionalOnProperty(
        value = {"oss.name"},
        havingValue = "qiniu"
)
public class QiniuConfiguration {

    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public QiniuConfiguration(final OssProperties ossProperties, final OssRule ossRule) {
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    @Bean
    @ConditionalOnMissingBean({com.qiniu.storage.Configuration.class})
    public com.qiniu.storage.Configuration qnConfiguration() {
        return new com.qiniu.storage.Configuration(Region.autoRegion());
    }

    @Bean
    @ConditionalOnMissingBean({Auth.class})
    public Auth auth() {
        return Auth.create(this.ossProperties.getAccessKey(), this.ossProperties.getSecretKey());
    }

    @Bean
    @ConditionalOnBean({com.qiniu.storage.Configuration.class})
    public UploadManager uploadManager(com.qiniu.storage.Configuration cfg) {
        return new UploadManager(cfg);
    }

    @Bean
    @ConditionalOnBean({com.qiniu.storage.Configuration.class})
    public BucketManager bucketManager(com.qiniu.storage.Configuration cfg) {
        return new BucketManager(Auth.create(this.ossProperties.getAccessKey(), this.ossProperties.getSecretKey()), cfg);
    }

    @Bean
    @ConditionalOnBean({Auth.class, UploadManager.class, BucketManager.class})
    @ConditionalOnMissingBean({QiniuTemplate.class})
    public QiniuTemplate qiniuTemplate(Auth auth, UploadManager uploadManager, BucketManager bucketManager) {
        return new QiniuTemplate(auth, uploadManager, bucketManager, this.ossProperties, this.ossRule);
    }

}

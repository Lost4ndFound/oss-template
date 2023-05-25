package cn.huadingyun.goods.oss.props;

import cn.huadingyun.goods.oss.model.Kv;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: lsw
 * @date: 2022/12/6 13:26
 */
@Component
@ConfigurationProperties(
        prefix = "oss"
)
public class OssProperties {

    private Boolean enabled;
    private String name;
    private String endpoint;
    private String cdnurl;
    private String appId;
    private String region;
    private String accessKey;
    private String secretKey;
    private String bucketName = "pms";
    private Kv args;

    public OssProperties() {
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getCdnurl() {
        return cdnurl;
    }

    public void setCdnurl(String cdnurl) {
        this.cdnurl = cdnurl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Kv getArgs() {
        return args;
    }

    public void setArgs(Kv args) {
        this.args = args;
    }
}

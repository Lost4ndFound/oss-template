package cn.huadingyun.goods.oss.config;

import cn.huadingyun.goods.oss.enums.CompressionType;
import cn.huadingyun.goods.oss.props.OssProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @description: TODO
 * @author: dyh
 * @date: 2022/3/4 15:55
 * @version:
 */
@Configuration(
        proxyBeanMethods = false
)
@ConditionalOnProperty(
        value = {"oss.enable"},
        havingValue = "true"
)
@AutoConfigureAfter({OssConfiguration.class})
@AutoConfigureBefore({AliossConfiguration.class, MinioConfiguration.class, QiniuConfiguration.class, TencentCosConfiguration.class})
@EnableConfigurationProperties({OssProperties.class})
public class OssAutoEnableConfig implements ApplicationContextAware, DisposableBean {

    /** 压缩类型，支持image；多种类型以","分割；默认支持image */
    @Value("${oss.compression-type:image}")
    private String compressionTypes;

    public static List<CompressionType> compressions = null;
    private static ApplicationContext applicationContext = null;

    @PostConstruct
    public void init() {
        // 规避无效参数
        System.setProperty("pms.env", "prod");
        if (compressionTypes == null || compressionTypes.trim().length() == 0) {
            compressions = new ArrayList<>();
            return;
        }
        List<String> types = Arrays.asList(compressionTypes.split(","));
        Set<CompressionType> cts = new HashSet<>();
        for (String type : types) {
            cts.add(CompressionType.ofType(type));
        }
        compressions = new ArrayList<>(cts);
    }

    @Override
    public void destroy() throws Exception {
        applicationContext = null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        OssAutoEnableConfig.applicationContext = applicationContext;
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getBean(requiredType);
    }
}

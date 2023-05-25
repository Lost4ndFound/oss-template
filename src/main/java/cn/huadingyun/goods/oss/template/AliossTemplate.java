package cn.huadingyun.goods.oss.template;

import cn.huadingyun.goods.oss.model.CommonFile;
import cn.huadingyun.goods.oss.model.OssFile;
import cn.huadingyun.goods.oss.props.OssProperties;
import cn.huadingyun.goods.oss.rule.OssRule;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: lsw
 * @date: 2022/12/6 13:24
 */
public class AliossTemplate implements OssTemplate{

    private final OSSClient ossClient;
    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public AliossTemplate(final OSSClient ossClient, final OssProperties ossProperties, final OssRule ossRule) {
        this.ossClient = ossClient;
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    public void makeBucket(String bucketName) {
        try {
            if (!this.bucketExists(bucketName)) {
                this.ossClient.createBucket(bucketName);
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeBucket(String bucketName) {
        try {
            this.ossClient.deleteBucket(bucketName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean bucketExists(String bucketName) {
        try {
            return this.ossClient.doesBucketExist(bucketName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void copyFile(String bucketName, String fileName, String destBucketName) {
        try {
            this.ossClient.copyObject(bucketName, fileName, bucketName, fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
        try {
            this.ossClient.copyObject(bucketName, fileName, bucketName, destFileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public OssFile statFile(String fileName) {
        try {
            return this.statFile(this.ossProperties.getBucketName(), fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public OssFile statFile(String bucketName, String fileName) {
        try {
            ObjectMetadata stat = this.ossClient.getObjectMetadata(bucketName, fileName);
            OssFile ossFile = new OssFile();
            ossFile.setName(fileName);
            ossFile.setLink(this.fileLink(ossFile.getName()));
            ossFile.setHash(stat.getContentMD5());
            ossFile.setLength(stat.getContentLength());
            ossFile.setPutTime(stat.getLastModified());
            ossFile.setContentType(stat.getContentType());
            return ossFile;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String filePath(String fileName) {
        try {
            return this.getOssHost().concat("/").concat(fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String filePath(String bucketName, String fileName) {
        try {
            return this.getOssHost(bucketName).concat("/").concat(fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String fileLink(String fileName) {
        try {
            return this.getOssHost().concat("/").concat(fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String fileLink(String bucketName, String fileName) {
        try {
            return this.getOssHost(bucketName).concat("/").concat(fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public CommonFile putFile(MultipartFile file) {
        try {
            return this.putFile(this.ossProperties.getBucketName(), file.getOriginalFilename(), file);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public CommonFile putFile(String fileName, MultipartFile file) {
        try {
            return this.putFile(this.ossProperties.getBucketName(), fileName, file);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public CommonFile putFile(String bucketName, String fileName, MultipartFile file) {
        try {
            return this.putFile(bucketName, fileName, file.getInputStream());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public CommonFile putFile(String fileName, InputStream stream) {
        try {
            return this.putFile(this.ossProperties.getBucketName(), fileName, stream);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public CommonFile putFile(String bucketName, String fileName, InputStream stream) {
        try {
            return this.put(bucketName, stream, fileName, false);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public CommonFile put(String bucketName, InputStream stream, String key, boolean cover) {
        try {
            this.makeBucket(bucketName);
            String originalName = key;
            key = this.getFileName(key);
            if (cover) {
                this.ossClient.putObject(bucketName, key, stream);
            } else {
                PutObjectResult response = this.ossClient.putObject(bucketName, key, stream);
                int retry = 0;

                for(byte retryCount = 5; StringUtils.isEmpty(response.getETag()) && retry < retryCount; ++retry) {
                    response = this.ossClient.putObject(bucketName, key, stream);
                }
            }

            CommonFile file = new CommonFile();
            file.setOriginalName(originalName);
            file.setName(key);
            file.setDomain(this.getOssHost(bucketName));
            file.setLink(this.fileLink(bucketName, key));
            return file;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFile(String fileName) {
        try {
            if (fileName == null || fileName.trim().length() == 0) {
                return;
            }
            this.ossClient.deleteObject(this.getBucketName(), fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFile(String bucketName, String fileName) {
        try {
            if (fileName == null || fileName.trim().length() == 0) {
                return;
            }
            this.ossClient.deleteObject(bucketName, fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFiles(List<String> fileNames) {
        try {
            if (fileNames == null || fileNames.isEmpty()) {
                return;
            }
            fileNames.forEach(this::removeFile);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFiles(String bucketName, List<String> fileNames) {
        try {
            if (fileNames == null || fileNames.isEmpty()) {
                return;
            }
            fileNames.forEach((fileName) -> {
                this.removeFile(bucketName, fileName);
            });
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeFileByUrl(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.trim().length() == 0) {
                return;
            }
            removeFile(reverseFileName(fileUrl));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeFileByUrl(String bucketName, String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.trim().length() == 0) {
                return;
            }
            removeFile(bucketName, reverseFileName(fileUrl));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeFileByUrls(List<String> fileUrls) {
        try {
            if (fileUrls == null || fileUrls.isEmpty()) {
                return;
            }
            removeFiles(fileUrls.stream().map(this::reverseFileName).collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeFileByUrls(String bucketName, List<String> fileUrls) {
        try {
            if (fileUrls == null || fileUrls.isEmpty()) {
                return;
            }
            removeFiles(bucketName, fileUrls.stream().map(this::reverseFileName).collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String reverseFileName(String fileUrl) {
        return this.ossRule.reverseFileName(fileUrl);
    }

    private String getBucketName() {
        return this.ossProperties.getBucketName();
    }

    private String getFileName(String originalFilename) {
        return this.ossRule.fileName(originalFilename);
    }

    public String getUploadToken() {
        return this.getUploadToken(this.ossProperties.getBucketName());
    }

    public String getUploadToken(String bucketName) {
        return this.getUploadToken(bucketName, (Long)this.ossProperties.getArgs().get("expireTime", 3600L));
    }

    public String getUploadToken(String bucketName, long expireTime) {
        String baseDir = "upload";
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000L;
        Date expiration = new Date(expireEndTime);
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem("content-length-range", 0L, (long)(Integer)this.ossProperties.getArgs().get("contentLengthRange", 10485760));
        policyConds.addConditionItem(MatchMode.StartWith, "key", baseDir);
        String postPolicy = this.ossClient.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = this.ossClient.calculatePostSignature(postPolicy);
        Map<String, String> respMap = new LinkedHashMap(16);
        respMap.put("accessid", this.ossProperties.getAccessKey());
        respMap.put("policy", encodedPolicy);
        respMap.put("signature", postSignature);
        respMap.put("dir", baseDir);
        respMap.put("host", this.getOssHost(bucketName));
        respMap.put("expire", String.valueOf(expireEndTime / 1000L));
        return JSONUtil.toJsonStr(respMap);
    }

    public String getOssHost(String bucketName) {
        String prefix = this.ossProperties.getEndpoint().contains("https://") ? "https://" : "http://";
        return prefix + bucketName + "." + this.ossProperties.getEndpoint().replaceFirst(prefix, "");
    }

    public String getOssHost() {
        return this.getOssHost(this.ossProperties.getBucketName());
    }
}

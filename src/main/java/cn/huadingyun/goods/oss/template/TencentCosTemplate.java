package cn.huadingyun.goods.oss.template;

import cn.huadingyun.goods.oss.model.OssFile;
import cn.huadingyun.goods.oss.model.CommonFile;
import cn.huadingyun.goods.oss.props.OssProperties;
import cn.huadingyun.goods.oss.rule.OssRule;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectResult;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: lsw
 * @date: 2022/12/6 13:25
 */
public class TencentCosTemplate implements OssTemplate{

    private final COSClient cosClient;
    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public TencentCosTemplate(final COSClient cosClient, final OssProperties ossProperties, final OssRule ossRule) {
        this.cosClient = cosClient;
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    public void makeBucket(String bucketName) {
        try {
            if (!this.bucketExists(bucketName)) {
                this.cosClient.createBucket(this.getBucketName(bucketName));
                this.cosClient.setBucketAcl(this.getBucketName(bucketName), CannedAccessControlList.PublicRead);
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeBucket(String bucketName) {
        try {
            this.cosClient.deleteBucket(this.getBucketName(bucketName));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean bucketExists(String bucketName) {
        try {
            return this.cosClient.doesBucketExist(this.getBucketName(bucketName));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void copyFile(String bucketName, String fileName, String destBucketName) {
        try {
            this.cosClient.copyObject(this.getBucketName(bucketName), fileName, this.getBucketName(destBucketName), fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
        try {
            this.cosClient.copyObject(this.getBucketName(bucketName), fileName, this.getBucketName(destBucketName), destFileName);
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
            ObjectMetadata stat = this.cosClient.getObjectMetadata(this.getBucketName(bucketName), fileName);
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
                this.cosClient.putObject(this.getBucketName(bucketName), key, stream, null);
            } else {
                PutObjectResult response = this.cosClient.putObject(this.getBucketName(bucketName), key, stream, null);
                int retry = 0;

                for(byte retryCount = 5; StringUtils.isEmpty(response.getETag()) && retry < retryCount; ++retry) {
                    response = this.cosClient.putObject(this.getBucketName(bucketName), key, stream, null);
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
            this.cosClient.deleteObject(this.getBucketName(), fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFile(String bucketName, String fileName) {
        try {
            this.cosClient.deleteObject(this.getBucketName(bucketName), fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFiles(List<String> fileNames) {
        try {
            fileNames.forEach(this::removeFile);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFiles(String bucketName, List<String> fileNames) {
        try {
            fileNames.forEach((fileName) -> {
                this.removeFile(this.getBucketName(bucketName), fileName);
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
        return this.getBucketName(this.ossProperties.getBucketName());
    }

    private String getBucketName(String bucketName) {
        return bucketName.concat("-").concat(this.ossProperties.getAppId());
    }

    private String getFileName(String originalFilename) {
        return this.ossRule.fileName(originalFilename);
    }

    public String getOssHost(String bucketName) {
        return "http://" + this.cosClient.getClientConfig().getEndpointBuilder().buildGeneralApiEndpoint(this.getBucketName(bucketName));
    }

    public String getOssHost() {
        return this.getOssHost(this.ossProperties.getBucketName());
    }

}

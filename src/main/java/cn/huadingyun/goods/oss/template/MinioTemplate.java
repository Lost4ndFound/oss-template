package cn.huadingyun.goods.oss.template;

import cn.huadingyun.goods.oss.enums.PolicyType;
import cn.huadingyun.goods.oss.model.CommonFile;
import cn.huadingyun.goods.oss.model.OssFile;
import cn.huadingyun.goods.oss.props.OssProperties;
import cn.huadingyun.goods.oss.rule.OssRule;
import cn.huadingyun.goods.oss.util.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.SetBucketPolicyArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author: lsw
 * @date: 2022/12/6 13:24
 */
public class MinioTemplate implements OssTemplate{

    private final MinioClient client;
    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public MinioTemplate(final MinioClient client, final OssProperties ossProperties, final OssRule ossRule) {
        this.client = client;
        this.ossProperties = ossProperties;
        this.ossRule = ossRule;
    }

    public void makeBucket(String bucketName) {
        try {
            if (!this.client.bucketExists((BucketExistsArgs.builder().bucket(bucketName)).build())) {
                this.client.makeBucket((MakeBucketArgs.builder().bucket(bucketName)).build());
                this.client.setBucketPolicy((SetBucketPolicyArgs.builder().bucket(bucketName)).config(getPolicyType(bucketName, PolicyType.READ)).build());
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Bucket getBucket() {
        try {
            return this.getBucket(this.getBucketName());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Bucket getBucket(String bucketName) {
        try {
            Optional<Bucket> bucketOptional = this.client.listBuckets().stream().filter((bucket) -> {
                return bucket.name().equals(bucketName);
            }).findFirst();
            return bucketOptional.orElse(null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public List<Bucket> listBuckets() {
        try {
            return this.client.listBuckets();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeBucket(String bucketName) {
        try {
            this.client.removeBucket((RemoveBucketArgs.builder().bucket(bucketName)).build());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean bucketExists(String bucketName) {
        try {
            return this.client.bucketExists((BucketExistsArgs.builder().bucket(bucketName)).build());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void copyFile(String bucketName, String fileName, String destBucketName) {
        try {
            this.copyFile(bucketName, fileName, destBucketName, fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
        try {
            this.client.copyObject(((CopyObjectArgs.builder().source(((CopySource.builder().bucket(bucketName)).object(fileName)).build()).bucket(destBucketName)).object(destFileName)).build());
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
            StatObjectResponse stat = this.client.statObject(((StatObjectArgs.builder().bucket(bucketName)).object(fileName)).build());
            OssFile ossFile = new OssFile();
            ossFile.setName(ObjectUtil.isEmpty(stat.object()) ? fileName : stat.object());
            ossFile.setLink(this.fileLink(ossFile.getName()));
            ossFile.setHash(String.valueOf(stat.hashCode()));
            ossFile.setLength(stat.size());
            ossFile.setPutTime(DateUtil.toDate(stat.lastModified().toLocalDateTime()));
            ossFile.setContentType(stat.contentType());
            return ossFile;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String filePath(String fileName) {
        return this.getBucketName().concat("/").concat(fileName);
    }

    public String filePath(String bucketName, String fileName) {
        return bucketName.concat("/").concat(fileName);
    }

    public String fileLink(String fileName) {
        try {
            return StrUtil.isNotBlank(this.ossProperties.getCdnurl()) ? this.ossProperties.getCdnurl().concat("/").concat(fileName) : this.ossProperties.getEndpoint().concat("/").concat("/").concat(fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String fileLink(String bucketName, String fileName) {
        try {
            return StrUtil.isNotBlank(this.ossProperties.getCdnurl()) ? this.ossProperties.getCdnurl().concat("/").concat(bucketName).concat("/").concat(fileName) : this.ossProperties.getEndpoint().concat("/").concat(bucketName).concat("/").concat(fileName);
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
            return this.putFile(bucketName, file.getOriginalFilename(), file.getInputStream());
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
            return this.putFile(bucketName, fileName, stream, "application/octet-stream");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public CommonFile putFile(String bucketName, String fileName, InputStream stream, String contentType) {
        try {
            this.makeBucket(bucketName);
            String originalName = fileName;
            fileName = this.getFileName(fileName);
            this.client.putObject(((PutObjectArgs.builder().bucket(bucketName)).object(fileName)).stream(stream, stream.available(), -1L).contentType(contentType).build());
            CommonFile file = new CommonFile();
            file.setOriginalName(originalName);
            file.setName(fileName);
            file.setDomain(this.getOssHost(bucketName));
            file.setLink(this.fileLink(bucketName, fileName));
            return file;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFile(String fileName) {
        try {
            this.removeFile(this.ossProperties.getBucketName(), fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFile(String bucketName, String fileName) {
        try {
            this.client.removeObject(((RemoveObjectArgs.builder().bucket(bucketName)).object(fileName)).build());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFiles(List<String> fileNames) {
        try {
            this.removeFiles(this.ossProperties.getBucketName(), fileNames);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFiles(String bucketName, List<String> fileNames) {
        try {
            List<DeleteObject> stream = fileNames.stream().map(DeleteObject::new).collect(Collectors.toList());
            for (Result<DeleteError> errorResult : this.client.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(stream).build())) {
                DeleteError error = errorResult.get();
                System.out.println("Failed to remove '" + error.objectName() + "'. Error:" + error.message());
            }
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

    public String getPresignedObjectUrl(String bucketName, String fileName, Integer expires) {
        try {
            return this.client.getPresignedObjectUrl(((GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucketName)).object(fileName)).expiry(expires).build());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String getPolicyType(PolicyType policyType) {
        return getPolicyType(this.getBucketName(), policyType);
    }

    public static String getPolicyType(String bucketName, PolicyType policyType) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("    \"Statement\": [\n");
        builder.append("        {\n");
        builder.append("            \"Action\": [\n");
        switch(policyType) {
            case WRITE:
                builder.append("                \"s3:GetBucketLocation\",\n");
                builder.append("                \"s3:ListBucketMultipartUploads\"\n");
                break;
            case READ_WRITE:
                builder.append("                \"s3:GetBucketLocation\",\n");
                builder.append("                \"s3:ListBucket\",\n");
                builder.append("                \"s3:ListBucketMultipartUploads\"\n");
                break;
            default:
                builder.append("                \"s3:GetBucketLocation\"\n");
        }

        builder.append("            ],\n");
        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append("            \"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("\"\n");
        builder.append("        },\n");
        if (PolicyType.READ.equals(policyType)) {
            builder.append("        {\n");
            builder.append("            \"Action\": [\n");
            builder.append("                \"s3:ListBucket\"\n");
            builder.append("            ],\n");
            builder.append("            \"Effect\": \"Deny\",\n");
            builder.append("            \"Principal\": \"*\",\n");
            builder.append("            \"Resource\": \"arn:aws:s3:::");
            builder.append(bucketName);
            builder.append("\"\n");
            builder.append("        },\n");
        }

        builder.append("        {\n");
        builder.append("            \"Action\": ");
        switch(policyType) {
            case WRITE:
                builder.append("[\n");
                builder.append("                \"s3:AbortMultipartUpload\",\n");
                builder.append("                \"s3:DeleteObject\",\n");
                builder.append("                \"s3:ListMultipartUploadParts\",\n");
                builder.append("                \"s3:PutObject\"\n");
                builder.append("            ],\n");
                break;
            case READ_WRITE:
                builder.append("[\n");
                builder.append("                \"s3:AbortMultipartUpload\",\n");
                builder.append("                \"s3:DeleteObject\",\n");
                builder.append("                \"s3:GetObject\",\n");
                builder.append("                \"s3:ListMultipartUploadParts\",\n");
                builder.append("                \"s3:PutObject\"\n");
                builder.append("            ],\n");
                break;
            default:
                builder.append("\"s3:GetObject\",\n");
        }

        builder.append("            \"Effect\": \"Allow\",\n");
        builder.append("            \"Principal\": \"*\",\n");
        builder.append("            \"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("/*\"\n");
        builder.append("        }\n");
        builder.append("    ],\n");
        builder.append("    \"Version\": \"2012-10-17\"\n");
        builder.append("}\n");
        return builder.toString();
    }

    public String getOssHost(String bucketName) {
        return this.ossProperties.getEndpoint() + "/" + bucketName;
    }

    public String getOssHost() {
        return this.getOssHost(this.ossProperties.getBucketName());
    }
}

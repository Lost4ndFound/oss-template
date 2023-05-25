package cn.huadingyun.goods.oss.template;

import cn.huadingyun.goods.oss.model.CommonFile;
import cn.huadingyun.goods.oss.model.OssFile;
import cn.huadingyun.goods.oss.props.OssProperties;
import cn.huadingyun.goods.oss.rule.OssRule;
import cn.hutool.core.util.ObjectUtil;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: lsw
 * @date: 2022/12/6 13:25
 */
public class QiniuTemplate implements OssTemplate{

    private final Auth auth;
    private final UploadManager uploadManager;
    private final BucketManager bucketManager;
    private final OssProperties ossProperties;
    private final OssRule ossRule;

    public QiniuTemplate(final Auth auth, final UploadManager uploadManager, final BucketManager bucketManager, final OssProperties ossProperties, final OssRule ossRule) {
        this.auth = auth;
        this.uploadManager = uploadManager;
        this.bucketManager = bucketManager;
        this.ossProperties = ossProperties;
        this.ossRule =ossRule;
    }

    public void makeBucket(String bucketName) {
        try {
            if (!contains(this.bucketManager.buckets(), bucketName)) {
                this.bucketManager.createBucket(bucketName, Zone.autoZone().getRegion());
            }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeBucket(String bucketName) {
    }

    public boolean bucketExists(String bucketName) {
        try {
            return contains(this.bucketManager.buckets(), bucketName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void copyFile(String bucketName, String fileName, String destBucketName) {
        try {
            this.bucketManager.copy(bucketName, fileName, destBucketName, fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void copyFile(String bucketName, String fileName, String destBucketName, String destFileName) {
        try {
            this.bucketManager.copy(bucketName, fileName, destBucketName, destFileName);
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
            FileInfo stat = this.bucketManager.stat(bucketName, fileName);
            OssFile ossFile = new OssFile();
            ossFile.setName(ObjectUtil.isEmpty(stat.key) ? fileName : stat.key);
            ossFile.setLink(this.fileLink(ossFile.getName()));
            ossFile.setHash(stat.hash);
            ossFile.setLength(stat.fsize);
            ossFile.setPutTime(new Date(stat.putTime / 10000L));
            ossFile.setContentType(stat.mimeType);
            return ossFile;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String filePath(String fileName) {
        try {
            return this.getBucketName().concat("/").concat(fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String filePath(String bucketName, String fileName) {
        try {
            return bucketName.concat("/").concat(fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String fileLink(String fileName) {
        try {
            return this.ossProperties.getEndpoint().concat("/").concat(fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public String fileLink(String bucketName, String fileName) {
        try {
            return this.ossProperties.getEndpoint().concat("/").concat(fileName);
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
                this.uploadManager.put(stream, key, this.getUploadToken(bucketName, key), (StringMap)null, (String)null);
            } else {
                Response response = this.uploadManager.put(stream, key, this.getUploadToken(bucketName), (StringMap)null, (String)null);
                int retry = 0;

                for(byte retryCount = 5; response.needRetry() && retry < retryCount; ++retry) {
                    response = this.uploadManager.put(stream, key, this.getUploadToken(bucketName), (StringMap)null, (String)null);
                }
            }

            CommonFile file = new CommonFile();
            file.setOriginalName(originalName);
            file.setName(key);
            file.setDomain(this.getOssHost());
            file.setLink(this.fileLink(bucketName, key));
            return file;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFile(String fileName) {
        try {
            this.bucketManager.delete(this.getBucketName(), fileName);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFile(String bucketName, String fileName) {
        try {
            this.bucketManager.delete(bucketName, fileName);
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

    public String getUploadToken(String bucketName) {
        return this.auth.uploadToken(bucketName);
    }

    private String getUploadToken(String bucketName, String key) {
        return this.auth.uploadToken(bucketName, key);
    }

    public String getOssHost() {
        return this.ossProperties.getEndpoint();
    }

    private <T> boolean contains(@Nullable T[] array, final T element) {
        return array == null ? false : Arrays.stream(array).anyMatch((x) -> nullSafeEquals(x, element));
    }

    private boolean nullSafeEquals(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o1, o2);
        }
        return false;
    }

    private boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }
        if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (o1 instanceof short[] && o2 instanceof short[]) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        return false;
    }

}

package cn.huadingyun.goods.oss.template;

import cn.huadingyun.goods.oss.model.CommonFile;
import cn.huadingyun.goods.oss.model.OssFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author: lsw
 * @date: 2022/12/6 11:17
 */
public interface OssTemplate {

    void makeBucket(String bucketName);

    void removeBucket(String bucketName);

    boolean bucketExists(String bucketName);

    void copyFile(String bucketName, String fileName, String destBucketName);

    void copyFile(String bucketName, String fileName, String destBucketName, String destFileName);

    OssFile statFile(String fileName);

    OssFile statFile(String bucketName, String fileName);

    String filePath(String fileName);

    String filePath(String bucketName, String fileName);

    String fileLink(String fileName);

    String fileLink(String bucketName, String fileName);

    CommonFile putFile(MultipartFile file);

    CommonFile putFile(String fileName, MultipartFile file);

    CommonFile putFile(String bucketName, String fileName, MultipartFile file);

    CommonFile putFile(String fileName, InputStream stream);

    CommonFile putFile(String bucketName, String fileName, InputStream stream);

    void removeFile(String fileName);

    void removeFile(String bucketName, String fileName);

    void removeFiles(List<String> fileNames);

    void removeFiles(String bucketName, List<String> fileNames);

    default void removeFileByUrl(String fileUrl) {
        throw new UnsupportedOperationException();
    }

    default void removeFileByUrl(String bucketName, String fileUrl) {
        throw new UnsupportedOperationException();
    }

    default void removeFileByUrls(List<String> fileUrls) {
        throw new UnsupportedOperationException();
    }

    default void removeFileByUrls(String bucketName, List<String> fileUrls) {
        throw new UnsupportedOperationException();
    }
}

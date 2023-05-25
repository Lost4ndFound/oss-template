package cn.huadingyun.goods.oss.util;

import cn.huadingyun.goods.oss.config.OssAutoEnableConfig;
import cn.huadingyun.goods.oss.enums.CompressionType;
import cn.huadingyun.goods.oss.model.CommonFile;
import cn.huadingyun.goods.oss.model.PmsFile;
import cn.huadingyun.goods.oss.template.OssTemplate;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;

/**
 * @description: TODO
 * @author: dyh
 * @date: 2022/3/4 15:46
 * @version:
 */
@Slf4j
public class OssUtil {

    public static final String DOT = ".";
    public static final String PATH = "/";

    private OssTemplate ossTemplate;

    private OssUtil(OssTemplate ossTemplate) {
        if (ossTemplate == null) {
            throw new IllegalStateException("OSS配置信息不存在。");
        }
        this.ossTemplate = ossTemplate;
    }

    private static class OssUtilHold {
        public static OssUtil util = new OssUtil(OssAutoEnableConfig.getBean(OssTemplate.class));
    }

    public static OssUtil HOLDER = OssUtilHold.util;

    /**
     * 通过OSS文件地址删除相应的文件
     *
     * @param fileUrls  OSS文件地址
     * @return  0：文件地址为空；-1：删除失败；大于0：删除的文件数
     */
    public int deleteFile(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return 0;
        }
        try {
            this.ossTemplate.removeFileByUrls(fileUrls);
            return fileUrls.size();
        } catch (Exception e) {
            log.error("OSS文件删除失败："+e.getMessage(), e);
            return -1;
        }
    }

    /**
     * 向OSS上传文件（计算文件MD5）<br>
     *
     * @param file
     * @return OSSFile
     * @see PmsFile
     * @see #uploadFile(String, InputStream)
     */
    public PmsFile uploadFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        try {
            return uploadFile(filename, file.getInputStream());
        } catch (Exception e) {
            log.error("OSS文件["+filename+"]上传失败："+e.getMessage(), e);
        }
        return PmsFile.fail(filename);
    }

    /**
     * 生成存储文件名
     *
     * @param originalFilename
     * @return
     */
    public static String generateFilename(String originalFilename) {
        return new StringBuffer(UUID.randomUUID().toString().replaceAll("-", ""))
                                .append(originalFilename.substring(originalFilename.lastIndexOf(DOT))).toString();

    }

    /**
     * 向OSS上传文件（计算文件MD5）
     *
     * @param filename          文件名（包含后缀名）
     * @param stream            文件输入流
     * @return PmsFile
     * @see PmsFile
     */
    public PmsFile uploadFile(String filename, InputStream stream) {
        try {
            if (stream == null || stream.available() <= 0) {
                log.error("{}文件数据流无效。", filename);
                return PmsFile.fail(filename);
            }
            return uploadFile0(filename, stream, true);
        } catch (Exception e) {
            log.error("OSS文件上传失败："+e.getMessage(), e);
        }
        return PmsFile.fail(filename);
    }

    /**
     * 向OSS上传文件（计算文件MD5）
     *
     * @param filename      文件名（包含后缀名）
     * @param fileData      文件字节数据
     * @return PmsFile
     * @see PmsFile
     */
    public PmsFile uploadFile(String filename, byte[] fileData) {
        return uploadFile(filename, fileData, true);
    }

    /**
     * 向OSS上传文件
     *
     * @param filename      文件名（包含后缀名）
     * @param fileData      文件字节数据
     * @param isMd5         是否计算文件的MD5
     * @return PmsFile
     * @see PmsFile
     */
    public PmsFile uploadFile(String filename, byte[] fileData, boolean isMd5) {
        if (fileData == null || fileData.length == 0) {
            log.error("{}文件数据无效。", filename);
        }
        return uploadFile0(filename, fileData, isMd5);
    }

    private PmsFile uploadFile0(String filename, Object fileData, boolean isMd5) {
        try {
            int idx = filename.lastIndexOf(DOT);
            String extension = ".";
            if (idx <= 0 || (extension = filename.substring(idx)).length() == 1) {
                log.error("文件名无效："+filename);
                return PmsFile.fail(filename);
            }
            String contentType = getContentType(extension);
            String ct = contentType.substring(0, contentType.indexOf("/"));
            CompressionType type = CompressionType.ofType(ct, false);
            byte[] data = null;
            if (fileData instanceof InputStream) {
                data = readStream((InputStream) fileData);
            } else if (fileData instanceof byte[]) {
                data = (byte[]) fileData;
            } else {
                log.error("OSS文件上传失败，无效的数据类型："+fileData.getClass().getSimpleName());
                return PmsFile.fail(filename);
            }
            String md5 = isMd5 ? fileMd5(data) : "";
            // 压缩
            data = compression(data, type);

            CommonFile commonFile = ossTemplate.putFile(filename, new ByteArrayInputStream(data));
            return new PmsFile(filename, commonFile.getName(), commonFile.getLink(), data.length, md5);
        } catch (Exception e) {
            log.error("OSS文件上传失败："+e.getMessage(), e);
        }
        return PmsFile.fail(filename);
    }

    private static byte[] readStream(InputStream src) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[CompressionType.MB_HALF];
        int len = 0;
        try {
            while ((len = src.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] compression(InputStream src, CompressionType type) {
        return compression(readStream(src), type);
    }

    private static byte[] compression(byte[] bytes, CompressionType type) {
        try {
            if (type == null) {
                return bytes;
            }
            switch (type) {
                case IMAGE:
                    bytes = compressionImage(bytes);
                    break;
                case VIDEO:
                default:
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return bytes;
    }


    private static byte[] compressionImage(byte[] src) throws Exception{
        long size = src.length;
        if (size < CompressionType.MB_1) {
            return src;
        }
        //图片质量压缩比例 从0-1，越接近1值质量越好
        float quality = 0.8f;
        // 大于2M
        if (size > CompressionType.MB_2) {
            quality = 0.5f;
        }
        if (size > CompressionType.MB_5) {
            quality = 0.3f;
        }

        //图片大小（长宽）压缩比例 从0-1，1表示原图
        Thumbnails.Builder builder = Thumbnails.of(new ByteArrayInputStream(src)).outputQuality(quality).scale(1f);
        ByteArrayOutputStream ops = new ByteArrayOutputStream();
        builder.toOutputStream(ops);
        return ops.toByteArray();
    }

    /**
     * 根据文件名获取Content-Type
     *
     * @param filenameExtension
     * @return
     */
    public static String getContentType(String filenameExtension) {
        //获取文件后缀
        String suffix = filenameExtension;
        if (".bmp".equalsIgnoreCase(suffix)) {
            return "image/bmp";
        }
        if (".gif".equalsIgnoreCase(suffix)) {
            return "image/gif";
        }
        if (".jpeg".equalsIgnoreCase(suffix) ||
                ".jpg".equalsIgnoreCase(suffix) ||
                ".png".equalsIgnoreCase(suffix)) {
            return "image/jpg";
        }
        if (".html".equalsIgnoreCase(suffix)) {
            return "text/html";
        }
        if (".txt".equalsIgnoreCase(suffix)) {
            return "text/plain";
        }
        if (".vsd".equalsIgnoreCase(suffix)) {
            return "application/vnd.visio";
        }
        if (".pptx".equalsIgnoreCase(suffix) ||
                ".ppt".equalsIgnoreCase(suffix)) {
            return "application/vnd.ms-powerpoint";
        }
        if (".docx".equalsIgnoreCase(suffix) ||
                ".doc".equalsIgnoreCase(suffix)) {
            return "application/msword";
        }
        if (".xml".equalsIgnoreCase(suffix)) {
            return "text/xml";
        }
        if (".mp4".equalsIgnoreCase(suffix)) {
            return "video/mp4";
        }
        if (".json".equalsIgnoreCase(suffix)) {
            return "application/json";
        }
        if (".csv".equalsIgnoreCase(suffix)) {
            return "text/csv";
        }
        return "application/octet-stream";
    }

    /**
     * 计算文件数据的MD5
     *
     * @param data
     * @return
     */
    public static String fileMd5(byte[] data){
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("无文件数据（或文件大小为0）");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(data);
            byte[] bytes = digest.digest();
            BigInteger bi = new BigInteger(1, bytes);
            return bi.toString(16);
        } catch (Exception e) {
            log.error("获取文件MD5值失败：{}", e.getMessage());
            return "";
        }
    }
}

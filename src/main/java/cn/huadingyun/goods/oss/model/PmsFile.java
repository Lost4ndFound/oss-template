package cn.huadingyun.goods.oss.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author: lsw
 * @date: 2022/12/6 16:42
 */
@Data
public class PmsFile implements Serializable {

    private static final long serialVersionUID = -6652978482279920703L;

    /** 原文件名 */
    @Setter(AccessLevel.PROTECTED)
    private String originalFilename;
    /** 存储文件名 */
    @Setter(AccessLevel.PROTECTED)
    private String storeFilename;
    /** 文件访问URL */
    @Setter(AccessLevel.PROTECTED)
    private String linkUrl = "";
    /** 文件上传的大小（文件可能压缩，所以该大小可能比文件实际大小要小） */
    @Setter(AccessLevel.PROTECTED)
    private long fileSize = 0;
    /** 文件数据的MD5值（以文件实际数据进行MD5，可空） */
    @Setter(AccessLevel.PROTECTED)
    private String fileMd5;
    PmsFile() {
    }

    public PmsFile(String originalFilename, String storeFilename, String linkUrl, long fileSize) {
        this(originalFilename, storeFilename, linkUrl, fileSize, "");
    }

    public PmsFile(String originalFilename, String storeFilename, String linkUrl, long fileSize, String fileMd5) {
        this();
        this.originalFilename = originalFilename;
        this.storeFilename = storeFilename;
        this.linkUrl = linkUrl;
        this.fileSize = fileSize;
        this.fileMd5 = fileMd5;
    }

    /**
     * 快速创建一个失败的文件信息
     * */
    public static PmsFile fail(String originalFilename) {
        return new PmsFile(originalFilename, "", "", -1);
    }

    /** 文件是否上传成功 */
    public boolean success() {
        return fileSize > 0;
    }

}

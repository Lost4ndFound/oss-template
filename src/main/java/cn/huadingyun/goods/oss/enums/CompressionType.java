package cn.huadingyun.goods.oss.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 系统支持的压缩类型
 * @author: dyh
 * @date: 2022/3/4 16:36
 * @version:
 */
public enum CompressionType {

    IMAGE("image"), VIDEO("video");

    private String type;

    private CompressionType(String type) {
        this.type = type;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CompressionType.class);
    public static final long MB_1 = 1024 * 1024L;
    public static final int MB_HALF = (int) (MB_1 / 2);
    public static final long MB_2 = 2 * MB_1;
    public static final long MB_5 = 5 * MB_1;

    public static CompressionType ofType(String type) {
        for (CompressionType ct : CompressionType.values()) {
            if (ct.type.equalsIgnoreCase(type)) {
                return ct;
            }
        }
        throw new IllegalArgumentException("压缩类型不存在："+type);
    }

    public static CompressionType ofType(String type, boolean throwException) {
        try {
            return ofType(type);
        } catch (RuntimeException e) {
            if (throwException) {
                throw e;
            } else {
                LOGGER.warn(e.getMessage());
            }
        }
        return null;
    }
}

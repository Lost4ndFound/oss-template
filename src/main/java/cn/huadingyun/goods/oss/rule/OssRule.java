package cn.huadingyun.goods.oss.rule;

/**
 * @author: lsw
 * @date: 2022/12/6 15:08
 */
public interface OssRule {

    String fileName(String originalFilename);

    String reverseFileName(String fileUrl);
}

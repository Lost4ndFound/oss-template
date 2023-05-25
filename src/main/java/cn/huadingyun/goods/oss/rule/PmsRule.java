package cn.huadingyun.goods.oss.rule;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.util.UUID;

/**
 * @author: lsw
 * @date: 2022/12/6 15:09
 */
public class PmsRule implements OssRule{

    private static final String PATH = "upload";

    public PmsRule() {
    }

    @Override
    public String fileName(String originalFilename) {
        return PATH+"/" + DateUtil.today() + "/" + UUID.randomUUID() + "." + getFileExtension(originalFilename);
    }

    private String getFileExtension(String fullName) {
        if (StrUtil.isBlank(fullName)) {
            return "";
        } else {
            int dotIndex = fullName.lastIndexOf(46);
            return dotIndex == -1 ? "" : fullName.substring(dotIndex + 1);
        }
    }

    @Override
    public String reverseFileName(String fileUrl) {
        int idx = fileUrl.indexOf(PATH);
        if (idx < 0) {
            throw new RuntimeException("文件地址URL无效。");
        }
        return fileUrl.substring(idx);
    }
}

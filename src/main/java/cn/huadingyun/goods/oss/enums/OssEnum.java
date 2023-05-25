package cn.huadingyun.goods.oss.enums;

/**
 * @author: lsw
 * @date: 2022/12/6 11:55
 */
public enum OssEnum {

    MINIO("minio", 1),
    QINIU("qiniu", 2),
    ALI("ali", 3),
    TENCENT("tencent", 4);

    final String name;
    final int category;

    OssEnum(final String name, final int category) {
        this.name = name;
        this.category = category;
    }

    public static OssEnum of(String name) {
        if (name == null) {
            return null;
        } else {
            OssEnum[] values = values();
            OssEnum[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                OssEnum ossEnum = var2[var4];
                if (ossEnum.name.equals(name)) {
                    return ossEnum;
                }
            }

            return null;
        }
    }

    public String getName() {
        return this.name;
    }

    public int getCategory() {
        return this.category;
    }

}

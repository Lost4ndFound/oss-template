package cn.huadingyun.goods.oss.enums;

/**
 * @author: lsw
 * @date: 2022/12/6 14:16
 */
public enum PolicyType {

    READ("read", "只读"),
    WRITE("write", "只写"),
    READ_WRITE("read_write", "读写");

    private final String type;
    private final String policy;

    public String getType() {
        return this.type;
    }

    public String getPolicy() {
        return this.policy;
    }

    private PolicyType(final String type, final String policy) {
        this.type = type;
        this.policy = policy;
    }

}

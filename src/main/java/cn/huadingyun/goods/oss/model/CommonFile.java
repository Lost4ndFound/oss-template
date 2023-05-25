package cn.huadingyun.goods.oss.model;

/**
 * @author: lsw
 * @date: 2022/12/6 11:18
 */
public class CommonFile {

    private String link;
    private String domain;
    private String name;
    private String originalName;
    private Long attachId;

    public CommonFile() {
    }

    public CommonFile(String link, String domain, String name, String originalName, Long attachId) {
        this.link = link;
        this.domain = domain;
        this.name = name;
        this.originalName = originalName;
        this.attachId = attachId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Long getAttachId() {
        return attachId;
    }

    public void setAttachId(Long attachId) {
        this.attachId = attachId;
    }

    public String toString() {
        return "PmsFile(link=" + this.getLink() + ", domain=" + this.getDomain() + ", name=" + this.getName() + ", originalName=" + this.getOriginalName() + ", attachId=" + this.getAttachId() + ")";
    }
}

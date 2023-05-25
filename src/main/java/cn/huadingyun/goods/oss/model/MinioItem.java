package cn.huadingyun.goods.oss.model;

import cn.huadingyun.goods.oss.util.DateUtil;
import io.minio.messages.Item;
import io.minio.messages.Owner;

import java.util.Date;

/**
 * @author: lsw
 * @date: 2022/12/6 11:31
 */
public class MinioItem {

    private String objectName;
    private Date lastModified;
    private String etag;
    private Long size;
    private String storageClass;
    private Owner owner;
    private boolean isDir;
    private String category;

    public MinioItem() {
    }

    public MinioItem(Item item) {
        this.objectName = item.objectName();
        this.lastModified = DateUtil.toDate(item.lastModified().toLocalDateTime());
        this.etag = item.etag();
        this.size = item.size();
        this.storageClass = item.storageClass();
        this.owner = item.owner();
        this.isDir = item.isDir();
        this.category = this.isDir ? "dir" : "file";
    }


    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String toString() {
        return "MinioItem(objectName=" + this.getObjectName() + ", lastModified=" + this.getLastModified() + ", etag=" + this.getEtag() + ", size=" + this.getSize() + ", storageClass=" + this.getStorageClass() + ", owner=" + this.getOwner() + ", isDir=" + this.isDir() + ", category=" + this.getCategory() + ")";
    }

}

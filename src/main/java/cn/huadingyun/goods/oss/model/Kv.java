package cn.huadingyun.goods.oss.model;

import org.springframework.lang.Nullable;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: lsw
 * @date: 2022/12/6 14:53
 */
public class Kv extends LinkedCaseInsensitiveMap<Object> {

    private Kv() {
    }

    public static Kv create() {
        return new Kv();
    }

    public static <K, V> HashMap<K, V> newMap() {
        return new HashMap(16);
    }

    public Kv set(String attr, Object value) {
        this.put(attr, value);
        return this;
    }

    public Kv setAll(Map<? extends String, ?> map) {
        if (map != null) {
            this.putAll(map);
        }

        return this;
    }

    public Kv setIgnoreNull(String attr, Object value) {
        if (attr != null && value != null) {
            this.set(attr, value);
        }

        return this;
    }

    public Object getObj(String key) {
        return super.get(key);
    }

    public <T> T get(String attr, T defaultValue) {
        Object result = this.get(attr);
        return result != null ? (T) result : defaultValue;
    }

    public String getStr(String attr) {
        return toStr(this.get(attr), null);
    }

    public Integer getInt(String attr) {
        return toInt(this.get(attr), -1);
    }

    public Long getLong(String attr) {
        return toLong(this.get(attr), -1L);
    }

    public Float getFloat(String attr) {
        return toFloat(this.get(attr), null);
    }

    public Double getDouble(String attr) {
        return toDouble(this.get(attr), null);
    }

    public Boolean getBool(String attr) {
        return toBoolean(this.get(attr), null);
    }

    public byte[] getBytes(String attr) {
        return this.get(attr, null);
    }

    public Date getDate(String attr) {
        return this.get(attr, null);
    }

    public Time getTime(String attr) {
        return this.get(attr, null);
    }

    public Timestamp getTimestamp(String attr) {
        return this.get(attr, null);
    }

    public Number getNumber(String attr) {
        return this.get(attr, null);
    }

    public static String toStr(Object obj, String defaultValue) {
        return null != obj && !obj.equals("null") ? String.valueOf(obj) : defaultValue;
    }

    public static int toInt(@Nullable final Object obj, final int defaultValue) {
        if (obj == null) {
            return defaultValue;
        } else {
            try {
                return Integer.valueOf(String.valueOf(obj));
            } catch (NumberFormatException var3) {
                return defaultValue;
            }
        }
    }

    public static long toLong(@Nullable final Object obj, final long defaultValue) {
        if (obj == null) {
            return defaultValue;
        } else {
            try {
                return Long.valueOf(String.valueOf(obj));
            } catch (NumberFormatException var4) {
                return defaultValue;
            }
        }
    }

    public static Float toFloat(@Nullable Object obj, Float defaultValue) {
        return obj != null ? Float.valueOf(String.valueOf(obj).trim()) : defaultValue;
    }

    public static Double toDouble(@Nullable Object obj, Double defaultValue) {
        return obj != null ? Double.valueOf(String.valueOf(obj).trim()) : defaultValue;
    }

    public static Boolean toBoolean(Object obj, Boolean defaultValue) {
        if (obj != null) {
            String val = String.valueOf(obj);
            val = val.toLowerCase().trim();
            return Boolean.parseBoolean(val);
        } else {
            return defaultValue;
        }
    }

    public Kv clone() {
        Kv clone = new Kv();
        clone.putAll(this);
        return clone;
    }

}

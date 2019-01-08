package com.cry.glide_like.cache;

import java.security.MessageDigest;

/**
 * 简单的Key,因为要放到HashMap中，所以一定要实现 hashCode和 equals方法
 */
public class ObjectKey implements Key {

    private final Object obj;

    public ObjectKey(Object object) {
        this.obj = object;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest digest) {
        digest.update(getKeyBytes());
    }

    @Override
    public byte[] getKeyBytes() {
        return obj.toString().getBytes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectKey objectKey = (ObjectKey) o;

        return obj != null ? obj.equals(objectKey.obj) : objectKey.obj == null;
    }

    @Override
    public int hashCode() {
        return obj != null ? obj.hashCode() : 0;
    }
}

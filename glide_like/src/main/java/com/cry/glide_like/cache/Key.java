package com.cry.glide_like.cache;

import java.security.MessageDigest;

/**
 * 用于保存的key.
 * 需要手动实现加密的方式
 */
public interface Key {
    void updateDiskCacheKey(MessageDigest digest);

    byte[] getKeyBytes();
}

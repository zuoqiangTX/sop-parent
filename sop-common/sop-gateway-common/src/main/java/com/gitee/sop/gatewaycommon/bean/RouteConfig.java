package com.gitee.sop.gatewaycommon.bean;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author tanghc
 */
@Data
public class RouteConfig {

    public static final byte STATUS_ENABLE = 1;
    public static final byte LIMIT_STATUS_CLOSE = 0;

    private String routeId;

    /** 限流策略，1：漏桶策略，2：令牌桶策略, 数据库字段：limit_type */
    private Byte limitType = 1;

    /** 每秒可处理请求数, 数据库字段：exec_count_per_second */
    private Integer execCountPerSecond = 10;

    /** 返回的错误码, 数据库字段：limit_code */
    private String limitCode = "isp.service-busy";

    /** 返回的错误信息, 数据库字段：limit_msg */
    private String limitMsg = "服务繁忙，请稍后再试";

    /** 令牌桶容量, 数据库字段：token_bucket_count */
    private Integer tokenBucketCount = 10;

    /** 限流开启状态，1:开启，0关闭, 数据库字段：limit_status */
    private Byte limitStatus = LIMIT_STATUS_CLOSE;

    /**
     * 状态，0：待审核，1：启用，2：禁用。默认启用
     */
    private Byte status = STATUS_ENABLE;

    /**
     * 漏桶计数器
     */
    private LoadingCache<Long, AtomicLong> counter = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build(new CacheLoader<Long, AtomicLong>() {
                @Override
                public AtomicLong load(Long seconds) throws Exception {
                    return new AtomicLong(0);
                }
            });

    /**
     * 令牌桶
     */
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private volatile RateLimiter rateLimiter;

    public synchronized void initRateLimiter() {
        rateLimiter = RateLimiter.create(tokenBucketCount);
    }

    /**
     * 获取令牌桶
     * @return
     */
    public synchronized RateLimiter fetchRateLimiter() {
        if (rateLimiter == null) {
            synchronized (this.routeId) {
                if (rateLimiter == null) {
                    rateLimiter = RateLimiter.create(tokenBucketCount);
                }
            }
        }
        return rateLimiter;
    }

    /**
     * 是否启用
     * @return true：启用
     */
    public boolean enable() {
        return status == STATUS_ENABLE;
    }
}

package com.example.vvpcommom.redis;


public class JedisConfig {

    private PoolConfig pool = new PoolConfig();

    public PoolConfig getPool() {
        return pool;
    }

    public void setPool(PoolConfig pool) {
        this.pool = pool;
    }
}

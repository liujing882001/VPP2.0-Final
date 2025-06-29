package com.example.vvpcommom.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 集群环境配置，在spring.redis.stand-alone中为false 时生效
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.redis", name = "standalone", havingValue = "false")
public class RedisClusterInitConfig {

    private static final String COMMA = ",";
    private static final String COLON = ":";

    @Autowired
    private RedisConfig redisConfig;

    @Bean
    public JedisPoolConfig poolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        PoolConfig pool = redisConfig.getJedis().getPool();
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMaxWaitMillis(pool.getMaxWait());
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMinIdle(pool.getMinIdle());

        return poolConfig;
    }

    @Bean
    public RedisClusterConfiguration redisConfiguration() {

        List<String> nodes = redisConfig.getCluster().getNodes();
        if (nodes == null || nodes.size() <= 0) {
            throw new RedisException("请配置集群连接地址");
        }
        List<RedisNode> redisNodes = new ArrayList<>();
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        for (String node : nodes) {
            String host = node.split(COLON)[0];
            Integer port = Integer.valueOf(node.split(COLON)[1]);
            redisNodes.add(new RedisNode(host, port));
        }
        redisClusterConfiguration.setClusterNodes(redisNodes);
        redisClusterConfiguration.setPassword(RedisPassword.of(redisConfig.getPassword()));
        return redisClusterConfiguration;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig poolConfig,
                                                         RedisClusterConfiguration redisConfiguration) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(redisConfiguration);
        connectionFactory.setPoolConfig(poolConfig);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }
}

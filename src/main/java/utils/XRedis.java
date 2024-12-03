package utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class XRedis {
    private static final JedisPool pool = new JedisPool("localhost", 6379);

    public static Jedis getPoolResource() {
        return pool.getResource();
    }
}

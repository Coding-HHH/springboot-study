package com.example.study.utils.reidsUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis 工具类
 */
@Component
public class RedisUtils {

    @Value("${spring.redis.host}")
    private  String host;

    @Value("${spring.redis.database}")
    private  Integer database;

    @Value("${spring.redis.port}")
    private  int port;

    @Value("${spring.redis.timeout}")
    private  int timeout;

    @Value("${spring.redis.password}")
    private  String password;

    //设置前缀
    private static final String keyPrefix = "TEST";

    public static JedisPool jedisPool;

    @Autowired
    public void setJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(8);
        jedisPoolConfig.setMaxTotal(8);
        jedisPoolConfig.setMaxWaitMillis(-1);
        jedisPoolConfig.setMinIdle(0);
        jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
    }


    private static void closeJedis(Jedis jedis) {
        if (jedis != null) {
            //获取连接失败时，应该返回给pool,否则每次发生异常将导致一个jedis对象没有被回收。
            jedis.close();
        }
    }

    public static void clear() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.flushAll();
        } finally {
            // 返还到连接池
            closeJedis(jedis);
        }
    }

    /**
     * 设置key-value对
     */
    public static boolean set(String keyPrefix, String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key,value);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
        } finally {
            // 返还到连接池
            closeJedis(jedis);
        }
        return true;
    }

    /**
     * 设置key-value对，并设置过期时间（单位：s）
     */
    public static boolean setEx(String keyPrefix, String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.setex(key, seconds, value);
            return true;
        } finally {
            // 返还到连接池
            closeJedis(jedis);
        }
    }

    /**
     * @param keyPrefix key前缀
     * @param key       key
     * @param value     value
     * @param nxxx      nx:key 不存在时set,xx:key 存在时set
     * @param expx      ex:seconds 秒,px:milliseconds 毫秒
     * @param time      时间
     * @return 执行结果:true 成功,false 失败
     */
    public static boolean set(String keyPrefix, String key, String value, String nxxx, String expx, long time) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String set = jedis.set(key, value, nxxx, expx, time);
            return "OK".equals(set);
        } finally {
            // 返还到连接池
            closeJedis(jedis);
        }
    }


    /**
     * 根据key查询value
     *
     * @param key
     * @return
     */
    public static String get(String keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            if (jedis != null) {
                jedis.close();
            }
        } finally {
            // 返还到连接池
            closeJedis(jedis);
        }
        return null;
    }


    /**
     * 删除key-value对
     *
     * @param key
     */
    public static void del(String keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
        } finally {
            // 返还到连接池
            closeJedis(jedis);
        }
    }

    /**
     * 对key对应的值减一
     *
     * @param key
     * @return
     */
    public static long decr(String keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.decr(key);
        } finally {
            // 返还到连接池
            closeJedis(jedis);
        }
    }


    /**
     * 对key对应的值减num
     *
     * @param key
     * @return
     */
    public static long decrBy(String keyPrefix, String key, long num) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.decrBy(key, num);
        } finally {
            // 返还到连接池
            closeJedis(jedis);
        }
    }


    /**
     * 对key对应的值加num
     *
     * @param key
     * @return
     */
    public static long incrBy(String keyPrefix, String key, long num) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.incrBy(key, num);
        } finally {
            // 返还到连接池
            closeJedis(jedis);
        }
    }



    /**
     * 对key对应的值加num
     * 过期时间
     *
     * @param key
     * @return
     */
    public static long incrByTime(String keyPrefix, String key, long num, int seconds) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Long aLong = jedis.incrBy(key, num);
            jedis.expire(key, seconds);
            return aLong;
        } finally {
            // 返还到连接池
            closeJedis(jedis);
        }
    }

    /**
     * @param key
     * @return Map<String, String>
     * @throws
     * @Title: hGetAll
     * @Description: 获取redis hash结构对应key的值
     */
    public static Map<String, String> hgetAll(String keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hgetAll(key);
        } finally {
            // 返还到连接池
            closeJedis(jedis);
        }
    }

    /**
     * @param key
     * @param field
     * @param value
     * @return
     * @throws
     * @Title: hSet
     * @Description: 存入redis hash结构
     */
    public static void hset(String keyPrefix, String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hset(key, field, value);
        } finally {
            // 返还到连接池
            closeJedis(jedis);
        }
    }

    /**
     * @param key
     * @param field
     * @return String value
     * @throws
     * @Title: hGet
     * @Description: 获取对应hash下key的数据
     */
    public static String hget(String keyPrefix, String key, String field) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String value = jedis.hget(key, field);
            return value;
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * @param key
     * @param fields
     * @return
     * @throws
     * @Title: hDel
     * @Description: redis hash结构删除key下对应field的值
     */
    public static void hdel(String keyPrefix, String key, String... fields) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            if (fields.length == 0) {
                jedis.hdel(key);
            } else {
                for (String field : fields) {
                    jedis.hdel(key, field);
                }
            }
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * @param key
     * @param field
     * @return boolean
     * @throws
     * @Title: HExists
     * @Description: redis hash结构：查看哈希表 key 中，给定域 field 是否存在。
     */
    public static boolean hexists(String keyPrefix, String key, String field) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hexists(key, field);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * @param key
     * @return boolean
     * @throws
     * @Title: HExists
     * @Description: redis hash结构：获取所有hash表的field
     */
    public static Set<String> hkeys(String keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hkeys(key);
        } finally {
            closeJedis(jedis);
        }
    }

    public static Long hincrby(String keyPrefix, String key, String field, Long count) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hincrBy(key, field, count);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * Set集合添加数据
     *
     * @param key
     * @param val
     */
    public static void sadd(String keyPrefix, String key, String val) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.sadd(key, val);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 设置
     *
     * @param key
     * @param val
     * @return
     */
    public static Long setnx(String keyPrefix, String key, String val) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.setnx(key, val);
        } catch (Exception e) {
            return 0L;
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param sec
     */
    public static void expire(String keyPrefix, String key, int sec) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.expire(key, sec);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 把java对象存入redis
     *
     * @param key
     * @param obj
     * @param expireSecond 过期时间
     * @param <T>
     * @author www.magicalcoder.com
     */
    public static <T> void setexObj(String keyPrefix, String key, int expireSecond, T obj) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            if (obj == null) {
                jedis.del(key);
                return;
            }
            //序列化
            byte[] bytes = ProtostuffUtil.serialize(obj);
            //如果 key 已经存在， setex 命令将会替换旧的值。
            jedis.setex(key.getBytes("UTF-8"), expireSecond, bytes);
        } catch (UnsupportedEncodingException e) {

        } finally {
            closeJedis(jedis);
        }
    }


    /**
     * 从redis获取java对象
     *
     * @param key
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T getObj(String keyPrefix, String key, Class<T> tClass) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] bytes = jedis.get(key.getBytes("UTF-8"));
            if (bytes != null) {
                //序列化
                return ProtostuffUtil.deserialize(bytes);
            }
        } catch (UnsupportedEncodingException e) {
            if (jedis != null) {
                //获取连接失败时，应该返回给pool,否则每次发生异常将导致一个jedis对象没有被回收。
                jedis.close();
            }
        } finally {
            if (jedis != null) {
                //获取连接失败时，应该返回给pool,否则每次发生异常将导致一个jedis对象没有被回收。
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 把java集合存入redis
     *
     * @param key
     * @param list
     * @author www.magicalcoder.com
     */
    public static <T> void setList(String keyPrefix, String key, List<T> list) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            for (Object o : list) {
                //序列化
                byte[] bytes = ProtostuffUtil.serialize(o);
                jedis.rpush(key.getBytes("UTF-8"), bytes);
//				jedis.expire(key.getBytes("UTF-8"),100);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 把java集合存入redis
     *
     * @param key
     * @param list
     * @author www.magicalcoder.com
     */
    public static <T> void setList(String keyPrefix, String key, List<T> list, Integer seconds) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            for (Object o : list) {
                //序列化
                byte[] bytes = ProtostuffUtil.serialize(o);
                jedis.rpush(key.getBytes("UTF-8"), bytes);
                if (seconds != null) {
                    jedis.expire(key.getBytes("UTF-8"), seconds);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 添加对象到集合中
     *
     * @param key
     * @param obj
     * @author www.magicalcoder.com
     */
    public static <T> void setObjToList(String keyPrefix, String key, T obj) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            //序列化
            byte[] bytes = ProtostuffUtil.serialize(obj);
            jedis.rpush(key.getBytes("UTF-8"), bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 获取集合
     *
     * @param key
     * @param keyPrefix
     * @author www.magicalcoder.com
     */
    public static <T> List<T> getList(String keyPrefix, String key) {
        Jedis jedis = null;
        List value = new ArrayList<T>();

        try {
            jedis = jedisPool.getResource();
            List<byte[]> list = jedis.lrange(key.getBytes("UTF-8"), 0, -1);
            for (byte[] bs : list) {
                value.add(ProtostuffUtil.deserialize(bs));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            closeJedis(jedis);
        }
        return value;
    }

    /**
     * 获取分页集合
     *
     * @param key
     * @param keyPrefix
     * @author www.magicalcoder.com
     */
    public static <T> List<T> getList(String keyPrefix, String key, Integer page, Integer limit) {
        int start = (page - 1) * limit;
        int end = page * limit - 1;
        Jedis jedis = null;
        List value = new ArrayList<T>();

        try {
            jedis = jedisPool.getResource();
            List<byte[]> list = jedis.lrange(key.getBytes("UTF-8"), start, end);
            for (byte[] bs : list) {
                value.add(ProtostuffUtil.deserialize(bs));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            closeJedis(jedis);
        }
        return value;
    }

    /**
     * 把java对象存入redis
     *
     * @param key
     * @param obj
     * @param <T>
     * @author www.magicalcoder.com
     */
    public static <T> void setexObj(String keyPrefix, String key, T obj) {
        Jedis jedis = null;

        try {
            jedis = jedisPool.getResource();
            if (obj == null) {
                jedis.del(key);
                return;
            }
            //序列化
            byte[] bytes = ProtostuffUtil.serialize(obj);
            jedis.set(key.getBytes("UTF-8"), bytes);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 是否存在key
     *
     * @param key
     * @return
     */
    public static boolean exist(String keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.exists(key);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * redis简单分布式锁 最主要是一个命令执行
     *
     * @param key
     * @param uuid      UUID.randomUUID().toString() 解锁使用
     * @param milliTime 毫秒
     * @return
     */
    public static boolean tryDistributeLock(String keyPrefix, String key, String uuid, int milliTime) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.set(key, uuid, "NX", "PX", milliTime);
            if ("OK".equals(result)) {
                return true;
            }
        } finally {
            closeJedis(jedis);
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param key
     * @param uuid
     * @return
     */
    public static void releaseDistributeLock(String keyPrefix, String key, String uuid) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String redisScript = "if redis.call('get', KEYS[1]) == ARGV[1] then redis.call('del', KEYS[1]) end";
            jedis.eval(redisScript, Collections.singletonList(key), Collections.singletonList(uuid));
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 获取指定键对应所有值的集合
     *
     * @param key
     * @return
     */
    public static Set getValSet(String keyPrefix, String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Set<String> valueSet = jedis.smembers(key);
            return valueSet;
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 获取指定键及对应条数值的集合
     *
     * @param key
     * @param count
     * @return
     */
    public static List<String> srandmber(String keyPrefix, String key, int count) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            List<String> valueSet = jedis.srandmember(key, count);
            return valueSet;
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 移除集合中的成员
     *
     * @param key
     * @param value
     * @return
     */
    public static void removeMem(String keyPrefix, String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.srem(key, value);
        } finally {
            closeJedis(jedis);
        }
    }

    public static void removeMem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.srem(key, value);
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 在制定expireSeconds时间内 key自增的最大
     * redis 执行lua脚本竟然不支持 >= <= 所以要改成> 或 == 或< 分开判断
     *
     * @param key
     * @param expireSeconds
     * @param maxTime
     * @return
     */
    public static boolean tryIncr(String keyPrefix, String key, int expireSeconds, int maxTime) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            StringBuilder redisScript = new StringBuilder("local current;");
            redisScript.append(" current = redis.call('incr',KEYS[1]); ");
            redisScript.append("  local curNo = tonumber(current);");
            redisScript.append("  local maxTime = tonumber(ARGV[2]);");
            redisScript.append(" if curNo > 1 then ");
            redisScript.append(" 	if curNo < maxTime then ");
            redisScript.append("     	return 2; ");
            redisScript.append(" 	elseif curNo == maxTime then ");
            redisScript.append("     	return 3; ");
            redisScript.append("	else ");
            redisScript.append("		return -1; ");
            redisScript.append("   end ");
            redisScript.append(" else");
            redisScript.append(" 	redis.call('expire',KEYS[1],ARGV[1]); ");
            redisScript.append("     return 1; ");
            redisScript.append(" end ");
            List<String> args = new ArrayList<>(2);
            args.add(String.valueOf(expireSeconds));
            args.add(String.valueOf(maxTime));
            Object ret = jedis.eval(redisScript.toString(), Collections.singletonList(key), args);
            Integer value = Integer.valueOf(ret.toString());
            if (value < 0) {
                return false;
            }
            return true;
        } finally {
            closeJedis(jedis);
        }
    }

    /**
     * 设置某个值进行自增处理 用于编号处理  2020-8-31
     *
     * @param key 缓存的键值
     */
    public Long  autoincrement(RedisTemplate redisTemplate, String key)
    {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.getAndIncrement();
        return increment;
    }


    /**
     * 设置某个值进行自减处理 用于编号处理  2020-8-31  数据是数字处理
     *
     * @param key 缓存的键值
     */
    public Long  autodecrement(RedisTemplate redisTemplate,String key)
    {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long decrment = entityIdCounter.getAndDecrement();
        return decrment;
    }

    /**
     * 创建锁
     * @param key         锁的Key
     * @param value       值(随便写毫无意义)
     * @param releaseTime 锁过期时间 防止死锁
     * @return boolean
     */
    public boolean lock(String key,String value, long releaseTime,RedisTemplate redisTemplate) {
        SessionCallback<Boolean> sessionCallback = new SessionCallback<Boolean>() {
            List<Object> exec = null;
            @Override
            @SuppressWarnings("unchecked")
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                redisTemplate.opsForValue().setIfAbsent(key, value);
                redisTemplate.expire(key, releaseTime, TimeUnit.SECONDS);
                exec = operations.exec();
                if(exec.size() > 0) {
                    //事务判断
                    return (Boolean) exec.get(0);
                }
                return false;
            }
        };
        return (boolean) redisTemplate.execute(sessionCallback);
    }


}

package com.zqf.util;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

/**
 * ParentDao 操作字符串redis缓存方法 list中的操作全是按照right方式
 * 
 * @author littlehow
 * @time 2016-08-12 09:02
 */
public class RedisUtil {
	/**
	 * 日志记录
	 */
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private RedisTemplate<String, String> redisTemplate;

	public RedisTemplate<String, String> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * 前缀
	 */
	public static final String KEY_PREFIX_VALUE = "dg:report:value:";
	public static final String KEY_PREFIX_SET = "dg:report:set:";
	public static final String KEY_PREFIX_LIST = "dg:report:list:";

	/**
	 * 缓存value操作
	 * 
	 * @param k
	 * @param v
	 * @param time
	 * @return
	 */
	protected boolean cacheValue(String k, String v, long time) {
		String key = KEY_PREFIX_VALUE + k;
		try {
			ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
			valueOps.set(key, v);
			if (time > 0)
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			return true;
		} catch (Throwable t) {
			logger.error("缓存[" + key + "]失败, value[" + v + "]", t);
		}
		return false;
	}

	/**
	 * 缓存value操作
	 * 
	 * @param k
	 * @param v
	 * @return
	 */
	protected boolean cacheValue(String k, String v) {
		return cacheValue(k, v, -1);
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param k
	 * @return
	 */
	protected boolean containsValueKey(String k) {
		return containsKey(KEY_PREFIX_VALUE + k);
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param k
	 * @return
	 */
	protected boolean containsSetKey(String k) {
		return containsKey(KEY_PREFIX_SET + k);
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param k
	 * @return
	 */
	protected boolean containsListKey(String k) {
		return containsKey(KEY_PREFIX_LIST + k);
	}

	protected boolean containsKey(String key) {
		try {
			return redisTemplate.hasKey(key);
		} catch (Throwable t) {
			logger.error("判断缓存存在失败key[" + key + ", error[" + t + "]");
		}
		return false;
	}

	/**
	 * 获取缓存
	 * 
	 * @param k
	 * @return
	 */
	protected String getValue(String k) {
		try {
			ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
			return valueOps.get(KEY_PREFIX_VALUE + k);
		} catch (Throwable t) {
			logger.error("获取缓存失败key[" + KEY_PREFIX_VALUE + k + ", error[" + t + "]");
		}
		return null;
	}

	/**
	 * 移除缓存
	 * 
	 * @param k
	 * @return
	 */
	protected boolean removeValue(String k) {
		return remove(KEY_PREFIX_VALUE + k);
	}

	protected boolean removeSet(String k) {
		return remove(KEY_PREFIX_SET + k);
	}

	protected boolean removeList(String k) {
		return remove(KEY_PREFIX_LIST + k);
	}

	/**
	 * 移除缓存
	 * 
	 * @param key
	 * @return
	 */
	protected boolean remove(String key) {
		try {
			redisTemplate.delete(key);
			return true;
		} catch (Throwable t) {
			logger.error("获取缓存失败key[" + key + ", error[" + t + "]");
		}
		return false;
	}

	/**
	 * 缓存set操作
	 * 
	 * @param k
	 * @param v
	 * @param time
	 * @return
	 */
	protected boolean cacheSet(String k, String v, long time) {
		String key = KEY_PREFIX_SET + k;
		try {
			SetOperations<String, String> valueOps = redisTemplate.opsForSet();
			valueOps.add(key, v);
			if (time > 0)
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			return true;
		} catch (Throwable t) {
			logger.error("缓存[" + key + "]失败, value[" + v + "]", t);
		}
		return false;
	}

	/**
	 * 缓存set
	 * 
	 * @param k
	 * @param v
	 * @return
	 */
	protected boolean cacheSet(String k, String v) {
		return cacheSet(k, v, -1);
	}

	/**
	 * 缓存set
	 * 
	 * @param k
	 * @param v
	 * @param time
	 * @return
	 */
	protected boolean cacheSet(String k, Set<String> v, long time) {
		String key = KEY_PREFIX_SET + k;
		try {
			SetOperations<String, String> setOps = redisTemplate.opsForSet();
			setOps.add(key, v.toArray(new String[v.size()]));
			if (time > 0)
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			return true;
		} catch (Throwable t) {
			logger.error("缓存[" + key + "]失败, value[" + v + "]", t);
		}
		return false;
	}

	/**
	 * 缓存set
	 * 
	 * @param k
	 * @param v
	 * @return
	 */
	protected boolean cacheSet(String k, Set<String> v) {
		return cacheSet(k, v, -1);
	}

	/**
	 * 获取缓存set数据
	 * 
	 * @param k
	 * @return
	 */
	protected Set<String> getSet(String k) {
		try {
			SetOperations<String, String> setOps = redisTemplate.opsForSet();
			return setOps.members(KEY_PREFIX_SET + k);
		} catch (Throwable t) {
			logger.error("获取set缓存失败key[" + KEY_PREFIX_SET + k + ", error[" + t + "]");
		}
		return null;
	}

	/**
	 * list缓存
	 * 
	 * @param k
	 * @param v
	 * @param time
	 * @return
	 */
	protected boolean cacheList(String k, String v, long time) {
		String key = KEY_PREFIX_LIST + k;
		try {
			ListOperations<String, String> listOps = redisTemplate.opsForList();
			listOps.rightPush(key, v);
			if (time > 0)
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			return true;
		} catch (Throwable t) {
			logger.error("缓存[" + key + "]失败, value[" + v + "]", t);
		}
		return false;
	}

	/**
	 * 缓存list
	 * 
	 * @param k
	 * @param v
	 * @return
	 */
	protected boolean cacheList(String k, String v) {
		return cacheList(k, v, -1);
	}

	/**
	 * 缓存list
	 * 
	 * @param k
	 * @param v
	 * @param time
	 * @return
	 */
	protected boolean cacheList(String k, List<String> v, long time) {
		String key = KEY_PREFIX_LIST + k;
		try {
			ListOperations<String, String> listOps = redisTemplate.opsForList();
			long l = listOps.rightPushAll(key, v);
			if (time > 0)
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			return true;
		} catch (Throwable t) {
			logger.error("缓存[" + key + "]失败, value[" + v + "]", t);
		}
		return false;
	}

	/**
	 * 缓存list
	 * 
	 * @param k
	 * @param v
	 * @return
	 */
	protected boolean cacheList(String k, List<String> v) {
		return cacheList(k, v, -1);
	}

	/**
	 * 获取list缓存
	 * 
	 * @param k
	 * @param start
	 * @param end
	 * @return
	 */
	protected List<String> getList(String k, long start, long end) {
		try {
			ListOperations<String, String> listOps = redisTemplate.opsForList();
			return listOps.range(KEY_PREFIX_LIST + k, start, end);
		} catch (Throwable t) {
			logger.error("获取list缓存失败key[" + KEY_PREFIX_LIST + k + ", error[" + t + "]");
		}
		return null;
	}

	/**
	 * 获取总条数, 可用于分页
	 * 
	 * @param k
	 * @return
	 */
	protected long getListSize(String k) {
		try {
			ListOperations<String, String> listOps = redisTemplate.opsForList();
			return listOps.size(KEY_PREFIX_LIST + k);
		} catch (Throwable t) {
			logger.error("获取list长度失败key[" + KEY_PREFIX_LIST + k + "], error[" + t + "]");
		}
		return 0;
	}

	/**
	 * 获取总条数, 可用于分页
	 * 
	 * @param listOps
	 * @param k
	 * @return
	 */
	protected long getListSize(ListOperations<String, String> listOps, String k) {
		try {
			return listOps.size(KEY_PREFIX_LIST + k);
		} catch (Throwable t) {
			logger.error("获取list长度失败key[" + KEY_PREFIX_LIST + k + "], error[" + t + "]");
		}
		return 0;
	}

	/**
	 * 移除list缓存
	 * 
	 * @param k
	 * @return
	 */
	protected boolean removeOneOfList(String k) {
		String key = KEY_PREFIX_LIST + k;
		try {
			ListOperations<String, String> listOps = redisTemplate.opsForList();
			listOps.rightPop(KEY_PREFIX_LIST + k);
			return true;
		} catch (Throwable t) {
			logger.error("移除list缓存失败key[" + KEY_PREFIX_LIST + k + ", error[" + t + "]");
		}
		return false;
	}
}

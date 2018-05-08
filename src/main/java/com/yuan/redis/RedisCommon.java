package com.yuan.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yuan.util.BaseException;
import com.yuan.util.LogUtil;
import com.yuan.util.constant.PubErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

/**
 *
 */
public abstract class RedisCommon extends RedisTemplate<String, Object> {

	private static Logger logger = LoggerFactory.getLogger(RedisCommon.class);
	public static final String REDIS_CODE = "UTF-8";

	/**
	 * 获得某个Key的值
	 * @param key
	 * @return
	 * @throws BaseException
	 */
	public String get(final String key) throws BaseException {
		Object val = this.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				Object v = null;
				try {
					byte[] bs = connection.get(key.getBytes());
					if (bs == null) {
						return v;
					}
					v = new String(bs, REDIS_CODE);
				} catch (Exception e) {
					v = e;
				} finally {
					connection.close();
				}
				return v;
			}

		});
		if (val != null && val instanceof Exception) {
		    throw LogUtil.handerEx(PubErrorCode.ERROR_REDIS_QUERY, "查询redis值失败,查询参数为："+key, LogUtil.ERROR, (Exception)val);
		}
		if (val == null) {
			return null;
		}
		return (String) val;
	}
	
	/**存放指定生效期内redis值
	 * @param key
	 * @param value
	 * @param liveTime：存放redis库中的有效时间
	 * @throws BaseException
	 */
	public void set(final byte[] key, final byte[] value, final long liveTime) throws BaseException {
		Exception res = this.execute(new RedisCallback<Exception>() {
			public Exception doInRedis(RedisConnection connection) throws DataAccessException {
				Exception result = null;
				try {
					connection.set(key, value);
					if (liveTime > 0) {
						connection.expire(key, liveTime);
					}
				} catch (Exception e) {
					result = e;
				} finally {
					connection.close();
				}
				return result;
			}
		});
		if (res != null) {
		    throw LogUtil.handerEx(PubErrorCode.ERROR_REDIS_SET, "存放redis值失败，存放key为："+key, LogUtil.ERROR, (Exception)res);
		}
	}

	public void set(String key, String value, long liveTime) throws BaseException {
		set(key.getBytes(), value.getBytes(), liveTime);
	}
	
	/**存值至redis中，数据类型都为string
	 * 备注：时效性无限
	 * @param key
	 * @param value
	 * @throws BaseException
	 */
	public void set(String key, String value) throws BaseException {
		set(key.getBytes(), value.getBytes(), 0L);
	}
	
	public void set(byte[] key, byte[] value) throws BaseException {
		set(key, value, 0L);
	}
	
	/**
	 * 删除一组keys的所有值
	 * @param keys
	 * @return
	 * @throws BaseException
	 */
	public boolean del(final String... keys) throws BaseException {
		Object obj = this.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				Object result = Boolean.FALSE;
				try {
					for (int i = 0; i < keys.length; i++) {
						connection.del(keys[i].getBytes());
					}
					result = Boolean.TRUE;
				} catch (Exception e) {
					result = e;
				} finally {
					connection.close();
				}
				return result;
			}
		});
		if (obj instanceof Exception) {
		    throw LogUtil.handerEx(PubErrorCode.ERROR_REDIS_DEL_SOME, "删除redis中一组key的值失败", LogUtil.ERROR, (Exception)obj);
		}
		return (Boolean) obj;
	}
	
	/**
	 * 删除某个key，成功返回true,失败返回false
	 * @param key
	 * @return
	 * @throws BaseException
	 */
	public boolean delSingleKey(final String key) throws BaseException {
		Object obj = this.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				Object result = Boolean.FALSE;
				try {
					Long row = connection.del(key.getBytes());
					if (row > 0) {
						result = Boolean.TRUE;
					} else {
						result = Boolean.FALSE;
					}
				} catch (Exception e) {
					result = e;
				} finally {
					connection.close();
				}
				return result;
			}
		});
		if (obj instanceof Exception) {
		    throw LogUtil.handerEx(PubErrorCode.ERROR_REDIS_DEL_ONE, "删除redis中某个key的值失败，key为："+key, LogUtil.ERROR, (Exception)obj);
		}
		return (Boolean) obj;
	}
	
	/**
	 * 判断redis中key是否存在
	 * @param key
	 * @return true存在，false不存在
	 * @throws BaseException
	 */
	public boolean exists(final String key) throws BaseException {
		Object obj = this.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				Object result = Boolean.FALSE;
				try {
					result = connection.exists(key.getBytes());
				} catch (Exception e) {
					result = e;
				} finally {
					connection.close();
				}
				return result;
			}
		});
		if (obj instanceof Exception) {
		    throw LogUtil.handerEx(PubErrorCode.ERROR_REDIS_EXISTS, "验证redis中某个key的值是否存在失败，key为："+key, LogUtil.ERROR, (Exception)obj);
		}
		return (Boolean) obj;
	}
	
	/**
	 * 获得整个list
	 * @param key
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("unchecked")
    public List<String> getList(final String key) throws BaseException {
		Object val = this.execute(new RedisCallback<Object>() {
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				List<String> slist = new ArrayList<String>();
				long begin = 0L;
				long end = -1L;
				List<byte[]> r = new ArrayList<byte[]>();
				try {
					r = connection.lRange(key.getBytes(), begin, end);
					if (r != null && r.size() > 0) {
						for (byte[] bs : r) {
							if (bs != null) {
								String res = new String(bs, REDIS_CODE);
								slist.add(res);
							}
						}
					}
				} catch (Exception e) {
					return e;
				} finally {
					connection.close();
				}
				return slist;
			}
		});
		if (val != null && val instanceof Exception) {
		    throw LogUtil.handerEx(PubErrorCode.ERROR_REDIS_QUERY, "验证redis中某个key的值是否存在失败，key为："+key, LogUtil.ERROR, (Exception)val);
		}
		return (List<String>) val;
	}

	/**
	 * 从 list 的尾部删除元素,并返回删除元素
	 * 
	 * @param key
	 * @return
	 * @throws BaseException
	 */
	public String rPop(final String key) {

		return this.execute(new RedisCallback<String>() {
			public String doInRedis(RedisConnection connection) throws DataAccessException {
				String value = null;
				try {
					byte[] listByte = connection.rPop(key.getBytes());
					if (listByte == null) {
						return value;
					}
					value = new String(listByte, REDIS_CODE);
				} catch (Exception e) {
					logger.error("Exception happens when rPop,the key is " + key, e);
				} finally {
					connection.close();
				}
				return value;
			}
		});
	}

	/**
	 * 在 key对应 list 的头部添加字符串元素
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws BaseException
	 */
	public boolean lpush(final String key, final String value) {

		return this.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				boolean resoult = false;
				try {
					connection.lPush(key.getBytes(), value.getBytes());
					resoult = true;
				} catch (Exception e) {
					logger.error("Redis lpush Exception :", e);
				} finally {
					connection.close();
				}
				return resoult;
			}
		});
	}
	
	/**查询队列是否为空
	 * @param listName
	 * @return
	 */
	public boolean isQueueEmpty(final String listName) {
		Long size = this.opsForList().size(listName);
		if (size > 0){
		    return false;
		}
		return true;
	}

	public Map<String, String> rkeyLike(final String keyLike) {
		return this.execute(new RedisCallback<Map<String, String>>() {
			public Map<String, String> doInRedis(RedisConnection connection) throws DataAccessException {
				Map<String, String> values = new HashMap<String, String>();
				try {
					Set<byte[]> keys = connection.keys((keyLike + "*").getBytes());
					for (byte[] b : keys) {
						byte[] v = connection.get(b);
						if (v != null) {
							values.put(new String(b, REDIS_CODE), new String(v, REDIS_CODE));
						}
					}
				} catch (Exception e) {
					logger.error("Exception happens when rkeyLike,the key is " + keyLike, e);
				} finally {
					connection.close();
				}
				return values;
			}
		});
	}
	/**
	 * 此方法是原子性的 作用:假如key不存在时则保存值并返回true表示保存成功,否则返回false保存失败
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean saveIfNotExist(final String key, final String value) {
		return this.execute(new RedisCallback<Boolean>() {
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					return connection.setNX(key.getBytes(), value.getBytes());
				} finally {
					connection.close();
				}
			}
		});
	}

	public Boolean MyExpire(String key, long timeout) {
		return true;
	};
	/**
	 * 删除当前redis的db的数据
	 */
	public void flushdb() {
		this.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				try {
					connection.flushDb();
					return true;
				} finally {
					connection.close();
				}
			}
		});
	}
}

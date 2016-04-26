package com.lap.zuzuweb.util;

import java.util.ArrayList;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LRUMap;

/**
 * 
 * @author eechih
 *
 * @param <K>
 * @param <T>
 */
public class CrunchifyInMemoryCache<K, T> {

	private long timeToLive;
	private LRUMap<K, CrunchifyCacheObject> crunchifyCacheMap;

	protected class CrunchifyCacheObject {
		public long lastAccessed = System.currentTimeMillis();
		public T value;

		protected CrunchifyCacheObject(T value) {
			this.value = value;
		}
	}

	/**
	 * 
	 * @param crunchifyTimeToLive (Seconds)
	 * @param crunchifyTimerInterval (Seconds)
	 * @param maxItems
	 */
	public CrunchifyInMemoryCache(long crunchifyTimeToLive, final long crunchifyTimerInterval, int maxItems) {
		this.timeToLive = crunchifyTimeToLive * 1000;

		crunchifyCacheMap = new LRUMap<K, CrunchifyCacheObject>(maxItems);

		if (timeToLive > 0 && crunchifyTimerInterval > 0) {

			Thread t = new Thread(new Runnable() {
				public void run() {
					while (true) {
						try {
							Thread.sleep(crunchifyTimerInterval * 1000);
						} catch (InterruptedException ex) {
						}
						cleanup();
					}
				}
			});

			t.setDaemon(true);
			t.start();
		}
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void put(K key, T value) {
		synchronized (crunchifyCacheMap) {
			crunchifyCacheMap.put(key, new CrunchifyCacheObject(value));
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public T get(K key) {
		synchronized (crunchifyCacheMap) {
			CrunchifyCacheObject c = (CrunchifyCacheObject) crunchifyCacheMap.get(key);

			if (c == null)
				return null;
			else {
				c.lastAccessed = System.currentTimeMillis();
				return c.value;
			}
		}
	}

	/**
	 * 
	 * @param key
	 */
	public void remove(K key) {
		synchronized (crunchifyCacheMap) {
			crunchifyCacheMap.remove(key);
		}
	}

	/**
	 * 
	 * @return
	 */
	public int size() {
		synchronized (crunchifyCacheMap) {
			return crunchifyCacheMap.size();
		}
	}

	/**
	 * 
	 */
	public void cleanup() {

		long now = System.currentTimeMillis();
		ArrayList<K> deleteKey = null;

		synchronized (crunchifyCacheMap) {
			MapIterator<K, CrunchifyCacheObject> itr = crunchifyCacheMap.mapIterator();

			deleteKey = new ArrayList<K>((crunchifyCacheMap.size() / 2) + 1);
			K key = null;
			CrunchifyCacheObject c = null;

			while (itr.hasNext()) {
				key = (K) itr.next();
				c = (CrunchifyCacheObject) itr.getValue();

				if (c != null && (now > (timeToLive + c.lastAccessed))) {
					deleteKey.add(key);
				}
			}
		}

		for (K key : deleteKey) {
			synchronized (crunchifyCacheMap) {
				crunchifyCacheMap.remove(key);
			}

			Thread.yield();
		}
	}
}

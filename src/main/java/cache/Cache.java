package cache;

public interface Cache<K, V> {

  V put(K key, V value);

  V remove(Object key);

  boolean containsKey(K key);

  int size();

  V get(K key);

  void clear();
}

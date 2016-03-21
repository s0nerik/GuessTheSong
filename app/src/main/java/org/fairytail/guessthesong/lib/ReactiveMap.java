package org.fairytail.guessthesong.lib;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class ReactiveMap<K, V> implements Map<K, V> {

    public static class ItemAddedEvent<K, V> {
        private final K key;
        private final V value;

        public ItemAddedEvent(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    public static class ItemRemovedEvent<K, V> {
        private final K key;
        private final V value;

        public ItemRemovedEvent(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    public static class ItemPutEvent<K, V> {
        private final K key;
        private final V oldValue;
        private final V newValue;

        public ItemPutEvent(K key, V oldValue, V newValue) {
            this.key = key;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public K getKey() {
            return key;
        }

        public V getOldValue() {
            return oldValue;
        }

        public V getNewValue() {
            return newValue;
        }
    }

    private final Map<K, V> map = new HashMap<>();

    private Subject<Integer, Integer> sizeChangedSubject = PublishSubject.create();
    private Subject<ItemAddedEvent<K, V>, ItemAddedEvent<K, V>> itemAddedSubject = PublishSubject.create();
    private Subject<ItemRemovedEvent<K, V>, ItemRemovedEvent<K, V>> itemRemovedSubject = PublishSubject.create();
    private Subject<ItemPutEvent<K, V>, ItemPutEvent<K, V>> itemPutSubject = PublishSubject.create();

    public Observable<Integer> onSizeChanged() {
        return sizeChangedSubject;
    }

    public Observable<ItemAddedEvent<K, V>> onItemAdded() {
        return itemAddedSubject;
    }

    public Observable<ItemRemovedEvent<K, V>> onItemRemoved() {
        return itemRemovedSubject;
    }

    public Observable<ItemPutEvent<K, V>> onItemPut() {
        return itemPutSubject;
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @NonNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @NonNull
    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public int size() {
        return map.size();
    }

    @NonNull
    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public V put(K key, V value) {
        V item = map.put(key, value);
        itemPutSubject.onNext(new ItemPutEvent<K, V>(key, item, value));
        if (item == null) {
            itemAddedSubject.onNext(new ItemAddedEvent<K, V>(key, value));
            sizeChangedSubject.onNext(map.size());
        }
        return item;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        List<K> addedKeys = new ArrayList<>();
        Map<K, V> replacedItems = new HashMap<>();
        for (K key : map.keySet()) {
            if (this.map.containsKey(key)) {
                replacedItems.put(key, this.map.get(key));
            } else {
                addedKeys.add(key);
            }
        }

        this.map.putAll(map);

        for (K key : addedKeys) {
            itemAddedSubject.onNext(new ItemAddedEvent<K, V>(key, this.map.get(key)));
            itemPutSubject.onNext(new ItemPutEvent<K, V>(key, null, this.map.get(key)));
        }

        for (K key : replacedItems.keySet()) {
            itemPutSubject.onNext(new ItemPutEvent<K, V>(key, replacedItems.get(key), this.map.get(key)));
        }

        if (!addedKeys.isEmpty()) {
            sizeChangedSubject.onNext(map.size());
        }
    }

    @Override
    public V remove(Object key) {
        V value = map.remove(key);
        if (value != null) {
            itemRemovedSubject.onNext(new ItemRemovedEvent<K, V>((K) key, value));
            sizeChangedSubject.onNext(map.size());
        }
        return value;
    }

    @Override
    public void clear() {
        Map<K, V> removedItems = new HashMap<>(map);
        map.clear();
        for (Entry<K, V> item : removedItems.entrySet()) {
            itemRemovedSubject.onNext(new ItemRemovedEvent<K, V>(item.getKey(), item.getValue()));
        }

        if (!removedItems.isEmpty()) {
            sizeChangedSubject.onNext(map.size());
        }
    }
}

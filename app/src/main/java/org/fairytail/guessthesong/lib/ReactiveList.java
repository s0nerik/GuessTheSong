package org.fairytail.guessthesong.lib;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import rx.Observable;
import rx.subjects.AsyncSubject;
import rx.subjects.Subject;

public class ReactiveList<T> implements List<T> {

    public static class ItemAddedEvent<T> {
        private final int index;
        private final T item;

        public ItemAddedEvent(int index, T item) {
            this.index = index;
            this.item = item;
        }

        public int getIndex() {
            return index;
        }

        public T getItem() {
            return item;
        }
    }

    public static class ItemRemovedEvent<T> {
        private final int index;
        private final T item;

        public ItemRemovedEvent(int index, T item) {
            this.index = index;
            this.item = item;
        }

        public int getIndex() {
            return index;
        }

        public T getItem() {
            return item;
        }
    }

    public static class ItemReplacedEvent<T> {
        private final int index;
        private final T oldItem;
        private final T newItem;

        public ItemReplacedEvent(int index, T oldItem, T newItem) {
            this.index = index;
            this.oldItem = oldItem;
            this.newItem = newItem;
        }

        public int getIndex() {
            return index;
        }

        public T getOldItem() {
            return oldItem;
        }

        public T getNewItem() {
            return newItem;
        }
    }

    private final List<T> list = new ArrayList<>();

    private Subject<Integer, Integer> sizeChangedSubject = AsyncSubject.create();
    private Subject<ItemAddedEvent<T>, ItemAddedEvent<T>> itemAddedSubject = AsyncSubject.create();
    private Subject<ItemRemovedEvent<T>, ItemRemovedEvent<T>> itemRemovedSubject = AsyncSubject.create();
    private Subject<ItemReplacedEvent<T>, ItemReplacedEvent<T>> itemReplacedSubject = AsyncSubject.create();

    public Observable<Integer> onSizeChanged() {
        return sizeChangedSubject;
    }

    public Observable<ItemAddedEvent<T>> onItemAdded() {
        return itemAddedSubject;
    }

    public Observable<ItemRemovedEvent<T>> onItemRemoved() {
        return itemRemovedSubject;
    }

    public Observable<ItemReplacedEvent<T>> onItemReplaced() {
        return itemReplacedSubject;
    }

    @Override
    public void add(int location, T object) {
        list.add(location, object);
        itemAddedSubject.onNext(new ItemAddedEvent<>(location, object));
        sizeChangedSubject.onNext(list.size());
    }

    @Override
    public boolean add(T object) {
        boolean ret = list.add(object);
        itemAddedSubject.onNext(new ItemAddedEvent<>(list.size() - 1, object));
        sizeChangedSubject.onNext(list.size());
        return ret;
    }

    @Override
    public boolean addAll(int location, Collection<? extends T> collection) {
        boolean ret = list.addAll(location, collection);
        int i = 0;
        for (T item : collection) {
            itemAddedSubject.onNext(new ItemAddedEvent<>(location + i++, item));
        }
        sizeChangedSubject.onNext(list.size());
        return ret;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        int prevSize = list.size();
        boolean ret = list.addAll(collection);
        int i = 0;
        for (T item : collection) {
            itemAddedSubject.onNext(new ItemAddedEvent<>(prevSize + i++, item));
        }
        sizeChangedSubject.onNext(list.size());
        return ret;
    }

    @Override
    public void clear() {
        List<T> removedItems = new ArrayList<>(list);
        list.clear();
        int i = 0;
        for (T item : removedItems) {
            itemRemovedSubject.onNext(new ItemRemovedEvent<>(i++, item));
        }
        sizeChangedSubject.onNext(list.size());
    }

    @Override
    public boolean contains(Object object) {
        return list.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return list.containsAll(collection);
    }

    @Override
    public T get(int location) {
        return list.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return list.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return list.lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<T> listIterator(int location) {
        return list.listIterator(location);
    }

    @Override
    public T remove(int location) {
        T item = list.remove(location);
        itemRemovedSubject.onNext(new ItemRemovedEvent<>(location, item));
        sizeChangedSubject.onNext(list.size());
        return item;
    }

    @Override
    public boolean remove(Object object) {
        int index = list.indexOf(object);
        boolean ret = list.remove(object);
        if (ret) {
            itemRemovedSubject.onNext(new ItemRemovedEvent<>(index, (T) object));
            sizeChangedSubject.onNext(list.size());
        }
        return ret;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        int[] indices = new int[collection.size()];
        int i = 0;
        for (Object o : collection) {
            indices[i++] = list.indexOf(o);
        }

        boolean ret = list.removeAll(collection);

        if (ret) {
            i = 0;
            for (Object o : collection) {
                if (indices[i] >= 0)
                    itemRemovedSubject.onNext(new ItemRemovedEvent<>(indices[i], (T) o));
            }
            sizeChangedSubject.onNext(list.size());
        }

        return ret;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        List<T> oldItems = new ArrayList<>(list);

        boolean ret = list.retainAll(collection);

        if (ret) {
            int index = 0;
            for (T item : oldItems) {
                if (!collection.contains(item)) {
                    itemRemovedSubject.onNext(new ItemRemovedEvent<>(index, item));
                }
                index++;
            }
            sizeChangedSubject.onNext(list.size());
        }

        return ret;
    }

    @Override
    public T set(int location, T object) {
        T old = list.set(location, object);
        itemReplacedSubject.onNext(new ItemReplacedEvent<>(location, old, object));
        return old;
    }

    @Override
    public int size() {
        return list.size();
    }

    @NonNull
    @Override
    public List<T> subList(int start, int end) {
        return list.subList(start, end);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @NonNull
    @Override
    public <T1> T1[] toArray(T1[] array) {
        return list.toArray(array);
    }
}

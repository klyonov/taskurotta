/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.taskurotta.hazelcast.queue.impl.proxy;

import com.hazelcast.core.IQueue;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.ValidationUtil;
import ru.taskurotta.hazelcast.queue.CachedQueue;
import ru.taskurotta.hazelcast.queue.impl.QueueService;
import ru.taskurotta.hazelcast.queue.impl.stats.LocalQueueStats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

/**
 * Proxy implementation for the Queue.
 *
 * @param <E>
 */
public class QueueProxyImpl<E> extends QueueProxySupport implements CachedQueue<E>, InitializingObject {

    public QueueProxyImpl(String name, QueueService queueService, NodeEngine nodeEngine) {
        super(name, queueService, nodeEngine);
        if (queueService instanceof IQueue){
            throw new IllegalArgumentException("IQueue service deprecated! Please use CachedQueue.");
        }
    }

    @Override
    public LocalQueueStats getLocalQueueStats() {
        return getService().createLocalQueueStats(name, partitionId);
    }

    @Override
    public boolean add(E e) {
        if (offer(e)) {
            return true;
        }
        throw new IllegalStateException("Queue is full!");
    }

    @Override
    public boolean offer(E e) {
        try {
            return offer(e, 0, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            return false;
        }
    }


    @Override
    public boolean offer(E e, long timeout, TimeUnit timeUnit) throws InterruptedException {
        final NodeEngine nodeEngine = getNodeEngine();
        final Data data = nodeEngine.toData(e);
        return offerInternal(data, timeUnit.toMillis(timeout));
    }

    @Override
    public E take() throws InterruptedException {
        return poll(-1, TimeUnit.MILLISECONDS);
    }

    @Override
    public E poll(long timeout, TimeUnit timeUnit) throws InterruptedException {
        final NodeEngine nodeEngine = getNodeEngine();
        final Object data = pollInternal(timeUnit.toMillis(timeout));
        return nodeEngine.toObject(data);
    }

    @Override
    public boolean remove(Object o) {
        final NodeEngine nodeEngine = getNodeEngine();
        final Data data = nodeEngine.toData(o);
        return removeInternal(data);
    }

    @Override
    public boolean contains(Object o) {
        final NodeEngine nodeEngine = getNodeEngine();
        final Data data = nodeEngine.toData(o);
        List<Data> dataSet = new ArrayList<Data>(1);
        dataSet.add(data);
        return containsInternal(dataSet);
    }

    @Override
    public E remove() {
        final E res = poll();
        if (res == null) {
            throw new NoSuchElementException("Queue is empty!");
        }
        return res;
    }

    @Override
    public E poll() {
        try {
            return poll(0, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //todo: interrupt status is lost
            return null;
        }
    }

    @Override
    public E element() {
        final E res = peek();
        if (res == null) {
            throw new NoSuchElementException("Queue is empty!");
        }
        return res;
    }

    @Override
    public E peek() {
        final NodeEngine nodeEngine = getNodeEngine();
        final Object data = peekInternal();
        return nodeEngine.toObject(data);
    }

    @Override
    public Iterator<E> iterator() {
        final NodeEngine nodeEngine = getNodeEngine();
        return new QueueIterator<E>(listInternal().iterator(), nodeEngine.getSerializationService(), false);
    }

    @Override
    public Object[] toArray() {
        final NodeEngine nodeEngine = getNodeEngine();
        List<Data> list = listInternal();
        int size = list.size();
        Object[] array = new Object[size];
        for (int i = 0; i < size; i++) {
            array[i] = nodeEngine.toObject(list.get(i));
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        T[] tsParam = ts;
        final NodeEngine nodeEngine = getNodeEngine();
        List<Data> list = listInternal();
        int size = list.size();
        if (tsParam.length < size) {
            tsParam = (T[]) java.lang.reflect.Array.newInstance(tsParam.getClass().getComponentType(), size);
        }
        for (int i = 0; i < size; i++) {
            tsParam[i] = nodeEngine.toObject(list.get(i));
        }
        return tsParam;
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return containsInternal(getDataList(objects));
    }

    @Override
    public boolean addAll(Collection<? extends E> es) {
        return addAllInternal(toDataList(es));
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        return compareAndRemove(getDataList(objects), false);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        return compareAndRemove(getDataList(objects), true);
    }

    private List<Data> getDataList(Collection<?> objects) {
        final NodeEngine nodeEngine = getNodeEngine();
        List<Data> dataList = new ArrayList<Data>(objects.size());
        for (Object o : objects) {
            dataList.add(nodeEngine.toData(o));
        }
        return dataList;
    }

    private List<Data> toDataList(Collection<?> objects) {
        final NodeEngine nodeEngine = getNodeEngine();
        List<Data> dataList = new ArrayList<Data>(objects.size());
        for (Object o : objects) {
            ValidationUtil.checkNotNull(o, "Object is null");
            dataList.add(nodeEngine.toData(o));
        }
        return dataList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("CachedQueue");
        sb.append("{name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

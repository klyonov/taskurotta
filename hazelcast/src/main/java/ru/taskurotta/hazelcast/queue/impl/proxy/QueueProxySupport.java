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

import com.hazelcast.config.QueueConfig;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.InitializingObject;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.util.ExceptionUtil;
import ru.taskurotta.hazelcast.queue.impl.QueueService;
import ru.taskurotta.hazelcast.queue.impl.operations.ClearOperation;
import ru.taskurotta.hazelcast.queue.impl.operations.IsEmptyOperation;
import ru.taskurotta.hazelcast.queue.impl.operations.OfferOperation;
import ru.taskurotta.hazelcast.queue.impl.operations.PollOperation;
import ru.taskurotta.hazelcast.queue.impl.operations.QueueOperation;
import ru.taskurotta.hazelcast.queue.impl.operations.SizeOperation;

import java.util.concurrent.Future;

abstract class QueueProxySupport extends AbstractDistributedObject<QueueService> implements InitializingObject {

    final String name;
    final int partitionId;
    final QueueConfig config;

    QueueProxySupport(final String name, final QueueService queueService, NodeEngine nodeEngine) {
        super(nodeEngine, queueService);
        this.name = name;
        this.partitionId = nodeEngine.getPartitionService().getPartitionId(getNameAsPartitionAwareData());
        this.config = nodeEngine.getConfig().findQueueConfig(name);
    }

    @Override
    public void initialize() {
    }

    boolean offerInternal(Data data, long timeout) throws InterruptedException {
        throwExceptionIfNull(data);
        OfferOperation operation = new OfferOperation(name, timeout, data);
        try {
            return (Boolean) invokeAndGet(operation);
        } catch (Throwable throwable) {
            throw ExceptionUtil.rethrowAllowInterrupted(throwable);
        }
    }

    public boolean isEmpty() {
        IsEmptyOperation operation = new IsEmptyOperation(name);
        return (Boolean) invokeAndGet(operation);
    }

    public int size() {
        SizeOperation operation = new SizeOperation(name);
        return (Integer) invokeAndGet(operation);
    }

    public void clear() {
        ClearOperation operation = new ClearOperation(name);
        invokeAndGet(operation);
    }


    Object pollInternal(long timeout) throws InterruptedException {
        PollOperation operation = new PollOperation(name, timeout);
        try {
            return invokeAndGet(operation);
        } catch (Throwable throwable) {
            throw ExceptionUtil.rethrowAllowInterrupted(throwable);
        }
    }

    private int getPartitionId() {
        return partitionId;
    }

    protected void throwExceptionIfNull(Object o) {
        if (o == null) {
            throw new NullPointerException("Object is null");
        }
    }

    private <T> T invokeAndGet(QueueOperation operation) {
        final NodeEngine nodeEngine = getNodeEngine();
        try {
            Future f = invoke(operation);
            return (T) nodeEngine.toObject(f.get());
        } catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    private InternalCompletableFuture invoke(Operation operation) {
        final NodeEngine nodeEngine = getNodeEngine();
        OperationService operationService = nodeEngine.getOperationService();
        return operationService.invokeOnPartition(QueueService.SERVICE_NAME, operation, getPartitionId());
    }

    private Object invokeAndGetData(QueueOperation operation) {
        final NodeEngine nodeEngine = getNodeEngine();
        try {
            OperationService operationService = nodeEngine.getOperationService();
            Future f = operationService.invokeOnPartition(QueueService.SERVICE_NAME, operation, partitionId);
            return f.get();
        } catch (Throwable throwable) {
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    @Override
    public final String getServiceName() {
        return QueueService.SERVICE_NAME;
    }

    @Override
    public final String getName() {
        return name;
    }

}

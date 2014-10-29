/*
 * eXtended Objects - Tinkerpop Blueprints Binding
 *
 * Copyright (C) 2014 SMB GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.smbtec.xo.tinkerpop.blueprints.impl;

import java.util.Iterator;

import com.buschmais.xo.api.ResultIterable;
import com.buschmais.xo.api.ResultIterator;
import com.buschmais.xo.spi.metadata.type.EntityTypeMetadata;
import com.buschmais.xo.spi.session.XOSession;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.smbtec.xo.tinkerpop.blueprints.api.TinkerPopRepository;
import com.smbtec.xo.tinkerpop.blueprints.impl.metadata.VertexMetadata;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

/**
 * Implementation of {@link TinkerPopRepository}.
 *
 * @author Lars Martin - lars.martin@smb-tec.com
 */
public class TinkerPopRepositoryImpl implements TinkerPopRepository {

    private XOSession xoSession;
    private Graph graph;

    public TinkerPopRepositoryImpl(Graph graph, XOSession xoSession) {
        this.graph = graph;
        this.xoSession = xoSession;
    }

    @Override
    public <T> ResultIterable<T> findAll(Class<T> type) {
        EntityTypeMetadata<VertexMetadata> entityTypeMetadata = xoSession.getEntityMetadata(type);
        String label = entityTypeMetadata.getDatastoreMetadata().getDiscriminator();
        Iterable<Vertex> vertices = graph.query().has(TinkerPopVertexManager.getDiscriminatorPropertyKey(label)).vertices();
        final Iterator<Vertex> iterator = vertices.iterator();
        return xoSession.toResult(new ResultIterator<Vertex>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Vertex next() {
                return iterator.next();
            }

            @Override
            public void remove() {
            }

            @Override
            public void close() {
            }
        });
    }

    @Override
    public <T> long count(Class<T> type) {
        EntityTypeMetadata<VertexMetadata> entityTypeMetadata = xoSession.getEntityMetadata(type);
        String label = entityTypeMetadata.getDatastoreMetadata().getDiscriminator();
        Iterable<Vertex> vertices = graph.query().has(TinkerPopVertexManager.getDiscriminatorPropertyKey(label)).vertices();
        return Iterables.size(vertices);
    }
}

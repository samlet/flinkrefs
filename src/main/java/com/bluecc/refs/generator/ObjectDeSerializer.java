/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bluecc.refs.generator;

import com.bluecc.refs.statemachine.event.Event;
import com.bluecc.refs.statemachine.event.EventType;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.bluecc.refs.source.Helper.GSON;

/** A serializer and deserializer for the {@link Event} type. */
public class ObjectDeSerializer<T> implements DeserializationSchema<T>, SerializationSchema<T> {

    private static final long serialVersionUID = 1L;
    Class<T> clz;
    public ObjectDeSerializer(Class<T> clz){
        this.clz=clz;
    }

    @Override
    public byte[] serialize(Object evt) {
        String body=GSON.toJson(evt);
        return body.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public T deserialize(byte[] message) throws IOException {
        return GSON.fromJson(new String(message), clz);
    }

    @Override
    public boolean isEndOfStream(T nextElement) {
        return false;
    }

    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(clz);
    }
}

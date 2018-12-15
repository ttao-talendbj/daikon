/*
 * Copyright 2013-2015 the original author or authors.
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

package org.talend.daikon.spring.configuration.etcd;

import java.util.concurrent.ExecutionException;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.cluster.MemberListResponse;

/**
 * @author Spencer Gibb
 */
@ConfigurationProperties(prefix = "endpoints.etcd", ignoreUnknownFields = false)
@Endpoint(id = "etcd")
public class EtcdEndpoint {

    private final Client etcd;

    public EtcdEndpoint(Client etcd) {
        this.etcd = etcd;
    }

    @ReadOperation
    public Data invoke() throws ExecutionException, InterruptedException {
        final MemberListResponse members = etcd.getClusterClient().listMember().get();
        return new Data(members);
    }

    public static class Data {

        private final MemberListResponse members;

        public Data(MemberListResponse members) {
            this.members = members;
        }

        public MemberListResponse getMembers() {
            return members;
        }
    }
}

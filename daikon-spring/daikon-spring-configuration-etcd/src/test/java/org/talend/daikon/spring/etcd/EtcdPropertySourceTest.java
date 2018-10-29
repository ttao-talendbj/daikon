package org.talend.daikon.spring.etcd;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.protobuf.ByteString;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;

@RunWith(MockitoJUnitRunner.class)
public class EtcdPropertySourceTest {

    private EtcdPropertySource propertySource;

    @Mock
    private Client client;

    @Before
    public void setUp() {
        propertySource = new EtcdPropertySource("default-name", client);
    }

    @Test
    public void shouldGetValueFromClient() {
        // given
        final KV kv = mock(KV.class);
        when(client.getKVClient()).thenReturn(kv);
        when(kv.get(eq(ByteSequence.from("testKey", Charset.forName("UTF-8")))))
                .thenReturn(new CompletableFuture<GetResponse>() {

                    @Override
                    public GetResponse get() {
                        final GetResponse response = mock(GetResponse.class);
                        final io.etcd.jetcd.api.KeyValue value = io.etcd.jetcd.api.KeyValue
                                .newBuilder()
                                .setValue(ByteString.copyFromUtf8("value"))
                                .build();
                        when(response.getKvs()).thenReturn(Collections.singletonList(new KeyValue(value)));
                        return response;
                    }
                });

        // when
        final String property = propertySource.getProperty("testKey");

        // then
        assertEquals("value", property);
        verify(kv, times(1)).get(any());
    }
}
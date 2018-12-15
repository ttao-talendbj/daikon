package org.talend.daikon.spring.configuration.etcd;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.launcher.junit.EtcdClusterResource;

public class EtcdPropertySourceTest {

    @Rule
    public final EtcdClusterResource etcd = new EtcdClusterResource("test-etcd", 1, false, false);

    private EtcdPropertySource source;

    private boolean init;

    @Before
    public void setUp() {
        if (!init) {
            Client client = Client.builder() //
                    .endpoints(etcd.cluster().getClientEndpoints()) //
                    .build();
            final KV kv = client.getKVClient();
            kv.put(ByteSequence.from("/application/key".getBytes()), ByteSequence.from("value".getBytes()));
            kv.put(ByteSequence.from("/application/key2".getBytes()), ByteSequence.from("value".getBytes()));

            final ApplicationContext context = mock(ApplicationContext.class);
            source = new EtcdPropertySource("/application", client, context);
            source.init();

            init = true;
        }
    }

    @Test
    public void shouldGetAllPropertyNames() {
        // when
        final String[] propertyNames = source.getPropertyNames();

        // then
        assertEquals(2, propertyNames.length);
        assertArrayEquals(new String[] { "key2", "key" }, propertyNames);
        assertEquals("value", source.getProperty("key"));
        assertEquals("value", source.getProperty("key2"));
    }

}
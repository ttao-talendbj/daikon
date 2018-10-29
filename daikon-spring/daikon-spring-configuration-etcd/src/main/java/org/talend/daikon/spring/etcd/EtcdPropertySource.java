package org.talend.daikon.spring.etcd;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;

/**
 * A {@link PropertySource} implementation that reads values from ETCD.
 */
public class EtcdPropertySource extends PropertySource<Client> {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    public EtcdPropertySource(String name, Client source) {
        super(name, source);
    }

    @Override
    @Nullable
    public String getProperty(String name) {
        try {
            final Client source = getSource();
            final KV kvClient = source.getKVClient();
            final CompletableFuture<GetResponse> future = kvClient.get(ByteSequence.from(name, CHARSET));
            return future.get().getKvs().get(0).getValue().toString(CHARSET);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package org.talend.daikon.spring.etcd;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;

/**
 * A {@link PropertySource} implementation that reads values from ETCD.
 */
public class EtcdPropertySource extends PropertySource<Client> {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    private String base;

    public EtcdPropertySource(String name, String base, Client source) {
        super(name, source);
        this.base = base;
    }

    @Override
    @Nullable
    public String getProperty(String name) {
        try {
            final Client source = getSource();
            final KV kvClient = source.getKVClient();
            final String etcdKeyName = "/" + base + '/' + name;
            final CompletableFuture<GetResponse> future = kvClient.get(ByteSequence.from(etcdKeyName, CHARSET));
            final List<KeyValue> values = future.get().getKvs();
            if (!values.isEmpty()) {
                final KeyValue keyValue = values.get(0);
                return keyValue.getValue().toString(CHARSET);
            } else {
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
        }
    }
}

package org.talend.daikon.spring.etcd;

import java.net.URI;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("etcd")
public class EtcdProperties {

    @NotEmpty
    private List<URI> uris;

    private boolean enabled = true;

    private String password;

    private boolean sslEnabled;

    public EtcdProperties() {
    }

    public EtcdProperties(List<URI> uris, boolean enabled) {
        this.uris = uris;
        this.enabled = enabled;
    }

    public List<URI> getUris() {
        return uris;
    }

    public void setUris(List<URI> uris) {
        this.uris = uris;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    public boolean isSslEnabled() {
        return sslEnabled;
    }

    public void setSslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
    }
}

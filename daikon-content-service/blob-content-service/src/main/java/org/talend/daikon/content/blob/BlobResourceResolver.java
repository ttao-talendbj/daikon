package org.talend.daikon.content.blob;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.talend.daikon.content.AbstractResourceResolver;
import org.talend.daikon.content.DeletableResource;

import com.microsoft.azure.spring.cloud.storage.BlobStorageResource;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.CopyStatus;

public class BlobResourceResolver extends AbstractResourceResolver {

    private CloudBlobClient blobClient;

    public BlobResourceResolver(CloudBlobClient blobClient) {
        super(new ResourcePatternAdapter(blobClient));
        this.blobClient = blobClient;
    }

    @Override
    protected DeletableResource convert(WritableResource writableResource) {
        return new BlobDeletableResource(blobClient, writableResource);
    }

    private static class ResourcePatternAdapter implements ResourcePatternResolver {

        private final CloudBlobClient blobClient;

        private ResourcePatternAdapter(CloudBlobClient blobClient) {
            this.blobClient = blobClient;
        }

        private static boolean matches(String s, String locationPattern) {
            return true;
        }

        @Override
        public Resource[] getResources(String locationPattern) throws IOException {
            try {
                final String name = AzureStorageUtils.getContainerName(locationPattern);
                final CloudBlobContainer container = blobClient.getContainerReference(name);

                final List<Resource> results = new ArrayList<>();
                container.listBlobs().forEach(item -> {
                    final String location = item.getUri().toString();
                    if (matches(location, locationPattern)) {
                        results.add(getResource(location));
                    }
                });

                return results.toArray(new Resource[0]);
            } catch (URISyntaxException | StorageException e) {
                throw new IOException(e);
            }
        }

        @Override
        public Resource getResource(String location) {
            return new BlobStorageResource(blobClient, location, true);
        }

        @Override
        public ClassLoader getClassLoader() {
            return Thread.currentThread().getContextClassLoader();
        }
    }

    private static class BlobDeletableResource implements DeletableResource {

        private final WritableResource delegate;

        private CloudBlobClient blobClient;

        private BlobDeletableResource(CloudBlobClient blobClient, WritableResource delegate) {
            this.blobClient = blobClient;
            this.delegate = delegate;
        }

        private static void waitForCopyToComplete(CloudBlob blob) throws InterruptedException, StorageException {
            CopyStatus copyStatus = CopyStatus.PENDING;
            while (copyStatus == CopyStatus.PENDING) {
                Thread.sleep(1000);
                blob.downloadAttributes();
                copyStatus = blob.getCopyState().getStatus();
            }
        }

        @Override
        public boolean isWritable() {
            return delegate.isWritable();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return delegate.getOutputStream();
        }

        @Override
        public boolean exists() {
            return delegate.exists();
        }

        @Override
        public boolean isReadable() {
            return delegate.isReadable();
        }

        @Override
        public boolean isOpen() {
            return delegate.isOpen();
        }

        @Override
        public URL getURL() throws IOException {
            return delegate.getURL();
        }

        @Override
        public URI getURI() throws IOException {
            return delegate.getURI();
        }

        @Override
        public File getFile() throws IOException {
            return delegate.getFile();
        }

        @Override
        public long contentLength() throws IOException {
            return delegate.contentLength();
        }

        @Override
        public long lastModified() throws IOException {
            return delegate.lastModified();
        }

        @Override
        public Resource createRelative(String relativePath) throws IOException {
            return delegate.createRelative(relativePath);
        }

        @Override
        public String getFilename() {
            return delegate.getFilename();
        }

        @Override
        public String getDescription() {
            return delegate.getDescription();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return delegate.getInputStream();
        }

        @Override
        public void delete() throws IOException {
            try {
                final String uri = delegate.getURI().toString();
                final String name = AzureStorageUtils.getContainerName(uri);
                final String blobName = AzureStorageUtils.getBlobName(uri);

                blobClient.getContainerReference(name).getBlockBlobReference(blobName).delete();
            } catch (StorageException | URISyntaxException e) {
                throw new IOException(e);
            }
        }

        @Override
        public void move(String location) throws IOException {
            try {
                final String uri = delegate.getURI().toString();
                final String name = AzureStorageUtils.getContainerName(uri);
                final String blobName = AzureStorageUtils.getBlobName(uri);

                final CloudBlockBlob dest = blobClient.getContainerReference(name).getBlockBlobReference(location);
                final CloudBlockBlob source = blobClient.getContainerReference(name).getBlockBlobReference(blobName);

                dest.startCopy(source);
                waitForCopyToComplete(dest);
                source.delete();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }
}

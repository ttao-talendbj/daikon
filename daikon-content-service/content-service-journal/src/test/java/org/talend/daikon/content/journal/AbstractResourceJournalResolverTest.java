package org.talend.daikon.content.journal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.ResourceResolver;

@Transactional // JPA tests require this annotation in common unit test class
public abstract class AbstractResourceJournalResolverTest {

    @Autowired
    protected ResourceJournal resolver;

    @Test
    public void testClear() {

        resolver.clear("/location1");

        assertFalse("Location 1.1 should not exist anymore", resolver.exist("/location1.1"));
        assertFalse("Location 1.1 should not exist anymore", resolver.exist("/location1.2"));
        assertFalse("Location 1.1 should not exist anymore", resolver.exist("/location1.3"));
        assertTrue("Location 2.1 should still exist", resolver.exist("/location2.1"));
        assertTrue("Location 2.1 should still exist", resolver.exist("/location2.2"));
    }

    @Before
    public void initData() {
        resolver.add("location1.1");
        resolver.add("location1.2");
        resolver.add("location1.3");
        resolver.add("location2.1");
        resolver.add("location2.2");
    }

    @After
    public void cleanData() {
        resolver.clear("/**");
    }

    @Test
    public void testContext() {
        assertNotNull(resolver);
    }

    @Test
    public void testStartWith() {
        List<String> listLocation = resolver.matches("/location1").collect(Collectors.toList());

        assertEquals(3, listLocation.size());
        for (String resourceJournalEntry : listLocation) {
            assertTrue(resourceJournalEntry.startsWith("/location1"));
        }
    }

    @Test
    public void testExist() {
        // Then
        assertTrue(resolver.exist("/location1.1"));
        assertFalse(resolver.exist("/location1.5"));
    }

    @Test
    public void testMatches() {
        // When
        List<String> listLocation = resolver.matches("/location1*").collect(Collectors.toList());

        // Then
        assertEquals("Size of the list should be equals", 3, listLocation.size());
        for (String location : listLocation) {
            assertTrue("Location should start by location1", location.startsWith("/location1"));
        }
    }

    @Test
    public void testClearWithPattern() {
        // Given
        resolver.add("location3.0");
        resolver.add("location3.0/location3.1");
        resolver.add("location3.0/location3.2/location3.2.1");
        resolver.add("location3.0/location3.3/location3.3.1");

        assertTrue(resolver.exist("location3.0"));
        assertTrue(resolver.exist("location3.0/location3.3/location3.3.1"));

        resolver.clear("location3.0/**");

        assertTrue(resolver.exist("location3.0"));
        assertFalse(resolver.exist("location3.0/location3.2/location3.2.1"));
        assertFalse(resolver.exist("location3.0/location3.3/location3.3.1"));

    }

    @Test
    public void testAdd() {
        // Given
        long nbLocation = countRecord();
        resolver.add("location3.0");

        // When
        final long count = countRecord();

        // Then
        assertEquals("Nb location should be equals", nbLocation + 1, count);
    }

    @Test
    public void testRemove() {
        // Given
        long nbLocation = countRecord();
        resolver.remove("/location2.2");

        // When
        final long count = countRecord();

        // Then
        assertEquals("Nb location should be equals", nbLocation - 1, count);
    }

    @Test
    public void testMove() {
        // Given
        assertTrue(resolver.exist("/location1.1"));
        assertFalse(resolver.exist("/location1.5"));

        // When
        resolver.move("/location1.1", "/location1.5");

        // Then
        assertTrue(resolver.exist("/location1.5"));
        assertFalse(resolver.exist("/location1.1"));
    }

    @Test
    public void shouldNotFailMoveWhenResourceDoesNotExist() {
        // When
        resolver.move("/does.not.exist", "/location1.5");

        // Then
        assertFalse(resolver.exist("/does.not.exist"));
        assertFalse(resolver.exist("/location1.5"));
    }

    @Test
    public void shouldNotBeReadyIfMarkerDoesNotExist() {
        // When
        final boolean ready = resolver.ready();

        // Then
        assertFalse(ready);
    }

    @Test
    public void shouldBeReadyIfMarkerExists() {
        // Given
        resolver.validate();

        // When
        final boolean ready = resolver.ready();

        // Then
        assertTrue(ready);
    }

    @Test
    public void shouldSyncWithResourceResolver() throws IOException {
        // Given
        final ResourceResolver resourceResolver = mock(ResourceResolver.class);
        final DeletableResource resource1 = mock(DeletableResource.class);
        final DeletableResource resource2 = mock(DeletableResource.class);
        when(resourceResolver.getResources(any())).thenReturn(new DeletableResource[] { resource1, resource2 });

        // When
        resolver.sync(resourceResolver);

        // Then
        verify(resourceResolver, times(1)).getResources(eq("/**"));
        verify(resource1, times(1)).getFilename();
        verify(resource2, times(1)).getFilename();
        assertTrue(resolver.ready());
    }

    @Test
    public void shouldNotMarkAsReadyWhenSyncFails() throws IOException {
        // Given
        final ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(resourceResolver.getResources(any())).thenThrow(new IOException("Unchecked on purpose"));

        // When
        try {
            resolver.sync(resourceResolver);
            fail("Expected an exception.");
        } catch (Exception e) {
            // Expected
        }

        // Then
        assertFalse(resolver.ready());
    }

    @Test
    public void shouldIgnoreIfAlreadyMarkedAsReady() throws IOException {
        // Given
        final ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(resourceResolver.getResources(any())).thenThrow(new IOException("Unchecked on purpose"));

        // When
        resolver.validate();
        resolver.sync(resourceResolver);

        // Then
        verify(resourceResolver, never()).getResources(eq("/**"));
    }

    @Test
    public void shouldMatchIgnoringAbsolutePath() {
        // When
        final Stream<String> matches = resolver.matches("/location1.1");

        // Then
        assertEquals(1, matches.count());
    }

    @Test
    public void shouldMatchIgnoringEmptyPath() {
        // When
        final Stream<String> matches = resolver.matches(null);

        // Then
        assertEquals(0, matches.count());
    }

    private long countRecord() {
        return resolver.matches("/**").count();
    }

}
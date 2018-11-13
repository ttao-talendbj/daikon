/*
 *
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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
 *
 */

package org.talend.daikon.logging.obfuscation;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.talend.daikon.logging.obfuscation.ObfuscatedLogger.OBFUSCATION_KEY_PROPERTY;
import static uk.org.lidalia.slf4jtest.LoggingEvent.debug;
import static uk.org.lidalia.slf4jtest.LoggingEvent.error;
import static uk.org.lidalia.slf4jtest.LoggingEvent.info;
import static uk.org.lidalia.slf4jtest.LoggingEvent.trace;
import static uk.org.lidalia.slf4jtest.LoggingEvent.warn;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Marker;

import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class ObfuscatedLoggerTest {

    @BeforeClass
    static public void staticInit() {
        System.setProperty(OBFUSCATION_KEY_PROPERTY, "sdfjazoirzajhfnf1232qsd");
    }

    @Test
    public void traceMethods() {
        // given
        TestLogger testLogger = TestLoggerFactory.getTestLogger("");
        ObfuscatedLogger obfuscateLogger = spy(new ObfuscatedLogger(testLogger));
        String mess = "message";
        String arg1 = "foo";
        String arg2 = "bar";
        String[] args = new String[] { "zoo", "zar" };
        String obfuscatedStr = "obfuscated";
        Marker testMarker = new TestMarker();
        when(obfuscateLogger.obfuscate(any(String.class))).thenReturn(obfuscatedStr);

        // trace
        obfuscateLogger.trace(mess, arg1);
        assertThat(testLogger.getLoggingEvents(), is(asList(trace(mess, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.trace(mess, arg1, arg2);
        assertThat(testLogger.getLoggingEvents(), is(asList(trace(mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.trace(mess, args);
        assertThat(testLogger.getLoggingEvents(), is(asList(trace(mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.trace(testMarker, mess, arg1);
        assertThat(testLogger.getLoggingEvents(), is(asList(trace(testMarker, mess, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.trace(testMarker, mess, arg1, arg2);
        assertThat(testLogger.getLoggingEvents(), is(asList(trace(testMarker, mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.trace(testMarker, mess, args);
        assertThat(testLogger.getLoggingEvents(), is(asList(trace(testMarker, mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();
    }

    @Test
    public void debugMethods() {
        // given
        TestLogger testLogger = TestLoggerFactory.getTestLogger("");
        ObfuscatedLogger obfuscateLogger = spy(new ObfuscatedLogger(testLogger));
        String mess = "message";
        String arg1 = "foo";
        String arg2 = "bar";
        String[] args = new String[] { "zoo", "zar" };
        String obfuscatedStr = "obfuscated";
        Marker testMarker = new TestMarker();
        when(obfuscateLogger.obfuscate(any(String.class))).thenReturn(obfuscatedStr);

        // debug
        obfuscateLogger.debug(mess, arg1);
        assertThat(testLogger.getLoggingEvents(), is(asList(debug(mess, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.debug(mess, arg1, arg2);
        assertThat(testLogger.getLoggingEvents(), is(asList(debug(mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.debug(mess, args);
        assertThat(testLogger.getLoggingEvents(), is(asList(debug(mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.debug(testMarker, mess, arg1);
        assertThat(testLogger.getLoggingEvents(), is(asList(debug(testMarker, mess, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.debug(testMarker, mess, arg1, arg2);
        assertThat(testLogger.getLoggingEvents(), is(asList(debug(testMarker, mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.debug(testMarker, mess, args);
        assertThat(testLogger.getLoggingEvents(), is(asList(debug(testMarker, mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();
    }

    @Test
    public void errorMethods() {
        // given
        TestLogger testLogger = TestLoggerFactory.getTestLogger("");
        ObfuscatedLogger obfuscateLogger = spy(new ObfuscatedLogger(testLogger));
        String mess = "message";
        String arg1 = "foo";
        String arg2 = "bar";
        String[] args = new String[] { "zoo", "zar" };
        String obfuscatedStr = "obfuscated";
        Marker testMarker = new TestMarker();
        when(obfuscateLogger.obfuscate(any(String.class))).thenReturn(obfuscatedStr);

        // error
        obfuscateLogger.error(mess, arg1);
        assertThat(testLogger.getLoggingEvents(), is(asList(error(mess, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.error(mess, arg1, arg2);
        assertThat(testLogger.getLoggingEvents(), is(asList(error(mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.error(mess, args);
        assertThat(testLogger.getLoggingEvents(), is(asList(error(mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.error(testMarker, mess, arg1);
        assertThat(testLogger.getLoggingEvents(), is(asList(error(testMarker, mess, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.error(testMarker, mess, arg1, arg2);
        assertThat(testLogger.getLoggingEvents(), is(asList(error(testMarker, mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.error(testMarker, mess, args);
        assertThat(testLogger.getLoggingEvents(), is(asList(error(testMarker, mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();
    }

    @Test
    public void warnMethods() {
        // given
        TestLogger testLogger = TestLoggerFactory.getTestLogger("");
        ObfuscatedLogger obfuscateLogger = spy(new ObfuscatedLogger(testLogger));
        String mess = "message";
        String arg1 = "foo";
        String arg2 = "bar";
        String[] args = new String[] { "zoo", "zar" };
        String obfuscatedStr = "obfuscated";
        Marker testMarker = new TestMarker();
        when(obfuscateLogger.obfuscate(any(String.class))).thenReturn(obfuscatedStr);

        // warn
        obfuscateLogger.warn(mess, arg1);
        assertThat(testLogger.getLoggingEvents(), is(asList(warn(mess, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.warn(mess, arg1, arg2);
        assertThat(testLogger.getLoggingEvents(), is(asList(warn(mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.warn(mess, args);
        assertThat(testLogger.getLoggingEvents(), is(asList(warn(mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.warn(testMarker, mess, arg1);
        assertThat(testLogger.getLoggingEvents(), is(asList(warn(testMarker, mess, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.warn(testMarker, mess, arg1, arg2);
        assertThat(testLogger.getLoggingEvents(), is(asList(warn(testMarker, mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.warn(testMarker, mess, args);
        assertThat(testLogger.getLoggingEvents(), is(asList(warn(testMarker, mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();
    }

    @Test
    public void infoMethods() {
        // given
        TestLogger testLogger = TestLoggerFactory.getTestLogger("");
        ObfuscatedLogger obfuscateLogger = spy(new ObfuscatedLogger(testLogger));
        String mess = "message";
        String arg1 = "foo";
        String arg2 = "bar";
        String[] args = new String[] { "zoo", "zar" };
        String obfuscatedStr = "obfuscated";
        Marker testMarker = new TestMarker();
        when(obfuscateLogger.obfuscate(any(String.class))).thenReturn(obfuscatedStr);

        // info
        obfuscateLogger.info(mess, arg1);
        assertThat(testLogger.getLoggingEvents(), is(asList(info(mess, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.info(mess, arg1, arg2);
        assertThat(testLogger.getLoggingEvents(), is(asList(info(mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.info(mess, args);
        assertThat(testLogger.getLoggingEvents(), is(asList(info(mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.info(testMarker, mess, arg1);
        assertThat(testLogger.getLoggingEvents(), is(asList(info(testMarker, mess, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.info(testMarker, mess, arg1, arg2);
        assertThat(testLogger.getLoggingEvents(), is(asList(info(testMarker, mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();

        obfuscateLogger.info(testMarker, mess, args);
        assertThat(testLogger.getLoggingEvents(), is(asList(info(testMarker, mess, obfuscatedStr, obfuscatedStr))));
        testLogger.clearAll();
    }

    @Test
    public void obfuscate() throws UnsupportedEncodingException {
        ObfuscatedLogger obfuscateLogger = new ObfuscatedLogger(null);
        assertThat(obfuscateLogger.obfuscate("foo"), is("If1wuKzm4+FlGv0LvoAYuruT/FsXotoo0rC8TxQZ1bY="));
    }

    private static class TestMarker implements Marker {

        /**
         * Get the name of this Marker.
         *
         * @return name of marker
         */
        @Override
        public String getName() {
            return null;
        }

        /**
         * Add a reference to another Marker.
         *
         * @param reference a reference to another marker
         * @throws IllegalArgumentException if 'reference' is null
         */
        @Override
        public void add(Marker reference) {

        }

        /**
         * Remove a marker reference.
         *
         * @param reference the marker reference to remove
         * @return true if reference could be found and removed, false otherwise.
         */
        @Override
        public boolean remove(Marker reference) {
            return false;
        }

        /**
         * @deprecated Replaced by {@link #hasReferences()}.
         */
        @Override
        public boolean hasChildren() {
            return false;
        }

        /**
         * Does this marker have any references?
         *
         * @return true if this marker has one or more references, false otherwise.
         */
        @Override
        public boolean hasReferences() {
            return false;
        }

        /**
         * Returns an Iterator which can be used to iterate over the references of this
         * marker. An empty iterator is returned when this marker has no references.
         *
         * @return Iterator over the references of this marker
         */
        @Override
        public Iterator<Marker> iterator() {
            return null;
        }

        /**
         * Does this marker contain a reference to the 'other' marker? Marker A is defined
         * to contain marker B, if A == B or if B is referenced by A, or if B is referenced
         * by any one of A's references (recursively).
         *
         * @param other The marker to test for inclusion.
         * @return Whether this marker contains the other marker.
         * @throws IllegalArgumentException if 'other' is null
         */
        @Override
        public boolean contains(Marker other) {
            return false;
        }

        /**
         * Does this marker contain the marker named 'name'?
         * <p>
         * If 'name' is null the returned value is always false.
         *
         * @param name The marker name to test for inclusion.
         * @return Whether this marker contains the other marker.
         */
        @Override
        public boolean contains(String name) {
            return false;
        }

    }
}
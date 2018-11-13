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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This factory creates {@link ObfuscatedLogger} instances from a default slf4j {@link Logger}.<br>
 * One suggested usage in you classes is the following
 * 
 * <pre>
 * 
 * {
 *     &#64;code
 *     private static final Logger LOGGER = LoggerFactory.getLogger(MyClass.class);
 *     private static final Logger OBFUSCATED_LOGGER = ObfuscatedLoggerFactory.getLogger(LOGGER);
 * }
 * </pre>
 * 
 * And then use the LOGGER when you need to log in clear and use the OBFUSCATED_LOGGER when you need
 * to log the arguments obfuscated.
 */
public class ObfuscatedLoggerFactory {

    /**
     * creates an ObfuscatedLogger that delegate its calls to the {@link Logger} created using the clazz.
     * 
     * @param clazz the returned logger will be named after clazz
     * @return an obfuscation logger
     */
    static public ObfuscatedLogger getLogger(Class<?> clazz) {
        return new ObfuscatedLogger(LoggerFactory.getLogger(clazz));
    }

    /**
     * creates an ObfuscatedLogger that delegate its calls to the {@link Logger} created using the name.
     * 
     * @param name the returned logger will be named name
     * @return an obfuscation logger
     */
    static public ObfuscatedLogger getLogger(String name) {
        return new ObfuscatedLogger(LoggerFactory.getLogger(name));
    }

    /**
     * creates an ObfuscatedLogger that delegate its calls to the {@link Logger} created using the name.
     * 
     * @param logger will be used as a delegate
     * @return an obfuscation logger
     */
    static public ObfuscatedLogger getLogger(Logger logger) {
        return new ObfuscatedLogger(logger);
    }
}

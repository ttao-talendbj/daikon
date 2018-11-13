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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * Provide logs api similar to {@link Logger} but will hash the parameters data using a hmac sha256.
 * A hash is not reversible so will never be decrypted.
 * All methods here only hash the arguments and not the message itself.
 */
public class ObfuscatedLogger {

    public static final String OBFUSCATION_KEY_PROPERTY = "talend.log.obfuscation.key";

    static final private Logger log = LoggerFactory.getLogger(ObfuscatedLogger.class);

    private final Logger delegate;

    private static Mac sha256_HMAC;

    static {
        try {
            String obfuscationKey = System.getProperty(OBFUSCATION_KEY_PROPERTY, null);
            if (obfuscationKey != null) {
                sha256_HMAC = Mac.getInstance("HmacSHA256");
                SecretKeySpec secret_key = new SecretKeySpec(obfuscationKey.getBytes("UTF-8"), "HmacSHA256");
                sha256_HMAC.init(secret_key);
            } // else sha256_HMAC remains null
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to initialize the obfuscation engine :", e);
        } catch (InvalidKeyException e) {
            // the key is not logged on purpose to not leak it.
            log.error("Failed to initialize the obfuscation engine (Invalid Key)");
        } catch (UnsupportedEncodingException e) {
            // the key is not logged on purpose to not leak it.
            log.error("Failed to initialize the obfuscation engine: UTF-8 is not a supported encoding !!");
        }
    }

    public ObfuscatedLogger(Logger logger) {
        this.delegate = logger;
    }

    /**
     * Return the name of this <code>Logger</code> instance.
     * 
     * @return name of this logger instance
     */

    public String getName() {
        return delegate.getName();
    }

    /**
     * Is the logger instance enabled for the TRACE level?
     *
     * @return True if this Logger is enabled for the TRACE level,
     * false otherwise.
     * @since 1.4
     */

    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    /**
     * Log a message at the TRACE level according to the specified format
     * and argument.
     * <p>
     * This form avoids superfluous object creation when the logger
     * is disabled for the TRACE level.
     * </p>
     *
     * @param format the format string
     * @param arg the argument
     * @since 1.4
     */

    public void trace(String format, Object arg) {
        delegate.trace(format, obfuscate(arg));
    }

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments.
     * <p>
     * This form avoids superfluous object creation when the logger
     * is disabled for the TRACE level.
     * </p>
     *
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @since 1.4
     */

    public void trace(String format, Object arg1, Object arg2) {
        delegate.trace(format, obfuscate(arg1), obfuscate(arg2));
    }

    /**
     * Log a message at the TRACE level according to the specified format
     * and arguments.
     * <p>
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the TRACE level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for TRACE. The variants taking {@link #trace(String, Object) one} and
     * {@link #trace(String, Object, Object) two} arguments exist solely in order to avoid this hidden cost.
     * </p>
     *
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     * @since 1.4
     */

    public void trace(String format, Object... arguments) {
        delegate.trace(format, obfuscate(arguments));
    }

    /**
     * This method is similar to {@link #trace(String, Object)} method except that the
     * marker data is also taken into consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     * @since 1.4
     */

    public void trace(Marker marker, String format, Object arg) {
        delegate.trace(marker, format, obfuscate(arg));
    }

    /**
     * This method is similar to {@link #trace(String, Object, Object)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @since 1.4
     */

    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        delegate.trace(marker, format, obfuscate(arg1), obfuscate(arg2));
    }

    /**
     * This method is similar to {@link #trace(String, Object...)}
     * method except that the marker data is also taken into
     * consideration.
     *
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param argArray an array of arguments
     * @since 1.4
     */

    public void trace(Marker marker, String format, Object... argArray) {
        delegate.trace(marker, format, obfuscate(argArray));
    }

    /**
     * Is the logger instance enabled for the DEBUG level?
     *
     * @return True if this Logger is enabled for the DEBUG level,
     * false otherwise.
     */

    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and argument.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger
     * is disabled for the DEBUG level.
     * </p>
     * 
     * @param format the format string
     * @param arg the argument
     */

    public void debug(String format, Object arg) {
        delegate.debug(format, obfuscate(arg));
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger
     * is disabled for the DEBUG level.
     * </p>
     * 
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */

    public void debug(String format, Object arg1, Object arg2) {
        delegate.debug(format, obfuscate(arg1), obfuscate(arg2));
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     * 
     * <p>
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the DEBUG level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for DEBUG. The variants taking
     * {@link #debug(String, Object) one} and {@link #debug(String, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.
     * </p>
     * 
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */

    public void debug(String format, Object... arguments) {
        delegate.debug(format, obfuscate(arguments));
    }

    /**
     * This method is similar to {@link #debug(String, Object)} method except that the
     * marker data is also taken into consideration.
     * 
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */

    public void debug(Marker marker, String format, Object arg) {
        delegate.debug(marker, format, obfuscate(arg));
    }

    /**
     * This method is similar to {@link #debug(String, Object, Object)}
     * method except that the marker data is also taken into
     * consideration.
     * 
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */

    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        delegate.debug(marker, format, obfuscate(arg1), obfuscate(arg2));
    }

    /**
     * This method is similar to {@link #debug(String, Object...)}
     * method except that the marker data is also taken into
     * consideration.
     * 
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */

    public void debug(Marker marker, String format, Object... arguments) {
        delegate.debug(marker, format, obfuscate(arguments));
    }

    /**
     * Is the logger instance enabled for the INFO level?
     *
     * @return True if this Logger is enabled for the INFO level,
     * false otherwise.
     */

    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and argument.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger
     * is disabled for the INFO level.
     * </p>
     * 
     * @param format the format string
     * @param arg the argument
     */

    public void info(String format, Object arg) {
        delegate.info(format, obfuscate(arg));
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger
     * is disabled for the INFO level.
     * </p>
     * 
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */

    public void info(String format, Object arg1, Object arg2) {
        delegate.info(format, obfuscate(arg1), obfuscate(arg2));
    }

    /**
     * Log a message at the INFO level according to the specified format
     * and arguments.
     * 
     * <p>
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the INFO level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for INFO. The variants taking
     * {@link #info(String, Object) one} and {@link #info(String, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.
     * </p>
     * 
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */

    public void info(String format, Object... arguments) {
        delegate.info(format, obfuscate(arguments));
    }

    /**
     * This method is similar to {@link #info(String, Object)} method except that the
     * marker data is also taken into consideration.
     * 
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */

    public void info(Marker marker, String format, Object arg) {
        delegate.info(marker, format, obfuscate(arg));
    }

    /**
     * This method is similar to {@link #info(String, Object, Object)}
     * method except that the marker data is also taken into
     * consideration.
     * 
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */

    public void info(Marker marker, String format, Object arg1, Object arg2) {
        delegate.info(marker, format, obfuscate(arg1), obfuscate(arg2));
    }

    /**
     * This method is similar to {@link #info(String, Object...)}
     * method except that the marker data is also taken into
     * consideration.
     * 
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */

    public void info(Marker marker, String format, Object... arguments) {
        delegate.info(marker, format, obfuscate(arguments));
    }

    /**
     * Is the logger instance enabled for the WARN level?
     *
     * @return True if this Logger is enabled for the WARN level,
     * false otherwise.
     */

    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and argument.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger
     * is disabled for the WARN level.
     * </p>
     * 
     * @param format the format string
     * @param arg the argument
     */

    public void warn(String format, Object arg) {
        delegate.warn(format, obfuscate(arg));
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     * 
     * <p>
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the WARN level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for WARN. The variants taking
     * {@link #warn(String, Object) one} and {@link #warn(String, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.
     * </p>
     * 
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */

    public void warn(String format, Object... arguments) {
        delegate.warn(format, obfuscate(arguments));
    }

    /**
     * Log a message at the WARN level according to the specified format
     * and arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger
     * is disabled for the WARN level.
     * </p>
     * 
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */

    public void warn(String format, Object arg1, Object arg2) {
        delegate.warn(format, obfuscate(arg1), obfuscate(arg2));
    }

    /**
     * This method is similar to {@link #warn(String, Object)} method except that the
     * marker data is also taken into consideration.
     * 
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */

    public void warn(Marker marker, String format, Object arg) {
        delegate.warn(marker, format, obfuscate(arg));
    }

    /**
     * This method is similar to {@link #warn(String, Object, Object)}
     * method except that the marker data is also taken into
     * consideration.
     * 
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */

    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        delegate.warn(marker, format, obfuscate(arg1), obfuscate(arg2));
    }

    /**
     * This method is similar to {@link #warn(String, Object...)}
     * method except that the marker data is also taken into
     * consideration.
     * 
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */

    public void warn(Marker marker, String format, Object... arguments) {
        delegate.warn(marker, format, obfuscate(arguments));
    }

    /**
     * Is the logger instance enabled for the ERROR level?
     *
     * @return True if this Logger is enabled for the ERROR level,
     * false otherwise.
     */

    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and argument.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger
     * is disabled for the ERROR level.
     * </p>
     * 
     * @param format the format string
     * @param arg the argument
     */

    public void error(String format, Object arg) {
        delegate.error(format, obfuscate(arg));
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     * 
     * <p>
     * This form avoids superfluous object creation when the logger
     * is disabled for the ERROR level.
     * </p>
     * 
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */

    public void error(String format, Object arg1, Object arg2) {
        delegate.error(format, obfuscate(arg1), obfuscate(arg2));
    }

    /**
     * Log a message at the ERROR level according to the specified format
     * and arguments.
     * 
     * <p>
     * This form avoids superfluous string concatenation when the logger
     * is disabled for the ERROR level. However, this variant incurs the hidden
     * (and relatively small) cost of creating an <code>Object[]</code> before invoking the method,
     * even if this logger is disabled for ERROR. The variants taking
     * {@link #error(String, Object) one} and {@link #error(String, Object, Object) two}
     * arguments exist solely in order to avoid this hidden cost.
     * </p>
     * 
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */

    public void error(String format, Object... arguments) {
        delegate.error(format, obfuscate(arguments));
    }

    /**
     * This method is similar to {@link #error(String, Object)} method except that the
     * marker data is also taken into consideration.
     * 
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg the argument
     */

    public void error(Marker marker, String format, Object arg) {
        delegate.error(marker, format, obfuscate(arg));
    }

    /**
     * This method is similar to {@link #error(String, Object, Object)}
     * method except that the marker data is also taken into
     * consideration.
     * 
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arg1 the first argument
     * @param arg2 the second argument
     */

    public void error(Marker marker, String format, Object arg1, Object arg2) {
        delegate.error(marker, format, obfuscate(arg1), obfuscate(arg2));
    }

    /**
     * This method is similar to {@link #error(String, Object...)}
     * method except that the marker data is also taken into
     * consideration.
     * 
     * @param marker the marker data specific to this log statement
     * @param format the format string
     * @param arguments a list of 3 or more arguments
     */

    public void error(Marker marker, String format, Object... arguments) {
        delegate.error(marker, format, obfuscate(arguments));
    }

    /**
     * obfuscate the toString of the argument if it is not null else return null
     *
     * @param arg
     * @return an obfuscated String of arg.toString if arg is not null else return null
     */
    String obfuscate(Object arg) {
        if (arg == null) {
            return null;
        }
        return encode(arg.toString());
    }

    String[] obfuscate(Object... args) {
        String[] obfuscated = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            obfuscated[i] = obfuscate(args[i]);
        }
        return obfuscated;
    }

    /**
     * encodes the data using hmac sha256 and encodes it using base64
     * 
     * @param data, to be encoded
     * @return base64 string of the hmac sha256 encoded data
     */
    public static String encode(String data) {
        if (sha256_HMAC == null) {
            return "<hidden>";
        } else {
            try {
                return Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                // should never happend
                log.error("Failed to obfuscate data due to a :  UTF-8 is not a supported encoding !! ");
                return "<hidden>";
            }
        }
    }

}

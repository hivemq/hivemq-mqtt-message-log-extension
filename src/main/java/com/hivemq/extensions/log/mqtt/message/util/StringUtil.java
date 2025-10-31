/*
 * Copyright 2019-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hivemq.extensions.log.mqtt.message.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utility methods for string encoding and formatting.
 *
 * @since 1.3.0
 */
public class StringUtil {

    private static final char @NotNull [] DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private StringUtil() {
    }

    /**
     * Converts a ByteBuffer to a UTF-8 string.
     *
     * @param buffer the ByteBuffer to convert (may be null)
     * @return the string representation, or null if buffer is null
     */
    public static @Nullable String getStringFromByteBuffer(final @Nullable ByteBuffer buffer) {
        if (buffer == null) {
            return null;
        }
        final var bytes = getBytes(buffer);
        return new String(bytes, UTF_8);
    }

    /**
     * Converts a ByteBuffer to a hexadecimal string.
     *
     * @param buffer the ByteBuffer to convert (may be null)
     * @return the hex string representation, or null if buffer is null
     */
    public static @Nullable String getHexStringFromByteBuffer(final @Nullable ByteBuffer buffer) {
        if (buffer == null) {
            return null;
        }
        final var bytes = getBytes(buffer);
        return asHexString(bytes);
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param data the byte array to convert
     * @return the hex string representation
     */
    public static @NotNull String asHexString(final byte @NotNull [] data) {
        final var length = data.length;
        final var out = new char[length << 1];
        for (int i = 0, j = 0; i < length; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return new String(out);
    }

    /**
     * Extracts bytes from a ByteBuffer without modifying its position.
     *
     * @param buffer the ByteBuffer to extract from
     * @return the byte array
     */
    public static byte @NotNull [] getBytes(final @NotNull ByteBuffer buffer) {
        final var bytes = new byte[buffer.remaining()];
        for (var i = 0; i < buffer.remaining(); i++) {
            bytes[i] = buffer.get(i);
        }
        return bytes;
    }
}

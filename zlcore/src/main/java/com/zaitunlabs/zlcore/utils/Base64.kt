package com.zaitunlabs.zlcore.utils

/*
 * Copyright (C) 2010 The Android Open Source Project
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
 */

import android.util.Base64OutputStream

import java.io.UnsupportedEncodingException

/**
 * Utilities for encoding and decoding the Base64 representation of
 * binary data. See RFCs [2045](http://www.ietf.org/rfc/rfc2045.txt) and [3548](http://www.ietf.org/rfc/rfc3548.txt).
 */
object Base64 {
    /**
     * Default values for encoder/decoder flags.
     */
    val DEFAULT = 0
    /**
     * Encoder flag bit to omit the padding '=' characters at the end
     * of the output (if any).
     */
    val NO_PADDING = 1
    /**
     * Encoder flag bit to omit all line terminators (i.e., the output
     * will be on one long line).
     */
    val NO_WRAP = 2
    /**
     * Encoder flag bit to indicate lines should be terminated with a
     * CRLF pair instead of just an LF. Has no effect if `NO_WRAP` is specified as well.
     */
    val CRLF = 4
    /**
     * Encoder/decoder flag bit to indicate using the "URL and
     * filename safe" variant of Base64 (see RFC 3548 section 4) where
     * `-` and `_` are used in place of `+` and
     * `/`.
     */
    val URL_SAFE = 8
    /**
     * Flag to pass to [Base64OutputStream] to indicate that it
     * should not close the output stream it is wrapping when it
     * itself is closed.
     */
    val NO_CLOSE = 16

    // --------------------------------------------------------
    // shared code
    // --------------------------------------------------------
    /* package */ internal abstract class Coder {
        var output: ByteArray? = null
        var op: Int = 0
        /**
         * Encode/decode another block of input data. this.output is
         * provided by the caller, and must be big enough to hold all
         * the coded data. On exit, this.opwill be set to the length
         * of the coded data.
         *
         * @param finish true if this is the final call to process for
         * this object. Will finalize the coder state and
         * include any final bytes in the output.
         *
         * @return true if the input so far is good; false if some
         * error has been detected in the input stream..
         */
        abstract fun process(input: ByteArray, offset: Int, len: Int, finish: Boolean): Boolean

        /**
         * @return the maximum number of bytes a call to process()
         * could produce for the given number of input bytes. This may
         * be an overestimate.
         */
        abstract fun maxOutputSize(len: Int): Int
    }
    // --------------------------------------------------------
    // decoding
    // --------------------------------------------------------
    /**
     * Decode the Base64-encoded data in input and return the data in
     * a new byte array.
     *
     *
     * The padding '=' characters at the end are considered optional, but
     * if any are present, there must be the correct number of them.
     *
     * @param str the input String to decode, which is converted to
     * bytes using the default charset
     * @param flags controls certain features of the decoded output.
     * Pass `DEFAULT` to decode standard Base64.
     *
     * @throws IllegalArgumentException if the input contains
     * incorrect padding
     */
    fun decode(str: String, flags: Int): ByteArray? {
        return decode(str.toByteArray(), flags)
    }

    /**
     * Decode the Base64-encoded data in input and return the data in
     * a new byte array.
     *
     *
     * The padding '=' characters at the end are considered optional, but
     * if any are present, there must be the correct number of them.
     *
     * @param input the input array to decode
     * @param flags controls certain features of the decoded output.
     * Pass `DEFAULT` to decode standard Base64.
     *
     * @throws IllegalArgumentException if the input contains
     * incorrect padding
     */
    fun decode(input: ByteArray, flags: Int): ByteArray? {
        return decode(input, 0, input.size, flags)
    }

    /**
     * Decode the Base64-encoded data in input and return the data in
     * a new byte array.
     *
     *
     * The padding '=' characters at the end are considered optional, but
     * if any are present, there must be the correct number of them.
     *
     * @param input the data to decode
     * @param offset the position within the input array at which to start
     * @param len the number of bytes of input to decode
     * @param flags controls certain features of the decoded output.
     * Pass `DEFAULT` to decode standard Base64.
     *
     * @throws IllegalArgumentException if the input contains
     * incorrect padding
     */
    fun decode(input: ByteArray, offset: Int, len: Int, flags: Int): ByteArray? {
        // Allocate space for the most data the input could represent.
        // (It could contain less if it contains whitespace, etc.)
        val decoder = Decoder(flags, ByteArray(len * 3 / 4))
        if (!decoder.process(input, offset, len, true)) {
            throw IllegalArgumentException("bad base-64")
        }
        // Maybe we got lucky and allocated exactly enough output space.
        if (decoder.op == decoder.output!!.size) {
            return decoder.output
        }
        // Need to shorten the array, so allocate a new one of the
        // right size and copy.
        val temp = ByteArray(decoder.op)
        System.arraycopy(decoder.output!!, 0, temp, 0, decoder.op)
        return temp
    }

    /* package */ internal class Decoder(flags: Int, output: ByteArray) : Coder() {
        /**
         * States 0-3 are reading through the next input tuple.
         * State 4 is having read one '=' and expecting exactly
         * one more.
         * State 5 is expecting no more data or padding characters
         * in the input.
         * State 6 is the error state; an error has been detected
         * in the input and no future input can "fix" it.
         */
        private var state: Int = 0 // state number (0 to 6)
        private var value: Int = 0
        private val alphabet: IntArray

        init {
            this.output = output
            alphabet = if (flags and URL_SAFE == 0) DECODE else DECODE_WEBSAFE
            state = 0
            value = 0
        }

        /**
         * @return an overestimate for the number of bytes `len` bytes could decode to.
         */
        override fun maxOutputSize(len: Int): Int {
            return len * 3 / 4 + 10
        }

        /**
         * Decode another block of input data.
         *
         * @return true if the state machine is still healthy. false if
         * bad base-64 data has been detected in the input stream.
         */
        override fun process(input: ByteArray, offset: Int, len: Int, finish: Boolean): Boolean {
            var len = len
            if (this.state == 6) return false
            var p = offset
            len += offset
            // Using local variables makes the decoder about 12%
            // faster than if we manipulate the member variables in
            // the loop. (Even alphabet makes a measurable
            // difference, which is somewhat surprising to me since
            // the member variable is final.)
            var state = this.state
            var value = this.value
            var op = 0
            val output = this.output
            val alphabet = this.alphabet
            while (p < len) {
                // Try the fast path: we're starting a new tuple and the
                // next four bytes of the input stream are all data
                // bytes. This corresponds to going through states
                // 0-1-2-3-0. We expect to use this method for most of
                // the data.
                //
                // If any of the next four bytes of input are non-data
                // (whitespace, etc.), value will end up negative. (All
                // the non-data values in decode are small negative
                // numbers, so shifting any of them up and or'ing them
                // together will result in a value with its top bit set.)
                //
                // You can remove this whole block and the output should
                // be the same, just slower.
                if (state == 0) {
                    while (p + 4 <= len && (value = alphabet[input[p] and 0xff] shl 18 or
                                    (alphabet[input[p + 1] and 0xff] shl 12) or
                                    (alphabet[input[p + 2] and 0xff] shl 6) or
                                    alphabet[input[p + 3] and 0xff]) >= 0) {
                        output[op + 2] = value.toByte()
                        output[op + 1] = (value shr 8).toByte()
                        output[op] = (value shr 16).toByte()
                        op += 3
                        p += 4
                    }
                    if (p >= len) break
                }
                // The fast path isn't available -- either we've read a
                // partial tuple, or the next four input bytes aren't all
                // data, or whatever. Fall back to the slower state
                // machine implementation.
                val d = alphabet[input[p++] and 0xff]
                when (state) {
                    0 -> if (d >= 0) {
                        value = d
                        ++state
                    } else if (d != SKIP) {
                        this.state = 6
                        return false
                    }
                    1 -> if (d >= 0) {
                        value = value shl 6 or d
                        ++state
                    } else if (d != SKIP) {
                        this.state = 6
                        return false
                    }
                    2 -> if (d >= 0) {
                        value = value shl 6 or d
                        ++state
                    } else if (d == EQUALS) {
                        // Emit the last (partial) output tuple;
                        // expect exactly one more padding character.
                        output[op++] = (value shr 4).toByte()
                        state = 4
                    } else if (d != SKIP) {
                        this.state = 6
                        return false
                    }
                    3 -> if (d >= 0) {
                        // Emit the output triple and return to state 0.
                        value = value shl 6 or d
                        output[op + 2] = value.toByte()
                        output[op + 1] = (value shr 8).toByte()
                        output[op] = (value shr 16).toByte()
                        op += 3
                        state = 0
                    } else if (d == EQUALS) {
                        // Emit the last (partial) output tuple;
                        // expect no further data or padding characters.
                        output[op + 1] = (value shr 2).toByte()
                        output[op] = (value shr 10).toByte()
                        op += 2
                        state = 5
                    } else if (d != SKIP) {
                        this.state = 6
                        return false
                    }
                    4 -> if (d == EQUALS) {
                        ++state
                    } else if (d != SKIP) {
                        this.state = 6
                        return false
                    }
                    5 -> if (d != SKIP) {
                        this.state = 6
                        return false
                    }
                }
            }
            if (!finish) {
                // We're out of input, but a future call could provide
                // more.
                this.state = state
                this.value = value
                this.op = op
                return true
            }
            // Done reading input. Now figure out where we are left in
            // the state machine and finish up.
            when (state) {
                0 -> {
                }
                1 -> {
                    // Read one extra input byte, which isn't enough to
                    // make another output byte. Illegal.
                    this.state = 6
                    return false
                }
                2 ->
                    // Read two extra input bytes, enough to emit 1 more
                    // output byte. Fine.
                    output[op++] = (value shr 4).toByte()
                3 -> {
                    // Read three extra input bytes, enough to emit 2 more
                    // output bytes. Fine.
                    output[op++] = (value shr 10).toByte()
                    output[op++] = (value shr 2).toByte()
                }
                4 -> {
                    // Read one padding '=' when we expected 2. Illegal.
                    this.state = 6
                    return false
                }
                5 -> {
                }
            }// Output length is a multiple of three. Fine.
            // Read all the padding '='s we expected and no more.
            // Fine.
            this.state = state
            this.op = op
            return true
        }

        companion object {
            /**
             * Lookup table for turning bytes into their position in the
             * Base64 alphabet.
             */
            private val DECODE = intArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
            /**
             * Decode lookup table for the "web safe" variant (RFC 3548
             * sec. 4) where - and _ replace + and /.
             */
            private val DECODE_WEBSAFE = intArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
            /** Non-data values in the DECODE arrays.  */
            private val SKIP = -1
            private val EQUALS = -2
        }
    }
    // --------------------------------------------------------
    // encoding
    // --------------------------------------------------------
    /**
     * Base64-encode the given data and return a newly allocated
     * String with the result.
     *
     * @param input the data to encode
     * @param flags controls certain features of the encoded output.
     * Passing `DEFAULT` results in output that
     * adheres to RFC 2045.
     */
    fun encodeToString(input: ByteArray, flags: Int): String {
        try {
            return String(encode(input, flags), "US-ASCII")
        } catch (e: UnsupportedEncodingException) {
            // US-ASCII is guaranteed to be available.
            throw AssertionError(e)
        }

    }

    /**
     * Base64-encode the given data and return a newly allocated
     * String with the result.
     *
     * @param input the data to encode
     * @param offset the position within the input array at which to
     * start
     * @param len the number of bytes of input to encode
     * @param flags controls certain features of the encoded output.
     * Passing `DEFAULT` results in output that
     * adheres to RFC 2045.
     */
    fun encodeToString(input: ByteArray, offset: Int, len: Int, flags: Int): String {
        try {
            return String(encode(input, offset, len, flags), "US-ASCII")
        } catch (e: UnsupportedEncodingException) {
            // US-ASCII is guaranteed to be available.
            throw AssertionError(e)
        }

    }

    /**
     * Base64-encode the given data and return a newly allocated
     * byte[] with the result.
     *
     * @param input the data to encode
     * @param flags controls certain features of the encoded output.
     * Passing `DEFAULT` results in output that
     * adheres to RFC 2045.
     */
    fun encode(input: ByteArray, flags: Int): ByteArray? {
        return encode(input, 0, input.size, flags)
    }

    /**
     * Base64-encode the given data and return a newly allocated
     * byte[] with the result.
     *
     * @param input the data to encode
     * @param offset the position within the input array at which to
     * start
     * @param len the number of bytes of input to encode
     * @param flags controls certain features of the encoded output.
     * Passing `DEFAULT` results in output that
     * adheres to RFC 2045.
     */
    fun encode(input: ByteArray, offset: Int, len: Int, flags: Int): ByteArray? {
        val encoder = Encoder(flags, null)
        // Compute the exact length of the array we will produce.
        var output_len = len / 3 * 4
        // Account for the tail of the data and the padding bytes, if any.
        if (encoder.do_padding) {
            if (len % 3 > 0) {
                output_len += 4
            }
        } else {
            when (len % 3) {
                0 -> {
                }
                1 -> output_len += 2
                2 -> output_len += 3
            }
        }
        // Account for the newlines, if any.
        if (encoder.do_newline && len > 0) {
            output_len += ((len - 1) / (3 * Encoder.LINE_GROUPS) + 1) * if (encoder.do_cr) 2 else 1
        }
        encoder.output = ByteArray(output_len)
        encoder.process(input, offset, len, true)
        assert(encoder.op == output_len)
        return encoder.output
    }

    /* package */ internal class Encoder(flags: Int, output: ByteArray) : Coder() {
        private val tail: ByteArray
        /* package */  var tailLen: Int = 0
        private var count: Int = 0
        val do_padding: Boolean
        val do_newline: Boolean
        val do_cr: Boolean
        private val alphabet: ByteArray

        init {
            this.output = output
            do_padding = flags and NO_PADDING == 0
            do_newline = flags and NO_WRAP == 0
            do_cr = flags and CRLF != 0
            alphabet = if (flags and URL_SAFE == 0) ENCODE else ENCODE_WEBSAFE
            tail = ByteArray(2)
            tailLen = 0
            count = if (do_newline) LINE_GROUPS else -1
        }

        /**
         * @return an overestimate for the number of bytes `len` bytes could encode to.
         */
        override fun maxOutputSize(len: Int): Int {
            return len * 8 / 5 + 10
        }

        override fun process(input: ByteArray, offset: Int, len: Int, finish: Boolean): Boolean {
            var len = len
            // Using local variables makes the encoder about 9% faster.
            val alphabet = this.alphabet
            val output = this.output
            var op = 0
            var count = this.count
            var p = offset
            len += offset
            var v = -1
            // First we need to concatenate the tail of the previous call
            // with any input bytes available now and see if we can empty
            // the tail.
            when (tailLen) {
                0 -> {
                }
                1 -> {
                    if (p + 2 <= len) {
                        // A 1-byte tail with at least 2 bytes of
                        // input available now.
                        v = tail[0] and 0xff shl 16 or
                                (input[p++] and 0xff shl 8) or
                                (input[p++] and 0xff)
                        tailLen = 0
                    }
                }
                2 -> if (p + 1 <= len) {
                    // A 2-byte tail with at least 1 byte of input.
                    v = tail[0] and 0xff shl 16 or
                            (tail[1] and 0xff shl 8) or
                            (input[p++] and 0xff)
                    tailLen = 0
                }
            }// There was no tail.
            if (v != -1) {
                output[op++] = alphabet[v shr 18 and 0x3f]
                output[op++] = alphabet[v shr 12 and 0x3f]
                output[op++] = alphabet[v shr 6 and 0x3f]
                output[op++] = alphabet[v and 0x3f]
                if (--count == 0) {
                    if (do_cr) output[op++] = '\r'.toByte()
                    output[op++] = '\n'.toByte()
                    count = LINE_GROUPS
                }
            }
            // At this point either there is no tail, or there are fewer
            // than 3 bytes of input available.
            // The main loop, turning 3 input bytes into 4 output bytes on
            // each iteration.
            while (p + 3 <= len) {
                v = input[p] and 0xff shl 16 or
                        (input[p + 1] and 0xff shl 8) or
                        (input[p + 2] and 0xff)
                output[op] = alphabet[v shr 18 and 0x3f]
                output[op + 1] = alphabet[v shr 12 and 0x3f]
                output[op + 2] = alphabet[v shr 6 and 0x3f]
                output[op + 3] = alphabet[v and 0x3f]
                p += 3
                op += 4
                if (--count == 0) {
                    if (do_cr) output[op++] = '\r'.toByte()
                    output[op++] = '\n'.toByte()
                    count = LINE_GROUPS
                }
            }
            if (finish) {
                // Finish up the tail of the input. Note that we need to
                // consume any bytes in tail before any bytes
                // remaining in input; there should be at most two bytes
                // total.
                if (p - tailLen == len - 1) {
                    var t = 0
                    v = (if (tailLen > 0) tail[t++] else input[p++]) and 0xff shl 4
                    tailLen -= t
                    output[op++] = alphabet[v shr 6 and 0x3f]
                    output[op++] = alphabet[v and 0x3f]
                    if (do_padding) {
                        output[op++] = '='.toByte()
                        output[op++] = '='.toByte()
                    }
                    if (do_newline) {
                        if (do_cr) output[op++] = '\r'.toByte()
                        output[op++] = '\n'.toByte()
                    }
                } else if (p - tailLen == len - 2) {
                    var t = 0
                    v = (if (tailLen > 1) tail[t++] else input[p++]) and 0xff shl 10 or ((if (tailLen > 0) tail[t++] else input[p++]) and 0xff shl 2)
                    tailLen -= t
                    output[op++] = alphabet[v shr 12 and 0x3f]
                    output[op++] = alphabet[v shr 6 and 0x3f]
                    output[op++] = alphabet[v and 0x3f]
                    if (do_padding) {
                        output[op++] = '='.toByte()
                    }
                    if (do_newline) {
                        if (do_cr) output[op++] = '\r'.toByte()
                        output[op++] = '\n'.toByte()
                    }
                } else if (do_newline && op > 0 && count != LINE_GROUPS) {
                    if (do_cr) output[op++] = '\r'.toByte()
                    output[op++] = '\n'.toByte()
                }
                assert(tailLen == 0)
                assert(p == len)
            } else {
                // Save the leftovers in tail to be consumed on the next
                // call to encodeInternal.
                if (p == len - 1) {
                    tail[tailLen++] = input[p]
                } else if (p == len - 2) {
                    tail[tailLen++] = input[p]
                    tail[tailLen++] = input[p + 1]
                }
            }
            this.op = op
            this.count = count
            return true
        }

        companion object {
            /**
             * Emit a new line every this many output tuples. Corresponds to
             * a 76-character line length (the maximum allowable according to
             * [RFC 2045](http://www.ietf.org/rfc/rfc2045.txt)).
             */
            val LINE_GROUPS = 19
            /**
             * Lookup table for turning Base64 alphabet positions (6 bits)
             * into output bytes.
             */
            private val ENCODE = byteArrayOf('A'.toByte(), 'B'.toByte(), 'C'.toByte(), 'D'.toByte(), 'E'.toByte(), 'F'.toByte(), 'G'.toByte(), 'H'.toByte(), 'I'.toByte(), 'J'.toByte(), 'K'.toByte(), 'L'.toByte(), 'M'.toByte(), 'N'.toByte(), 'O'.toByte(), 'P'.toByte(), 'Q'.toByte(), 'R'.toByte(), 'S'.toByte(), 'T'.toByte(), 'U'.toByte(), 'V'.toByte(), 'W'.toByte(), 'X'.toByte(), 'Y'.toByte(), 'Z'.toByte(), 'a'.toByte(), 'b'.toByte(), 'c'.toByte(), 'd'.toByte(), 'e'.toByte(), 'f'.toByte(), 'g'.toByte(), 'h'.toByte(), 'i'.toByte(), 'j'.toByte(), 'k'.toByte(), 'l'.toByte(), 'm'.toByte(), 'n'.toByte(), 'o'.toByte(), 'p'.toByte(), 'q'.toByte(), 'r'.toByte(), 's'.toByte(), 't'.toByte(), 'u'.toByte(), 'v'.toByte(), 'w'.toByte(), 'x'.toByte(), 'y'.toByte(), 'z'.toByte(), '0'.toByte(), '1'.toByte(), '2'.toByte(), '3'.toByte(), '4'.toByte(), '5'.toByte(), '6'.toByte(), '7'.toByte(), '8'.toByte(), '9'.toByte(), '+'.toByte(), '/'.toByte())
            /**
             * Lookup table for turning Base64 alphabet positions (6 bits)
             * into output bytes.
             */
            private val ENCODE_WEBSAFE = byteArrayOf('A'.toByte(), 'B'.toByte(), 'C'.toByte(), 'D'.toByte(), 'E'.toByte(), 'F'.toByte(), 'G'.toByte(), 'H'.toByte(), 'I'.toByte(), 'J'.toByte(), 'K'.toByte(), 'L'.toByte(), 'M'.toByte(), 'N'.toByte(), 'O'.toByte(), 'P'.toByte(), 'Q'.toByte(), 'R'.toByte(), 'S'.toByte(), 'T'.toByte(), 'U'.toByte(), 'V'.toByte(), 'W'.toByte(), 'X'.toByte(), 'Y'.toByte(), 'Z'.toByte(), 'a'.toByte(), 'b'.toByte(), 'c'.toByte(), 'd'.toByte(), 'e'.toByte(), 'f'.toByte(), 'g'.toByte(), 'h'.toByte(), 'i'.toByte(), 'j'.toByte(), 'k'.toByte(), 'l'.toByte(), 'm'.toByte(), 'n'.toByte(), 'o'.toByte(), 'p'.toByte(), 'q'.toByte(), 'r'.toByte(), 's'.toByte(), 't'.toByte(), 'u'.toByte(), 'v'.toByte(), 'w'.toByte(), 'x'.toByte(), 'y'.toByte(), 'z'.toByte(), '0'.toByte(), '1'.toByte(), '2'.toByte(), '3'.toByte(), '4'.toByte(), '5'.toByte(), '6'.toByte(), '7'.toByte(), '8'.toByte(), '9'.toByte(), '-'.toByte(), '_'.toByte())
        }
    }
}// don't instantiate
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.servlethelpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

/**
 * Manage response body content.
 */
class ResponseBodySupport {

    private boolean enableCheckForClosedWriter = false;
    private ByteArrayOutputStream outputStream;
    private ServletOutputStream servletOutputStream;
    private PrintWriter printWriter;

    public ResponseBodySupport() {
        reset();
    }

    public void setEnableCheckForClosedWriter(boolean enableCheckForClosedWriter) {
        this.enableCheckForClosedWriter = enableCheckForClosedWriter;
        reset();
    }


    public void reset() {
        outputStream = new ByteArrayOutputStream();
        servletOutputStream = null;
        printWriter = null;
    }

    public ServletOutputStream getOutputStream() {
        if (servletOutputStream == null) {
            servletOutputStream = new ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    outputStream.write(b);
                }
                @Override
                public boolean isReady() {
                    return true;
                }
                @Override
                public void setWriteListener(WriteListener writeListener) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return servletOutputStream;
    }

    public PrintWriter getWriter(String charset) {
        if (printWriter == null) {
            try {
                PrintWriter printWriter1 = new PrintWriter(new OutputStreamWriter(getOutputStream(), defaultCharset(charset)));
                printWriter = enableCheckForClosedWriter ? new CheckForClosedPrintWriter(printWriter1) : printWriter1;
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException("Unsupported encoding: " + defaultCharset(charset), ex);
            }
        }
        return printWriter;
    }

    public byte[] getOutput() {
        if (printWriter != null) {
            printWriter.flush();
        }
        if (servletOutputStream != null) {
            try {
                servletOutputStream.flush();
            } catch (IOException ex) {
                // ignore
            }
        }
        return outputStream.toByteArray();
    }

    public String getOutputAsString(String charset) {
        try {
            return new String(getOutput(), defaultCharset(charset));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Unsupported encoding: " + defaultCharset(charset), ex);
        }
    }
    
    private String defaultCharset(String charset) {
        return StringUtils.defaultString(charset, CharEncoding.UTF_8);
    }

    private class CheckForClosedPrintWriter extends PrintWriter {

        private final PrintWriter delegatee;

        private boolean isClosed = false;

        public CheckForClosedPrintWriter(PrintWriter delegatee) {
            super(delegatee);
            this.delegatee = delegatee;
        }

        private void checkClosed() {
            if ( this.isClosed ) {
                throw new WriterAlreadyClosedException();
            }
        }

        @Override
        public PrintWriter append(final char arg0) {
            this.checkClosed();
            return delegatee.append(arg0);
        }

        @Override
        public PrintWriter append(final CharSequence arg0, final int arg1, final int arg2) {
            this.checkClosed();
            return delegatee.append(arg0, arg1, arg2);
        }

        @Override
        public PrintWriter append(final CharSequence arg0) {
            this.checkClosed();
            return delegatee.append(arg0);
        }

        @Override
        public boolean checkError() {
            this.checkClosed();
            return delegatee.checkError();
        }

        @Override
        public void close() {
            this.checkClosed();
            this.isClosed = true;
            delegatee.close();
        }

        @Override
        public void flush() {
            this.checkClosed();
            delegatee.flush();
        }

        @Override
        public PrintWriter format(final Locale arg0, final String arg1,
                                  final Object... arg2) {
            this.checkClosed();
            return delegatee.format(arg0, arg1, arg2);
        }

        @Override
        public PrintWriter format(final String arg0, final Object... arg1) {
            this.checkClosed();
            return delegatee.format(arg0, arg1);
        }

        @Override
        public void print(final boolean arg0) {
            this.checkClosed();
            delegatee.print(arg0);
        }

        @Override
        public void print(final char arg0) {
            this.checkClosed();
            delegatee.print(arg0);
        }

        @Override
        public void print(final char[] arg0) {
            this.checkClosed();
            delegatee.print(arg0);
        }

        @Override
        public void print(final double arg0) {
            this.checkClosed();
            delegatee.print(arg0);
        }

        @Override
        public void print(final float arg0) {
            this.checkClosed();
            delegatee.print(arg0);
        }

        @Override
        public void print(final int arg0) {
            this.checkClosed();
            delegatee.print(arg0);
        }

        @Override
        public void print(final long arg0) {
            this.checkClosed();
            delegatee.print(arg0);
        }

        @Override
        public void print(final Object arg0) {
            this.checkClosed();
            delegatee.print(arg0);
        }

        @Override
        public void print(final String arg0) {
            this.checkClosed();
            delegatee.print(arg0);
        }

        @Override
        public PrintWriter printf(final Locale arg0, final String arg1,
        final Object... arg2) {
            this.checkClosed();
            return delegatee.printf(arg0, arg1, arg2);
        }

        @Override
        public PrintWriter printf(final String arg0, final Object... arg1) {
            this.checkClosed();
            return delegatee.printf(arg0, arg1);
        }

        @Override
        public void println() {
            this.checkClosed();
            delegatee.println();
        }

        @Override
        public void println(final boolean arg0) {
            this.checkClosed();
            delegatee.println(arg0);
        }

        @Override
        public void println(final char arg0) {
            this.checkClosed();
            delegatee.println(arg0);
        }

        @Override
        public void println(final char[] arg0) {
            this.checkClosed();
            delegatee.println(arg0);
        }

        @Override
        public void println(final double arg0) {
            this.checkClosed();
            delegatee.println(arg0);
        }

        @Override
        public void println(final float arg0) {
            this.checkClosed();
            delegatee.println(arg0);
        }

        @Override
        public void println(final int arg0) {
            this.checkClosed();
            delegatee.println(arg0);
        }

        @Override
        public void println(final long arg0) {
            this.checkClosed();
            delegatee.println(arg0);
        }

        @Override
        public void println(final Object arg0) {
            this.checkClosed();
            delegatee.println(arg0);
        }

        @Override
        public void println(final String arg0) {
            this.checkClosed();
            delegatee.println(arg0);
        }

        @Override
        public void write(final char[] arg0, final int arg1, final int arg2) {
            this.checkClosed();
            delegatee.write(arg0, arg1, arg2);
        }

        @Override
        public void write(final char[] arg0) {
            this.checkClosed();
            delegatee.write(arg0);
        }

        @Override
        public void write(final int arg0) {
            this.checkClosed();
            delegatee.write(arg0);
        }

        @Override
        public void write(final String arg0, final int arg1, final int arg2) {
            this.checkClosed();
            delegatee.write(arg0, arg1, arg2);
        }

        @Override
        public void write(final String arg0) {
            this.checkClosed();
            delegatee.write(arg0);
        }

    };

    public class WriterAlreadyClosedException extends RuntimeException {
    }
}

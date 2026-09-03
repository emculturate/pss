package cli;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

/**
 * Byte-accurate MCP stdio framing. Content-Length is always measured in UTF-8 bytes.
 * Header lines are read from the raw {@link InputStream} so body bytes are never consumed early.
 */
final class SqlParseMcpFraming {

    private SqlParseMcpFraming() {
    }

    /**
     * Reads one MCP frame: header block terminated by blank line, then exactly
     * {@code Content-Length} bytes. Returns {@code null} when the stream ends before a frame.
     */
    static byte[] readContentFrame(InputStream in) throws IOException {
        int contentLength = readContentLengthHeader(in);
        if (contentLength < 0) {
            return null;
        }
        return readFully(in, contentLength);
    }

    static int readContentLengthHeader(InputStream in) throws IOException {
        int contentLength = -1;
        StringBuilder line = new StringBuilder();
        while (true) {
            int b = in.read();
            if (b < 0) {
                return -1;
            }
            if (b == '\n') {
                String headerLine = line.toString();
                if (headerLine.endsWith("\r")) {
                    headerLine = headerLine.substring(0, headerLine.length() - 1);
                }
                if (headerLine.isEmpty()) {
                    break;
                }
                if (headerLine.regionMatches(true, 0, "Content-Length:", 0, 15)) {
                    contentLength = Integer.parseInt(headerLine.substring(15).trim());
                }
                line.setLength(0);
                continue;
            }
            line.append((char) b);
        }
        return contentLength;
    }

    static byte[] readFully(InputStream in, int length) throws IOException {
        if (length < 0) {
            throw new IOException("negative Content-Length: " + length);
        }
        byte[] body = new byte[length];
        int offset = 0;
        while (offset < length) {
            int read = in.read(body, offset, length - offset);
            if (read < 0) {
                throw new EOFException(
                        "truncated MCP frame body: expected " + length + " bytes, got " + offset);
            }
            offset += read;
        }
        return body;
    }

    static void writeJsonRpcFrame(OutputStream out, Gson gson, Object responseObject) throws IOException {
        try {
            String jsonResponse = gson.toJson(responseObject);
            writeUtf8Frame(out, jsonResponse);
        } catch (Exception primaryFailure) {
            String requestId = extractIdFromResponseObject(responseObject);
            String message = SqlParseMcpSupport.unwrapThrowable(primaryFailure).getMessage();
            if (message == null || message.isBlank()) {
                message = "MCP response serialization failed";
            }
            writeFallbackErrorFrame(out, gson, requestId, -32603, message);
        }
    }

    static void writeUtf8Frame(OutputStream out, String jsonResponse) throws IOException {
        byte[] payload = jsonResponse.getBytes(StandardCharsets.UTF_8);
        String header = "Content-Length: " + payload.length + "\r\n\r\n";
        out.write(header.getBytes(StandardCharsets.US_ASCII));
        out.write(payload);
        out.flush();
    }

    private static void writeFallbackErrorFrame(
            OutputStream out, Gson gson, String requestId, int code, String message) throws IOException {
        JsonObject envelope = new JsonObject();
        envelope.addProperty("jsonrpc", "2.0");
        if (requestId == null) {
            envelope.add("id", JsonNull.INSTANCE);
        } else {
            envelope.addProperty("id", requestId);
        }
        JsonObject error = new JsonObject();
        error.addProperty("code", code);
        error.addProperty("message", message);
        envelope.add("error", error);
        writeUtf8Frame(out, gson.toJson(envelope));
    }

    private static String extractIdFromResponseObject(Object responseObject) {
        if (responseObject instanceof SqlParseMCP.JsonRpcResponse) {
            return ((SqlParseMCP.JsonRpcResponse) responseObject).id;
        }
        if (responseObject instanceof SqlParseMCP.JsonRpcErrorResponse) {
            return ((SqlParseMCP.JsonRpcErrorResponse) responseObject).id;
        }
        return null;
    }
}

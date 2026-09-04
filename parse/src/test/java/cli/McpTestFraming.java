package cli;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

final class McpTestFraming {

    private static final Gson GSON = new Gson();

    private McpTestFraming() {
    }

    static byte[] parseSqlRequest(String id, String endPoint, String sqlText) {
        String params = "{\"endPoint\":\"" + endPoint + "\",\"sqlText\":" + GSON.toJson(sqlText) + "}";
        return jsonRpcFrame(id, "tool/parseSql", params);
    }

    static byte[] parseSqlStreamRequest(String id, String endPoint, byte[] sqlBytes) {
        String params = String.format(
                "{\"endPoint\":\"%s\",\"sqlByteLength\":%d}",
                endPoint, sqlBytes.length);
        byte[] frame1 = jsonRpcFrame(id, "tool/parseSqlStream", params);
        String frame2Header = "Content-Length: " + sqlBytes.length + "\r\n\r\n";
        byte[] headerBytes = frame2Header.getBytes(StandardCharsets.US_ASCII);
        byte[] request = new byte[frame1.length + headerBytes.length + sqlBytes.length];
        System.arraycopy(frame1, 0, request, 0, frame1.length);
        System.arraycopy(headerBytes, 0, request, frame1.length, headerBytes.length);
        System.arraycopy(sqlBytes, 0, request, frame1.length + headerBytes.length, sqlBytes.length);
        return request;
    }

    static byte[] jsonRpcFrame(String id, String method, String paramsJson) {
        String payload = String.format(
                "{\"jsonrpc\":\"2.0\",\"id\":\"%s\",\"method\":\"%s\",\"params\":%s}",
                id, method, paramsJson);
        return utf8Frame(payload);
    }

    static byte[] utf8Frame(String jsonPayload) {
        byte[] payload = jsonPayload.getBytes(StandardCharsets.UTF_8);
        String header = "Content-Length: " + payload.length + "\r\n\r\n";
        byte[] headerBytes = header.getBytes(StandardCharsets.US_ASCII);
        byte[] frame = new byte[headerBytes.length + payload.length];
        System.arraycopy(headerBytes, 0, frame, 0, headerBytes.length);
        System.arraycopy(payload, 0, frame, headerBytes.length, payload.length);
        return frame;
    }

    static JsonObject firstJsonRpcResponse(byte[] outputBytes) {
        List<JsonObject> responses = allJsonRpcResponses(outputBytes);
        if (responses.isEmpty()) {
            return null;
        }
        return responses.get(0);
    }

    static List<JsonObject> allJsonRpcResponses(byte[] outputBytes) {
        List<JsonObject> responses = new ArrayList<>();
        int offset = 0;
        while (offset < outputBytes.length) {
            int headerEnd = indexOf(outputBytes, "\r\n\r\n", offset);
            if (headerEnd < 0) {
                break;
            }
            String header = new String(outputBytes, offset, headerEnd - offset, StandardCharsets.US_ASCII);
            int contentLength = parseContentLength(header);
            if (contentLength < 0) {
                break;
            }
            int bodyStart = headerEnd + 4;
            int bodyEnd = bodyStart + contentLength;
            if (bodyEnd > outputBytes.length) {
                break;
            }
            String json = new String(outputBytes, bodyStart, contentLength, StandardCharsets.UTF_8);
            responses.add(parseLenientJsonObject(json));
            offset = bodyEnd;
        }
        return responses;
    }

    private static int parseContentLength(String headerBlock) {
        int contentLength = -1;
        for (String line : headerBlock.split("\r\n")) {
            if (line.regionMatches(true, 0, "Content-Length:", 0, 15)) {
                contentLength = Integer.parseInt(line.substring(15).trim());
            }
        }
        return contentLength;
    }

    private static int indexOf(byte[] haystack, String needle, int fromIndex) {
        byte[] needleBytes = needle.getBytes(StandardCharsets.US_ASCII);
        if (fromIndex < 0 || fromIndex >= haystack.length) {
            return -1;
        }
        outer:
        for (int index = fromIndex; index <= haystack.length - needleBytes.length; index++) {
            for (int needleIndex = 0; needleIndex < needleBytes.length; needleIndex++) {
                if (haystack[index + needleIndex] != needleBytes[needleIndex]) {
                    continue outer;
                }
            }
            return index;
        }
        return -1;
    }

    private static JsonObject parseLenientJsonObject(String json) {
        JsonReader reader = new JsonReader(new java.io.StringReader(json));
        reader.setLenient(true);
        return GSON.fromJson(reader, JsonObject.class);
    }
}

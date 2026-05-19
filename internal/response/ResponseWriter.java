package internal.response;

import internal.headers.Headers;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ResponseWriter{
    private ResponseWriter(){}

    public static void writeStatusLine(OutputStream outputStream, StatusCode statusCode) throws IOException{
        String reason = switch(statusCode){
            case OK -> "OK";
            case BAD_REQUEST -> "ERROR";
            case INTERNAL_SERVER_ERROR -> "INTERNAL SERVER ERROR";
            default -> "";
        };

        String headerLine = "HTTP/1.1 " + statusCode.getCode() + " " + reason + "\r\n";

        outputStream.write(headerLine.getBytes(StandardCharsets.US_ASCII));
    }

    public static Headers getDefaultHeaders(int contentLength) {
        Headers headers = Headers.newHeaders();
        headers.put("content-length", String.valueOf(contentLength));
        headers.put("connection", "close");
        headers.put("content-type", "text/plain");
        return headers;
    }

    public static void writeHeaders(OutputStream outputStream, Headers headers) throws IOException{
        for(Map.Entry<String, String> header: headers.entrySet()){
            String h = header.getKey() + ": " + header.getValue() + "\r\n";
            outputStream.write(h.getBytes(StandardCharsets.US_ASCII));    
        }
        outputStream.write("\r\n".getBytes(StandardCharsets.US_ASCII));
    }
}
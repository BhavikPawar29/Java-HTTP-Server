package internal.headers;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Headers extends HashMap<String, String>{

    private static final String allowedCharacters = "!#$%&'*+-.^_`|~";

    public static Headers newHeaders(){
        return new Headers();
    }

    public String getHeader(String key){
        if (key == null) return null;
        return get(key.toLowerCase());
    }

    public void set(String key, String value){
        put(key.toLowerCase(), value);
    }

    //@Override
    public void remove(String key){
        super.remove(key.toLowerCase());
    }

    public HeadersResult parseHeaders(byte[] buffer, int offset, int length){

        if(buffer == null || buffer.length == 0){
            return new HeadersResult(0, false, null);
        }

        for (int i = offset; i < offset + length - 1; i++ ){
            if(buffer[i] == '\r' && buffer[i+1] == '\n'){
                if (i == offset) {
                    // empty line = headers done
                    return new HeadersResult(2, true, null); //2 = length of /r/n
                }

                String headerLine = new String(buffer, offset, i - offset, StandardCharsets.US_ASCII);
                parseHeaderLine(headerLine);

                return new HeadersResult((i-offset) + 2, false, null);
            }
        }
        //no CRLF found, need more data
        return new HeadersResult(0, false, null);
    }

    private void parseHeaderLine(String headerLine) {
        int colonIndex = headerLine.indexOf(":");

        if (colonIndex <= 0){
            throw new IllegalArgumentException("Malformed Headers");
        }

        if (headerLine.charAt(colonIndex - 1) == ' '){
            throw new IllegalArgumentException("Invalid spacing before colon");
        }

        String key = headerLine.substring(0, colonIndex).trim().toLowerCase();

        if(!isValidHeaderName(key)){
            throw new IllegalArgumentException("Invalid header name");
        }

        String value = headerLine.substring(colonIndex + 1).trim();
        
        String existingValue = getOrDefault(key, null);
        put(key, existingValue == null ? value : existingValue.concat(", ").concat(value));
    }

    private boolean isValidHeaderName(String key){
        if(key.isEmpty()) {
            return false;
        }

        for (char c : key.toCharArray()){
            boolean isAllowed = allowedCharacters.indexOf(c) >= 0;

            if(!Character.isLetterOrDigit(c) && !isAllowed){
                return false;
            }
        }
        return true;
    }
}
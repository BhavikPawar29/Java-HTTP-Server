package src.internal.request;

public class RequestParser{

    public RequestLine parseRequestLineStrict(String line){

        if (line == null || line.isEmpty()){
            throw new IllegalArgumentException("Request line cannot be null or empty");
        }
        
        String[] details = line.split(" ");
        
        if (details.length != 3){
            throw new IllegalArgumentException("Request line cannot be null or empty");
        }
        
        String method = details[0];
        String requestTarget = details[1];
        String httpVersion = details[2];

        if (!method.matches("^[A-Z]+$")){
            throw new IllegalArgumentException("Invalid request method");
        }
        if (!httpVersion.contains("HTTP/")){
            throw new IllegalArgumentException("Invalid HTTP version");
        }
        
        String version = httpVersion.substring("HTTP/".length());
        if (!version.equals("1.1")){
            throw new IllegalArgumentException("Unsupported HTTP version (only 1.1 is supported)");
        }

        return new RequestLine(version, requestTarget, method);
    }
}

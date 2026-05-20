package src.internal.request;

import src.internal.headers.Headers;
import src.internal.headers.HeadersResult;
import java.nio.charset.StandardCharsets;

public class Request{
    private RequestLine requestLine; 
    private final Headers headers;
    
    private byte[] body = new byte[0];
    private ParserState parserState = ParserState.INITIALIZED;
    private final RequestParser requestParser;

    public Request() {
        this.headers = Headers.newHeaders();
        this.requestParser = new RequestParser();
    }

    public RequestLine getRequestLine() {
    return this.requestLine;
    }

    public Headers getHeaders() {
        return this.headers;
    }

    public byte[] getBody() {
        return this.body;
    }

    public ParserState getParserState() {
        return this.parserState;
    }

    //after request line, we move to header parsing
    public void setRequestLine(RequestLine rl){
        this.requestLine = rl;
        this.parserState = ParserState.PARSING_HEADERS;
    }
    
    //after headers, we move to body parsing
    public void setHeaders(){
        this.parserState = ParserState.PARSING_BODY;
    }

    public int parse(byte[] buffer, int length){
        int totalBytesParsed  = 0;

        while (parserState != ParserState.DONE) {
            int n = parserSingle(buffer, totalBytesParsed, length - totalBytesParsed);

            if(n == 0){
                break; // no more data
            }

            totalBytesParsed += n;
        }
        return totalBytesParsed;
    }
    private int parserSingle(byte[] buffer, int offset, int length) {
        switch (parserState) {
            case INITIALIZED ->{
                return parseRequestLineState(buffer, offset, length);
            }
            case PARSING_HEADERS ->{
                return parseHeadersState(buffer, offset, length);
            }
            case PARSING_BODY ->{
                return parseBodyState(buffer, offset, length);
            }
        }
        return 0;
    }

    private int parseBodyState(byte[] buffer, int offset, int length) {
        String contentLengthValue = headers.getHeader("content-length");

        if(contentLengthValue == null){
            //no Content-Length = No Body
            this.parserState = ParserState.DONE;
            return 0;
        }

        int contentLength = Integer.parseInt(contentLengthValue);

        int remaining = contentLength - body.length;

        /*
        * Here, we copy the received bytes into a buffer.
        * Then, we incrementally swap this buffer with a new,
        * larger-sized buffer until the remaining length becomes zero.
        
        * We gradually accumulate the body across multiple buffer reads.
        * Example: Content-Length is 100 bytes, but we receive 
        * data in chunks:
        *
        * 1st call: buffer has 40 bytes
        *    - body.length = 0, remaining = 100
        *    - toCopyLength = min(100, 40) = 40
        *
        * 2nd call: buffer has 50 bytes  
        *    - body.length = 40, remaining = 60
        *    - toCopyLength = min(60, 50) = 50
        *
        * 3rd call: buffer has 20 bytes
        *    - body.length = 90, remaining = 10
        *    - toCopyLength = min(10, 20) = 10 (only take what we need!)
        */

        int toCopyLength = Math.min(remaining, length);

        byte[] newBody = new byte[body.length + toCopyLength];

        System.arraycopy(this.body, 0, newBody, 0, body.length);
        System.arraycopy(buffer, offset, newBody,body.length, toCopyLength);

        body = newBody;

        if (body.length == contentLength){
            parserState = ParserState.DONE;
        }

        return toCopyLength;
        
    }

    private int parseHeadersState(byte[] buffer, int offset, int length) {
        HeadersResult headersResult = this.headers.parseHeaders(buffer, offset, length);

        if (headersResult.getError() != null) {
            throw new IllegalArgumentException(headersResult.getError());
        }

        if (headersResult.getIsDone()) {
            setHeaders();
        }
        return headersResult.getBytesConsumed();
    }

    private int parseRequestLineState(byte[] buffer, int offset, int length) {
       
        if (buffer == null || length < 2){
            return 0;
        }

        for (int i = offset; i < offset + length - 1; i++){
            if(buffer[i] == '\r' && buffer[i + 1] =='\n'){
                String line = new String(buffer, offset, i - offset, StandardCharsets.US_ASCII);
                RequestLine rl = requestParser.parseRequestLineStrict(line);

                setRequestLine(rl);

                return (i-offset) + 2;
            }
        }

        return 0;
    }
    

}

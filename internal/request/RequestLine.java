package internal.request;

public class RequestLine{
    private final String httpVersion;
    private final String requestTarget;
    private final String method;

    public RequestLine(String httpVersion, String requestTarget, String method) {
        this.httpVersion = httpVersion;
        this.requestTarget = requestTarget;
        this.method = method;
    }

    public String getHttpVersion() {
    return httpVersion;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getMethod() {
        return method;
    }
}
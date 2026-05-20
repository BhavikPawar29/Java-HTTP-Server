package src.internal.headers;

public class HeadersResult {

    private final int bytesConsumed;
    private final Boolean done;
    private final Exception error;

    public HeadersResult(int bytesConsumed, boolean done, Exception error){
        this.bytesConsumed = bytesConsumed;
        this.done = done;
        this.error = error;
    }

    public int getBytesConsumed() {
    return this.bytesConsumed;
    }

    public boolean getIsDone() {
        return this.done;
    }

    public Exception getError() {
        return this.error;
    }

    
}

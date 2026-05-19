package internal.response;

//import com.sun.net.httpserver.Headers;
import internal.headers.Headers;
import java.io.IOException;
import java.io.OutputStream;


public class Writer {
    private enum WriterState {
        INIT,
        STATUS_WRITTEN,
        HEADERS_WRITTEN,
        BODY_WRITTEN,
    }

    private WriterState state = WriterState.INIT;
    private final OutputStream outputStream;

    public Writer(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeStatus(StatusCode statusCode) throws IOException {
        if (state != WriterState.INIT) {
            throw new IllegalStateException("Status line must be written first");
        }
        ResponseWriter.writeStatusLine(outputStream, statusCode);
        state = WriterState.STATUS_WRITTEN;
    }

   public void writeHeaders(Headers headers) throws IOException {
        if (state != WriterState.STATUS_WRITTEN) {
            throw new IllegalStateException("Headers line must be written after status line");
        }
        ResponseWriter.writeHeaders(outputStream, headers);
        state = WriterState.HEADERS_WRITTEN;
    }

    public int writeBody(byte[] body)throws IOException{
        if(state != WriterState.HEADERS_WRITTEN){
            throw new IllegalStateException("Body line must be written after headers");
        }

        int n = body.length;
        outputStream.write(body);
        state = WriterState.BODY_WRITTEN;

        return n;
    }
}
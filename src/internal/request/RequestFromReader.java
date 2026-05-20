package src.internal.request;

import java.io.Reader;

public class RequestFromReader {

    private static final int INITIAL_BUFFER_SIZE = 1024;
    private static final int MAX_BUFFER_SIZE = 64 * 1024;

    public Request requestFromReader(Reader reader) {
        Request request = new Request();

        char[] charBuff = new char[8];
        byte[] buffer = new byte[INITIAL_BUFFER_SIZE];

        int bufferLength = 0;

        try {
            while (request.getParserState() != ParserState.DONE) {
                int n = reader.read(charBuff);

                if (n == -1) {
                    break; // EOF
                }

                for (int i = 0; i < n; i++) {
                    if (bufferLength >= buffer.length) {
                        if (buffer.length == MAX_BUFFER_SIZE) {
                            throw new IllegalArgumentException("Request too large");
                        }

                        // grow buffer
                        int nextSize = Math.min(buffer.length * 2, MAX_BUFFER_SIZE);

                        byte[] grownBufferSize = new byte[nextSize];
                        System.arraycopy(buffer, 0, grownBufferSize, 0, bufferLength);
                        buffer = grownBufferSize;
                    }

                    buffer[bufferLength++] = (byte) charBuff[i];
                }

                int consumed = request.parse(buffer, bufferLength);

                if (consumed > 0) {
                    // shift unconsumed bytes to the front
                    System.arraycopy(buffer, consumed, buffer, 0, bufferLength - consumed);
                    bufferLength -= consumed;
                }
            }

            if (request.getParserState() != ParserState.DONE) {
                throw new IllegalArgumentException("Incomplete Request");
            }

            return request;

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}

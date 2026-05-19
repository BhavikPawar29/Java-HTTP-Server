package internal.server;

import internal.request.Request;
import internal.response.Writer;



@FunctionalInterface
public interface Handler {
    void handle(Writer writer, Request request) throws Exception;
}
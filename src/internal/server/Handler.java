package src.internal.server;

import src.internal.request.Request;
import src.internal.response.Writer;



@FunctionalInterface
public interface Handler {
    void handle(Writer writer, Request request) throws Exception;
}


import java.nio.charset.StandardCharsets;

import internal.headers.Headers;
import internal.request.Request;
import internal.response.ResponseWriter;
import internal.response.StatusCode;
import internal.response.Writer;
import internal.server.Handler;
import internal.server.Server;


public class HTTPServerMain{
    private static final int PORT = 42069;
    public static void main(String[] args) {
        try{

            Handler handler = HTTPServerMain::handleRequest;
            Server server = Server.serve(PORT, handler);

            System.out.println("Sever started on port " + PORT);
        
            
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");

                try {
                    server.close();
                } catch (Exception e) {
                }
            }));

            Thread.currentThread().join();
        }
        catch (Exception e) {
            throw new RuntimeException("Error starting server: ", e);
        }
                
    }

    private static void handleRequest(Writer writer, Request request) throws Exception {
       String path = request.getRequestLine().getRequestTarget();

       Headers headers;
       byte[] body;
       StatusCode statuscode;

       if("/yourproblem".equals(path)){
        statuscode = StatusCode.BAD_REQUEST;
        body = "<h1>400 - Your Problem</h1>".getBytes(StandardCharsets.US_ASCII);
       }
       else if ("/myproblem".equals(path)){
        statuscode = StatusCode.INTERNAL_SERVER_ERROR;
        body = "<h1>500 - My Problem</h1>".getBytes(StandardCharsets.US_ASCII);
       }
       else{
        statuscode = StatusCode.OK;
        body = "<h1>Hello World!</h1>".getBytes(StandardCharsets.US_ASCII);
       }

       headers = ResponseWriter.getDefaultHeaders(body.length);
       headers.set("content-type", "text/html");

       writer.writeHeaders(headers);
       writer.writeStatus(statuscode);
       writer.writeBody(body);
    }
}
package src.internal.server;


import src.internal.headers.Headers;
import src.internal.request.Request;
import src.internal.request.RequestFromReader;
import src.internal.response.ResponseWriter;
import src.internal.response.StatusCode;
import src.internal.response.Writer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    private final ServerSocket serverSocket;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private final Handler handler;


    public Server (ServerSocket serversocket, Handler handler){
        this.serverSocket = serversocket;
        this.handler = handler;
    }
    
    public static Server serve(int port, Handler handler) throws Exception{
    
        ServerSocket serverSock = new ServerSocket(port);
        Server server = new Server(serverSock, handler);
    
        Thread listenerThread = new Thread(server::listen);
        listenerThread.start();
    
        return server;
    }
    
    public void listen(){
    
        while(!closed.get()){
    
            try{
                Socket conn = serverSocket.accept();
                System.out.println("Connection accepted");

                new Thread(() -> {
                    try {
                        handle(conn);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
            catch(Exception e){
    
            }
        }
    }

    public void close() throws IOException {
        closed.set(true);
        serverSocket.close();
    }

    public void handle(Socket conn) throws IOException{
        try(conn; OutputStream outputStream = conn.getOutputStream()){
            Request request;
            Writer writer = new Writer(outputStream);

            try {
                request = new RequestFromReader().requestFromReader(
                    new InputStreamReader(conn.getInputStream())
                );
            } catch (Exception e) {
                writer.writeStatus(StatusCode.BAD_REQUEST);
                handlerBadRequest(writer);
                return;
            }
            handler.handle(writer, request);
        } catch(Exception e){
            try (OutputStream outputStream = conn.getOutputStream()){

                Writer writer = new Writer(outputStream);
                writer.writeStatus(StatusCode.INTERNAL_SERVER_ERROR);
                handlerInternalServerError(writer);
                
            } catch (Exception ignoreed) {}
        }
    }

    private void handlerBadRequest(Writer w) throws Exception{

        byte body[] = "<h1>400 Bad Request</h1>".getBytes(StandardCharsets.US_ASCII);

        Headers headers = ResponseWriter.getDefaultHeaders(body.length);

        headers.set("content-type", "text/html");
        w.writeHeaders(headers);
        w.writeBody(body);
    }

    private void handlerInternalServerError(Writer w) throws Exception{

        byte[] body = "<h1>500 Internal Server Error</h1>".getBytes(StandardCharsets.US_ASCII);

        Headers headers = ResponseWriter.getDefaultHeaders(body.length);
        
        headers.set("content-type", "text/html");
        w.writeHeaders(headers);
        w.writeBody(body);
        
    }
    
}

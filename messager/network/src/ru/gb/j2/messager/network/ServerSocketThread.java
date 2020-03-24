package ru.gb.j2.messager.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketThread extends Thread {
    private int port;
    private int timeout;
    private ServerSockerThreadListener listener;

    public ServerSocketThread(ServerSockerThreadListener listener, String name, int port, int timeout) {
        super(name);
        this.port = port;
        this.timeout = timeout;
        this.listener = listener;
        start();
    }

    @Override
    public void run() {
        listener.onServerStart(this);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            listener.onServerSocketCreated(this, serverSocket);
            serverSocket.setSoTimeout(timeout);
            while (!isInterrupted()) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    listener.onServerTimeout(this, serverSocket);
                    continue;
                }
                listener.onSocketAccepted(this, serverSocket, socket);
            }
        } catch (IOException e) {
            listener.onServerException(this, e);
        } finally {
            listener.onServerStop(this);
        }
    }
}

package ru.gb.j2.messager.network;

import java.net.ServerSocket;
import java.net.Socket;

public interface ServerSockerThreadListener {
    void onServerStart(ServerSocketThread thread);
    void onServerStop(ServerSocketThread thread);
    void onServerSocketCreated(ServerSocketThread thread, ServerSocket server);
    void onServerTimeout(ServerSocketThread thread, ServerSocket server);
    void onSocketAccepted(ServerSocketThread thread, ServerSocket server, Socket socket);
    void onServerException(ServerSocketThread thread, Throwable exception);
}

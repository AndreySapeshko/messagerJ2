package ru.gb.j2.messager.chat.server.core;
import ru.gb.j2.messager.network.ServerSockerThreadListener;
import ru.gb.j2.messager.network.ServerSocketThread;
import ru.gb.j2.messager.network.SocketThread;
import ru.gb.j2.messager.network.SocketThreadListener;

import java.net.ServerSocket;
import java.net.Socket;


public class ChatServer implements ServerSockerThreadListener, SocketThreadListener {
    private ServerSocketThread serverSocket;

    public ChatServer() {
    }

    public void start(int port) {
        if (serverSocket == null || !serverSocket.isAlive()) {
            System.out.println("Server started at port: " + port);
            serverSocket = new ServerSocketThread(this,"Server", port, 2000);
        } else {
            System.out.println("Server already started!");
        }
    }

    public void stop() {
        if (serverSocket != null && serverSocket.isAlive()) {
            System.out.println("Server stopped from stop");
            serverSocket.interrupt();
        } else {
            System.out.println("Server is not running!");
        }
    }

    private void putLog(String str) {
        System.out.println(str);
    }

    @Override
    public void onServerStart(ServerSocketThread thread) {
        putLog("Server started");
    }

    @Override
    public void onServerStop(ServerSocketThread thread) {
        putLog("Server stopped");
    }

    @Override
    public void onServerSocketCreated(ServerSocketThread thread, ServerSocket server) {
        putLog("Server created");
    }

    @Override
    public void onServerTimeout(ServerSocketThread thread, ServerSocket server) {

    }

    @Override
    public void onSocketAccepted(ServerSocketThread thread, ServerSocket server, Socket socket) {
        putLog("Client connected");
        String name = "Socket thread " + socket.getInetAddress() + ":" + socket.getPort();
        new SocketThread(this, name, socket);
    }

    @Override
    public void onServerException(ServerSocketThread thread, Throwable exception) {
        exception.printStackTrace();
    }

    /*
    * Socket Thread methods
    */

    @Override
    public void onSocketStart(SocketThread thread, Socket socket) {
        putLog("Client connected");
    }

    @Override
    public void onSocketStop(SocketThread thread) {
        putLog("Socket thread stopped");
    }

    @Override
    public void onSocketReady(SocketThread thread, Socket socket) {
        putLog("Client is ready to chat");
    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String msg) {
        thread.sendMessage("Echo: " + msg);
    }

    @Override
    public void onSocketException(SocketThread thread, Exception exception) {
        exception.printStackTrace();
    }
}

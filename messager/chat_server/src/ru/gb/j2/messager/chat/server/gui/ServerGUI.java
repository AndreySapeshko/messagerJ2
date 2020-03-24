package ru.gb.j2.messager.chat.server.gui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import ru.gb.j2.messager.chat.server.core.ChatServer;

public class ServerGUI extends JFrame implements ActionListener, UncaughtExceptionHandler {
    private static final int POS_X = 1000;
    private static final int POS_Y = 550;
    private static final int HEIGHT = 100;
    private static final int WIDTH = 200;
    private final ChatServer chatServer = new ChatServer();
    private final JButton btnStart = new JButton("Start");
    private final JButton btnStop = new JButton("Stop");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ServerGUI();
            }
        });
        throw new RuntimeException("Hello from main");
    }

    private ServerGUI() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.setDefaultCloseOperation(3);
        this.setBounds(1000, 550, 200, 100);
        this.setResizable(false);
        this.setTitle("Chat server");
        this.setAlwaysOnTop(true);
        this.setLayout(new GridLayout(1, 2));
        this.btnStart.addActionListener(this);
        this.btnStop.addActionListener(this);
        this.add(this.btnStart);
        this.add(this.btnStop);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object srs = e.getSource();
        if (srs == this.btnStart) {
            this.chatServer.start(8189);
        } else if (srs == this.btnStop) {
//            throw new RuntimeException("Hello from EDT");
            chatServer.stop();
        } else {
            throw new RuntimeException("Unknown source: " + srs);
        }
    }

    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        StackTraceElement[] ste = e.getStackTrace();
        String var10000 = t.getName();
        String msg = "Exception in the thread " + var10000 + " " + e.getClass().getCanonicalName() + ": " + e.getMessage() + "\n\t" + ste[0];
        JOptionPane.showMessageDialog((Component)null, msg, "Exception", 0);
        System.exit(1);
    }
}

package ru.gb.j2.messager.chat.client;

import ru.gb.j2.messager.network.SocketThread;
import ru.gb.j2.messager.network.SocketThreadListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClientGUI extends JFrame implements ActionListener, UncaughtExceptionHandler, SocketThreadListener {
    private static final int WIDHT = 400;
    private static final int HEIGTH = 300;
    private final JTextArea log = new JTextArea();
    private final JPanel topPanel = new JPanel(new GridLayout(2, 3));
    private final JTextField tfIpAddress = new JTextField("127.0.0.1");
    private final JTextField tfPort = new JTextField("8189");
    private final JCheckBox cbAlwaysOnTop = new JCheckBox("Always on top");
    private final JTextField tfLogin = new JTextField("Ivan");
    private SocketThread socketThread;
    private final JPasswordField pfPassword = new JPasswordField("123");
    private final JButton btnLogin = new JButton("Login");
    private final JPanel bottomPanel = new JPanel(new BorderLayout());
    private final JButton btnDisconnect = new JButton("<html><b>Disconnect</b></html>");
    private final JTextField tfMesseg = new JTextField();
    private final JButton btnSend = new JButton("Send");
    private final JList<String> userList = new JList();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ClientGUI();
            }
        });
        throw new RuntimeException("Hello from main");
    }

    private ClientGUI() {
        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo((Component)null);
        this.setSize(new Dimension(400, 300));
        this.setResizable(false);
        this.setTitle("Chat client");
        String[] users = new String[]{"user1", "user2", "user3", "user4", "user5", "user_with_an_exceptionally_long_name_in_this_chat"};
        this.userList.setListData(users);
        this.log.setEditable(false);
        JScrollPane scrolLog = new JScrollPane(this.log);
        JScrollPane scrolUsers = new JScrollPane(this.userList);
        scrolUsers.setPreferredSize(new Dimension(100, 0));
        this.cbAlwaysOnTop.addActionListener(this);
        this.tfMesseg.addActionListener(this);
        this.btnSend.addActionListener(this);
        this.btnLogin.addActionListener(this);

        this.topPanel.add(this.tfIpAddress);
        this.topPanel.add(this.tfPort);
        this.topPanel.add(this.cbAlwaysOnTop);
        this.topPanel.add(this.tfLogin);
        this.topPanel.add(this.pfPassword);
        this.topPanel.add(this.btnLogin);
        this.bottomPanel.add(this.btnDisconnect, "West");
        this.bottomPanel.add(this.tfMesseg, "Center");
        this.bottomPanel.add(this.btnSend, "East");

        this.add(scrolLog, "Center");
        this.add(scrolUsers, "East");
        this.add(this.topPanel, "North");
        this.add(this.bottomPanel, "South");
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        Object srs = e.getSource();
        if (srs == this.cbAlwaysOnTop) {
            this.setAlwaysOnTop(this.cbAlwaysOnTop.isSelected());
        } else if (srs == this.tfMesseg || srs == this.btnSend) {
            sendMessage();
        } else if ( srs == btnLogin) {
            connect();
        }
        else
            throw new RuntimeException("Unknown source: " + srs);
    }

    private void connect() {
        try {
            Socket socket = new Socket(tfIpAddress.getText(), Integer.parseInt(tfPort.getText()));
            socketThread = new SocketThread(this, "Client", socket);
        } catch (IOException e) {
            showException(Thread.currentThread(), e);
        }
    }

    private void sendMessage() {
        String msg = tfMesseg.getText();
        String username = tfLogin.getText();
        if ("".equals(msg)) {
            return;
        }
        tfMesseg.setText(null);
        tfMesseg.grabFocus();
        socketThread.sendMessage(msg);
//        putLog(String.format("%s: %s", username, msg));
//        writeMsgToLogFile(msg, username);
    }

    private void writeMsgToLogFile(String msg, String username) {
        try (FileWriter fw = new FileWriter("chatlog.txt", true)) {
            fw.write(username + ": " + msg + "\n");
            fw.flush();
        } catch (IOException e) {
            showException(Thread.currentThread(), e);
        }
    }

    private void putLog(String msg) {
        if ("".equals(msg)) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }

    private void showException(Thread t, Throwable e) {
        String msg = "";
        StackTraceElement[] ste = e.getStackTrace();
        if (ste.length == 0) {
            msg = "Empti StackTrace";
        } else {
            msg = "Exception in " + t.getName() + " " +
                    e.getClass().getCanonicalName() + ": " + e.getMessage() + "\n\t" + ste[0];
        }
        JOptionPane.showMessageDialog((Component)null, msg, "Exception", 0);
    }

    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        showException(t, e);
        System.exit(1);
    }

    /*
    * Socket Thread methods
    * */

    @Override
    public void onSocketStart(SocketThread thread, Socket socket) {
        putLog("Socket thread started");
    }

    @Override
    public void onSocketStop(SocketThread thread) {
        putLog("Socket thread stopped");
    }

    @Override
    public void onSocketReady(SocketThread thread, Socket socket) {
        putLog("Server is ready to chat");
    }

    @Override
    public void onReceiveString(SocketThread thread, Socket socket, String msg) {
        putLog(msg);
    }

    @Override
    public void onSocketException(SocketThread thread, Exception exception) {
        showException(Thread.currentThread(), exception);
    }
}

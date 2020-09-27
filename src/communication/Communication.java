package communication;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Communication {

    private BufferedWriter writer;
    private BufferedReader reader;

    private static Socket socket = null;
    private static Communication comms = null;
    

    public static Communication getComms() {
        if (comms == null) {
            comms = new Communication();
        }
        return comms;
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public void openSocket() {
        System.out.println("Opening socket's connection...");

        try {
            // B8:27:EB:F0::59:DB
            String HOST = "192.168.21.21";
            int PORT = 65431;
            socket = new Socket(HOST, PORT);
            writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(socket.getOutputStream())));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Socket successfully opened.");
            return;

        } catch (UnknownHostException e) {
            System.out.println("Failed to open socket. UnkownHostException found.");
        } catch (IOException e) {
            System.out.println("Failed to open socket. IOException found.");
        } catch (Exception e) {
            System.out.println("Failed to open socket.");
        }
        System.out.println("Failed to establish connection.");
    }

    public void closeSocket() {
        System.out.println("Closing socket's connection...");

        try {
            reader.close();

            if (socket != null) {
                socket.close();
                socket = null;
            }
            System.out.println("Socket's Connection closed.");
        } catch (IOException e) {
            System.out.println("Failed to close socket. IOException found.");
        } catch (NullPointerException e) {
            System.out.println("Failed to close socket. NullPointerException found.");
        } catch (Exception e) {
            System.out.println("Failed to close socket.");
        }
    }

    public void testSendMessage(String message) {
        try {
            writer.write(message);
            writer.flush();
        }catch (IOException e) {
            System.out.println("IOException");
        }catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void testReceiveMessage() {
        System.out.println("Receiving a message...");

        try {
            StringBuilder sb = new StringBuilder();
            String input = reader.readLine();

            if (input != null && input.length() > 0) {
                sb.append(input);
                System.out.println(sb.toString());
                //return sb.toString();
            }
        } catch (IOException e) {
            System.out.println("IOException");
        } catch (Exception e) {
            System.out.println("Exception");
            System.out.println(e.toString());
        }

        //return null;

    }

    
    public void sendMessage(String message) {
        try {
            System.out.println("Send: " + message);
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            System.out.println("IOEXception in sending message.");
        } catch (Exception e) {
            System.out.println("Exception in sending message.");
            System.out.println(e.toString());
        }
    }

    public String receiveMessage() {
        System.out.println("Receiving a message...");
        try {
            StringBuilder sb = new StringBuilder();
            String input = reader.readLine();

            if (input != null && input.length() > 0) {
                sb.append(input);
                System.out.println("Received: " + sb.toString());
                return sb.toString();
            }
        } catch (IOException e) {
            System.out.println("IOEXception in receiving message.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception in sending message.");
            System.out.println(e.toString());
        }

        return null;
    }

}
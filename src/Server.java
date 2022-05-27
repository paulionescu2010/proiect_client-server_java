import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    //functie pentru pornirea serverului
    public void startServer() {
        try {
            // se așteaptă conectarea cliențiilor
            while (!serverSocket.isClosed()) { //cat timp socket-ul e inchis, nu se poate initia comunicarea
                Socket socket = serverSocket.accept(); //functie care face conectivitatea intre client, server si port
                System.out.println("S-a conectat un client nou!");
                Operator operator = new Operator(socket);
                Thread thread = new Thread(operator);
                thread.start(); //se incepe executia thread-ului nou creat
            }

        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}


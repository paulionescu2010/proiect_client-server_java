import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {

            close(socket, bufferedReader, bufferedWriter);
        }
    }

    public void close(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {

        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void trimiteMesaje() {
        try {
            bufferedWriter.write(username); // se scrie numele utilizatorului
            bufferedWriter.newLine();
            bufferedWriter.flush(); //se trimite mesajul la destinatie
            Scanner scanner = new Scanner(System.in); //se creeaza un scanner pentru a prelua mesajele de la tastatura
            while (socket.isConnected()) { // cat timp avem conexiunea cu serverul activa
                String mesajDeTrimis = scanner.nextLine(); //se citeste mesajul de la tastatura
                bufferedWriter.write(username + ": " + mesajDeTrimis);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            close(socket, bufferedReader, bufferedWriter);
        }
    }

    public void receptorMesaje() { //functie care asteapta sa primeasca mesaje de la conversatia de grup
        new Thread(() -> { //este nevoie de un thread nou, altfel programul s-ar bloca
            String conversatieGrup;
            while (socket.isConnected()) { // cat timp avem conexiunea cu serverul activa
                try {
                    conversatieGrup = bufferedReader.readLine();
                    System.out.println(conversatieGrup);
                } catch (IOException e) {
                    close(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }


    public static void main(String[] args) throws IOException {

        // Utilizatorul trebuie sa dea un nume de utilizator pentru conectare
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduceti un nume de utilizator: ");
        String nume = scanner.nextLine();
        // Creăm un socket pentru a ne conecta la server
        Socket socket = new Socket("localhost", 1234); //localhost = adresa masinii gazda
        // 1234 - port, folosit pentru transmisii de mesaje între computere
        //Initializăm clientul prin transmiterea socket-ului creat și numele de utilizatori ca parametri
        Client client = new Client(socket, nume);
        client.receptorMesaje();
        client.trimiteMesaje();
    }
}
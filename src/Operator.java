import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


public class Operator implements Runnable { //Clasa implementeaza interfata Runnable, care este responsabila de executia thread-urilor


    public static ArrayList<Operator> operatori = new ArrayList<>(); //lista unde vor fi adaugati operatorii care gestioneaza conexiunea dintre server si clienti


    private Socket socket; //socket pentru conexiune
    private BufferedReader cititor; //buffer pentru citirea mesajelor
    private BufferedWriter scriitor; //buffer pentru scrierea mesajelor
    private String usernameUtilizator; //username-ul utilizatorului


    public Operator(Socket socket) {  //Constructorul care initializeaza variabilele din clasa cu valori specifice
        try {
            this.socket = socket;
            this.cititor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.scriitor = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.usernameUtilizator = cititor.readLine(); //cand utilizatorul se conecteaza, este folosit username-ul acestuia
            operatori.add(this); //se adauga operatorul curent la lista
            mesajBroadcast("SERVER: " + usernameUtilizator + " a intrat in conversatie!");
        } catch (IOException e) {
            inchide(socket, cititor, scriitor);
        }
    }




    @Override
    public void run() {    // Fiecare operator din lista va crea un nou thread, asteptand mesaje de la utilizator, cat timp exista conexiune
        String mesajClient;

        while (socket.isConnected()) {
            try {
                mesajClient = cititor.readLine();
                mesajBroadcast(mesajClient);
            } catch (IOException e) {
                inchide(socket, cititor, scriitor);
                break;
            }
        }
    }

    public void mesajBroadcast(String mesajDeTrimis) { //se trimite un mesaj tuturor participantilor la conversatie care sunt diferiti de clientul care a trimis mesajul

        for (Operator operator : operatori) {
            try {
                if (!operator.usernameUtilizator.equals(usernameUtilizator)) {
                    operator.scriitor.write(mesajDeTrimis);
                    operator.scriitor.newLine();
                    operator.scriitor.flush();
                }
            } catch (IOException e) {
                inchide(socket, cititor, scriitor);
            }
        }
    }

    public void stergeOperator() {
        operatori.remove(this);
        mesajBroadcast("SERVER: " + usernameUtilizator + " a parasit conversatia!");
    }


    public void inchide(Socket socket, BufferedReader cititor, BufferedWriter scriitor) { //functie care inchide conexiunea dintre un client si server

        stergeOperator();
        try { // pentru inchiderea conexiunii si curatarea memoriei vom inchide pe rand stream-urile pentru citire si scriere si apoi socket-ul
            if (cititor != null) {
                cititor.close();
            }
            if (scriitor != null) {
                scriitor.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

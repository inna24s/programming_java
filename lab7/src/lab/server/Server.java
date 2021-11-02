package lab.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static int port = 8080;
    private static ServerSocket serverSocket;
    private static DataBaseHandler db;

    public static void main(String[] args) {

        if (args.length>0){
            try{port = Integer.parseInt(args[0]);}
            catch(NumberFormatException e){e.getLocalizedMessage();}
        }

        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException ignored) {}

        ManyHats manyHats = new ManyHats();


        try {
            serverSocket = new ServerSocket(port);
            db = new DataBaseHandler();
            System.out.println("ServerSocketСервер запущен и слушает порт " + port + "...");
            db.loadHats(manyHats);
        } catch (IOException e) {
            System.out.println("Ошибка создания серверного сокета (" + e.getLocalizedMessage() + "), приложение будет остановлено.");
            System.exit(1);


        }



        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(new RequestResolver(clientSocket, manyHats, db)).start();
            } catch (IOException e) {
            }
        }
    }



}
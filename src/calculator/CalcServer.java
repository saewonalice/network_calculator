package calculator;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalcServer {
    private static ExecutorService pool = Executors.newFixedThreadPool(10);

    // Process the client's request and return the result or an error message.
    public static String processRequest(String request) {
        StringTokenizer st = new StringTokenizer(request, " ");
        String responseType = st.nextToken();
        String result = "";

        try {
            switch (responseType.toUpperCase()) {
                case "ADD":
                    int sum = Integer.parseInt(st.nextToken()) + Integer.parseInt(st.nextToken());
                    result = "Answer:" + sum;
                    break;
                case "DIV":
                    int num1 = Integer.parseInt(st.nextToken());
                    int num2 = Integer.parseInt(st.nextToken());
                    if (num2 == 0) {
                        result = "Error:DividedByZero";
                    } else {
                        result = "Answer:" + (num1 / num2);
                    }
                    break;
                case "MUL":
                    int mul = Integer.parseInt(st.nextToken()) * Integer.parseInt(st.nextToken());
                    result = "Answer:" + mul;
                    break;
                case "SUB":
                    // Check the number of arguments
                    if (st.countTokens() != 2) {
                        result = "Error:IncorrectArguments";
                    } else {
                        int subtract = Integer.parseInt(st.nextToken()) - Integer.parseInt(st.nextToken());
                        result = "Answer:" + subtract;
                    }
                    break;
                default:
                    result = "Error:InvalidOperation";
            }
        } catch (NumberFormatException e) {
            // 오류 세부 정보를 기
            System.err.println("InvalidNumberFormat Error - Request: " + request);
            result = "Error:InvalidNumberFormat";
        }

        return result;
    }


    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(9999);
            System.out.println("Server started. Waiting for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");

                // Create a new thread to handle the client's request.
                Runnable worker = new WorkerThread(clientSocket);
                pool.execute(worker);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

// WorkerThread class handles the communication with a single client.
class WorkerThread implements Runnable {
    private Socket clientSocket;

    public WorkerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            while (true) {
                // Read the client's request.
                String inputRequest = in.readLine();
                if (inputRequest.equalsIgnoreCase("bye")) {
                    System.out.println("Client disconnected.");
                    break;
                }

                System.out.println("Received request: " + inputRequest);
                // Process the request and send the result back to the client.
                String result = CalcServer.processRequest(inputRequest);

                out.write(result + "\n");
                out.flush();
            }

            // Close the socket when done.
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

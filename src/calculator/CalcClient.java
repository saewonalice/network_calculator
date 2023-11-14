package calculator;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class CalcClient {
    public static void main(String[] args) {
        BufferedReader in = null;
        BufferedWriter out = null;
        Socket socket = null;
        Scanner scanner = new Scanner(System.in);

        try {
            // Server information
            String serverIP = "localhost";
            int serverPort = 9999;

            // Connect to the server
            socket = new Socket(serverIP, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            while (true) {
                // Prompt the user to enter an expression
                System.out.print("Enter expression (separated by space, e.g., 24 + 42) >> ");
                String outputExpression = scanner.nextLine();

                // Check if the user wants to exit
                if (outputExpression.equalsIgnoreCase("bye")) {
                    out.write(outputExpression + "\n");
                    out.flush();
                    break;
                }

                // Send the expression to the server
                out.write(outputExpression + "\n");
                out.flush();

                // Receive and display the result from the server
                String inputResult = in.readLine();
                System.out.println(inputResult);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                // Close resources when done
                scanner.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}

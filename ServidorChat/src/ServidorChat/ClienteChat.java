package ServidorChat;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClienteChat {
	
    public static void main(String[] args) throws IOException {
    	
        Socket cliente = new Socket("localhost", 7001); // Substitua "localhost" pelo endereço IP do servidor
        Thread threadReceber = new Thread(new ReceberMensagens(cliente));
        threadReceber.start();
        
        try {
            String mensagem;
            do {
                Scanner entrada = new Scanner(System.in);
                mensagem = entrada.nextLine();
                PrintStream saida = new PrintStream(cliente.getOutputStream());
                saida.println(mensagem);
            } while (!"sair".equals(mensagem));
        } finally {
            cliente.close();
        }
    }
    

    static class ReceberMensagens implements Runnable {
        private Socket cliente;

        public ReceberMensagens(Socket cliente) {
            this.cliente = cliente;
        }

        @Override
        public void run() {
            try {
                Scanner scanner = new Scanner(cliente.getInputStream());
                while (scanner.hasNextLine()) {
                    String mensagem = scanner.nextLine();
                    System.out.println("Mensagem recebida do servidor: " + mensagem);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

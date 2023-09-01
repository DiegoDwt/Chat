package ServidorChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServidorChat {
	
    private static ServidorChat instance;
    private ServerSocket servidor;
    private List<ClienteHandler> clientes;

    private ServidorChat() {
        clientes = new ArrayList<>();
        try {
            servidor = new ServerSocket(7001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized ServidorChat getInstance() {
        if (instance == null) {
            instance = new ServidorChat();
        }
        return instance;
    }

    public void iniciar() {
        while (true) {
            try {
                System.out.println("Aguardando conexões...");
                Socket conexao = servidor.accept();
                System.out.println("Novo cliente conectado: " + conexao.getInetAddress().getHostAddress());
                ClienteHandler clienteHandler = new ClienteHandler(conexao);
                clientes.add(clienteHandler);
                clienteHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void enviarMensagemParaTodos(String mensagem) {
        for (ClienteHandler clienteHandler : clientes) {
            clienteHandler.enviarMensagem(mensagem);
        }
    }

    public static void main(String[] args) {
        ServidorChat servidorChat = ServidorChat.getInstance();
        servidorChat.iniciar();
    }
}

class ClienteHandler extends Thread {
    private Socket conexao;
    private BufferedReader bufferedReader;

    public ClienteHandler(Socket conexao) {
        this.conexao = conexao;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String mensagem = bufferedReader.readLine();
                if (mensagem == null || mensagem.equals("sair")) {
                    conexao.close();
                    break;
                }
                System.out.println("Mensagem recebida de " + conexao.getInetAddress().getHostAddress() + ": " + mensagem);
                ServidorChat.getInstance().enviarMensagemParaTodos(mensagem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMensagem(String mensagem) {
        try {
            PrintStream saida = new PrintStream(conexao.getOutputStream());
            saida.println(mensagem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ufrgs.gppd.offload.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Guilherme
 */
public class Server {
    
    private Integer porta;
    private ServerSocket serverSocket;
    private ArrayList<ConnectionHandler> minhasConexoes;
    private String modelsFolder;

    public Server(Integer port) throws IOException {
        this.porta = port;
        minhasConexoes = new ArrayList<ConnectionHandler>();
        criaConexao();
    }

    private void criaConexao() throws IOException {
        //Cria o socket servidor para receber conexÃµes
        serverSocket = new ServerSocket(porta);
    }


    /*
     * Fica permanentemente esperando a chegada de novas conexÃµes.
     * Quando chega uma, trata criando uma nova thread para esta.
     */
    public void aguardaConexoes() throws IOException {

        Socket socket;

        while (true) { //mantém o servidor vivo

            System.out.println("Waiting a client connection");
            //aguarda uma conexÃ£o de um cliente
            socket = serverSocket.accept();

            ConnectionHandler newConn = new ConnectionHandler(socket, this);
            minhasConexoes.add(newConn);
            newConn.setModelsFolder(modelsFolder);
            newConn.start();

        }
    }

    public void setModelsFolder(String modelsFolder) {
        this.modelsFolder = modelsFolder;
    }
    
}

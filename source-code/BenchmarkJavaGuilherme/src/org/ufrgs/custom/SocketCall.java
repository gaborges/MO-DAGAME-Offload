/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ufrgs.custom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guilherme
 */
public class SocketCall {
     private String host;
    private Integer port;
    private Boolean estaLigado;

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public SocketCall(String host, Integer port) {
        this.host = host;
        this.port = port;
        createConnection();
        //trataMenssagens();
    }
    public String offload(String content) throws SocketException{
        socket.setSoTimeout(30000);
        sendMessage(content);
        return receiveMessage();
    }

    private void createConnection() {
        try {
            //cria o socket para se conectar ao servidor
            socket = new Socket(host, port);
            //cria stream de saÃ­da
            outputStream = new DataOutputStream(socket.getOutputStream());
            //cria stream de entrada
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (UnknownHostException ex) {

            System.out.println("[ERRO] Invalid host!");
            Logger.getLogger(SocketCall.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("[ERRO] Communication problem!");
            Logger.getLogger(SocketCall.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void sendMessage(String menssage) {
        try {
            //envia mensagem
            outputStream.writeUTF(menssage);
            outputStream.flush();
        } catch (IOException ex) {
            System.out.println("[ERRO] Problema no envio de mensagem = " + menssage);

            Logger.getLogger(SocketCall.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String receiveMessage() {
        try {

            //aguarda retorno do servidor
            return inputStream.readUTF();

        } catch (IOException ex) {
            System.out.println("[ERRO] Erro ao receber mensagem!");
            Logger.getLogger(SocketCall.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public void closeConnection() throws IOException{
        socket.close();
    }
    
}

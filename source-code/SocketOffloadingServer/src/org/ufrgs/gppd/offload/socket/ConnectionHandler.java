/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ufrgs.gppd.offload.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.util.JMException;
import org.json.simple.parser.ParseException;
import org.ufrgs.gppd.offload.OffloadUtils;

/**
 *
 * @author Guilherme
 */
public class ConnectionHandler extends Thread {

    Socket clientSocket;
    Server server;
    DataOutputStream outputStream;
    DataInputStream inputStream;
    OffloadUtils utils;
    String modelsFolder;

    ConnectionHandler(Socket socket, Server server) throws IOException {
        this.server = server;
        this.clientSocket = socket;
        outputStream = new DataOutputStream(socket.getOutputStream());
        inputStream = new DataInputStream(socket.getInputStream());
        utils = new OffloadUtils();
    }

    @Override
    public void run() {

        try {
            String content = receiveMessage();
            //System.out.println("Mensagem recebida: " + content);

            String responseContent = "";
            utils.setModelsFolder(modelsFolder);
            responseContent = utils.doOptimizationProcess(content);

            sendMessage(responseContent);
            clientSocket.close();
        } catch (JMException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void sendMessage(String message) {
        try {
            //envia mensagem
            outputStream.writeUTF(message);
            outputStream.flush();
        } catch (IOException ex) {
            System.out.println("[ERRO] Problema no envio de mensagem = " + message + " - " + ex.getLocalizedMessage());
        }
    }

    private String receiveMessage() {
        try {

            //aguarda retorno do servidor
            return inputStream.readUTF();

        } catch (IOException ex) {
            System.out.println("[ERRO] Erro ao receber mensagem! " + ex);
            return null;
        }

    }

    void setModelsFolder(String modelsFolder) {
        this.modelsFolder = modelsFolder;
    }
}

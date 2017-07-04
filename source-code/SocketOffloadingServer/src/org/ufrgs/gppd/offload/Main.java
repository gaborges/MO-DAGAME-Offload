/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ufrgs.gppd.offload;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ufrgs.gppd.offload.socket.Server;

/**
 *
 * @author Guilherme
 */
public class Main {
    public static void main(String[] args) {
        try {
            
            
            if (args.length < 1) {
                System.out.println("INFORME: modelsFolder");
                System.exit(0);
           } 
            
            //args[0] = "5001";
            // Integer porta = Integer.parseInt(args[0]);
            System.out.println(""+args[0]);
            Server server = new Server(5001);
            server.setModelsFolder(args[0]);
            if(args.length == 2){
               OffloadUtils.setDebug(Boolean.parseBoolean(args[1]));
            }
            server.aguardaConexoes();


        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

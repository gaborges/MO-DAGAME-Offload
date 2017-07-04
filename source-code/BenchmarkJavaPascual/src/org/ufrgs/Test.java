/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ufrgs;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Guilherme
 */
public class Test {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        String xmlFile="file://C:\\Users\\Guilherme\\Dropbox\\Tese\\Primeiro Artigo\\feature_models_test\\H-mobile_guide.xml";
        System.out.println("ssss "+xmlFile);
        xmlFile="C:\\Users\\Guilherme\\Dropbox\\Tese\\Primeiro Artigo\\feature_models_test\\H-mobile_guide.xml";
        Document doc = db.parse(xmlFile);
    }
}

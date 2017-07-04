/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ufrgs.gppd.offload;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import jmetal.core.Solution;
import jmetal.util.JMException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.ufrgs.gppd.offload.custom.OffloadingExperiment;
import org.uma.mo_dagame.algorithm.jmetalcustomization.MoDagameAlgorithm;
import org.uma.mo_dagame.algorithm.jmetalcustomization.MoDagameAlgorithmResult;

/**
 *
 * @author Guilherme
 */
public class OffloadUtils {
    
    public static boolean debug = false;
    private String modelsFolder ="";
    private static final String FILE_SEPARATOR = File.separator;
    // avalilable feature models
    private static String[] featureModelFileNames = null;
    
    public void setModelsFolder(String modelsFolder) {
        this.modelsFolder = modelsFolder;
    }

    public static void setDebug(boolean debug) {
        OffloadUtils.debug = debug;
    }
    
    private static HashMap<String,String> map = new HashMap<String,String>();

    public static HashMap<String, String> getMap() {
        return map;
    }
    
    public String doOptimizationProcess(String content) throws JMException, ClassNotFoundException, ParseException{
        long startTime = System.nanoTime();
        // Static load 
        OffloadingExperiment exp = new OffloadingExperiment();
        exp.independentRuns_ = 1;
        if(map.isEmpty()){
            map.put("x264ProductLine", "A-x264");
            map.put("WgetProductLine", "B-Wget");
            map.put("BerkeleyDBMemoryversionProductLine", "C-BerkeleyDBMemory");
            map.put("SensorNetworkProductLine", "D-SensorNetwork");
            map.put("StrategyMobileGame", "E-mobile_game");
            map.put("TankWar", "F-TankWar");
            map.put("MobileMedia2", "G-mobile_media2");
            map.put("MobileGuide", "H-mobile_guide");
            map.put("SPLOT-3CNF-FM-500-50-1.00-SAT-1", "I-SPLOT-3CNF-FM-500-50-1.00-SAT-1");
        }
        exp.algorithmNameList_ =
                new String[]{"MoDagameNSGAII", "MoDagameIBEA", "MoDagameMOCHC", "MoDagameMOCell",
                        "MoDagamePAES", "MoDagameSPEA2"};
        MoDagameAlgorithm[] algorithm; // jMetal algorithms to be executed
        algorithm = new MoDagameAlgorithm[exp.algorithmNameList_.length];
        JSONObject jsonObject;
        //Cria o parse de tratamento
        org.json.simple.parser.JSONParser parser = new JSONParser();
        
        jsonObject=(JSONObject) parser.parse(content);

        // parameters
        JSONArray array = (JSONArray) jsonObject.get("seed");
        boolean[] inputFeatureModelconfig = new boolean[array.size()]; // configuration
        for(int i = 0; array.size() > i ; i++){
            inputFeatureModelconfig[i] = (Boolean)array.get(i);
        }
        exp.setInputFeatureModelConfiguration(inputFeatureModelconfig);
        int algorithmId = Integer.parseInt(jsonObject.get("algorithm").toString());
        int problemId = Integer.parseInt(jsonObject.get("problemId").toString());
        int maxEvaluations = 0;
        if(jsonObject.get("maxEvaluations")!=null){
            maxEvaluations=Integer.parseInt(jsonObject.get("maxEvaluations").toString());
        }
        int populationSize = 0;
        if(jsonObject.get("populationSize")!=null){
            populationSize = Integer.parseInt(jsonObject.get("populationSize").toString());
        }
        exp.experimentName_ = (String) jsonObject.get("experimentName");// feature model experiment name
        
        exp.experimentBaseDirectory_ = modelsFolder;
        if(exp.experimentBaseDirectory_.equals("")){
            exp.experimentBaseDirectory_= "/home"+File.separator+"guilherme"+File.separator+"feature_models";
        }
        //C:\Users\Guilherme\Dropbox\Tese\Primeiro Artigo\feature_models_test
        // carrega os problemas de modelos de features, se elas não foram carregadas
   
        if(featureModelFileNames == null){
            //File modelsFolder = new File("/home/guilherme/feature_models");
            File modelsFolder = new File(exp.experimentBaseDirectory_);
            featureModelFileNames = modelsFolder.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".xml");
                }

            });
            for (int i = 0; i < featureModelFileNames.length; i++) {
                featureModelFileNames[i] = featureModelFileNames[i].substring(0, featureModelFileNames[i].lastIndexOf("."));
            }
        }
        exp.problemList_ = featureModelFileNames;
        exp.indicatorList_ = new String[]{"HV", "GD", "TIME"};

        // Generate Pareto front automatically
        exp.paretoFrontFile_ = new String[exp.problemList_.length];
        exp.paretoFrontDirectory_ = "";
        
        exp.initExperiment();
        
        //TODO return proper representation object
        exp.algorithmSettings(exp.experimentName_, problemId, algorithm);
        
        if(debug){
            System.out.println("Problem: "+problemId+" name: "+exp.experimentName_+" algorithm:"+algorithmId+" pop: "+populationSize+" maxEv:"+maxEvaluations);
        }
        MoDagameAlgorithmResult result = exp.executeExperiment(algorithmId,problemId,algorithm,populationSize,maxEvaluations);
        JSONObject response = new JSONObject();
        response.put("fixTime", result.getFixOperatorTime());
        response.put("frontSize", result.getFinalFront().size());
        response.put("timeToCopyInitialFront", result.getTimeToCopyInitialFront());
        generateResults(result,response);
        response.put("remoteExecutionTime",System.nanoTime() - startTime);
        return response.toJSONString();
        
    }
    
    public JSONObject generateResults(MoDagameAlgorithmResult result,JSONObject root){
        JSONArray array = new JSONArray();
        String values = "";
        // variable values of feasible solutions into a file
        if (result.getFinalFront().size()>0) {
            int numberOfVariables = result.getFinalFront().get(0).getDecisionVariables().length ;
            for (int i = 0; i < result.getFinalFront().size(); i++) {
                Solution aSolutionsList_ = result.getFinalFront().get(i);
                values = "";
                if (aSolutionsList_.getOverallConstraintViolation() == 0.0) {
                    for (int j = 0; j < numberOfVariables; j++)
                        values = values + aSolutionsList_.getDecisionVariables()[j].toString();
                }
                array.add(values);
            }
            root.put("var", array);
        }
//        array = new JSONArray();
        // the function values of feasible solutions into a file
//        for (int i = 0; i < result.getFinalFront().size(); i++) {
//            
//            Solution aSolutionsList_ = result.getFinalFront().get(i);
//            if (aSolutionsList_.getOverallConstraintViolation() == 0.0) {
//                array.add(aSolutionsList_.getDecisionVariables()[i].toString());
//            }
//        }
//        root.put("fun", array);
        array = new JSONArray();
        // Returns a string representing the solution.
        for (int i = 0; i < result.getFinalFront().size(); i++) {
            Solution aSolutionsList_ = result.getFinalFront().get(i);
            array.add(aSolutionsList_.toString());
        }
//        root.put("solution", array);
        root.put("fun", array);
        
        //
        
        return root;
    }
    
    
    
}

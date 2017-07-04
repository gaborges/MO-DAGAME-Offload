/*
 * Copyright 2017 Guilherme Antonio Borges
 *
 * This file is part of MO-DAGAME
 * *
 * MO-DAGAME is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MO-DAGAME is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MO-DAGAME.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.ufrgs.custom;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.core.SolutionSet;
import jmetal.experiments.Settings;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.uma.mo_dagame.algorithm.jmetalcustomization.MoDagameAlgorithm;
import org.uma.mo_dagame.algorithm.jmetalcustomization.MoDagameAlgorithmResult;

/**
 * Class implementing the steps to run an experiment
 */
public class RunExperiment extends Thread {

    public Experiment experiment_;
    public int id_;
    public HashMap<String, Object> map_;
    public int numberOfThreads_;
    public int numberOfProblems_;

    // result to be sent to user
    SolutionSet resultFront = null;

    // Inicio modificación planificación Threads
    static boolean finished;
    // Fin modificación planificación Threads

    String experimentName_;
    String[] algorithmNameList_; // List of the names of the algorithms to be executed
    String[] problemList_; // List of problems to be solved
    String[] paretoFrontFile_; // List of the files containing the pareto fronts
    // corresponding to the problems in problemList_
    String[] indicatorList_; // List of the quality indicators to be applied
    String experimentBaseDirectory_; // Directory to store the results
    String latexDirectory_; // Directory to store the latex files
    String rDirectory_; // Directory to store the generated R scripts
    String paretoFrontDirectory_; // Directory containing the Pareto front files
    String outputParetoFrontFile_; // Name of the file containing the output
    String outputTimeFile_; // Name of the file containing the execution time output
    String outputFixTimeFile_;
    String outputFrontSizeFile_;
    // Pareto front
    String outputParetoSetFile_; // Name of the file containing the output
    // Pareto set
    int independentRuns_; // Number of independent runs per algorithm
    boolean saveFirstFront_;
    Settings[] algorithmSettings_; // Paremeter experiments.settings of each algorithm

    public RunExperiment(Experiment experiment, HashMap<String, Object> map, int id,
            int numberOfThreads, int numberOfProblems, boolean saveFirstFront) {
        experiment_ = experiment;
        id_ = id;
        map_ = map;
        numberOfThreads_ = numberOfThreads;
        numberOfProblems_ = numberOfProblems;
        saveFirstFront_ = saveFirstFront;

        // Inicio modificación planificación Threads
        finished = false;
        // Fin modificación planificación Threads
    }

    @Override
    public void run() {
        MoDagameAlgorithm[] algorithm; // jMetal algorithms to be executed

        String experimentName = (String) map_.get("experimentName");
        experimentBaseDirectory_ = (String) map_.get("experimentDirectory");
        algorithmNameList_ = (String[]) map_.get("algorithmNameList");
        problemList_ = (String[]) map_.get("problemList");
        indicatorList_ = (String[]) map_.get("indicatorList");
        paretoFrontDirectory_ = (String) map_.get("paretoFrontDirectory");
        paretoFrontFile_ = (String[]) map_.get("paretoFrontFile");
        independentRuns_ = (Integer) map_.get("independentRuns");
        outputParetoFrontFile_ = (String) map_.get("outputParetoFrontFile");
        outputParetoSetFile_ = (String) map_.get("outputParetoSetFile");
        outputTimeFile_ = (String) map_.get("outputTimeFile");
        outputFixTimeFile_ = (String) map_.get("outputFixTimeFile");
        outputFrontSizeFile_ = (String) map_.get("outputFrontSizeFile");

        int numberOfAlgorithms = algorithmNameList_.length;

        algorithm = new MoDagameAlgorithm[numberOfAlgorithms];

        File experimentDirectory;
        String directory;
        long executionTime = -1;
        int frontSize = 0;
        MoDagameAlgorithmResult result = null;

        // Threads scheduling [start]
        int[] problemData; // Contains current problemId, algorithmId and iRun

        while (!finished) {

            problemData = experiment_.getNextProblem();

            if (!finished && problemData != null) {
                int problemId = problemData[0];
                int alg = problemData[1];
                int runs = problemData[2];

                // The problem to solve
                String problemName;

                // STEP 2: get the problem from the list
                problemName = problemList_[problemId];
                // do the offloading checking Here
                // if the conditions are true, than skipe all steps until step 8
                //// ---- Verify if do offloading or not
                boolean offload = Experiment.isConditionsToOffloadSatisfied();
                // if so, send the parameters to the server
                if (offload) {
                    // STEP 6: create output directories
                    directory = experimentBaseDirectory_ + "/data/" + algorithmNameList_[alg] + ((offload) ? Experiment.OFFLOAD_SUFIX : "")
                            + "/" +problemList_[problemId];

                    experimentDirectory = new File(directory);

                    if (!experimentDirectory.exists()) {
                        boolean resultDir = new File(directory).mkdirs();
                        System.out.println("Creating " + directory);
                    }

                    // do offload
                    // pepare parameters
                    JSONObject sendContent = new JSONObject(), responseContent = null;
                    JSONArray arrayContent = new JSONArray();
                    // seed new boolean[array.size()]
                    //algorithm[alg].getProblem().
                    for (boolean b : experiment_.getInitialConfig().getConfiguration()) {
                        arrayContent.add(b);
                    }
                    sendContent.put("seed", arrayContent);
                    // algorithm == alg
                    sendContent.put("algorithm", map_.get("algorithm_"));
                    // problemId
                    sendContent.put("problemId", problemId);
                    // experimentName
                    sendContent.put("experimentName", problemName);
                    // populationSize
                    sendContent.put("populationSize", map_.get("populationSize"));
                    // maxEvaluations
                    sendContent.put("maxEvaluations", map_.get("maxEvaluations"));
                    
                    long startTime = System.nanoTime();
                    // do the call                
                    // receive result
                    //HTTPCall h = new HTTPCall();
                    String stringResponse = null;
                    SocketCall s = new SocketCall(experiment_.getOffloadServerAddress(), experiment_.getOffloadServerPort());
                    long timeToCopyInitialFront = 0, remoteExecutionTime = 0;
                    try {
                        System.out.println("Json Content: " + sendContent.toJSONString());
                        //stringResponse = h.offload("http://localhost:8084/ServerOffloadingGuilherme/webresources/offloadService", sendContent.toJSONString());                
                        stringResponse = s.offload(sendContent.toJSONString());
                        s.closeConnection();
                    } catch (IOException ex) {
                        Logger.getLogger(RunExperiment.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //result = null;//algorithm[alg].advancedExecute(saveFirstFront_);
                    executionTime = System.nanoTime() - startTime;// - result.getTimeToCopyInitialFront();
                    if (stringResponse != null) {
                        System.out.println("Found a solution: " + stringResponse);
                        JSONParser parser = new JSONParser();
                        try {
                            responseContent = (JSONObject) parser.parse(stringResponse);
                        } catch (ParseException ex) {
                            Logger.getLogger(RunExperiment.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        if (responseContent != null) {
                            timeToCopyInitialFront = Long.parseLong(responseContent.get("timeToCopyInitialFront").toString());
                            executionTime = executionTime - timeToCopyInitialFront;
                            remoteExecutionTime = Long.parseLong(responseContent.get("remoteExecutionTime").toString());

                            // STEP 8: put the results in the output directory
                            printExecutionTimeToFile(executionTime / 1000000.0,
                                    directory + "/" + outputTimeFile_ + "." + runs);
                            printExecutionTimeToFile((Long.parseLong(responseContent.get("fixTime").toString())) / 1000000.0,
                                    directory + "/" + outputFixTimeFile_ + "." + runs);
                            printFrontSizeToFile((Integer.parseInt(responseContent.get("frontSize").toString())),
                                    directory + "/" + outputFrontSizeFile_ + "." + runs);
                            printExecutionTimeToFile((remoteExecutionTime-timeToCopyInitialFront) / 1000000.0,
                                    directory + "/" + outputTimeFile_ + ".REMOTE." + runs);
                            printExecutionTimeToFile((executionTime-remoteExecutionTime) / 1000000.0,
                                    directory + "/" + outputTimeFile_ + ".LATENCY." + runs);
                            //if (saveFirstFront_) {
                            //    result.getInitialFront().printObjectivesToFile(
                            //            directory + "/" + outputParetoFrontFile_ + "_INITIAL." + runs);
                            //}
                            arrayContent = (JSONArray) responseContent.get("fun");
                            printObjectivesToFile(arrayContent, directory + "/" + outputParetoFrontFile_ + "." + runs);
                            //result.getFinalFront().printObjectivesToFile(
                            //        directory + "/" + outputParetoFrontFile_ + "." + runs);
                            arrayContent = (JSONArray) responseContent.get("var");
                            printVariablesToFile(arrayContent, directory + "/" + outputParetoSetFile_ + "." + runs);
                            //result.getFinalFront()
                            //        .printVariablesToFile(directory + "/" + outputParetoSetFile_ + "." + runs);
                        }
                    } else {
                        System.out.println("Offload failed!");
                    }
                    //frontSize = result.getFinalFront().size();
                    //if (frontSize == 0) {
                    //    System.out.println("No solution found - " + problemName + " [" +
                    //            algorithmNameList_[alg] + "]");
                    //} else {
                    //    System.out.println("Found a solution: "+);
                    //}
                    System.out.println("Execution time: " + executionTime + " ns = " + (executionTime / 1000000.0) +" ms");
                    if (!finished) {
                        if (experiment_.finished_) {
                            finished = true;
                            //resultFront = result.getFinalFront();
                        }
                    }
                } else {

                    // STEP 3: check the file containing the Pareto front of the problem
                    // STEP 4: configure the algorithms
                    try {
                        experiment_.algorithmSettings(problemName, problemId, algorithm);
                    } catch (ClassNotFoundException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    // STEP 5: run the algorithms
                    // STEP 6: create output directories
                    directory = experimentBaseDirectory_ + "/data/" + algorithmNameList_[alg] + "/"
                            + problemList_[problemId];

                    experimentDirectory = new File(directory);
                    if (!experimentDirectory.exists()) {
                        boolean resultDir = new File(directory).mkdirs();
                        System.out.println("Creating " + directory);
                    }

                    // STEP 7: run the algorithm
                    System.out.println(Thread.currentThread().getName() + " Running algorithm: "
                            + algorithmNameList_[alg]
                            + ", problem: " + problemList_[problemId]
                            + ", run: " + runs);

                    try {
                        try {
                            long startTime = System.nanoTime();
                            result = algorithm[alg].advancedExecute(saveFirstFront_);
                            executionTime
                                    = System.nanoTime() - startTime - result.getTimeToCopyInitialFront();
                            frontSize = result.getFinalFront().size();
                            if (frontSize == 0) {
                                System.out.println("No solution found - " + problemName + " ["
                                        + algorithmNameList_[alg] + "]");
                            }
                        } catch (ClassNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } catch (JMException ex) {
                        Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    // STEP 8: put the results in the output directory
                    printExecutionTimeToFile(executionTime / 1000000.0,
                            directory + "/" + outputTimeFile_ + "." + runs);
                    printExecutionTimeToFile(result.getFixOperatorTime() / 1000000.0,
                            directory + "/" + outputFixTimeFile_ + "." + runs);
                    printFrontSizeToFile(frontSize,
                            directory + "/" + outputFrontSizeFile_ + "." + runs);
                    if (saveFirstFront_) {
                        result.getInitialFront().printObjectivesToFile(
                                directory + "/" + outputParetoFrontFile_ + "_INITIAL." + runs);
                    }
                    result.getFinalFront().printObjectivesToFile(
                            directory + "/" + outputParetoFrontFile_ + "." + runs);
                    result.getFinalFront()
                            .printVariablesToFile(directory + "/" + outputParetoSetFile_ + "." + runs);
                    if (!finished) {
                        if (experiment_.finished_) {
                            finished = true;
                            resultFront = result.getFinalFront();
                        }
                    }
                } // offload if
            } // if
        } //while
        // Threads scheduling [end]
    }

    private void printExecutionTimeToFile(double time, String file) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.print(time);
            writer.close();
        } catch (IOException e) {
            Configuration.logger_.severe("Error acceding to the file");
            e.printStackTrace();
        }
    }

    private void printFrontSizeToFile(int success, String file) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            writer.print(success);
            writer.close();
        } catch (IOException e) {
            Configuration.logger_.severe("Error acceding to the file");
            e.printStackTrace();
        }
    }

    public SolutionSet getResultFront() {
        return resultFront;
    }

    private void printObjectivesToFile(JSONArray arrayContent, String path) {
        try {
            /* Open the file */
            FileOutputStream fos = new FileOutputStream(path);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);

            for (int i = 0; i < arrayContent.size(); i++) {
                //if (this.vector[i].getFitness()<1.0) {
                bw.write(arrayContent.get(i).toString());
                bw.newLine();
                //}
            }

            /* Close the file */
            bw.close();
        } catch (IOException e) {
            Configuration.logger_.severe("Error acceding to the file");
            e.printStackTrace();
        }
    }

    private void printVariablesToFile(JSONArray arrayContent, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);

            if (arrayContent.size() > 0) {
                int numberOfVariables = arrayContent.get(0).toString().length();
                for (int i = 0; i < arrayContent.size(); i++) {
                    String line = arrayContent.get(i).toString();
                    for (int j = 0; j < numberOfVariables; j++) {
                        bw.write(line.charAt(j) + " ");
                    }
                    bw.newLine();
                }
            }
            bw.close();
        } catch (IOException e) {
            Configuration.logger_.severe("Error acceding to the file");
            e.printStackTrace();
        }

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ufrgs.gppd.offload.custom;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Algorithm;
import jmetal.util.JMException;
import org.ufrgs.gppd.offload.OffloadUtils;
import org.uma.mo_dagame.algorithm.jmetalcustomization.Experiment;
import org.uma.mo_dagame.algorithm.jmetalcustomization.MoDagameAlgorithm;
import org.uma.mo_dagame.algorithm.jmetalcustomization.MoDagameAlgorithmResult;
import org.uma.mo_dagame.algorithm.main.MoDagameStudy;
import org.uma.mo_dagame.algorithm.settings.MoDagameIBEAsettings;
import org.uma.mo_dagame.algorithm.settings.MoDagameMOCELLsettings;
import org.uma.mo_dagame.algorithm.settings.MoDagameMOCHCsettings;
import org.uma.mo_dagame.algorithm.settings.MoDagameNSGAIIsettings;
import org.uma.mo_dagame.algorithm.settings.MoDagamePAESsettings;
import org.uma.mo_dagame.algorithm.settings.MoDagameSPEA2settings;
import org.uma.mo_dagame.feature_models.Configuration;
import org.uma.mo_dagame.feature_models.ConfigurationParser;
import org.uma.mo_dagame.feature_models.FeatureModel;
import org.uma.mo_dagame.feature_models.ObjectivesValuesParser;
import org.uma.mo_dagame.feature_models.SxfmParser;

/**
 *
 * @author Guilherme
 */
public class OffloadingExperiment extends Experiment {

    private static final String FILE_SEPARATOR = File.separator;
    private static final int NUMBER_OF_THREADS = 1;
    private boolean[] inputFeatureModelConfiguration = null;
    
    @Override
    public void algorithmSettings(String problemName, int problemId, Algorithm[] algorithm) throws ClassNotFoundException {
        try {
            int numberOfAlgorithms = algorithmNameList_.length;

            HashMap[] parameters = new HashMap[numberOfAlgorithms];
            
            //problemList_ =  java.util.Arrays.sort(problemList_); 
            java.util.Arrays.sort(problemList_, new java.util.Comparator() {
			public int compare(Object o1, Object o2) {
				String a = (String) o1;
				String b = (String) o2;
				return a.compareTo(b);
			}
		});
            for (int i = 0; i < numberOfAlgorithms; i++) {
                parameters[i] = new HashMap();
            } // for

//            if (paretoFrontFile_[problemIndex] != null &&
//                    !paretoFrontFile_[problemIndex].equals("")) {
//                for (int i = 0; i < numberOfAlgorithms; i++) {
//                    parameters[i].put("paretoFrontFile_", paretoFrontFile_[problemIndex]);
//                }
//            }

//            String modelFile = experimentBaseDirectory_ + FILE_SEPARATOR + "models" +
//                    FILE_SEPARATOR + problemName + ".xml";
//            String seedFile = experimentBaseDirectory_ + FILE_SEPARATOR + "seeds" + FILE_SEPARATOR +
//                    problemName + ".csv";
//            String objFile = experimentBaseDirectory_ + FILE_SEPARATOR + "models" +
//                    FILE_SEPARATOR + problemName + ".obj";
            
            String modelFile;
            String seedFile;
            String objFile;
            if(OffloadUtils.getMap().get(experimentName_)!= null){
                modelFile = experimentBaseDirectory_ + FILE_SEPARATOR + OffloadUtils.getMap().get(experimentName_)+ ".xml";
                seedFile = experimentBaseDirectory_ + FILE_SEPARATOR + OffloadUtils.getMap().get(experimentName_)+ ".csv";
                objFile = experimentBaseDirectory_ + FILE_SEPARATOR + OffloadUtils.getMap().get(experimentName_)+ ".obj";
            } else {
                modelFile = experimentBaseDirectory_ + FILE_SEPARATOR + problemList_[problemId]+".xml";
                seedFile = experimentBaseDirectory_ + FILE_SEPARATOR + problemList_[problemId]+ ".csv";
                objFile = experimentBaseDirectory_ + FILE_SEPARATOR + problemList_[problemId]+ ".obj";
            }
            
            /*
            System.out.println("Experiment: Name: " + experimentName_);
            System.out.println("Experiment: Number of algorithms: " + algorithmNameList_.length) ;
            System.out.println("Experiment: Number of problems: " + problemList_.length);
            System.out.println("Experiment: Experiment directory: " + experimentBaseDirectory_);
            System.out.println("Experiment: Problem: " + problemList_[problemId]);
            System.out.println("ProblemID:"+problemId);
            System.out.println("FileByHash:"+OffloadUtils.getMap().get(experimentName_));
            System.out.println(modelFile);
            System.out.println("");
            */
            Configuration seed;
            FeatureModel fm = SxfmParser.parse(modelFile);
            ObjectivesValuesParser.parse(objFile, fm);
            if (getInputFeatureModelConfiguration() == null){
                seed = ConfigurationParser.parse(seedFile, fm);
            } else {
                seed = new Configuration(fm,getInputFeatureModelConfiguration());
            }
            
            algorithm[0] = new MoDagameNSGAIIsettings(problemName, fm, seed).configure(parameters[0]);
            algorithm[1] = new MoDagameIBEAsettings(problemName, fm, seed).configure(parameters[1]);
            algorithm[2] = new MoDagameMOCHCsettings(problemName, fm, seed).configure(parameters[2]);
            algorithm[3] = new MoDagameMOCELLsettings(problemName, fm, seed).configure(parameters[3]);
            algorithm[4] = new MoDagamePAESsettings(problemName, fm, seed).configure(parameters[4]);
            algorithm[5] = new MoDagameSPEA2settings(problemName, fm, seed).configure(parameters[5]);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MoDagameStudy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MoDagameStudy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMException ex) {
            Logger.getLogger(MoDagameStudy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MoDagameStudy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setInputFeatureModelConfiguration(boolean[] seed) {
        this.inputFeatureModelConfiguration = seed;
    }

    public boolean[] getInputFeatureModelConfiguration() {
        return inputFeatureModelConfiguration;
    }

    public MoDagameAlgorithmResult executeExperiment(int algorithmId, int problemId, MoDagameAlgorithm[] algorithm,int populationSize,int maxEvaluations) throws JMException, ClassNotFoundException {
        boolean saveFirstFront_ = true;
        MoDagameAlgorithmResult result = null;
        // STEP 7: run the algorithm
//        System.out.println(Thread.currentThread().getName() + " Running algorithm: " +
//                algorithmNameList_[algorithmId] +
//                ", problem: " + problemList_[problemId]);
//        System.out.println("algs: "+Arrays.toString(algorithm));
//        System.out.println("alg: "+algorithmId);
        if(populationSize != 0){
            algorithm[algorithmId].setInputParameter("populationSize", populationSize);
        }
        if(maxEvaluations != 0){
            algorithm[algorithmId].setInputParameter("maxEvaluations", maxEvaluations);
        }
        result = algorithm[algorithmId].advancedExecute(saveFirstFront_);
        return result;
    }
    
}

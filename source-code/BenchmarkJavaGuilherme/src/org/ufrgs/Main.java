/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ufrgs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.uma.mo_dagame.benchmark.BenchmarkApplication;
import org.uma.mo_dagame.benchmark.data.FeatureModelBundle;
import org.uma.mo_dagame.benchmark.data.Preferences;
import org.uma.mo_dagame.benchmark.util.BenchmarkExecutor;
import org.uma.mo_dagame.benchmark.util.FeatureModelsLoader;

/**
 *
 * @author Guilherme
 */
public class Main {

    private static final String[] ALGORITHM_NAME =
            {"MoDagameNSGAII", "MoDagameIBEA", "MoDagameMOCHC", "MoDagameMOCell", "MODagamePAES",
                    "MoDagameSPEA2"};
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Starting experiment: " + (new Date()).getTime());
        System.out.println("Starting experiment: " 
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()));
        
        System.out.println("Starting configurations step");
        // carregar configuração
        Preferences.setBaseAlgorithm(4); // cada referente a uma posição em: ALGORITHM_NAME
        Preferences.setIndependentRuns(10);
        Preferences.setMaximumNumberOfEvaluations(5000);
        Preferences.setPopulationSize(100);
        Preferences.setOutputFolder("C:"+File.separator+"Users"+File.separator+"Guilherme"+File.separator+"Dropbox"+File.separator+"Tese"+File.separator+"Primeiro Artigo"+File.separator+"output");
        Preferences.setOutputFolder("C:\\Users\\Guilherme\\Desktop\\offload-experiments\\outputTeste");
        Preferences.setModelsFolder("C:\\Users\\Guilherme\\Dropbox\\Tese\\Primeiro Artigo\\feature_models_test");
        Preferences.setModelsFolder("C:\\Users\\Guilherme\\Desktop\\offload-experiments\\feature_models");
        // fazer offload?
        Preferences.setOffload(true);
        Preferences.setOffloadServerAddress("localhost");
        //Preferences.setOffloadServerAddress("192.168.0.13");
        Preferences.setOffloadServerPort(5001);
        //Preferences.setModelsFolder("C:/Users/Guilherme/Dropbox/Tese/Primeiro Artigo/feature_models_test");
        // carregar features
        System.out.println("Starting feature models loading step");
        FeatureModelsLoader loader = new FeatureModelsLoader(
                new FeatureModelsLoader.OnFeatureModelsLoadedListener() {
                    @Override
                    public void onFeatureModelsLoaded(
                            List<FeatureModelBundle> featureModels) {
                        BenchmarkApplication.setFeatureModels(featureModels);
                        System.out.println("Processo de carregar modelos de feature terminou");
                    }
                }
        );
        loader.load();
        
        // executar benchmark
        System.out.println("Starting benchmarkstep");
        // Start benchmark
        BenchmarkExecutor executor =
            new BenchmarkExecutor(BenchmarkApplication.getFeatureModels(),
                new BenchmarkExecutor.OnBenchmarkFinishedListener() {
                    @Override
                    public void onBenchmarkFinished() {
                        System.out.println("Benchmark process has finished!");
                    }
                }
            );
        executor.execute();
        
        System.out.println("Finishing experiment: " + (new Date()).getTime());
        System.out.println("Finishing experiment: " + 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()));
    }
    
}

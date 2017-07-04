/*
 * Copyright 2014 Gustavo García Pascual, Mónica Pinto and Lidia Fuentes
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

package org.uma.mo_dagame.benchmark.util;

import org.uma.mo_dagame.algorithm.jmetalcustomization.Experiment;
import org.uma.mo_dagame.algorithm.settings.MoDagameIBEAsettings;
import org.uma.mo_dagame.algorithm.settings.MoDagameMOCELLsettings;
import org.uma.mo_dagame.algorithm.settings.MoDagameMOCHCsettings;
import org.uma.mo_dagame.algorithm.settings.MoDagameNSGAIIsettings;
import org.uma.mo_dagame.algorithm.settings.MoDagamePAESsettings;
import org.uma.mo_dagame.algorithm.settings.MoDagameSPEA2settings;
import org.uma.mo_dagame.benchmark.data.FeatureModelBundle;
import org.uma.mo_dagame.feature_models.Configuration;
import org.uma.mo_dagame.feature_models.FeatureModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.util.JMException;

public class AlgorithmsStudy extends Experiment {

    private static final String EXPERIMENT_NAME = "MODAGAMEBenchmark";
    private static final int QUALITY_INDICATOR_THREADS = 4;
    private static final String[] ALGORITHM_NAME =
            {"MoDagameNSGAII", "MoDagameIBEA", "MoDagameMOCHC", "MoDagameMOCell", "MODagamePAES",
                    "MoDagameSPEA2"};

    private int algorithm_;
    private int populationSize_;
    private int evaluations_;

    private List<FeatureModelBundle> mFeatureModels;

    public AlgorithmsStudy(List<FeatureModelBundle> featureModels, int baseAlgorithm,
                           int independentRuns, int populationSize, int evaluations,
                           String outputFolder) {
        super();
        experimentName_ = EXPERIMENT_NAME;
        indicatorList_ = new String[]{"TIME"};
        mFeatureModels = featureModels;
        algorithm_ = baseAlgorithm;
        independentRuns_ = independentRuns;
        algorithmNameList_ = new String[]{ALGORITHM_NAME[baseAlgorithm]};
        populationSize_ = populationSize;
        evaluations_ = evaluations;
        experimentBaseDirectory_ = outputFolder;
    }

    public void algorithmSettings(String problemName, int problemIndex, Algorithm[] algorithm)
            throws ClassNotFoundException {
        try {
 
            HashMap parameters = new HashMap();

            if (paretoFrontFile_[problemIndex] != null &&
                    !paretoFrontFile_[problemIndex].equals("")) {
                parameters.put("paretoFrontFile_", paretoFrontFile_[problemIndex]);
            }

            parameters.put("populationSize_", populationSize_);
            parameters.put("maxEvaluations_", evaluations_);

            FeatureModel fm = mFeatureModels.get(problemIndex).getFeatureModel();
            Configuration seed = mFeatureModels.get(problemIndex).getSeed();

            switch (algorithm_) {
                case 0:
                    algorithm[0] =
                            new MoDagameNSGAIIsettings(problemName, fm, seed).configure(parameters);
                    break;
                case 1:
                    algorithm[0] =
                            new MoDagameIBEAsettings(problemName, fm, seed).configure(parameters);
                    break;
                case 2:
                    algorithm[0] =
                            new MoDagameMOCHCsettings(problemName, fm, seed).configure(parameters);
                    break;
                case 3:
                    algorithm[0] =
                            new MoDagameMOCELLsettings(problemName, fm, seed).configure(parameters);
                    break;
                case 4:
                    algorithm[0] =
                            new MoDagamePAESsettings(problemName, fm, seed).configure(parameters);
                    break;
                case 5:
                    algorithm[0] =
                            new MoDagameSPEA2settings(problemName, fm, seed).configure(parameters);
                    break;
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AlgorithmsStudy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AlgorithmsStudy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMException ex) {
            Logger.getLogger(AlgorithmsStudy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() throws JMException, IOException {
        int featureModelsCount = mFeatureModels.size();
        problemList_ = new String[featureModelsCount];
        for (int i = 0; i < featureModelsCount; i++) {
            String name = mFeatureModels.get(i).getFeatureModel().getName();
            problemList_[i] = name.replaceAll("\\s", "");
        }

        // Generate Pareto front automatically
        paretoFrontFile_ = new String[problemList_.length];
        paretoFrontDirectory_ = "";

        // Setup and run experiment
        algorithmSettings_ = new Settings[1];
        initExperiment();
        runExperiment(1, false);

        // Generate quality indicators and talbles
        generateQualityIndicators(QUALITY_INDICATOR_THREADS);
        generateLatexTables();
    }
}

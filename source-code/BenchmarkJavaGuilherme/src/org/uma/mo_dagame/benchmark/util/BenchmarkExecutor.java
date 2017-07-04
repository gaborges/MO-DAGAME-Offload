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

import org.ufrgs.custom.AlgorithmsStudy;
import org.uma.mo_dagame.benchmark.data.FeatureModelBundle;
import org.uma.mo_dagame.benchmark.data.Preferences;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jmetal.util.JMException;

public class BenchmarkExecutor {

    private final List<FeatureModelBundle> mFeatureModels;
    private final OnBenchmarkFinishedListener mOnBenchmarkFinishedListener;

    public BenchmarkExecutor(List<FeatureModelBundle> featureModels,
                             OnBenchmarkFinishedListener onBenchmarkFinishedListener) {
        mFeatureModels = featureModels;
        mOnBenchmarkFinishedListener = onBenchmarkFinishedListener;
    }

    public void execute() {
        new BenchmarkTask().execute(mFeatureModels);
    }

    private class BenchmarkTask extends Thread {

        private List<FeatureModelBundle> mFeatureModels;

        protected void onPreExecute() {
            System.out.println("Starting benchmark: " + (new Date()).getTime());
            String currentData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date());
            System.out.println("Starting benchmark: " + currentData);
        }


        protected Void doInBackground(List<FeatureModelBundle>... params) {
            int independentRuns = Preferences.getIndependentRuns();
            int baseAlgorithm = Preferences.getBaseAlgorithm();
            int populationSize = Preferences.getPopulationSize();
            int evaluations = Preferences.getEvaluations();
            String outputFolder = Preferences.getOutputFolder();
            boolean offloadActivicated = Preferences.isOffload();
            String offloadServerAddress = Preferences.getOffloadServerAddress();
            int offloadServerPort = Preferences.getOffloadServerPort();

            try {
                AlgorithmsStudy study = new AlgorithmsStudy(params[0], baseAlgorithm, independentRuns, populationSize,
                       evaluations, outputFolder);
                study.setIsConditionsToOffloadSatisfied(offloadActivicated);
                study.setOffloadServerAddress(offloadServerAddress);
                study.setOffloadServerPort(offloadServerPort);
                study.run();
            } catch (JMException e) {
                System.out.println("Erro: "+e);
            } catch (IOException e) {
                System.out.println("Erro: "+e);
            }

            return null;
        }

        protected void onPostExecute() {
            System.out.println("Finishing benchmark: " + (new Date()).getTime());
            String currentData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date());
            System.out.println("Finishing benchmark: " + currentData);
        }

        @Override
        public void run() {
         
            
        }

        private void execute(List<FeatureModelBundle> mFeatureModels) {
            this.mFeatureModels = mFeatureModels;
            onPreExecute();
            //this.start();
            doInBackground(mFeatureModels);
            onPostExecute();
        }
        
    }
    
    public interface OnBenchmarkFinishedListener {
        public void onBenchmarkFinished();
    }
}

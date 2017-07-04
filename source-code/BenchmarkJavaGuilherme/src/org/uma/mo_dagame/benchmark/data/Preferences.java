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

package org.uma.mo_dagame.benchmark.data;

import java.io.File;

public class Preferences {

    private static String modelsFolder = "";
    private static String outputFolder = "";
    private static int baseAlgorithm = 0;
    private static int independentRuns = 100;
    private static int populationSize = 100;
    private static int maximumNumberOfEvaluations = 5000;
    private static boolean offload= false;
    private static String offloadServerAddress = "localhost";
    private static int offloadServerPort = 5001;
    
    private static final String DEFAULT_MODELS_FOLDER = Environment.getExternalStorageDirectory() +
            File.separator +
            "modagame-benchmark" +
            File.separator +
            "models";

    private static final String DEFAULT_OUTPUT_FOLDER = Environment.getExternalStorageDirectory() +
            File.separator +
            "modagame-benchmark" +
            File.separator +
            "output";

    public static final String PREFS_NAME = "preferences";

    public static String getModelsFolder() {
        if(modelsFolder.length() > 0){
            return modelsFolder;
        } else
        return DEFAULT_MODELS_FOLDER;
    }

    public static String getOutputFolder() {
        if(outputFolder.length() > 0){
            return outputFolder;
        } else
        return DEFAULT_OUTPUT_FOLDER;
    }

    public static int getIndependentRuns() {
        return independentRuns;
    }

    public static int getBaseAlgorithm() {
        return baseAlgorithm;
    }

    public static int getPopulationSize() {
        return populationSize;
    }

    public static int getEvaluations() {
        return maximumNumberOfEvaluations;
    }

    public static void setBaseAlgorithm(int baseAlgorithm) {
        Preferences.baseAlgorithm = baseAlgorithm;
    }

    public static void setIndependentRuns(int independentRuns) {
        Preferences.independentRuns = independentRuns;
    }

    public static void setMaximumNumberOfEvaluations(int maximumNumberOfEvaluations) {
        Preferences.maximumNumberOfEvaluations = maximumNumberOfEvaluations;
    }

    public static void setModelsFolder(String modelsFolder) {
        Preferences.modelsFolder = modelsFolder;
    }

    public static void setOutputFolder(String outputFolder) {
        Preferences.outputFolder = outputFolder;
    }

    public static void setPopulationSize(int populationSize) {
        Preferences.populationSize = populationSize;
    }

    public static boolean isOffload() {
        return offload;
    }

    public static void setOffload(boolean offload) {
        Preferences.offload = offload;
    }

    public static void setOffloadServerAddress(String offloadServerAddress) {
        Preferences.offloadServerAddress = offloadServerAddress;
    }

    public static void setOffloadServerPort(int offloadServerPort) {
        Preferences.offloadServerPort = offloadServerPort;
    }

    public static String getOffloadServerAddress() {
        return offloadServerAddress;
    }

    public static int getOffloadServerPort() {
        return offloadServerPort;
    }
    
}

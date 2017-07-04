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

import android.content.Context;
import android.os.Environment;

import org.uma.mo_dagame.benchmark.R;

import java.io.File;

public class Preferences {

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

    private static final String DEFAULT_OFFLOAD_SERVER = "localhost";
    private static final String DEFAULT_OFFLOAD_SERVER_PORT = "5001";


    public static final String PREFS_NAME = "preferences";

    public static String getModelsFolder(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(context.getString(R.string.settings_key_models_folder),
                        DEFAULT_MODELS_FOLDER);
    }

    public static String getOutputFolder(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(context.getString(R.string.settings_key_output_folder),
                        DEFAULT_OUTPUT_FOLDER);
    }

    public static int getIndependentRuns(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(context.getString(R.string.settings_key_independent_runs),
                        context.getResources()
                                .getInteger(R.integer.settings_default_independent_runs)
                );
    }

    public static int getBaseAlgorithm(Context context) {
        return Integer.parseInt(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(context.getString(R.string.settings_key_base_algorithm),
                        Integer.toString(context.getResources()
                                .getInteger(R.integer.settings_default_base_algorithm))
                ));
    }

    public static int getPopulationSize(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(context.getString(R.string.settings_key_population_size),
                        context.getResources()
                                .getInteger(R.integer.settings_default_population_size)
                );
    }

    public static int getEvaluations(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(context.getString(R.string.settings_key_evaluations),
                        context.getResources().getInteger(R.integer.settings_default_evaluations));

    }

    public static String getOffloadServerAddress(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(context.getString(R.string.settings_key_offload_server_address),
                        DEFAULT_OFFLOAD_SERVER);
    }

    public static int getOffloadServerPort(Context context) {
        return Integer.parseInt(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(context.getString(R.string.settings_key_offload_server_port),
                        DEFAULT_OFFLOAD_SERVER_PORT));
    }

    public static boolean getOffloadMethod(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(context.getString(R.string.settings_key_offload),
                        false);
    }
}

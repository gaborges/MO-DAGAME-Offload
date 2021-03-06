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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Window;

import org.uma.mo_dagame.benchmark.R;
import org.uma.mo_dagame.benchmark.data.FeatureModelBundle;
import org.uma.mo_dagame.benchmark.data.Preferences;
import org.uma.mo_dagame.feature_models.Configuration;
import org.uma.mo_dagame.feature_models.ConfigurationParser;
import org.uma.mo_dagame.feature_models.FeatureModel;
import org.uma.mo_dagame.feature_models.ObjectivesValuesParser;
import org.uma.mo_dagame.feature_models.SxfmParser;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FeatureModelsLoader {

    private static final String EXT_SEED_DATA = ".csv";
    private static final String EXT_OBJECTIVES_DATA = ".obj";
    private static final String PROTOCOL_FILE = "file://";
    private static FilenameFilter XML_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".xml");
        }
    };

    private final Context mContext;
    private final OnFeatureModelsLoadedListener mOnFeatureModelsLoadedListener;
    private final String mFolder;

    public FeatureModelsLoader(Context context,
                               OnFeatureModelsLoadedListener onFeatureModelsLoadedListener) {
        mContext = context;
        mOnFeatureModelsLoadedListener = onFeatureModelsLoadedListener;
        mFolder = Preferences.getModelsFolder(context);
    }

    public void load() {
        new LoadModelsTask().execute(mFolder);
    }


    private class LoadModelsTask extends AsyncTask<String, Void, List<FeatureModelBundle>> {

        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setMessage(mContext.getString(R.string.loading_models));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected List<FeatureModelBundle> doInBackground(String... params) {
            String folder = params[0];
            List<FeatureModelBundle> data = new ArrayList<FeatureModelBundle>();
            File[] xmlFiles = new File(folder).listFiles(XML_FILTER);
            if (xmlFiles != null) {
                Arrays.sort(xmlFiles);
                for (File modelFile : xmlFiles) {
                    String modelBasename = modelFile.getAbsolutePath()
                            .substring(0, modelFile.getAbsolutePath().length() - 4);
                    String objectivesFilename = modelBasename + EXT_OBJECTIVES_DATA;
                    String seedFilename = modelBasename + EXT_SEED_DATA;
                    File objectivesFile = new File(objectivesFilename);

                    if (objectivesFile.exists()) {
                        FeatureModel fm;
                        try {
                            fm = SxfmParser.parse(PROTOCOL_FILE + modelFile.getAbsolutePath());
                            ObjectivesValuesParser.parse(objectivesFilename, fm);

                            File seedFile = new File(seedFilename);
                            Configuration seed = null;
                            if (seedFile.exists()) {
                                seed = ConfigurationParser.parse(seedFilename, fm);
                            }

                            data.add(new FeatureModelBundle(fm, seed));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(List<FeatureModelBundle> list) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            mOnFeatureModelsLoadedListener.onFeatureModelsLoaded(list);
        }
    }

    public interface OnFeatureModelsLoadedListener {
        public void onFeatureModelsLoaded(List<FeatureModelBundle> featureModels);
    }
}

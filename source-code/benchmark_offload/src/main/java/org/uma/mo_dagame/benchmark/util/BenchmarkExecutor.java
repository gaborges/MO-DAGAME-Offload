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
import android.media.audiofx.PresetReverb;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;

import org.uma.mo_dagame.benchmark.R;
import org.uma.mo_dagame.benchmark.data.FeatureModelBundle;
import org.uma.mo_dagame.benchmark.data.Preferences;

import java.io.IOException;
import java.util.List;

import jmetal.util.JMException;

public class BenchmarkExecutor {

    private final Context mContext;
    private final List<FeatureModelBundle> mFeatureModels;
    private final OnBenchmarkFinishedListener mOnBenchmarkFinishedListener;

    public BenchmarkExecutor(Context context, List<FeatureModelBundle> featureModels,
                             OnBenchmarkFinishedListener onBenchmarkFinishedListener) {
        mContext = context;
        mFeatureModels = featureModels;
        mOnBenchmarkFinishedListener = onBenchmarkFinishedListener;
    }

    public void execute() {
        new BenchmarkTask().execute(mFeatureModels);
    }

    private class BenchmarkTask extends AsyncTask<List<FeatureModelBundle>, Void, Void> {

        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setMessage(mContext.getString(R.string.benchmark_in_progress));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(List<FeatureModelBundle>... params) {
            int independentRuns = Preferences.getIndependentRuns(mContext);
            int baseAlgorithm = Preferences.getBaseAlgorithm(mContext);
            int populationSize = Preferences.getPopulationSize(mContext);
            int evaluations = Preferences.getEvaluations(mContext);
            String outputFolder = Preferences.getOutputFolder(mContext);
            boolean offloadActivicated = Preferences.getOffloadMethod(mContext);
            String offloadServerAddress = Preferences.getOffloadServerAddress(mContext);
            int offloadServerPort = Preferences.getOffloadServerPort(mContext);
            Log.d("OFFLOAD_CONFIGS","OFFLOAD_CONFIGS: "+offloadActivicated+" + "+offloadServerAddress+" + "+offloadServerPort);
            try {
                AlgorithmsStudy study = new AlgorithmsStudy(params[0], baseAlgorithm, independentRuns, populationSize,
                        evaluations, outputFolder);
                study.setOffloadServerAddress(offloadServerAddress);
                study.setOffloadServerPort(offloadServerPort);
                study.setIsConditionsToOffloadSatisfied(offloadActivicated);
                study.run();
            } catch (JMException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            mOnBenchmarkFinishedListener.onBenchmarkFinished();
        }
    }

    public interface OnBenchmarkFinishedListener {
        public void onBenchmarkFinished();
    }
}

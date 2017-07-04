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

package org.uma.mo_dagame.benchmark;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.uma.mo_dagame.benchmark.data.Preferences;

public class SettingsFragment extends PreferenceFragment
        implements OnSharedPreferenceChangeListener {

    private Preference mModelsFolderPreference;
    private Preference mOutputFolderPreference;
    private Preference mIndependentRunsPreference;
    private Preference mBaseAlgorithmPreference;
    private Preference mPopulationSizePreference;
    private Preference mEvaluationsPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(Preferences.PREFS_NAME);
        addPreferencesFromResource(R.xml.preferences);

        mModelsFolderPreference = findPreference(getString(R.string.settings_key_models_folder));
        mOutputFolderPreference = findPreference(getString(R.string.settings_key_output_folder));
        mIndependentRunsPreference =
                findPreference(getString(R.string.settings_key_independent_runs));
        mBaseAlgorithmPreference = findPreference(getString(R.string.settings_key_base_algorithm));
        mPopulationSizePreference =
                findPreference(getString(R.string.settings_key_population_size));
        mEvaluationsPreference = findPreference(getString(R.string.settings_key_evaluations));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        updateModelsFolderSummary();
        updateOutputFolderSummary();
        updateIndependentRunsSummary();
        updateBaseAlgorithmSummary();
        updatePopulationSizeSummary();
        updateEvaluationsSummary();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    private void updateModelsFolderSummary() {
        String modelsFolder = Preferences.getModelsFolder(getActivity());
        mModelsFolderPreference.setSummary(modelsFolder);
        ((EditTextPreference) mModelsFolderPreference).setText(modelsFolder);
    }

    private void updateOutputFolderSummary() {
        String outputFolder = Preferences.getOutputFolder(getActivity());
        mOutputFolderPreference.setSummary(outputFolder);
        ((EditTextPreference) mOutputFolderPreference).setText(outputFolder);
    }

    private void updateIndependentRunsSummary() {
        int independentRuns = Preferences.getIndependentRuns(getActivity());
        mIndependentRunsPreference.setSummary(Integer.toString(independentRuns));
    }

    private void updateBaseAlgorithmSummary() {
        int baseAlgorithm = Preferences.getBaseAlgorithm(getActivity());
        mBaseAlgorithmPreference.setSummary(getResources()
                .getStringArray(R.array.settings_entries_baseAlgorithm)[baseAlgorithm]);
    }

    private void updatePopulationSizeSummary() {
        int populationSize = Preferences.getPopulationSize(getActivity());
        mPopulationSizePreference.setSummary(Integer.toString(populationSize));
    }

    private void updateEvaluationsSummary() {
        int evaluations = Preferences.getEvaluations(getActivity());
        mEvaluationsPreference.setSummary(Integer.toString(evaluations));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_key_models_folder))) {
            updateModelsFolderSummary();
        } else if (key.equals(getString(R.string.settings_key_output_folder))) {
            updateOutputFolderSummary();
        } else if (key.equals(getString(R.string.settings_key_independent_runs))) {
            updateIndependentRunsSummary();
        } else if (key.equals(getString(R.string.settings_key_base_algorithm))) {
            updateBaseAlgorithmSummary();
        } else if (key.equals(getString(R.string.settings_key_population_size))) {
            updatePopulationSizeSummary();
        } else if (key.equals(getString(R.string.settings_key_evaluations))) {
            updateEvaluationsSummary();
        }
    }
}


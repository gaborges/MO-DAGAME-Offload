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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.uma.mo_dagame.benchmark.cards.BenchmarkFinishedCard;
import org.uma.mo_dagame.benchmark.cards.ModelListCard;
import org.uma.mo_dagame.benchmark.data.FeatureModelBundle;
import org.uma.mo_dagame.benchmark.util.BenchmarkExecutor;
import org.uma.mo_dagame.benchmark.util.FeatureModelsLoader;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.view.CardView;

public class MainActivity extends Activity {

    private Button mActionButton;
    private ModelListCard mModelListCard;
    private CardView mBenchmarkFinishedCardView;
    private List<FeatureModelBundle> mFeatureModels;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        mBenchmarkFinishedCardView = (CardView) findViewById(R.id.card_benchmark_finished);
        BenchmarkFinishedCard benchmarkFinishedCard = new BenchmarkFinishedCard(this);
        mBenchmarkFinishedCardView.setCard(benchmarkFinishedCard);

        CardView modelListCardView = (CardView) findViewById(R.id.card_model_list);
        mModelListCard = new ModelListCard(this, new ArrayList<FeatureModelBundle>());
        mModelListCard.init();
        modelListCardView.setCard(mModelListCard);

        mActionButton = (Button) findViewById(R.id.actionButton);
        mActionButton.setOnClickListener(new ActionButtonOnClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFeatureModels = BenchmarkApplication.getFeatureModels();
        if (mFeatureModels == null || mFeatureModels.size() == 0) {
            mActionButton.setText(getString(R.string.load_models));
        } else {
            mModelListCard.updateData(mFeatureModels);
            mActionButton.setText(getString(R.string.start_benchmark));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        BenchmarkApplication.setFeatureModels(mFeatureModels);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_about:
                new AboutDialog().show(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private class ActionButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mFeatureModels == null || mFeatureModels.size() == 0) {
                FeatureModelsLoader loader = new FeatureModelsLoader(MainActivity.this,
                        new FeatureModelsLoader.OnFeatureModelsLoadedListener() {
                            @Override
                            public void onFeatureModelsLoaded(
                                    List<FeatureModelBundle> featureModels) {
                                mFeatureModels = featureModels;
                                mModelListCard.updateData(featureModels);
                                if (featureModels.size() > 0) {
                                    mActionButton.setText(R.string.start_benchmark);
                                }
                            }
                        }
                );
                loader.load();
            } else {
                mBenchmarkFinishedCardView.setVisibility(View.GONE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                // Start benchmark
                BenchmarkExecutor executor =
                        new BenchmarkExecutor(MainActivity.this, mFeatureModels,
                                new BenchmarkExecutor.OnBenchmarkFinishedListener() {
                                    @Override
                                    public void onBenchmarkFinished() {
                                        mBenchmarkFinishedCardView.setVisibility(View.VISIBLE);
                                        getWindow().clearFlags(
                                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                    }
                                }
                        );
                executor.execute();
            }
        }
    }
}

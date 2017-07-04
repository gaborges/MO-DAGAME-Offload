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

import android.app.Application;

import org.uma.mo_dagame.benchmark.data.FeatureModelBundle;

import java.util.List;

public class BenchmarkApplication extends Application {

    private static List<FeatureModelBundle> mFeatureModels;

    public static List<FeatureModelBundle> getFeatureModels() {
        return mFeatureModels;
    }

    public static void setFeatureModels(List<FeatureModelBundle> featureModels) {
        mFeatureModels = featureModels;
    }
}

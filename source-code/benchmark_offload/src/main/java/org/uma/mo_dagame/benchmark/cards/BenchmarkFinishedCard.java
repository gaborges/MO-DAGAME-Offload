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

package org.uma.mo_dagame.benchmark.cards;

import android.content.Context;

import org.uma.mo_dagame.benchmark.R;

import it.gmariotti.cardslib.library.internal.Card;

public class BenchmarkFinishedCard extends Card {

    public BenchmarkFinishedCard(Context context) {
        super(context, R.layout.card_benchmark_finished_inner_main);
    }

    public BenchmarkFinishedCard(Context context, int innerLayout) {
        super(context, innerLayout);
    }
}

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.uma.mo_dagame.benchmark.R;
import org.uma.mo_dagame.benchmark.data.FeatureModelBundle;
import org.uma.mo_dagame.feature_models.FeatureModel;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.prototypes.CardWithList;

public class ModelListCard extends CardWithList {

    private List<FeatureModelBundle> mData;

    public ModelListCard(Context context, List<FeatureModelBundle> data) {
        super(context);
        mData = data;
    }

    @Override
    protected CardHeader initCardHeader() {
        CardHeader header = new CardHeader(getContext());
        header.setTitle(getContext().getString(R.string.model_list));
        return header;
    }

    @Override
    protected void initCard() {
        setEmptyViewViewStubLayoutId(R.layout.card_model_list_empty);
    }

    @Override
    protected List<ListObject> initChildren() {
        List<ListObject> objects = new ArrayList<ListObject>();
        if (mData != null) {
            for (FeatureModelBundle fmBundle : mData) {
                objects.add(new FeatureModelObject(this, fmBundle.getFeatureModel()));
            }
        }

        return objects;
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.card_model_list_inner_main;
    }

    @Override
    public View setupChildView(int i, ListObject listObject, View view, ViewGroup viewGroup) {
        TextView modelTextView = (TextView) view.findViewById(R.id.model_name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.model_summary);
        FeatureModel model = ((FeatureModelObject) listObject).getFeatureModel();
        modelTextView.setText(model.getName());
        summaryTextView.setText(getContext()
                .getString(R.string.model_summary, model.getFeatures().size(),
                        model.getCrossTreeConstraints().size()));

        return view;
    }

    public void updateData(List<FeatureModelBundle> data) {
        getLinearListAdapter().clear();
        List<FeatureModelObject> objectList = new ArrayList<FeatureModelObject>();
        for (FeatureModelBundle featureModelBundle : data) {
            objectList.add(new FeatureModelObject(this, featureModelBundle.getFeatureModel()));
        }
        getLinearListAdapter().addAll(objectList);
    }

    private class FeatureModelObject extends DefaultListObject {
        private FeatureModel mFeatureModel;

        public FeatureModelObject(Card parentCard, FeatureModel model) {
            super(parentCard);
            mFeatureModel = model;
        }

        public FeatureModel getFeatureModel() {
            return mFeatureModel;
        }
    }
}

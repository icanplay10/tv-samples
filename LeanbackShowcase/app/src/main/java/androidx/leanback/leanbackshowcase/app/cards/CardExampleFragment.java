/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package androidx.leanback.leanbackshowcase.app.cards;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.leanback.app.BrowseFragment;
import androidx.leanback.leanbackshowcase.R;
import androidx.leanback.leanbackshowcase.app.details.DetailViewExampleActivity;
import androidx.leanback.leanbackshowcase.app.details.DetailViewExampleFragment;
import androidx.leanback.leanbackshowcase.app.details.ShadowRowPresenterSelector;
import androidx.leanback.leanbackshowcase.cards.presenters.CardPresenterSelector;
import androidx.leanback.leanbackshowcase.models.Card;
import androidx.leanback.leanbackshowcase.models.CardRow;
import androidx.leanback.leanbackshowcase.utils.CardListRow;
import androidx.leanback.leanbackshowcase.utils.Utils;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.DividerRow;
import androidx.leanback.widget.SectionRow;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.core.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

/**
 * This fragment will be shown when the "Card Examples" card is selected at the home menu. It will
 * display multiple card types.
 */
public class CardExampleFragment extends BrowseFragment {

    private ArrayObjectAdapter mRowsAdapter;

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupUi();
        setupRowAdapter();
    }

    private void setupUi() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setTitle(getString(R.string.card_examples_title));
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), getString(R.string.implement_search),
                        Toast.LENGTH_LONG).show();
            }
        });
        setOnItemViewClickedListener(new OnItemViewClickedListener() {

            @Override
            public void onItemClicked(Presenter.ViewHolder viewHolder, Object item, RowPresenter.ViewHolder viewHolder1, Row row) {
                if (!(item instanceof Card)) return;
                if (!(viewHolder.view instanceof ImageCardView)) return;

                ImageView imageView = ((ImageCardView) viewHolder.view).getMainImageView();
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        imageView, DetailViewExampleFragment.TRANSITION_NAME).toBundle();
                Intent intent = new Intent(getActivity().getBaseContext(),
                        DetailViewExampleActivity.class);
                Card card = (Card) item;
                int imageResId = card.getLocalImageResourceId(getContext());
                intent.putExtra(DetailViewExampleFragment.EXTRA_CARD, imageResId);
                startActivity(intent, bundle);
            }

        });

        prepareEntranceTransition();
    }

    private void setupRowAdapter() {
        mRowsAdapter = new ArrayObjectAdapter(new ShadowRowPresenterSelector());
        setAdapter(mRowsAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createRows();
                startEntranceTransition();
            }
        }, 500);
    }

    private void createRows() {
        String json = Utils
                .inputStreamToString(getResources().openRawResource(R.raw.cards_example));
        CardRow[] rows = new Gson().fromJson(json, CardRow[].class);
        for (CardRow row : rows) {
            mRowsAdapter.add(createCardRow(row));
        }
    }

    private Row createCardRow(final CardRow cardRow) {
        switch (cardRow.getType()) {
            case CardRow.TYPE_SECTION_HEADER:
                return new SectionRow(new HeaderItem(cardRow.getTitle()));
            case CardRow.TYPE_DIVIDER:
                return new DividerRow();
            case CardRow.TYPE_DEFAULT:
            default:
                // Build main row using the ImageCardViewPresenter.
                //TODO:CardPresenterSelector相当于card的选择器？
                PresenterSelector presenterSelector = new CardPresenterSelector(getActivity());
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(presenterSelector);
                for (Card card : cardRow.getCards()) {
                    listRowAdapter.add(card);
                }
//                return new CardListRow(new HeaderItem(cardRow.getTitle()), listRowAdapter, cardRow);
                HeaderItem headerItem = new HeaderItem(cardRow.getTitle());
                /**
                 * title:標題
                 * description:子標題
                 * contentDescription：一般用不到
                 * TODO:
                 * 1.首页里面并没有用到headerItem（详情页有用到），首页中抽象了模板的概念，此title作为了模板的一个view而存在
                 * 2.那么官方的例子中，并没有抽象出模板的概念，他是怎么做到的呢？
                 * 3.而且，官方的例子中，每一行都是可以右滑滚动的，层级上是否有消耗?
                 */
                headerItem.setDescription("setDescription");
                headerItem.setContentDescription("setContentDescription");
                /**
                 * TODO
                 * 1.ListRow是什么？
                 * 2.ListRow里面包含了一个HorizontalGridView，集成了一个列表控件
                 * 3.另外，发现在某一个行模板按住按键快速移动时，会省略中间选中的过程，直接跳转到最后一个，这是怎么做到的？
                 *    看起来效果还可以，是否都是一样的实现，只是做了一个remove&&postDelay的逻辑？
                 *    看起来流畅的原因是什么，布局没那么复杂？没有选中边框效果？我们的详情页相关推荐，看着明显卡顿，应该是做了按键减速的处理，流畅性需要看下是怎么保证的
                 */
            return new CardListRow(headerItem, listRowAdapter, cardRow);
        }
    }

}

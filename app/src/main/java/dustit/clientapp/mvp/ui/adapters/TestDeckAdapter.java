package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.TestMemEntity;
import dustit.clientapp.utils.IConstants;
import dustit.clientapp.utils.L;

/**
 * Created by shevc on 30.09.2017.
 * Let's GO!
 */

public class TestDeckAdapter extends ArrayAdapter<TestMemEntity> {

    public TestDeckAdapter(Context context) {
        super(context, 0);
    }

    /*public void updateList(List<TestMemEntity> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }*/

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CardViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_test_card, parent, false);
            holder = new CardViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CardViewHolder) convertView.getTag();
        }

        final TestMemEntity memEntity = getItem(position);
        if (memEntity != null) {
            L.print("started loading");
            Picasso.get().load(IConstants.BASE_URL + "/feed/imgs?id=" + memEntity.getMemId())
                    .into(holder.ivMem);
        }
        return convertView;
    }

    static class CardViewHolder {
        @BindView(R.id.ivTestCardMem)
        ImageView ivMem;

        private CardViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

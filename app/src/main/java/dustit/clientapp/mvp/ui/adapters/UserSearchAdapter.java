package dustit.clientapp.mvp.ui.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.UserEntity;
import dustit.clientapp.utils.IConstants;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.UserViewHolder> {
    private List<UserEntity> users;
    private LayoutInflater inflater;
    private IUserSearchInteraction interaction;

    public UserSearchAdapter(Context context) {
        users = new ArrayList<>(0);
        inflater = LayoutInflater.from(context);
        interaction = (IUserSearchInteraction) context;
    }

    public void updateList(List<UserEntity> users) {
        this.users.clear();
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(inflater.inflate(R.layout.item_search_user, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        TypedValue outValue = new TypedValue();
        holder.itemView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        holder.itemView.setBackgroundResource(outValue.resourceId);
        UserEntity user = users.get(position);
        holder.sdvUser.setImageURI(IConstants.USER_IMAGE_URL + user.getUsername());
        holder.tvUsername.setText(user.getUsername());
        holder.itemView.setOnClickListener(v -> interaction.onUserSelected(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).getUserId();
    }

    public interface IUserSearchInteraction {
        void onUserSelected(UserEntity user);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sdvUserIcon)
        SimpleDraweeView sdvUser;
        @BindView(R.id.tvUsername)
        TextView tvUsername;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

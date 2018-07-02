package dustit.clientapp.mvp.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dustit.clientapp.R;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.PersonalCategory;
import dustit.clientapp.mvp.presenters.fragments.ChangeCategoriesFragmentPresenter;
import dustit.clientapp.mvp.ui.adapters.ChangeCategoriesRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.adapters.ResultRecyclerViewAdapter;
import dustit.clientapp.mvp.ui.interfaces.IChangeCategoriesFragmentView;
import dustit.clientapp.utils.AlertBuilder;

public class ChangeCategoriesFragment extends Fragment implements IChangeCategoriesFragmentView {

    @BindView(R.id.rvChangeCcategoriesFragment)
    RecyclerView rvCategories;
    @BindView(R.id.btnChangeCategoriesApply)
    Button btnApply;

    private ChangeCategoriesRecyclerViewAdapter adapter;

    private Unbinder unbinder;
    private final ChangeCategoriesFragmentPresenter presenter = new ChangeCategoriesFragmentPresenter();

    public interface IChangeCategoriesCallback {
        void closeChangeCategoriesFragment();
    }

    private IChangeCategoriesCallback callback;

    @Override
    public void onAttach(Context context) {
        if (context instanceof IChangeCategoriesCallback) {
            callback = (IChangeCategoriesCallback) context;
        }
        super.onAttach(context);
    }

    public static ChangeCategoriesFragment newInstance() {
        return new ChangeCategoriesFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_change_categories, container, false);
        unbinder = ButterKnife.bind(this, v);
        presenter.bind(this);
        adapter = new ChangeCategoriesRecyclerViewAdapter(getContext());
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvCategories.setAdapter(adapter);
        btnApply.setOnClickListener(view -> presenter.sendCategories(adapter.getChecked()));
        presenter.getCategories();
        return v;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        presenter.unbind();
        super.onDestroyView();
    }

    @Override
    public void onCategoriesChanged() {
        Toast.makeText(getActivity(), getString(R.string.applied), Toast.LENGTH_SHORT).show();
        callback.closeChangeCategoriesFragment();
    }

    @Override
    public void onErrorInCategoriesChanging() {
        Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNotRegistered() {
        AlertBuilder.showNotRegisteredPrompt(getContext());
    }

    @Override
    public void updateCategories(List<PersonalCategory> categories) {
        adapter.updateList(categories);
        rvCategories.scheduleLayoutAnimation();
    }
}

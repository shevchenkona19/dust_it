package dustit.clientapp.mvp.presenters.fragments;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dustit.clientapp.App;
import dustit.clientapp.mvp.datamanager.DataManager;
import dustit.clientapp.mvp.model.entities.Category;
import dustit.clientapp.mvp.model.entities.MemEntity;
import dustit.clientapp.mvp.model.entities.ResponseEntity;
import dustit.clientapp.mvp.presenters.base.BasePresenter;
import dustit.clientapp.mvp.presenters.interfaces.ICategoriesFragmentPresenter;
import dustit.clientapp.mvp.ui.interfaces.ICategoriesFragmentView;
import dustit.clientapp.utils.FavoritesUtils;
import rx.Subscriber;

public class CategoriesFragmentPresenter extends BasePresenter<ICategoriesFragmentView> implements ICategoriesFragmentPresenter {
    @Inject
    DataManager dataManager;

    public CategoriesFragmentPresenter() {
        App.get().getAppComponent().inject(this);
    }

    @Override
    public void loadBase(String categoryId) {
        getView().onStartLoading();
        final List<MemEntity> list = new ArrayList<>();
        addSubscription(dataManager.getCategoriesFeed(categoryId, 6, 0)
                .subscribe(new Subscriber<MemEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onBaseUpdated(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemEntity memEntity) {
                        list.add(memEntity);
                    }
                }));
    }

    @Override
    public void loadWithOffset(String categoryId, int offset) {
        getView().onStartLoading();
        final List<MemEntity> list = new ArrayList<>();
        addSubscription(dataManager.getCategoriesFeed(categoryId, 5, offset)
                .subscribe(new Subscriber<MemEntity>() {
                    @Override
                    public void onCompleted() {
                        getView().onPartialUpdate(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onErrorInLoading();
                    }

                    @Override
                    public void onNext(MemEntity memEntity) {
                        list.add(memEntity);
                    }
                }));
    }

    @Override
    public void getCategories() {
        final List<Category> categoryList = new ArrayList<>();
        addSubscription(dataManager.getCategories()
                .subscribe(new Subscriber<Category>() {
                    @Override
                    public void onCompleted() {
                        getView().onCategoriesLoaded(categoryList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getView().onCategoriesFailedToLoad();
                    }

                    @Override
                    public void onNext(Category category) {
                        Log.d("MY", "Id in presenter: " + category.getId());
                        categoryList.add(category);
                    }
                }));
    }

    @Override
    public void postLike(final String id) {
        final int[] code = {0};
        addSubscription(dataManager.postLike(id).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                if (code[0] == 200) {
                    getView().onLikePostedSuccessfully(id);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("MY", "deleteLike: " + e.getMessage());
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                code[0] = responseEntity.getResponse();
            }
        }));
    }

    @Override
    public void deleteLike(final String id) {
        final int[] code = {0};
        addSubscription(dataManager.deleteLike(id).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                if (code[0] == 200) {
                    getView().onLikeDeletedSuccessfully(id);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("MY", "deleteLike: " + e.getMessage());
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                code[0] = responseEntity.getResponse();
            }
        }));
    }

    @Override
    public void postDislike(final String id) {
        final int[] code = {0};
        addSubscription(dataManager.postDislike(id).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                if (code[0] == 200) {
                    getView().onDislikePostedSuccessfully(id);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("MY", "deleteLike: " + e.getMessage());
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                code[0] = responseEntity.getResponse();
            }
        }));
    }

    @Override
    public void deleteDislike(final String id) {
        final int[] code = {0};
        addSubscription(dataManager.deleteDislike(id).subscribe(new Subscriber<ResponseEntity>() {
            @Override
            public void onCompleted() {
                if (code[0] == 200) {
                    getView().onDislikeDeletedSuccessfully(id);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d("MY", "deleteLike: " + e.getMessage());
            }

            @Override
            public void onNext(ResponseEntity responseEntity) {
                code[0] = responseEntity.getResponse();
            }
        }));
    }

    @Override
    public void addToFavorites(String id) {
        FavoritesUtils favoritesUtils = new FavoritesUtils(dataManager);
        favoritesUtils.addCallback(new FavoritesUtils.IFavoriteCallback() {
            @Override
            public void onAddedToFavorites(String id) {
                getView().onAddedToFavorites(id);
            }

            @Override
            public void onError(String id) {
                getView().onErrorInAddingToFavorites(id);
            }
        });
        favoritesUtils.addToFavorites(id);
    }
}

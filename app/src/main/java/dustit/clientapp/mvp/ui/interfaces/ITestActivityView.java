package dustit.clientapp.mvp.ui.interfaces;

import java.util.List;

import dustit.clientapp.mvp.model.entities.TestMemEntity;

/**
 * Created by Никита on 09.11.2017.
 */

public interface ITestActivityView extends IActivityView{
    void onTestArrived(List<TestMemEntity> list);
    void onErrorInLoadingTest();
}

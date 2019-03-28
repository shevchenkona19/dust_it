package dustit.clientapp.mvp.presenters.interfaces;

import dustit.clientapp.mvp.model.entities.MemEntity;

public interface IReportMemeDialogPresenter {
    void reportMeme(String reportReason, MemEntity mem);
}

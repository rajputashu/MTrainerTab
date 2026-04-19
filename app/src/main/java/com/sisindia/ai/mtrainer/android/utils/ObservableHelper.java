package com.sisindia.ai.mtrainer.android.utils;

import android.view.View;

import androidx.appcompat.widget.SearchView;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class ObservableHelper {
    public static Observable<String> getObservable(View view) {
        if (view instanceof SearchView)
            return searchViewObservable((SearchView) view);
        return null;
    }

    private static Observable<String> searchViewObservable(SearchView view) {
        final PublishSubject<String> subject = PublishSubject.create();
        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                subject.onComplete();
                view.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                subject.onNext(newText);
                return false;
            }
        });
        return subject;
    }
}

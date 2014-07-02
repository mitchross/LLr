package com.HyperStandard.llr.app;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nonex_000 on
 * @since /30/2014
 */
public class ListFragment extends Fragment {
    private ListCallback callback;

    public ListFragment() {

    }

    public static ListFragment newInstance(int position, String URL) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString("URL", URL);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (callback != null) {//Load the page as soon as the fragment is created
            callback.onFragmentLoad(getArguments().getString("URL"));
            Log.e("here", "sending callback");
        }
        return inflater.inflate(R.layout.fragment_container, container, false);

    }

    public void setCallback(ListCallback callback) {
        Log.e("setting callback", "test");
        this.callback = callback;
    }

    public interface ListCallback {
        public void onFragmentLoad(String URL);
    }
}



package com.flex.jie.flexdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jie on 2016/7/21.
 */
public class TabFragment extends Fragment {

    private ListAdapter mAdapter;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(
                R.id.fragment_list_rv);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        mAdapter = new ListAdapter(getActivity(), generateData(30));
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private List<String> generateData(int count) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add("Item " + i);
        }
        return list;
    }


    public boolean isTop() {
        return linearLayoutManager == null || linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0;
    }

    public void scrollBy(int distance) {
        if (recyclerView != null) {
            recyclerView.scrollBy(0, distance);
        }
    }

    public void scroll2Top() {
        if (recyclerView != null)
            recyclerView.scrollToPosition(0);
    }

}

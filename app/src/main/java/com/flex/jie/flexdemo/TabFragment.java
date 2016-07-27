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

    private String mItemData = "Lorem Ipsum is simply dummy text of the printing and "
            + "typesetting industry Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, " +
            "when an unknown printer took a galley of type and scrambled it to make a type specimen book It has " +
            "survived not only five centuries, but also the leap into electronic typesetting, remaining essentially " +
            "unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum " +
            "passages, and more recently with desktop publishing software like Aldus PageMaker including versions of " +
            "Lorem Ipsum.";

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

        String[] listItems = mItemData.split(" ");

        List<String> list = new ArrayList<String>();
        Collections.addAll(list, listItems);

        mAdapter = new ListAdapter(getActivity(), list);
        recyclerView.setAdapter(mAdapter);

        return view;
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

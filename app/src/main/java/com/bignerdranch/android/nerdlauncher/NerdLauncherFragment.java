package com.bignerdranch.android.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by liubin on 2017/2/22.
 */

public class NerdLauncherFragment extends Fragment {

    private static final String TAG = "NerdLauncherFragment";

    private RecyclerView mRecyclerView;


    public static NerdLauncherFragment newInstance() {
        return new NerdLauncherFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_nerd_launcher,container,false);

        mRecyclerView = (RecyclerView)v.findViewById(R.id.fragment_nerd_launcher_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //设置适配器

        setupAdapter();
        return v;
    }

    private void setupAdapter() {

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);

        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        //用于检索的各种相关的那些设备上当前所安装的应用程序包的信息。你可以找到通过这个类getPackageManager()
        PackageManager pm = getActivity().getPackageManager();

        //可以让给定的意图来执行的所有活动。queryIntentActivities
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {

                PackageManager pm = getActivity().getPackageManager();

                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),
                        b.loadLabel(pm).toString()
                );
            }
        });
        Log.i(TAG, "Found " + activities.size() + " activities.");//I/NerdLauncherFragment: Found 84 activities.
        mRecyclerView.setAdapter(new ActivityAdapter(activities));
    }

    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ResolveInfo mResolveInfo;
        private TextView mNameTextView;


        public ActivityHolder(View itemView) {
            super(itemView);

            mNameTextView = (TextView) itemView;
            mNameTextView.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            mResolveInfo = resolveInfo;

            PackageManager pm = getActivity().getPackageManager();

            String appName = mResolveInfo.loadLabel(pm).toString();

            Log.i(TAG, "appname is " + appName);

            mNameTextView.setText(appName);
        }

        @Override
        public void onClick(View v) {

            ActivityInfo activityInfo = mResolveInfo.activityInfo;
            Log.i(TAG, "packageName is " + activityInfo.applicationInfo.packageName);
            Log.i(TAG, "activityInfo.name is "+ activityInfo.name);

            Intent i = new Intent(Intent.ACTION_MAIN)
                    .setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            //新的任务在当前程序的任务里
            //重新打开一个新的任务要使用addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {

        private final List<ResolveInfo> mActivities;

        private ActivityAdapter(List<ResolveInfo> activities) {
            mActivities = activities;
        }



        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent,false);

            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            ResolveInfo resolveInfo = mActivities.get(position);
            holder.bindActivity(resolveInfo);

        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }









}

package com.yize.autobus.autobus;


import android.annotation.SuppressLint;
import android.app.Fragment;


public class AutoBusBlankFragment extends Fragment {
    private AutoBusFragmentLifeCycle lifeCycle;

    public AutoBusBlankFragment() {
        this(new AutoBusFragmentLifeCycle());

    }

    @SuppressLint("ValidFragment")
    public AutoBusBlankFragment(AutoBusFragmentLifeCycle lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public AutoBusFragmentLifeCycle getLifeCycle(){
        return this.lifeCycle;
    }

    @Override
    public void onStart() {
        super.onStart();
        lifeCycle.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        lifeCycle.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifeCycle.onDestroy();
    }
}

package com.yize.autobus.autobus;


import android.annotation.SuppressLint;
import android.app.Fragment;

/*************************************************************************************************************************************
    Fragment的生命周期的方法：
    onAttach( )：当Fagment和Activity产生关联时被调用；
    onCreate( )：当Fragment被创建时调用；
    onCreateView（）：创建并返回与Fragment相关的view界面；
    onViewCreate（）：在onCreateView执行完后立即执行；
    onActivityCreated( )：通知Fragment，他所关联的Activity已经完成了onCreate的调用；
    onStart( )：让Fragment准备可以被用户所见，该方法和Activity的onStart（）方法相关联；
    onResume（）：Fragment可见，可以和用户交互，该方法和Activity的onResume方法相关联；
    onPause（）：当用户离开Fragment时调用该方法，此操作是由于所在的Activity被遮挡或者是在Activity中的另一个Fragment操作所引起的；
    onStop（）：对用户而言，Fragment不可见时调用该方法，此操作是由于他所在的Activity不再可见或者是在Activity中的一个Fragment操作所引起的；
    onDestroyView（）：ment清理和它的view相关的资源；
    onDestroy（）:最终清理Fragment的状态；
    onDetach（）：Fragment与Activity不再产生关联；
 *************************************************************************************************************************************/
/**
 * 一个没有界面的Fragment，将其绑定在activity或者fragment上
 * 这样，在他被绑定的组件的生命周期结束的时候，就可以实现生命
 * 周期的监听
 */
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

    /**
     * 这个空白fragment的父组件经历生命周期的onCreate方法之后
     * 会调用这个空白Fragment的onAttach方法，然后调用onCreate方法，在这里我们就可以对
     * onCreate进行监听回调了
     *
     */
    @Override
    public void onStart() {
        super.onStart();
        lifeCycle.onStart();
    }

    /**
     * 父组件onStop执行后调用此方法
     * 以Activity为例，当另一个activity遮挡到当前activity，
     * 当前activity不可见的时候会调用onStop，触发空白fragment的这个方法
     * 然后我们可以监听回调了
     */
    @Override
    public void onStop() {
        super.onStop();
        lifeCycle.onStop();
    }

    /**
     * 在父组件真正销毁之前调用。空白fragment会在父组件销毁之前销毁
     * 因此，我们在回调函数那里可以以此为依据，取消总线上的注册。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        lifeCycle.onDestroy();
    }
}

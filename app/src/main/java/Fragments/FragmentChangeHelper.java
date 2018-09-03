package Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Fragment跳转助手类，管理Fragment 跳转相关内容及操作
 */
public class FragmentChangeHelper {

    //****************************  成员变量  *********************************

    /**
     * 跳转目标Fragment
     */
    private Fragment targetFragment;

    /**
     * 需要传递给目标Fragment 的数据
     */
    private Bundle args;

    /**
     * 添加目标Fragment 到回退栈时给目标Fragment的tag
     */
    private String targetFragmentTag;

    /**
     * 根据tag删除回退栈中的Fragment，可删除多个
     */
    private String[] removeFragmentTag;

    /**
     * 跳转前是否清空回退栈
     */
    private boolean clearAllBackStack;

    /**
     * 设置返回到回退栈中指定Tag 的Fragment
     */
    private String backToFragmentTag;


    //****************************  构造方法  *********************************

    /**
     * 构造方法1，需要传入跳转目标Fragment
     * @param targetFragment 跳转目标
     */
    public FragmentChangeHelper(Fragment targetFragment) {
        this.targetFragment = targetFragment;
    }

    /**
     * 构造方法2，需要传入回退栈中目标Fragment 的Tag
     * @param backToFragmentTag
     */
    public FragmentChangeHelper(String backToFragmentTag) {
        this.backToFragmentTag = backToFragmentTag;
    }


    //**********************  设置内容和操作的相关方法  *************************

    /**
     * 设置需要传递给目标Fragment 的数据
     * @param args
     */
    public void setArguments(Bundle args) {
        this.args = args;
    }

    /**
     * 将目标Fragment 添加到回退栈，同时为目标Fragment 设置Tag
     * @param targetFragmentTag 为目标设置的Tag，这个Tag应该在每个
     *                          Fragment中以类名作为常量值定义
     */
    public void addToBackStack(String targetFragmentTag) {
        this.targetFragmentTag = targetFragmentTag;
    }

    /**
     * 删除回退栈中指定Tag的Fragment，可同时删除多个
     * @param removeFragmentTag
     */
    public void removeFragmentFromBackStack(String... removeFragmentTag){
        this.removeFragmentTag = removeFragmentTag;
    }

    /**
     * 设置是否在跳转前清空回退栈
     * @param clearAllBackStack
     */
    public void clearAllBackStack(boolean clearAllBackStack) {
        this.clearAllBackStack = clearAllBackStack;
    }


    //**********************  获取内容和操作的相关方法  *************************

    public Bundle getArgs() {
        return args;
    }

    public boolean isClearAllBackStack() {
        return clearAllBackStack;
    }

    public String getTargetFragmentTag() {
        return targetFragmentTag;
    }

    public String[] getRemoveFragmentTag() {
        return removeFragmentTag;
    }

    public Fragment getTargetFragment() {
        return targetFragment;
    }

    public String getBackToFragmentTag() {
        return backToFragmentTag;
    }

}

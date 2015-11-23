/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Pierre Teyssedre
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.teyssedre.paranoya.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ca.teyssedre.paranoya.ParanoyaActivity;
import ca.teyssedre.paranoya.R;
import ca.teyssedre.paranoya.fragments.ParanoyaFragment;

public class FragmentHelper {

    private final ParanoyaActivity activity;
    private ParanoyaFragment currentFragment;

    public FragmentHelper(ParanoyaActivity context) {
        this.activity = context;
    }


    public <T extends Fragment> T PushFragment(String tag, Class<T> type) {
        return PushFragmentIn(tag, type, R.id.content);
    }


    public <T extends ParanoyaFragment> T PushParanoyaFragment(String tag, Class<T> type) {
        return PushFragmentIn(tag, type, R.id.content);
    }

    public <T extends Fragment> T PushFragmentIn(String tag, Class<T> type, int resourceId) {
        T t = null;
        if (this.activity != null) {
            FragmentManager sfm = this.activity.getSupportFragmentManager();
            Fragment fragmentByTag = GetFragment(tag, type);
            if (fragmentByTag == null) {
                try {
                    t = type.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                t = type.cast(fragmentByTag);
            }
            sfm.beginTransaction().replace(resourceId, t, tag).commit();
        }
        return t;
    }

    public <T extends ParanoyaFragment> T PushParanoyaFragmentIn(String tag, Class<T> type, int resourceId) {
        T t = null;
        if (this.activity != null) {
            FragmentManager sfm = this.activity.getSupportFragmentManager();
            Fragment fragmentByTag = GetParanoyaFragment(tag, type);
            if (fragmentByTag == null) {
                try {
                    t = type.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                t = type.cast(fragmentByTag);
            }
            sfm.beginTransaction().replace(resourceId, t, tag).commit();
            setCurrentFragment(t);
        }
        return t;
    }

    public <T extends Fragment> T GetFragment(String tag, Class<T> type) {
        if (this.activity != null) {
            FragmentManager sfm = this.activity.getSupportFragmentManager();
            Fragment fragmentByTag = sfm.findFragmentByTag(tag);
            if (fragmentByTag == null) {
                return null;
            }
            return type.cast(fragmentByTag);
        }
        return null;
    }

    public <T extends ParanoyaFragment> T GetParanoyaFragment(String tag, Class<T> type) {
        if (this.activity != null) {
            FragmentManager sfm = this.activity.getSupportFragmentManager();
            Fragment fragmentByTag = sfm.findFragmentByTag(tag);
            if (fragmentByTag == null) {
                return null;
            }
            return type.cast(fragmentByTag);
        }
        return null;
    }

    public <T extends ParanoyaFragment> boolean IsFragmentOfTypeAvailabel(String tag, Class<T> type) {
        return GetFragment(tag, type) != null;
    }

    public ParanoyaFragment getCurrentFragment() {
        return currentFragment;
    }

    private void setCurrentFragment(ParanoyaFragment fragment) {
        this.currentFragment = fragment;
        if (this.currentFragment == null) {
            return;
        }
        if (this.currentFragment.fabAction != null) {
            activity.LinkFabAction(this.currentFragment.fabAction);
        } else {
            activity.HideFab();
        }
    }
}

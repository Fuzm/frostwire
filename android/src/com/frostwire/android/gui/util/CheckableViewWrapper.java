/*
 * Created by Angel Leon (@gubatron), Alden Torres (aldenml),
 * Marcelina Knitter (@marcelinkaaa), Jose Molina (@votaguz)
 * Copyright (c) 2011-2017, FrostWire(R). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frostwire.android.gui.util;

import android.view.View;
import android.widget.Checkable;

import com.frostwire.android.gui.views.AbstractListAdapter;
import com.frostwire.util.Logger;
import com.frostwire.util.Ref;

import java.lang.ref.WeakReference;

/**
 * If you want to make a view logically clickable, wrap it with CheckableViewWrapper
 * @author aldenml
 * @author gubatron
 * @author marcelinkaaa
 *         Created on 4/27/17.
 */


public abstract class CheckableViewWrapper<T> extends View implements Checkable {
    private static final Logger LOG = Logger.getLogger(CheckableViewWrapper.class);
    private final WeakReference<View> backgroundViewRef;
    private final WeakReference<View> overlayCheckedViewRef;
    private final AbstractListAdapter<T>.CheckboxOnCheckedChangeListener onCheckedChangeListener;
    private boolean checked;

    public CheckableViewWrapper(View backgroundView, View overlayCheckedView, AbstractListAdapter<T>.CheckboxOnCheckedChangeListener onCheckedChangeListener) {
        super(backgroundView.getContext());
        setClickable(true);
        backgroundViewRef = Ref.weak(backgroundView);
        overlayCheckedViewRef = Ref.weak(overlayCheckedView);
        this.onCheckedChangeListener = onCheckedChangeListener;
        initClickListeners(backgroundView, overlayCheckedView);
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        if (this.onCheckedChangeListener != null) {
            this.onCheckedChangeListener.onCheckedChanged(this, checked);
        }
    }

    @Override
    public boolean isChecked() {
        LOG.info("isChecked()");
        return this.checked;
    }

    @Override
    public void toggle() {
        LOG.info("toggle()");
        setChecked(!checked);
    }

    public void setTag(Object tag) {
        if (Ref.alive(backgroundViewRef) && Ref.alive(overlayCheckedViewRef)) {
            LOG.info("setTag("+tag.getClass().getSimpleName()+")");
            backgroundViewRef.get().setTag(tag);
            overlayCheckedViewRef.get().setTag(tag);
        }
    }

    public Object getTag() {
        if (Ref.alive(backgroundViewRef) && Ref.alive(overlayCheckedViewRef)) {
            LOG.info("getTag() -> " + backgroundViewRef.get().getTag().getClass().getSimpleName());
            return backgroundViewRef.get().getTag();
        } else {
            return null;
        }
    }

    private void initClickListeners(View backgroundView, View overlayCheckedView) {
        backgroundView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackgroundViewClick(v);
            }
        });
        backgroundView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onBackgroundViewLongClick(v);
            }
        });
        overlayCheckedView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onOverlayCheckedViewClick(v);
            }
        });
        overlayCheckedView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onOverlayCheckedViewLongClick(v);
            }
        });
    }

    private void onBackgroundViewClick(View v) {
        LOG.info("onBackgroundViewClick(v="+v.getClass().getSimpleName()+")");
        if (Ref.alive(overlayCheckedViewRef)) {
            if (overlayCheckedViewRef.get().getVisibility() == View.VISIBLE) {
                return;
            }
            overlayCheckedViewRef.get().setVisibility(View.VISIBLE);
            setChecked(true);
        }
    }

    private void onOverlayCheckedViewClick(View v) {
        LOG.info("onOverlayCheckedViewClick(v="+v.getClass().getSimpleName()+")");
        if (Ref.alive(backgroundViewRef) && Ref.alive(overlayCheckedViewRef)) {
            overlayCheckedViewRef.get().setVisibility(View.GONE);
            backgroundViewRef.get().setVisibility(View.VISIBLE);
            setChecked(false);
        }
    }

    public abstract boolean onBackgroundViewLongClick(View v);

    public abstract boolean onOverlayCheckedViewLongClick(View v);
}

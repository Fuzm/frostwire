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


public final class CheckableViewWrapper<T> extends View implements Checkable {
    private static final Logger LOG = Logger.getLogger(CheckableViewWrapper.class);

    private final WeakReference<View> viewRef;
    private boolean checked;
    private AbstractListAdapter<T>.CheckboxOnCheckedChangeListener onCheckedChangeListener;

    public CheckableViewWrapper(View view, AbstractListAdapter<T>.CheckboxOnCheckedChangeListener onCheckedChangeListener) {
        super(view.getContext());
        viewRef = Ref.weak(view);
        this.onCheckedChangeListener = onCheckedChangeListener;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
                v.invalidate();
            }
        });
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        if (this.onCheckedChangeListener != null) {
            View view = viewRef.get();
            try {
                if (Ref.alive(viewRef)) {
                    view.setVisibility(checked ? View.VISIBLE : View.GONE);
                }
                this.onCheckedChangeListener.onCheckedChanged(this, checked);
            } catch (Throwable t) {
                String viewClassName = view != null ? view.getClass().getSimpleName() : "null reference to view";
                LOG.error("toogle() -> onCheckedListener.onCheckedChanged(checked=" + checked + ") failed. (Wraps " + viewClassName + ")", t);
            }
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

    public void setOnCheckedChangeListener(AbstractListAdapter<T>.CheckboxOnCheckedChangeListener listener) {
        this.onCheckedChangeListener = listener;
    }

    public void setTag(Object tag) {
        if (Ref.alive(viewRef)) {
            LOG.info("setTag("+tag+")");
            viewRef.get().setTag(tag);
        }
    }

    public Object getTag() {
        if (Ref.alive(viewRef)) {
            LOG.info("getTag()");
            return viewRef.get().getTag();
        } else {
            return null;
        }
    }
}

/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.higgses.griffin.utils.view;

import android.view.View;
import android.widget.EditText;

/**
 * EditText工具箱
 * Created by higgses on 14-4-17.
 */
public class GinUEditText
{
    /**
     * 获得焦点不显示hint，失去焦点显示hint
     *
     * @param editText
     * @param hintId
     */
    public static void lostFocusChangeHint(final EditText editText, final int hintId)
    {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    editText.setHint(null);
                } else
                {
                    editText.setHint(hintId);
                }
            }
        });
    }
}

package com.hippo.androidchinesestring;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by Hippo on 2015/2/19.
 */
public class YesNoDialog extends DialogWrapper {
    
    public static final int BUTTON_YES = 0;
    public static final int BUTTON_NO = 1;
    
    private String mMessage;
    private String mTitle;
    private OnButtonClickListener mListener;

    protected YesNoDialog(@Nullable Project project, boolean canBeParent,
            String message, String title, OnButtonClickListener listener) {
        super(project, canBeParent);

        mMessage = message;
        mTitle = title;
        mListener = listener;

        setTitle(title);
        setButtonsAlignment(SwingConstants.RIGHT);
        setOKButtonText("Yes");
        setCancelButtonText("No");
        setDoNotAskOption(null);
        init();
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();

        if (mListener != null) {
            mListener.onButtonClick(BUTTON_YES);
        }
    }

    @Override
    public void doCancelAction() {
        super.doCancelAction();

        if (mListener != null) {
            mListener.onButtonClick(BUTTON_NO);
        }
    }
    
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return new JLabel(mMessage);
    }

    public interface OnButtonClickListener {
        public void onButtonClick(int which);
    }
}

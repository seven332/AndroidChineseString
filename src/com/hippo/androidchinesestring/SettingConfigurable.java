package com.hippo.androidchinesestring;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by Hippo on 2015/2/17.
 */
public class SettingConfigurable implements Configurable {

    private static final String DISPLAY_NAME = "Android Chinese String";

    private JLabel mLableText;

    @Nls
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return DISPLAY_NAME;
    }

    @Nullable
    @Override
    public JComponent createComponent() {

        if (mLableText == null) {
            mLableText = new JLabel();
            mLableText.setText("Hello World !");
        }

        return mLableText;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public void reset() {

    }

    @Override
    public void disposeUIResources() {

    }
}

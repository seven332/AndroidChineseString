package com.hippo.androidchinesestring;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.ComboBox;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Hippo on 2015/2/17.
 */
public class ConvertSetting implements Configurable, ActionListener {

    private static final String DISPLAY_NAME = "Android Chinese String";

    private PropertiesComponent mProperties;
    
    private JComponent mComponent;
    private JComboBox mComboBoxOutputFileEncoding;
    
    private String mCurrentEncoding;
    private boolean mChangeOutputFileEncoding = false;

    public ConvertSetting() {
        mProperties = PropertiesComponent.getInstance();
    }

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

        if (mComponent == null) {
            mComponent = new JPanel();
            mComponent.setLayout(new BorderLayout());

            // Output file encoding
            JPanel ofePanel = new JPanel();
            ofePanel.setLayout(new BorderLayout(8, 8));
            JLabel ofeLabel = new JLabel("Output file encoding");
            mCurrentEncoding = mProperties.getValue(StorageDataKey.KEY_OUTPUT_FILE_ENCODING,
                    StorageDataKey.VALUE_OUTPUT_FILE_ENCODING_SYSTEM_DEFAULT);
            String[] encodings = {
                    StorageDataKey.VALUE_OUTPUT_FILE_ENCODING_SYSTEM_DEFAULT,
                    StorageDataKey.VALUE_OUTPUT_FILE_ENCODING_UTF_8
            };
            mComboBoxOutputFileEncoding = new ComboBox(encodings);
            mComboBoxOutputFileEncoding.setEnabled(true);
            mComboBoxOutputFileEncoding.setSelectedItem(mCurrentEncoding);
            mComboBoxOutputFileEncoding.addActionListener(this);

            ofePanel.add(BorderLayout.WEST, ofeLabel);
            ofePanel.add(BorderLayout.CENTER, mComboBoxOutputFileEncoding);

            mComponent.add(BorderLayout.NORTH, ofePanel);
        }

        return mComponent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newEncoding = (String) mComboBoxOutputFileEncoding.getSelectedItem();
        mChangeOutputFileEncoding = !mCurrentEncoding.equals(newEncoding);
    }

    @Override
    public boolean isModified() {
        return mChangeOutputFileEncoding;
    }

    @Override
    public void apply() throws ConfigurationException {
        mProperties.setValue(StorageDataKey.KEY_OUTPUT_FILE_ENCODING,
                mComboBoxOutputFileEncoding.getSelectedItem().toString());
        mChangeOutputFileEncoding = false;
    }

    @Override
    public void reset() {
        mComboBoxOutputFileEncoding.setSelectedItem(mCurrentEncoding);
        mChangeOutputFileEncoding = false;
    }

    @Override
    public void disposeUIResources() {

    }
}

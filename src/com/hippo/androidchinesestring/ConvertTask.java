package com.hippo.androidchinesestring;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by Hippo on 2015/2/17.
 */
public class ConvertTask extends Task.Backgroundable {

    private VirtualFile mSourceFile;
    private boolean mOverride;
    private PropertiesComponent mProperties;
    private transient boolean mCanOverride = false;

    private final Object mLock = new Object();

    public ConvertTask(Project project, VirtualFile file) {
        super(project, "Convert in progress", true);
        setCancelText("Convert has been canceled");
        mSourceFile = file;
        mProperties = PropertiesComponent.getInstance();
    }

    private void showErrorDialog(final String message) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                Messages.showDialog(message, "Error", new String[]{"OK"}, 0, null);
            }
        });
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        progressIndicator.setIndeterminate(true);

        VirtualFile parent = mSourceFile.getParent();
        if (parent == null) {
            showErrorDialog("Can't find the parent of the source strings.xml");
            return;
        }

        // Get source and destination
        String sourceValueName = parent.getName();
        ChineseLanguage.Chinese[] chs = ChineseLanguage.parserValueName(sourceValueName);
        if (chs == null) {
            showErrorDialog("The source strings.xml is not in Chinese value folder");
            return;
        }

        // Get res folder
        VirtualFile resFolder = parent.getParent();
        if (resFolder == null) {
            showErrorDialog("Can't get res folder");
            return;
        }


        // Get body for strings.xml
        String body;
        InputStream is = null;
        try {
            is =  mSourceFile.getInputStream();
            body = StreamUtil.readText(is, mSourceFile.getCharset().name());
        } catch (IOException e) {
            showErrorDialog("Open the source strings.xml error");
            return;
        } finally {
            Utils.closeQuietly(is);
        }

        ChineseLanguage.Chinese src = chs[0];
        if (src == ChineseLanguage.CHINESE_S) {
            convertS(body, resFolder, progressIndicator);
        } else if (src == ChineseLanguage.CHINESE_T_HK) {
            convertTHK(body, resFolder, progressIndicator);
        } else if (src == ChineseLanguage.CHINESE_T_TW) {
            convertTTW(body, resFolder, progressIndicator);
        }
    }

    private void convertS(@NotNull String body, @NotNull VirtualFile resFolder,
            @NotNull ProgressIndicator progressIndicator) {
        progressIndicator.setText("Simplified Chinese to Traditional Chinese (Hong Kong Standard)");
        try {
            convert(body, resFolder, ChineseLanguage.STRING_CHINSES_T_HK, "s2hk.json");
        } catch (Exception e) {
            showCovertException(e);
        }
        progressIndicator.setFraction(0.5);

        progressIndicator.setText("Simplified Chinese to Traditional Chinese (Taiwan Standard) with Taiwanese idiom");
        try {
            convert(body, resFolder, ChineseLanguage.STRING_CHINSES_T_TW, "s2twp.json");
        } catch (Exception e) {
            showCovertException(e);
        }
        progressIndicator.setFraction(1);
    }

    private void convertTHK(@NotNull String body, @NotNull VirtualFile resFolder,
            @NotNull ProgressIndicator progressIndicator) {
        // First Covert to Simplified Chinese, then to Taiwan Standard
        try {
            progressIndicator.setText("Traditional Chinese (Hong Kong Standard) to Simplified Chinese");
            String chineseS = convert(body, resFolder, ChineseLanguage.STRING_CHINSES_S, "hk2s.json");
            progressIndicator.setFraction(0.5);

            progressIndicator.setText("Simplified Chinese to Traditional Chinese (Taiwan Standard) with Taiwanese idiom");
            convert(chineseS, resFolder, ChineseLanguage.STRING_CHINSES_T_TW, "s2twp.json");
            progressIndicator.setFraction(1);
        } catch (Exception e) {
            showCovertException(e);
        }
    }

    private void convertTTW(@NotNull String body, @NotNull VirtualFile resFolder,
            @NotNull ProgressIndicator progressIndicator) {
        // First Covert to Simplified Chinese, then to Hong Kong Standard
        try {
            progressIndicator.setText("Traditional Chinese (Taiwan Standard) to Simplified Chinese with Mainland Chinese idiom");
            String chineseS = convert(body, resFolder, ChineseLanguage.STRING_CHINSES_S, "tw2sp.json");
            progressIndicator.setFraction(0.5);

            progressIndicator.setText("Simplified Chinese to Traditional Chinese (Hong Kong Standard)");
            convert(chineseS, resFolder, ChineseLanguage.STRING_CHINSES_T_HK, "s2hk.json");
            progressIndicator.setFraction(1);
        } catch (Exception e) {
            showCovertException(e);
        }
    }

    private String convert(String body, VirtualFile resFolder, String valueName, String json)
            throws Exception {
        String result = convertChinese(body, json);
        File valueFolder = new File(resFolder.getPath(), valueName);
        createDir(valueFolder);

        File stringsFile = new File(valueFolder, "strings.xml");
        writeStringToFile(result, stringsFile);

        openFileInEditor(stringsFile);

        return result;
    }

    private void openFileInEditor(File file) {
        LocalFileSystem lfs = LocalFileSystem.getInstance();

        VirtualFile virtualFile = lfs.findFileByIoFile(file);
        if (virtualFile != null) {
            final VirtualFile finalVirtualFile = virtualFile;
            virtualFile.refresh(true, false, new Runnable() {
                @Override
                public void run() {
                    openFileInEditor(finalVirtualFile);
                }
            });
        } else {
            virtualFile = lfs.refreshAndFindFileByIoFile(file);
            if (virtualFile != null) {
                openFileInEditor(virtualFile);
            } else {
                Log.d("Can't get VirtualFile");
            }
        }
    }
    
    private void openFileInEditor(@NotNull final VirtualFile virtualFile) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                final FileEditorManager editorManager = FileEditorManager.getInstance(myProject);
                editorManager.openFile(virtualFile, true);
            }
        });
    }

    private void createDir(File file) throws Exception {
        //noinspection ResultOfMethodCallIgnored
        file.mkdirs();

        if (!file.isDirectory()) {
            throw new Exception("Can't create dir " + file.getPath());
        }
    }

    private void showOverrideDialog(final String filePath) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                new YesNoDialog(myProject, false, "Override " + filePath + " ?", "Warning",
                        new YesNoDialog.OnButtonClickListener() {
                            @Override
                            public void onButtonClick(int which) {
                                mCanOverride = (which == YesNoDialog.BUTTON_YES);
                                synchronized (mLock) {
                                    mLock.notify();
                                }
                            }
                        }).show();
            }
        });
    }

    private void writeStringToFile(String body, File file) throws IOException {

        if (mProperties.getBoolean(StorageDataKey.KEY_ASK_BEFORE_OVERRIDE, true) &&
                file.exists()) {
            showOverrideDialog(file.getPath());

            synchronized (mLock) {
                try {
                    mLock.wait();
                } catch (InterruptedException e) {
                    // Empty
                }
            }

            if (!mCanOverride) {
                return;
            }
        }

        BufferedWriter writer;

        String encoding = mProperties.getValue(StorageDataKey.KEY_OUTPUT_FILE_ENCODING,
                StorageDataKey.VALUE_OUTPUT_FILE_ENCODING_SYSTEM_DEFAULT);
        if (encoding.equals(StorageDataKey.VALUE_OUTPUT_FILE_ENCODING_SYSTEM_DEFAULT) ||
                !Charset.isSupported(encoding)) {
            writer = new BufferedWriter
                    (new OutputStreamWriter(new FileOutputStream(file)));
        } else {
            writer = new BufferedWriter
                    (new OutputStreamWriter(new FileOutputStream(file), encoding));
        }
        writer.write(body);
        writer.close();
    }

    private void showCovertException(Exception e) {
        // TODO Need A better way
        showErrorDialog(e.getMessage());
    }

    private String convertChinese(@NotNull String body, @NotNull String json) throws Exception {
        String[][] args = {
                {"text", body},
                {"config", json},
                {"precise", "0"}
        };
        HttpHelper hh = new HttpHelper();
        String result = hh.postForm("http://opencc.byvoid.com/convert", args);
        int responseCode = hh.getResponseCode();
        if (responseCode >= 400) {
            throw new ResponseCodeException(responseCode, result);
        }
        return result;
    }
}

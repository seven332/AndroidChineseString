package com.hippo.androidchinesestring;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Hippo on 2015/2/17.
 */
public class ConvertAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        if (project == null) {
            Messages.showDialog("Can't get project", "Error", new String[]{"OK"}, 0, null);
            return;
        }

        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (file == null) {
            Messages.showDialog("Can't get selected file", "Error", new String[]{"OK"}, 0, null);
            return;
        }

        new ConvertTask(project, file).queue();
    }

    @Override
    public void update(AnActionEvent e) {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());

        boolean isStringXML = isValidStringXML(file);
        e.getPresentation().setEnabled(isStringXML);
        e.getPresentation().setVisible(isStringXML);
    }

    private boolean isValidStringXML(@Nullable VirtualFile file) {
        if (file == null || !file.getName().equals("strings.xml")) {
            return false;
        }

        VirtualFile parent = file.getParent();
        if (parent == null) {
            return false;
        }

        String parentName = parent.getName();
        if (ChineseLanguage.isChineseValueName(parentName)) {
            return true;
        }

        return false;
    }
}

package io.jenkins.x.eclipse.plugin.view;


import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);
        layout.setFixed(true);

        layout.addView(JenkinsView.ID, IPageLayout.LEFT, 1.0f, editorArea);
    }

}
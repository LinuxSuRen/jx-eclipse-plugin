package io.jenkins.x.eclipse.plugin.view;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.*;

import io.jenkins.x.client.PipelineClient;
import io.jenkins.x.client.kube.PipelineActivity;
import io.jenkins.x.client.kube.Statuses;
import io.jenkins.x.client.tree.BaseNode;
import io.jenkins.x.client.tree.BranchNode;
import io.jenkins.x.client.tree.BuildNode;
import io.jenkins.x.client.tree.OwnerNode;
import io.jenkins.x.client.tree.PipelineTreeModel;
import io.jenkins.x.client.tree.RepoNode;
import io.jenkins.x.client.tree.TreeItem;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.IAdaptable;

import javax.imageio.ImageIO;
import javax.inject.Inject;

/**
 * 
 * @author suren
 */
public class JenkinsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "io.jenkins.x.eclipse.plugin.view.JenkinsView";

	@Inject IWorkbench workbench;
	
	private TreeViewer viewer;
	private BeAction startPipelineAction;
	private BeAction openRepoAction;
	private BeAction reloadAction;

	private PipelineTreeModel model = PipelineTreeModel.newInstance();

	class ViewContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getElements(Object parent) {
			List<TreeItem> items = model.getChildrenItems();
			if(items == null || items.size() == 0) {
				return new Object[] {};
			}
			
			return items.toArray();
		}
		
		@Override
		public Object getParent(Object child) {
			if (child instanceof TreeItem) {
				return ((TreeItem)child).getParent();
			}
			return null;
		}
		
		@Override
		public Object [] getChildren(Object parent) {
			if (parent instanceof TreeItem) {
				return ((TreeItem)parent).getChildrenItems().toArray();
			}
			return new Object[0];
		}

		@Override
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeItem)
				return ((TreeItem)parent).getChildrenItems().size() > 0;
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			if(obj instanceof TreeItem) {
				return ((TreeItem) obj).getLabel();
			}
			return obj.toString();
		}
		
		public Image getImage(Object obj) {
			String image = null;
			if(obj instanceof BaseNode) {
				image = ((TreeItem) obj).getIconPath();
			}
			
			if(image != null && !"".equals(image)) {
				ClassLoader loader = getClass().getClassLoader();
				InputStream imageInput = loader.getResourceAsStream(image);
				if(imageInput != null) {
					return new Image(Display.getCurrent(), imageInput);
				}
			}
			
			return null;
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new ViewLabelProvider());
		
		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "jx-eclipse-plugin.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		
		IToolBarManager tbm= getViewSite().getActionBars().getToolBarManager();
		configureToolBar(tbm);
	}
	
	protected void configureToolBar(IToolBarManager mgr) {
		mgr.add(this.reloadAction);
	}

	private void makeActions() {
		this.startPipelineAction = new BeAction() {
			public void run() {
			}
		};
		startPipelineAction.setText("Start Pipeline");
		startPipelineAction.setToolTipText("Start Pipeline");
		startPipelineAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		openRepoAction = new BeAction() {
			public void run() {
				if(getUrl() == null) {
					return;
				}
				
				try {
					URI uri = new URI(this.getUrl());
					
					Desktop.getDesktop().browse(uri);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		openRepoAction.setText("Open Repo");
		openRepoAction.setToolTipText("Open Repo");
		
		reloadAction = new BeAction() {
			public void run() {
				viewer.getTree().redraw();
				viewer.refresh();
			}
		};
		reloadAction.setText("Reload");
		reloadAction.setToolTipText("Reload");
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}

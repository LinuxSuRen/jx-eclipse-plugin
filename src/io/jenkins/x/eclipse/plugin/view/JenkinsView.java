package io.jenkins.x.eclipse.plugin.view;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.*;

import io.fabric8.zjsonpatch.internal.guava.Strings;
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
import io.jenkins.x.eclipse.plugin.util.ActionUtils;

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
	private BeAction openAction;
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

	class ViewLabelProvider extends StyledCellLabelProvider {
		private Map<String, Image> imageCache = new HashMap<String, Image>();

		@Override
		public void update(ViewerCell cell) {
			Object obj = cell.getElement();
			StyledString styledString = new StyledString(getText(obj));

			cell.setText(styledString.toString());
			cell.setStyleRanges(styledString.getStyleRanges());
			cell.setImage(getImage(obj));
			
			super.update(cell);
		}

		private String getText(Object obj) {
			if(obj instanceof TreeItem) {
				return ((TreeItem) obj).getLabel();
			}
			return obj.toString();
		}
		
		private Image getImage(Object obj) {
			String imagePath = null;
			if(obj instanceof BaseNode) {
				imagePath = ((TreeItem) obj).getIconPath();
			}
			
			Image image = null;
			if(imagePath != null && !"".equals(imagePath)) {
				image = imageCache.get(imagePath);
				
				if(image == null) {
					ClassLoader loader = getClass().getClassLoader();
					InputStream imageInput = loader.getResourceAsStream(imagePath);
					if(imageInput != null) {
						image = new Image(Display.getCurrent(), imageInput);
						image = new Image(Display.getCurrent(), image.getImageData().scaledTo(20, 20));
						imageCache.put(imagePath, image);
					}
				}
			}
			
			return image;
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
		
		hookContextMenu();
		
		IToolBarManager tbm= getViewSite().getActionBars().getToolBarManager();
		configureToolBar(tbm);
	}
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ISelection obj = viewer.getSelection();
				IActionBars actionBars = getViewSite().getActionBars();
				IToolBarManager tbm = actionBars.getToolBarManager();
				
				ActionUtils.removeOpenBrowseActions(tbm);
				
				Object ele = null;
				if(obj instanceof TreeSelection) {
					ele = ((TreeSelection) obj).getFirstElement();
				}
				
				if(ele == null || !(ele instanceof TreeItem)) {
					actionBars.updateActionBars();
					return;
				}
				
				if(ele instanceof BuildNode) {
					BuildNode buildNode = (BuildNode) ele;
					
					String buildLogsUrl = buildNode.getBuildLogsUrl();
					String buildUrl = buildNode.getBuildUrl();
					String gitUrl = buildNode.getGitUrl();
					
					ActionUtils.addOpenBrowseAction(tbm, buildLogsUrl, "Logs");
					ActionUtils.addOpenBrowseAction(tbm, buildUrl, "Build");
					ActionUtils.addOpenBrowseAction(tbm, gitUrl, "Git Repo");
					actionBars.updateActionBars();
				}
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
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

package io.jenkins.x.eclipse.plugin.view;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import io.jenkins.x.client.tree.BuildNode;
import io.jenkins.x.client.tree.PipelineTreeModel;
import io.jenkins.x.client.tree.TreeItem;
import io.jenkins.x.client.tree.TreeModelListenerAdapter;
import io.jenkins.x.eclipse.plugin.Logger;
import io.jenkins.x.eclipse.plugin.util.ActionUtils;

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
	private BeAction reloadAction;

	private PipelineTreeModel model;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		initModel();
		
		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "jx-eclipse-plugin.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		
		hookContextMenu();
		
		IToolBarManager tbm= getViewSite().getActionBars().getToolBarManager();
		configureToolBar(tbm);
		
		update();
	}
	
	private void initModel() {
		if(model != null) {
			return;
		}
		
		model = PipelineTreeModel.newInstance();
		model.addListener(new TreeModelListenerAdapter() {

			@Override
			public void event(TreeItem item, String event) {
				update();
				
				Logger.info("Tree reload " + item.getLabel());
			}
		});
		viewer.setContentProvider(new ViewContentProvider(this.model));
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getViewSite());
	}
	
	private void stopModel() throws IOException {
		if(model == null) {
			return;
		}

		model.getPipelineClient().close();
		model = null;
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
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
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
				try {
					stopModel();
				} catch (IOException e) {
					Logger.error("Jx Client stop error.", e);
				}
				
				initModel();
			}
		};
		reloadAction.setText("Reload");
		reloadAction.setToolTipText("Reload");
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	private void update() {
		viewer.getTree().getParent().setRedraw(true);
		viewer.getTree().getParent().update();
		viewer.getTree().redraw();
		viewer.refresh();
	}
}

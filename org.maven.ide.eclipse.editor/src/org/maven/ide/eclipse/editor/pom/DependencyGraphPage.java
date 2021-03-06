/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.editor.pom;

import java.util.ArrayList;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.zest.core.viewers.ZoomContributionViewItem;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.CompositeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.maven.ide.eclipse.MavenPlugin;
import org.maven.ide.eclipse.core.IMavenConstants;
import org.maven.ide.eclipse.editor.MavenEditorImages;
import org.maven.ide.eclipse.project.IMavenProjectChangedListener;
import org.maven.ide.eclipse.project.MavenProjectChangedEvent;


/**
 * Dependency graph editor page
 * 
 * @author Eugene Kuleshov
 */
public class DependencyGraphPage extends FormPage implements IZoomableWorkbenchPart, IMavenProjectChangedListener, IPomFileChangedListener {

  @Override
  public void dispose() {
    MavenPlugin.getDefault().getMavenProjectManager().removeMavenProjectChangedListener(this);
    super.dispose();
  }

  private static final String DEPENDENCY_GRAPH = "Dependency Graph";

  protected static final Object[] EMPTY = new Object[0];

  final MavenPomEditor pomEditor;

  GraphViewer viewer;

  private IAction selectAllAction;

  private IAction showVersionAction;

  private IAction showGroupAction;

  private IAction showScopeAction;

  private IAction showIconAction;

  private IAction wrapLabelAction;

  private IAction showAllScopeAction;

  private IAction showCompileScopeAction;

  private IAction showTestScopeAction;

  private IAction showRuntimeScopeAction;

  private IAction showProvidedScopeAction;

  private IAction showSystemScopeAction;

  private IAction showResolvedAction;

  private IAction radialLayoutAction;

  private IAction showLegendAction;

  DependencyGraphLabelProvider graphLabelProvider;

  DependencyGraphContentProvider graphContentProvider;

  private ZoomContributionViewItem zoomContributionItem;

  private SearchControl searchControl;

  String currentScope = Artifact.SCOPE_TEST;

  public DependencyGraphPage(MavenPomEditor pomEditor) {
    super(pomEditor, IMavenConstants.PLUGIN_ID + ".dependency.graph", DEPENDENCY_GRAPH);
    this.pomEditor = pomEditor;
  }

  public AbstractZoomableViewer getZoomableViewer() {
    return viewer;
  }

  public MavenPomEditor getPomEditor() {
    return pomEditor;
  }

  protected void createFormContent(final IManagedForm managedForm) {
    MavenPlugin.getDefault().getMavenProjectManager().addMavenProjectChangedListener(this);

    ScrolledForm form = managedForm.getForm();
    form.setText(formatFormTitle());
    form.setExpandHorizontal(true);
    form.setExpandVertical(true);

    class MavenGraphViewer extends GraphViewer {
      public MavenGraphViewer(Composite parent, int style) {
        super(parent, style);
        setControl(new Graph(parent, style) {
          public Point computeSize(int wHint, int hHint, boolean changed) {
            return new Point(0, 0);
          }
        });
      }
    }

    Composite body = form.getBody();
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    body.setLayout(layout);

    viewer = new MavenGraphViewer(body, SWT.NONE);
    viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // viewer.setNodeStyle(ZestStyles.NODES_FISHEYE);
    viewer.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);

    // graphViewer.getGraphControl().setScrollBarVisibility(SWT.NONE);

    // graphViewer.setLayoutAlgorithm(new
    // HorizontalTreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
    // graphViewer.setLayoutAlgorithm(new
    // SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
    // graphViewer.setLayoutAlgorithm(new
    // RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING |
    // LayoutStyles.ENFORCE_BOUNDS));

    // graphViewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(
    // LayoutStyles.NO_LAYOUT_NODE_RESIZING | LayoutStyles.ENFORCE_BOUNDS, //
    // new LayoutAlgorithm[] { //
    // // new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING |
    // LayoutStyles.ENFORCE_BOUNDS), //
    // // new VerticalLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING |
    // LayoutStyles.ENFORCE_BOUNDS), //
    // // new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING |
    // LayoutStyles.ENFORCE_BOUNDS), //
    // // new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING |
    // LayoutStyles.ENFORCE_BOUNDS), //
    // // new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING |
    // LayoutStyles.ENFORCE_BOUNDS), //
    // }));

    viewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(
        LayoutStyles.NO_LAYOUT_NODE_RESIZING | LayoutStyles.ENFORCE_BOUNDS, // 
        new LayoutAlgorithm[] { //
            new DirectedGraphLayoutAlgorithm(
                LayoutStyles.NO_LAYOUT_NODE_RESIZING
                    | LayoutStyles.ENFORCE_BOUNDS), // 
            new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING
                | LayoutStyles.ENFORCE_BOUNDS) }));

    graphContentProvider = new DependencyGraphContentProvider();
    viewer.setContentProvider(graphContentProvider);

    graphLabelProvider = new DependencyGraphLabelProvider(viewer, graphContentProvider);
    viewer.setLabelProvider(graphLabelProvider);
    viewer.addSelectionChangedListener(graphLabelProvider);

    searchControl = new SearchControl("Find", managedForm);

    IToolBarManager toolBarManager = form.getForm().getToolBarManager();
    toolBarManager.add(searchControl);
    toolBarManager.add(new ScopeDropdown());
    toolBarManager.add(new Separator());

    toolBarManager.add(new Action("Refresh", MavenEditorImages.REFRESH) {
      public void run() {
        loadData();
      }
    });

    createActions();
    initPopupMenu();

    // compatibility proxy to support Eclipse 3.2
    FormUtils.decorateHeader(managedForm.getToolkit(), form.getForm());

    form.updateToolBar();

    searchControl.getSearchText().addFocusListener(new FocusAdapter() {
      public void focusGained(FocusEvent e) {
        selectElements();
      }
    });

    searchControl.getSearchText().addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        selectElements();
      }
    });

    updateGraphAsync(false, currentScope);
  }

  String formatFormTitle() {
    return DEPENDENCY_GRAPH + " [" + currentScope + "]";
  }

  void selectElements() {
    ArrayList<Artifact> elements = new ArrayList<Artifact>();
    String text = searchControl.getSearchText().getText();
    if(text.length() > 0) {
      Object[] nodeElements = viewer.getNodeElements();
      for(int i = 0; i < nodeElements.length; i++ ) {
        Artifact a = (Artifact) nodeElements[i];
        if(a.getGroupId().indexOf(text) > -1 || a.getArtifactId().indexOf(text) > -1) {
          elements.add(a);
        }
      }
    }
    viewer.setSelection(new StructuredSelection(elements), true);
  }

  private void createActions() {
    showVersionAction = new Action("Show &Version", SWT.CHECK) {
      public void run() {
        graphLabelProvider.setShowVersion(isChecked());
      }
    };
    showVersionAction.setChecked(true);

    showGroupAction = new Action("Show &Group", SWT.CHECK) {
      public void run() {
        graphLabelProvider.setShowGroup(isChecked());
      }
    };
    showGroupAction.setChecked(false);

    showScopeAction = new Action("Show &Scope", SWT.CHECK) {
      public void run() {
        graphLabelProvider.setShowScope(isChecked());
      }
    };
    showScopeAction.setChecked(true);

    showIconAction = new Action("Show &Icon", SWT.CHECK) {
      public void run() {
        graphLabelProvider.setShowIcon(isChecked());
      }
    };
    showIconAction.setChecked(true);

    wrapLabelAction = new Action("&Wrap Label", SWT.CHECK) {
      public void run() {
        graphLabelProvider.setWarpLabel(isChecked());
      }
    };
    wrapLabelAction.setChecked(true);

    showResolvedAction = new Action("Show Resolved", SWT.CHECK) {
      public void run() {
        graphContentProvider.setShowResolved(isChecked());
        updateGraphAsync(false, currentScope);
      }
    };
    showResolvedAction.setChecked(true);

    radialLayoutAction = new Action("&Radial Layout", SWT.CHECK) {
      public void run() {
        if(isChecked()) {
          viewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING, //
              new LayoutAlgorithm[] { //
              new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING),
                new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING),
              }));

        } else {
          viewer.setLayoutAlgorithm(new CompositeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING /*| LayoutStyles.ENFORCE_BOUNDS*/, // 
                  new LayoutAlgorithm[] { //
                      new DirectedGraphLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING /*| LayoutStyles.ENFORCE_BOUNDS*/), //
                new HorizontalShift(LayoutStyles.NO_LAYOUT_NODE_RESIZING)
              }));
        }
        viewer.getGraphControl().applyLayout();
      }
    };
    radialLayoutAction.setChecked(false);

    selectAllAction = new Action("Select &All") {
      public void run() {
        viewer.setSelection(new StructuredSelection(viewer.getNodeElements()));
      }
    };

    showLegendAction = new Action("Show UI Legend") {
      public void run() {
        DependencyGraphLegendPopup popup = new DependencyGraphLegendPopup(getEditor().getSite().getShell());
        popup.open();
      }
    };

//    showAllScopeAction = new Action("Show All Scopes", SWT.CHECK) {
//      public void run() {
//        updateGraph(null);
//        updateScopeActions(this);
//      }
//    };
//    showAllScopeAction.setChecked(true);
//
//    showCompileScopeAction = new Action("Show Compile Scope", SWT.CHECK) {
//      public void run() {
//        updateGraph(ArtifactScopeEnum.compile);
//        updateScopeActions(this);
//      }
//    };
//    showCompileScopeAction.setChecked(false);
//
//    showTestScopeAction = new Action("Show Test Scope", SWT.CHECK) {
//      public void run() {
//        updateGraph(ArtifactScopeEnum.test);
//        updateScopeActions(this);
//      }
//    };
//    showTestScopeAction.setChecked(false);
//
//    showRuntimeScopeAction = new Action("Show Runtime Scope", SWT.CHECK) {
//      public void run() {
//        updateGraph(ArtifactScopeEnum.runtime);
//        updateScopeActions(this);
//      }
//    };
//    showRuntimeScopeAction.setChecked(false);
//
//    showProvidedScopeAction = new Action("Show Provided Scope", SWT.CHECK) {
//      public void run() {
//        updateGraph(ArtifactScopeEnum.provided);
//        updateScopeActions(this);
//      }
//    };
//    showProvidedScopeAction.setChecked(false);
//
//    showSystemScopeAction = new Action("Show System Scope", SWT.CHECK) {
//      public void run() {
//        updateGraph(ArtifactScopeEnum.system);
//        updateScopeActions(this);
//      }
//    };
//    showSystemScopeAction.setChecked(false);

  }

  private void initPopupMenu() {
    zoomContributionItem = new ZoomContributionViewItem(this);

    MenuManager menuMgr = new MenuManager("graph");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {
      public void menuAboutToShow(IMenuManager manager) {
        fillContextMenu(manager);
      }
    });

    Menu menu = menuMgr.createContextMenu(viewer.getControl());
    viewer.getControl().setMenu(menu);
    
    getEditorSite().registerContextMenu(MavenPomEditor.EDITOR_ID + ".graph", menuMgr, viewer, false);
  }

  void updateScopeActions(IAction action) {
    showAllScopeAction.setChecked(showAllScopeAction == action);
    showCompileScopeAction.setChecked(showCompileScopeAction == action);
    showTestScopeAction.setChecked(showTestScopeAction == this);
    showRuntimeScopeAction.setChecked(showRuntimeScopeAction == action);
    showProvidedScopeAction.setChecked(showProvidedScopeAction == action);
    showSystemScopeAction.setChecked(showSystemScopeAction == action);
  }

  void updateGraphAsync(final boolean force, final String scope) {
    FormUtils.setMessage(getManagedForm().getForm(), "Resolving dependencies...", IMessageProvider.WARNING);

    new Job("Loading pom.xml") {
      protected IStatus run(IProgressMonitor monitor) {
        try {
          final DependencyNode dependencyNode = pomEditor.readDependencies(force, monitor, scope);
          getPartControl().getDisplay().asyncExec(new Runnable() {
            public void run() {
              FormUtils.setMessage(getManagedForm().getForm(), null, IMessageProvider.NONE);
              updateGraph(dependencyNode);
            }
          });
        } catch(final CoreException ex) {
          getPartControl().getDisplay().asyncExec(new Runnable() {
            public void run() {
              FormUtils.setMessage(getManagedForm().getForm(), ex.getMessage(), IMessageProvider.ERROR);
            }
          });
        }

        return Status.OK_STATUS;
      }
    }.schedule();
  }

  void updateGraph(DependencyNode node) {
    // MetadataGraph graph = scope==null && result!=null ? result.getGraph() : result.getGraph(scope);
    // MetadataGraph graph = result.getGraph(ArtifactScopeEnum.DEFAULT_SCOPE);
    // MetadataGraph graph = result.getGraph(MetadataResolutionRequestTypeEnum.versionedGraph);
    // MetadataGraph graph = result.getGraph(MetadataResolutionRequestTypeEnum.scopedGraph);

    viewer.setInput(node);

    IStructuredContentProvider contentProvider = (IStructuredContentProvider) viewer.getContentProvider();
    viewer.setSelection(new StructuredSelection(contentProvider.getElements(node)));
  }

  void fillContextMenu(IMenuManager manager) {
    manager.add(selectAllAction);
    
    if(!viewer.getSelection().isEmpty()) {
      manager.add(new Separator());
    }

    manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    manager.add(new Separator());
    
    {
      MenuManager subMenu = new MenuManager("Presentation");
      
      subMenu.add(showGroupAction);
      subMenu.add(showVersionAction);
      subMenu.add(showScopeAction);
      subMenu.add(showIconAction);
      subMenu.add(wrapLabelAction);
  
      subMenu.add(new Separator());
      subMenu.add(showResolvedAction);
      subMenu.add(radialLayoutAction);
      
      manager.add(subMenu);
    }

    manager.add(new Separator());
    manager.add(showLegendAction);

    manager.add(new Separator());
    manager.add(zoomContributionItem);
  }

/*
  private static final class MavenViewerFilter extends ViewerFilter {
    private final GraphViewer graphViewer;

    private boolean showCompile = true;
    private boolean showTest = true;
    private boolean showRuntime = true;
    private boolean showProvided = true;
    private boolean showSystem = true;

    public MavenViewerFilter(GraphViewer graphViewer) {
      this.graphViewer = graphViewer;
    }

    public void setShowCompile(boolean showCompile) {
      this.showCompile = showCompile;
      update();
    }

    public void setShowTest(boolean showTest) {
      this.showTest = showTest;
      update();
    }

    public void setShowRuntime(boolean showRuntime) {
      this.showRuntime = showRuntime;
      update();
    }

    public void setShowProvided(boolean showProvided) {
      this.showProvided = showProvided;
      update();
    }

    public void setShowSystem(boolean showSystem) {
      this.showSystem = showSystem;
      update();
    }

    private void update() {
      graphViewer.refresh(true);
      graphViewer.applyLayout();
    }

    public boolean select(Viewer viewer, Object parentElement, Object element) {
      if(element instanceof MetadataGraphVertex) {
        MetadataGraphVertex v = (MetadataGraphVertex) element;
        if(!select(v.getMd())) {
          return false;
        }
      }
      return true;
    }

    private boolean select(ArtifactMetadata md) {
      ArtifactScopeEnum artifactScope = md.getArtifactScope();
      if (artifactScope == ArtifactScopeEnum.compile) {
        return showCompile;
      } else if (artifactScope == ArtifactScopeEnum.test) {
        return showTest;
      } else if (artifactScope == ArtifactScopeEnum.runtime) {
        return showRuntime;
      } else if (artifactScope == ArtifactScopeEnum.provided) {
        return showProvided;
      } else if (artifactScope == ArtifactScopeEnum.system) {
        return showSystem;
      }
      return true;
    }

  }
*/

  public class ScopeAction extends Action {
    private final String scope;

    public ScopeAction(String text, String scope) {
      super(text, IAction.AS_RADIO_BUTTON);
      this.scope = scope;
    }

    public void run() {
      if(isChecked()) {
        currentScope = scope;
        IManagedForm managedForm = DependencyGraphPage.this.getManagedForm();
        managedForm.getForm().setText(formatFormTitle());
        updateGraphAsync(false, scope);
      }
    }
  }

  class ScopeDropdown extends Action implements IMenuCreator {
    private Menu menu;

    public ScopeDropdown() {
      setText("Scope");
      setImageDescriptor(MavenEditorImages.SCOPE);
      setMenuCreator(this);
    }

    public Menu getMenu(Menu parent) {
      return null;
    }

    public Menu getMenu(Control parent) {
      if(menu != null) {
        menu.dispose();
      }

      menu = new Menu(parent);
      addToMenu(menu, "all (test)", Artifact.SCOPE_TEST, currentScope);
      addToMenu(menu, "compile", Artifact.SCOPE_COMPILE, currentScope);
      addToMenu(menu, "runtime", Artifact.SCOPE_RUNTIME, currentScope);
      addToMenu(menu, "provided", Artifact.SCOPE_PROVIDED, currentScope);
      addToMenu(menu, "system", Artifact.SCOPE_SYSTEM, currentScope);
      return menu;
    }

    protected void addToMenu(Menu parent, String text, String scope, String currentScope) {
      ScopeAction action = new ScopeAction(text, scope);
      action.setChecked(scope.equals(currentScope));
      new ActionContributionItem(action).fill(parent, -1);
    }

    public void dispose() {
      if(menu != null) {
        menu.dispose();
        menu = null;
      }
    }
  }

  public void loadData() {
    updateGraphAsync(true, currentScope);
  }

  public void mavenProjectChanged(MavenProjectChangedEvent[] events, IProgressMonitor monitor) {
    if (getManagedForm() == null || getManagedForm().getForm() == null)
      return;
    
    for (int i=0; i<events.length; i++) {
      if (events[i].getSource().equals(((MavenPomEditor) getEditor()).getPomFile())) {
        // file has been changed. need to update graph  
        new UIJob("Reloading") {
          public IStatus runInUIThread(IProgressMonitor monitor) {
            loadData();
            FormUtils.setMessage(getManagedForm().getForm(), null, IMessageProvider.WARNING);
            return Status.OK_STATUS;
          }
        }.schedule();
      }
    }
  }

  public void fileChanged() {
    if (getManagedForm() == null || getManagedForm().getForm() == null)
      return;
    
    new UIJob("Reloading") {
      public IStatus runInUIThread(IProgressMonitor monitor) {
        FormUtils.setMessage(getManagedForm().getForm(), "Updating dependencies...", IMessageProvider.WARNING);
        return Status.OK_STATUS;
      }
    }.schedule();
  }
}

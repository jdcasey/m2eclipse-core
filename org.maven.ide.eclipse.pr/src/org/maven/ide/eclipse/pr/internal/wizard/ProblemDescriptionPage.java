/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.pr.internal.wizard;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.maven.ide.eclipse.pr.internal.ProblemReportingImages;
import org.maven.ide.eclipse.wizards.AbstractMavenWizardPage;


/**
 * A problem description page
 * 
 * @author Anton Kraev
 */
public class ProblemDescriptionPage extends AbstractMavenWizardPage {

  String description = "";

  String summary = "";

  private CheckboxTableViewer projectsViewer;

  private IStructuredSelection selection;

  protected ProblemDescriptionPage(IStructuredSelection selection) {
    super("problemDescriptionPage");
    this.selection = selection;
    setTitle("Problem details");
    setDescription("Enter problem summary and description");
    setImageDescriptor(ProblemReportingImages.REPORT_WIZARD);
    setPageComplete(false);
  }

  public void createControl(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout());
    setControl(composite);

    Label summaryLabel = new Label(composite, SWT.NONE);
    summaryLabel.setData("name", "summaryLabel");
    summaryLabel.setText("Problem &summary:");

    final Text summaryText = new Text(composite, SWT.BORDER);
    summaryText.setData("name", "summaryText");
    summaryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    summaryText.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        summary = summaryText.getText();
        updatePage();
      }
    });

    Label descriptionLabel = new Label(composite, SWT.NONE);
    descriptionLabel.setData("name", "descriptionLabel");
    descriptionLabel.setText("Problem &description:");

    final Text descriptionText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP);
    descriptionText.setData("name", "descriptionText");
    GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
    gd.heightHint = 100;
    descriptionText.setLayoutData(gd);
    descriptionText.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        description = descriptionText.getText();
        updatePage();
      }
    });

    Label projectsLabel = new Label(composite, SWT.NONE);
    projectsLabel.setData("name", "projectsLabel");
    projectsLabel.setText("Projects to &submit (with sources):");

    projectsViewer = CheckboxTableViewer.newCheckList(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.MULTI);
    projectsViewer.setData("name", "projectsViewer");
    projectsViewer.addCheckStateListener(new ICheckStateListener() {
      public void checkStateChanged(CheckStateChangedEvent event) {
        updatePage();
      }
    });
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    gridData.heightHint = 62;
    projectsViewer.getTable().setLayoutData(gridData);

    projectsViewer.setContentProvider(new WorkbenchContentProvider());
    projectsViewer.setLabelProvider(new WorkbenchLabelProvider());

    projectsViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
    if(selection != null && selection.getFirstElement() instanceof IProject) {
      projectsViewer.setCheckedElements(new Object[] {(IProject) selection.getFirstElement()});
    }
    
        Link link = new Link(composite, SWT.NONE);
        link.setFont(composite.getFont());
        link.setText("<A>Set Jira user (optional)</A>");
        link.addSelectionListener(new SelectionListener() {
          public void widgetSelected(SelectionEvent e) {
            doLaunchProblemReportingPrefs();
          }

          public void widgetDefaultSelected(SelectionEvent e) {
            doLaunchProblemReportingPrefs();
          }
        });
    new Label(composite, SWT.NONE);
        
        Button button = new Button(composite, SWT.NONE);
        button.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
        button.addSelectionListener(new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
            captureScreenFromClipboard();
          }
        });
        button.setText("&Capture Screenshot from Clipboard");

  }

  private void doLaunchProblemReportingPrefs() {
    PreferencesUtil.createPreferenceDialogOn(getShell(),
        "org.maven.ide.eclipse.preferences.ProblemReportingPreferencePage",
        new String[] {"org.maven.ide.eclipse.preferences.ProblemReportingPreferencePage"}, null).open();
  }

  protected void updatePage() {
    boolean isSummaryBlank = summary.trim().length() == 0;
    boolean isDescriptionBlank = description.trim().length() == 0;
    boolean isComplete = true;

    if(isSummaryBlank) {
      if(isDescriptionBlank) {
        setErrorMessage("Problem summary and description should not be blank");
      } else {
        setErrorMessage("Problem summary should not be blank");
      }
      isComplete = false;
    } else if(isDescriptionBlank) {
      setErrorMessage("Problem description should not be blank");
      isComplete = false;
    }

    setPageComplete(isComplete);
    if(isComplete) {
      setErrorMessage(null);

      selection = new StructuredSelection(projectsViewer.getCheckedElements());
    }
  }

  public String getProblemSummary() {
    return summary.trim();
  }

  public String getProblemDescription() {
    return description.trim();
  }

  public IStructuredSelection getSelectedProjects() {
    return selection;
  }

  private File screenCapture;
  
  public File getScreenCapture() {
    return screenCapture;
  }
  
  private void captureScreenFromClipboard() {
    final Display display = Display.getDefault();
    final Clipboard clipboard = new Clipboard(display);
    ImageData imageData = (ImageData) clipboard.getContents(ImageTransfer.getInstance());
    if(imageData != null) {
      Image image = new Image(display, imageData);
      ImageLoader loader = new ImageLoader();
      loader.data = new ImageData[] {image.getImageData()};
      screenCapture = new File(ResourcesPlugin.getPlugin().getStateLocation().toFile(), "capture.png" );
      System.out.println( "file: " + screenCapture);
      loader.save(screenCapture.getAbsolutePath(), SWT.IMAGE_PNG);
      image.dispose();
    }
  }
}

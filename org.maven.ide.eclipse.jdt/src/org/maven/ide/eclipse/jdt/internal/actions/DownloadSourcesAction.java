/*******************************************************************************
 * Copyright (c) 2007, 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.jdt.internal.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkingSet;

import org.maven.ide.eclipse.jdt.BuildPathManager;
import org.maven.ide.eclipse.jdt.MavenJdtPlugin;


public class DownloadSourcesAction implements IObjectActionDelegate {

  public static final String ID_SOURCES = "org.maven.ide.eclipse.downloadSourcesAction";

  public static final String ID_JAVADOC = "org.maven.ide.eclipse.downloadJavaDocAction";
  
  private IStructuredSelection selection;

  private String id;
  
  public DownloadSourcesAction(String id) {
    this.id = id;
  }

  public void run(IAction action) {
    if(selection != null) {
      BuildPathManager buildpathManager = MavenJdtPlugin.getDefault().getBuildpathManager();
      for(Iterator<?> it = selection.iterator(); it.hasNext();) {
        Object element = it.next();
        if(element instanceof IProject) {
          IProject project = (IProject) element;
          buildpathManager.scheduleDownload(project, ID_SOURCES.equals(id), !ID_SOURCES.equals(id));
        } else if(element instanceof IPackageFragmentRoot) {
          IPackageFragmentRoot fragment = (IPackageFragmentRoot) element;
          buildpathManager.scheduleDownload(fragment, ID_SOURCES.equals(id), !ID_SOURCES.equals(id));
        } else if(element instanceof IWorkingSet) {
          IWorkingSet workingSet = (IWorkingSet) element;
          for(IAdaptable adaptable : workingSet.getElements()) {
            IProject project = (IProject) adaptable.getAdapter(IProject.class);
            buildpathManager.scheduleDownload(project, ID_SOURCES.equals(id), !ID_SOURCES.equals(id));
          }
        }
      }
    }
  }

  public void selectionChanged(IAction action, ISelection selection) {
    if(selection instanceof IStructuredSelection) {
      this.selection = (IStructuredSelection) selection;
    } else {
      this.selection = null;
    }
  }

  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
  }

}

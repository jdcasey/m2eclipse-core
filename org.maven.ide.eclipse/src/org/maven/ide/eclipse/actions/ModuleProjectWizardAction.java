/*******************************************************************************
 * Copyright (c) 2007, 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import org.maven.ide.eclipse.wizards.MavenModuleWizard;

/**
 * A module project wizard action.
 */
public class ModuleProjectWizardAction implements IObjectActionDelegate {

  /** action id */
  public static final String ID =
    "org.maven.ide.eclipse.actions.moduleProjectWizardAction";
  
  /** the current selection */
  private IStructuredSelection selection;
  
  /** parent shell */
  private Shell parent;

  /** Runs the action. */
  public void run( IAction action ) {
    MavenModuleWizard wizard = new MavenModuleWizard();
    wizard.init( PlatformUI.getWorkbench(), selection );
    WizardDialog dialog = new WizardDialog( parent, wizard );
    dialog.open();
  }

  
  /** Sets the active workbench part. */
  public void setActivePart( IAction action, IWorkbenchPart part ) {
    parent = part.getSite().getShell();
  }


  /** Handles the selection change */
  public void selectionChanged( IAction action, ISelection selection ) {
    if( selection instanceof IStructuredSelection ) {
      this.selection = ( IStructuredSelection ) selection;
    }
  }
}

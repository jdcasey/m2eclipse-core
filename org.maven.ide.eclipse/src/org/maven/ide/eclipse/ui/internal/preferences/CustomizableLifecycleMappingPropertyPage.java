/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.ui.internal.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.maven.ide.eclipse.internal.lifecycle.AbstractLifecyclePropertyPage;
import org.maven.ide.eclipse.internal.lifecycle.ProjectConfiguratorsTable;

/**
 * CustomizableLifecycleMappingPropertyPage
 *
 * @author dyocum
 */
public class CustomizableLifecycleMappingPropertyPage extends AbstractLifecyclePropertyPage{


  /* (non-Javadoc)
   * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
   */
  public Control createContents(Composite parent) {
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(2, false));
    composite.setLayoutData(new GridData(GridData.FILL));
    new ProjectConfiguratorsTable(composite, getProject());
    return composite;
  }

  public void performDefaults(){
    //do nothing
  }
  
  public boolean performOk() {
    return true;
  }

  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.lifecycle.AbstractLifecyclePropertyPage#getMessage()
   */
  public String getMessage() {
    // TODO Auto-generated method getMessage
    return "Customizable Lifecycle Mapping";
  }
}

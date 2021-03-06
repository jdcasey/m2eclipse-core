/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.ui.internal.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import org.maven.ide.eclipse.core.MavenLogger;
import org.maven.ide.eclipse.internal.lifecycle.ILifecyclePropertyPage;
import org.maven.ide.eclipse.internal.lifecycle.LifecycleMappingPropertyPageFactory;
import org.maven.ide.eclipse.project.configurator.ILifecycleMapping;

/**
 * Maven project preference page
 *
 * @author Dan Yocum
 */
public class MavenProjectLifecycleMappingPage extends PropertyPage{

  private ILifecyclePropertyPage currentPage;
  
  public MavenProjectLifecycleMappingPage() {
    
    setTitle("");
  }

  protected Control createContents(Composite parent) {
    currentPage = loadCurrentPage((IProject)getElement());
    setMessage(currentPage.getName());
    return currentPage.createContents(parent);
  }
  
  private ILifecyclePropertyPage getErrorPage(String msg){
    SimpleLifecycleMappingPropertyPage p = new SimpleLifecycleMappingPropertyPage(msg);
    return p;
  }
  
  private ILifecyclePropertyPage getPage(ILifecycleMapping lifecycleMapping){
    ILifecyclePropertyPage page = LifecycleMappingPropertyPageFactory.getFactory().getPageForId(lifecycleMapping.getId(), getProject(), this.getShell());
    if(page == null){
      page = getErrorPage("No lifecycle mapping property page found.");
      page.setName(lifecycleMapping.getName());
    }
    return page;
  }

  private ILifecyclePropertyPage loadCurrentPage(IProject project){
    ILifecyclePropertyPage page = null;
    try{
      ILifecycleMapping lifecycleMapping = LifecycleMappingPropertyPageFactory.getLifecycleMapping(project);
      if(lifecycleMapping == null){
        return getErrorPage("No lifecycle mapping strategy found.");
      }
      page = getPage(lifecycleMapping);
      return page;
    } catch(CoreException ce){
      MavenLogger.log(ce);
      SimpleLifecycleMappingPropertyPage p = new SimpleLifecycleMappingPropertyPage("Unable to load the lifecycle mapping property page.");
      return p;
    }
  }

  protected void performDefaults() {
    currentPage.performDefaults();
  }

  protected IProject getProject() {
    return (IProject) getElement();
  }

  public boolean performOk() {
    return currentPage.performOk();
  }

  public void setElement(IAdaptable element){
    if(currentPage != null && element instanceof IProject){
      currentPage.setProject((IProject)element);
    }
    super.setElement(element);
  }

}


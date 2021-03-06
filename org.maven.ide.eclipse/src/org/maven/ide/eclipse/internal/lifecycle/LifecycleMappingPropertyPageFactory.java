/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.internal.lifecycle;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Shell;

import org.maven.ide.eclipse.MavenPlugin;
import org.maven.ide.eclipse.core.MavenLogger;
import org.maven.ide.eclipse.project.IMavenProjectFacade;
import org.maven.ide.eclipse.project.IProjectConfigurationManager;
import org.maven.ide.eclipse.project.MavenProjectManager;
import org.maven.ide.eclipse.project.ResolverConfiguration;
import org.maven.ide.eclipse.project.configurator.ILifecycleMapping;


/**
 * LifecycleMappingPropertyPageFactory
 * 
 * @author dyocum
 */
public class LifecycleMappingPropertyPageFactory {

  public static final String EXTENSION_LIFECYCLE_MAPPING_PROPERTY_PAGE = "org.maven.ide.eclipse.lifecycleMappingPropertyPage";

  private static final String ATTR_LIFECYCLE_MAPPING_ID = "lifecycleMappingId";

  private static final String ATTR_LIFECYCLE_PROP_NAME = "name";

  private static final String ATTR_LIFECYCLE_PROP_ID = "id";

  private static final String ELEMENT_LIFECYCLE_MAPPING_PROPERTY_PAGE = "lifecycleMappingPropertyPage";

  private static LifecycleMappingPropertyPageFactory factory;

  private Map<String, ILifecyclePropertyPage> pageMap;

  public static LifecycleMappingPropertyPageFactory getFactory() {
    if(factory == null) {
      factory = new LifecycleMappingPropertyPageFactory();
      factory.buildFactory();
    }
    return factory;
  }

  /**
   * Get a particular lifecycle property page, set in the project to use for the lifecycle mapping, set the Shell for
   * displaying dialogs.
   * 
   * @param id
   * @param project
   * @param shell
   * @return
   */
  public ILifecyclePropertyPage getPageForId(String id, IProject project, Shell shell) {
    if(id == null){
      //for the no-op (empty) lifecycle mapping, use that page
      id = "NULL";
    }
    ILifecyclePropertyPage page = getFactory().pageMap.get(id);
    if(page == null){
      return null;
    }
    page.setProject(project);
    page.setShell(shell);
    return page;
  }
  
  public ILifecyclePropertyPage getPage(String id){
    return getFactory().pageMap.get(id);
  }

  public void buildFactory() {
    pageMap = new HashMap<String, ILifecyclePropertyPage>();
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IExtensionPoint configuratorsExtensionPoint = registry.getExtensionPoint(EXTENSION_LIFECYCLE_MAPPING_PROPERTY_PAGE);
    if(configuratorsExtensionPoint != null) {
      IExtension[] configuratorExtensions = configuratorsExtensionPoint.getExtensions();
      for(IExtension extension : configuratorExtensions) {
        IConfigurationElement[] elements = extension.getConfigurationElements();
        for(IConfigurationElement element : elements) {
          if(element.getName().equals(ELEMENT_LIFECYCLE_MAPPING_PROPERTY_PAGE)) {
            try {
              Object o = element.createExecutableExtension("class");
              ILifecyclePropertyPage propPage = (ILifecyclePropertyPage) o;
              String id = element.getAttribute(ATTR_LIFECYCLE_MAPPING_ID);

              propPage.setLifecycleMappingId(id);
              String name = element.getAttribute(ATTR_LIFECYCLE_PROP_NAME);
              propPage.setName(name);

              String pageId = element.getAttribute(ATTR_LIFECYCLE_PROP_ID);
              if(pageId != null) {
                propPage.setPageId(pageId);
              }
              pageMap.put(id, propPage);
            } catch(CoreException ex) {
              MavenLogger.log(ex);
            }
          }
        }
      }
    }
  }

  public static IMavenProjectFacade getProjectFacade(IProject project) {
    MavenProjectManager projectManager = MavenPlugin.getDefault().getMavenProjectManager();
    return projectManager.create(project, new NullProgressMonitor());
  }

  public static ResolverConfiguration getResolverConfiguration(IProject project) {
    MavenProjectManager projectManager = MavenPlugin.getDefault().getMavenProjectManager();
    return projectManager.getResolverConfiguration(project);
  }

  public static ILifecycleMapping getLifecycleMapping(IProject project) throws CoreException {
    IMavenProjectFacade facade = getProjectFacade(project);
    ILifecycleMapping lifecycleMapping = null;
    IProjectConfigurationManager configurationManager = MavenPlugin.getDefault().getProjectConfigurationManager();
    lifecycleMapping = configurationManager.getLifecycleMapping(facade, new NullProgressMonitor());
    return lifecycleMapping;
  }
}

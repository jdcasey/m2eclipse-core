/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.internal.project;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.codehaus.plexus.util.xml.Xpp3Dom;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;

import org.maven.ide.eclipse.MavenPlugin;
import org.maven.ide.eclipse.core.IMavenConstants;
import org.maven.ide.eclipse.core.MavenLogger;
import org.maven.ide.eclipse.project.IMavenProjectFacade;
import org.maven.ide.eclipse.project.configurator.AbstractBuildParticipant;
import org.maven.ide.eclipse.project.configurator.AbstractLifecycleMapping;
import org.maven.ide.eclipse.project.configurator.AbstractProjectConfigurator;
import org.maven.ide.eclipse.project.configurator.ILifecycleMapping;
import org.maven.ide.eclipse.project.configurator.MojoExecutionBuildParticipant;
import org.maven.ide.eclipse.project.configurator.ProjectConfigurationRequest;


/**
 * CustomizableLifecycleMapping
 * 
 * @author igor
 */
public class CustomizableLifecycleMapping extends AbstractLifecycleMapping implements ILifecycleMapping {
  public static final String EXTENSION_ID = "customizable";

  public CustomizableLifecycleMapping() {

  }
  
  public List<AbstractProjectConfigurator> getProjectConfigurators(IMavenProjectFacade facade, IProgressMonitor monitor)
      throws CoreException {
    MavenProject mavenProject = facade.getMavenProject(monitor);
    Plugin plugin = mavenProject.getPlugin("org.maven.ide.eclipse:lifecycle-mapping");

    if(plugin == null) {
      throw new IllegalArgumentException("no mapping");
    }

    // TODO assert version

    Map<String, AbstractProjectConfigurator> configuratorsMap = new LinkedHashMap<String, AbstractProjectConfigurator>();
    for(AbstractProjectConfigurator configurator : getProjectConfigurators()) {
      configuratorsMap.put(configurator.getId(), configurator);
    }

    Xpp3Dom config = (Xpp3Dom) plugin.getConfiguration();

    if(config == null) {
      throw new IllegalArgumentException("Empty lifecycle mapping configuration");
    }

    Xpp3Dom configuratorsDom = config.getChild("configurators");
    Xpp3Dom executionsDom = config.getChild("mojoExecutions");

    List<AbstractProjectConfigurator> configurators = new ArrayList<AbstractProjectConfigurator>();
    
    if (configuratorsDom != null) {
      for(Xpp3Dom configuratorDom : configuratorsDom.getChildren("configurator")) {
        String configuratorId = configuratorDom.getAttribute("id");
        AbstractProjectConfigurator configurator = configuratorsMap.get(configuratorId);
        if(configurator == null) {
          String message = "Configurator '"+configuratorId+"' is not available for project '"+facade.getProject().getName()+"'. To enable full functionality, install the configurator and run Maven->Update Project Configuration.";
          MavenPlugin.getDefault().getLog().log(new Status(IStatus.WARNING, IMavenConstants.PLUGIN_ID, message));
          MavenPlugin.getDefault().getConsole().logError(message);
//          throw new IllegalArgumentException(message);
        }else{
          configurators.add(configurator);
        }
      }
    }
    
    if (executionsDom != null) {
      for(Xpp3Dom execution : executionsDom.getChildren("mojoExecution")) {
        String strRunOnIncremental = execution.getAttribute("runOnIncremental");
        configurators.add(MojoExecutionProjectConfigurator.fromString(execution.getValue(), toBool(strRunOnIncremental, true)));
      }
    }

    return configurators;
  }
  
  private boolean toBool(String value, boolean def) {
    if(value == null || value.length() == 0) {
      return def;
    }
    return Boolean.parseBoolean(value);
  }

  public List<AbstractBuildParticipant> getBuildParticipants(IMavenProjectFacade facade, IProgressMonitor monitor)
      throws CoreException {

    List<AbstractProjectConfigurator> configurators = getProjectConfigurators(facade, monitor);

    return getBuildParticipants(facade, configurators, monitor);
  }
  
  public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
    super.configure(request, monitor);

    addMavenBuilder(request.getProject(), monitor);
  }
  
  public List<String> getPotentialMojoExecutionsForBuildKind(IMavenProjectFacade projectFacade, int kind,
      IProgressMonitor progressMonitor) {
    List<String> mojos = new LinkedList<String>();
    try {
      for (MojoExecution execution : projectFacade.getExecutionPlan(progressMonitor).getExecutions()) {
        for (AbstractProjectConfigurator configurator : getProjectConfigurators(projectFacade, progressMonitor)) {
          AbstractBuildParticipant participant = configurator.getBuildParticipant(execution);
          if (participant != null && participant instanceof MojoExecutionBuildParticipant) {
            if(((MojoExecutionBuildParticipant)participant).appliesToBuildKind(kind)) {
              MojoExecution mojo = ((MojoExecutionBuildParticipant)participant).getMojoExecution();
              mojos.add(MojoExecutionUtils.getExecutionKey(mojo));
            }
          }
        }
      }
    } catch(CoreException ex) {
      MavenLogger.log(ex);
    }
    return mojos;
  }

}

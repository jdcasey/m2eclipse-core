/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.project;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import org.apache.maven.lifecycle.MavenExecutionPlan;
import org.apache.maven.project.MavenProject;

import org.maven.ide.eclipse.embedder.ArtifactKey;
import org.maven.ide.eclipse.embedder.ArtifactRef;
import org.maven.ide.eclipse.embedder.ArtifactRepositoryRef;
import org.maven.ide.eclipse.project.configurator.ILifecycleMapping;

/**
 * IMavenProjectFacade
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *
 * @author Igor Fedorenko
 */
public interface IMavenProjectFacade {

  /**
   * Returns project relative paths of resource directories
   */
  IPath[] getResourceLocations();

  /**
   * Returns project relative paths of test resource directories
   */
  IPath[] getTestResourceLocations();

  IPath[] getCompileSourceLocations();

  IPath[] getTestCompileSourceLocations();

  /**
   * Returns project resource for given file system location or null the location is outside of project.
   * 
   * @param resourceLocation absolute file system location
   * @return IPath the full, absolute workspace path resourceLocation
   */
  IPath getProjectRelativePath(String resourceLocation);

  /**
   * Returns the full, absolute path of this project maven build output directory relative to the workspace or null if
   * maven build output directory cannot be determined or outside of the workspace.
   */
  IPath getOutputLocation();

  /**
   * Returns the full, absolute path of this project maven build test output directory relative to the workspace or null
   * if maven build output directory cannot be determined or outside of the workspace.
   */
  IPath getTestOutputLocation();

  IPath getFullPath();

  /**
   * Lazy load and cache MavenProject instance
   */
  MavenProject getMavenProject(IProgressMonitor monitor) throws CoreException;

  /**
   * Returns cached MavenProject instance associated with this facade or null,
   * if the cache has not been populated yet.
   */
  MavenProject getMavenProject();

  /**
   * Lazy load and cache build execution plan
   */
  MavenExecutionPlan getExecutionPlan(IProgressMonitor monitor) throws CoreException;

  String getPackaging();

  IProject getProject();

  IFile getPom();

  File getPomFile();

  /**
   * Returns the full, absolute path of the given file relative to the workspace. Returns null if the file does not
   * exist or is not a member of this project.
   */
  IPath getFullPath(File file);

  /**
   * Visits trough Maven project artifacts and modules
   * 
   * @param visitor a project visitor used to visit Maven project
   * @param flags flags to specify visiting behavior. See {@link IMavenProjectVisitor#LOAD},
   *          {@link IMavenProjectVisitor#NESTED_MODULES}.
   */
  void accept(IMavenProjectVisitor visitor, int flags) throws CoreException;

  void accept(IMavenProjectVisitor2 visitor, int flags, IProgressMonitor monitor) throws CoreException;

  List<String> getMavenProjectModules();

  Set<ArtifactRef> getMavenProjectArtifacts();

  ResolverConfiguration getResolverConfiguration();

  /**
   * @return true if maven project needs to be re-read from disk  
   */
  boolean isStale();

  ArtifactKey getArtifactKey();

  /**
   * Associates the value with the key in session (i.e. transient) context.
   * Intended as a mechanism to cache state derived from MavenProject.
   * Session properties are cleared when MavenProject is re-read from disk.
   * 
   * @see #getSessionProperty(String)
   */
  public void setSessionProperty(String key, Object value);

  /**
   * @return the value associated with the key in session context or null
   *   if the key is not associated with any value.
   *   
   * @see #setSessionProperty(String, Object)
   */
  public Object getSessionProperty(String key);

  public Set<ArtifactRepositoryRef> getArtifactRepositoryRefs();

  public Set<ArtifactRepositoryRef> getPluginArtifactRepositoryRefs();

  public ILifecycleMapping getLifecycleMapping(IProgressMonitor monitor) throws CoreException;
}

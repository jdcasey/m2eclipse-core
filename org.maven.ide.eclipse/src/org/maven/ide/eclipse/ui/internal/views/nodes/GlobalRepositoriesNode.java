/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.ui.internal.views.nodes;

import java.util.List;

import org.maven.ide.eclipse.repository.IRepository;
import org.maven.ide.eclipse.repository.IRepositoryRegistry;


/**
 * Parent node for all artifact repositories and mirrors defined in settings.xml.
 * 
 * @author dyocum
 */
public class GlobalRepositoriesNode extends AbstractRepositoriesNode {

  public String getName() {
    return "Global Repositories";
  }

  protected List<IRepository> getRepositories() {
    return repositoryRegistry.getRepositories(IRepositoryRegistry.SCOPE_SETTINGS);
  }

}

/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.embedder;

import org.maven.ide.components.pom.Model;

/**
 * Project updater
 *
 * @author Eugene Kuleshov
 */
public abstract class ProjectUpdater {

  public abstract void update(Model model);

}

/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;


/**
 * Common interface implemented by all m2e background processing jobs. 
 *
 * @author igor
 */
public interface IBackgroundProcessingQueue {
  public void join() throws InterruptedException;

  public boolean isEmpty();
  
  public IStatus run(IProgressMonitor monitor);
  
  public boolean cancel();
}

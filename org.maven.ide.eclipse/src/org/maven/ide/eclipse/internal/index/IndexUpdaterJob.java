/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.internal.index;

import java.util.ArrayList;
import java.util.Stack;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.IProgressConstants;

import org.maven.ide.eclipse.actions.OpenMavenConsoleAction;
import org.maven.ide.eclipse.core.IMavenConstants;
import org.maven.ide.eclipse.core.MavenConsole;
import org.maven.ide.eclipse.jobs.IBackgroundProcessingQueue;

class IndexUpdaterJob extends Job implements IBackgroundProcessingQueue {

  public static class IndexUpdaterRule implements ISchedulingRule {

    public boolean contains(ISchedulingRule rule) {
      return rule == this;
    }

    public boolean isConflicting(ISchedulingRule rule) {
      return rule == this;
    }
    
  }  

  public interface IndexCommand {
    abstract void run(IProgressMonitor monitor) throws CoreException;
  }

  private final Stack<IndexUpdaterJob.IndexCommand> updateQueue = new Stack<IndexUpdaterJob.IndexCommand>();

  public IndexUpdaterJob(NexusIndexManager indexManager, MavenConsole console) {
    super("Updating indexes");
    setProperty(IProgressConstants.ACTION_PROPERTY, new OpenMavenConsoleAction());
    setRule(new IndexUpdaterRule());
  }

  public void addCommand(IndexUpdaterJob.IndexCommand indexCommand) {
    updateQueue.add(indexCommand);
  }

  public IStatus run(IProgressMonitor monitor) {
    monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);

    ArrayList<IStatus> problems = new ArrayList<IStatus>();

    while(!updateQueue.isEmpty()) {
      if (monitor.isCanceled()) {
        throw new OperationCanceledException();
      }

      IndexUpdaterJob.IndexCommand command = updateQueue.pop();
      try {
        command.run(monitor);
      } catch(CoreException ex) {
        problems.add(ex.getStatus());
      }
    }

    monitor.done();

    return problems.isEmpty()? Status.OK_STATUS: new MultiStatus(IMavenConstants.PLUGIN_ID, -1, problems.toArray(new IStatus[problems.size()]), null, null);
  }

  public boolean isEmpty() {
    return updateQueue.isEmpty();
  }
  
}
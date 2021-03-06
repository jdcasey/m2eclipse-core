/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.internal.launch;

import org.eclipse.core.runtime.Plugin;

public class MavenLaunchPlugin extends Plugin {
  
  private static MavenLaunchPlugin instance;

  public MavenLaunchPlugin() {
    instance = this;
  }

  public static MavenLaunchPlugin getDefault() {
    return instance;
  }
}

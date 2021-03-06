/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.internal.lifecycle;

/**
 * AbstractPropertyPageExtensionPoint
 *
 * @author dyocum
 */
public class AbstractPropertyPageExtensionPoint implements ILifecyclePropertyPageExtensionPoint {

  private String name;
  private String id;
  private String lifecycleMappingId;
  
  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.lifecycle.ILifecyclePropertyPageExtensionPoint#getLifecycleMappingId()
   */
  public String getLifecycleMappingId() {
    return lifecycleMappingId;
  }

  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.lifecycle.ILifecyclePropertyPageExtensionPoint#getName()
   */
  public String getName() {
    return name;
  }

  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.lifecycle.ILifecyclePropertyPageExtensionPoint#getPageId()
   */
  public String getPageId() {
    return id;
  }

  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.lifecycle.ILifecyclePropertyPageExtensionPoint#setLifecycleMappingId(java.lang.String)
   */
  public void setLifecycleMappingId(String lifecycleMappingId) {
    this.lifecycleMappingId = lifecycleMappingId;
  }

  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.lifecycle.ILifecyclePropertyPageExtensionPoint#setName()
   */
  public void setName(String name) {
    this.name = name;
  }

  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.lifecycle.ILifecyclePropertyPageExtensionPoint#setPageId()
   */
  public void setPageId(String id) {
    this.id = id;
  }

}

/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.scm;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.maven.ide.eclipse.core.IMavenConstants;

/**
 * An SCM URL wrapper used to adapt 3rd party resources:
 * 
 * <pre>
 * scm:{scm_provider}:{scm_provider_specific_part}
 * </pre>
 * 
 * @see http://maven.apache.org/scm/scm-url-format.html
 * @see org.eclipse.core.runtime.IAdapterManager
 *
 * @author Eugene Kuleshov
 */
public class ScmUrl {
  private final String scmUrl;
  private final String scmParentUrl;
  private final ScmTag tag;

  public ScmUrl(String scmUrl) {
    this(scmUrl, null);
  }
  
  public ScmUrl(String scmUrl, String scmParentUrl) {
    this(scmUrl, null, null);
  }

  public ScmUrl(String scmUrl, String scmParentUrl, ScmTag tag) {
    this.scmUrl = scmUrl;
    this.scmParentUrl = scmParentUrl;
    this.tag = tag;
  }
  
  /**
   * Return SCM url
   */
  public String getUrl() {
    return scmUrl;
  }
  
  /**
   * Return SCM url for the parent folder
   */
  public String getParentUrl() {
    return scmParentUrl;
  }
  
  /**
   * Return SCM tag
   */
  public ScmTag getTag() {
    return this.tag;
  }
  
  /**
   * Return provider-specific part of the SCM url
   * 
   * @return
   */
  public String getProviderUrl() {
    try {
      String type = ScmUrl.getType(scmUrl);
      return scmUrl.substring(type.length() + 5);
      
    } catch(CoreException ex) {
      return null;
    }
  }

  public static synchronized String getType(String url) throws CoreException {
    if(!url.startsWith("scm:")) {
      throw new CoreException(new Status(IStatus.ERROR, IMavenConstants.PLUGIN_ID, -1, "Invalid SCM url " + url, null));
    }
    int n = url.indexOf(":", 4);
    if(n == -1) {
      throw new CoreException(new Status(IStatus.ERROR, IMavenConstants.PLUGIN_ID, -1, "Invalid SCM url " + url, null));
    }
    return url.substring(4, n);
  }

}

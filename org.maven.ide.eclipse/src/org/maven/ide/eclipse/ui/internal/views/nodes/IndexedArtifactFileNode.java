/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.ui.internal.views.nodes;

import org.eclipse.swt.graphics.Image;

import org.maven.ide.eclipse.MavenImages;
import org.maven.ide.eclipse.index.IIndex;
import org.maven.ide.eclipse.index.IndexedArtifactFile;
import org.maven.ide.eclipse.internal.index.NexusIndexManager;

/**
 * IndexedArtifactFileNode
 *
 * @author dyocum
 */
public class IndexedArtifactFileNode implements IMavenRepositoryNode, IArtifactNode {

  private IndexedArtifactFile artifactFile;

  public IndexedArtifactFileNode(IndexedArtifactFile artifactFile){
    this.artifactFile = artifactFile;
  }
  
  public IndexedArtifactFile getIndexedArtifactFile(){
    return this.artifactFile;
  }
  
  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.ui.internal.views.IMavenRepositoryNode#getChildren()
   */
  public Object[] getChildren() {
    return null;
  }

  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.ui.internal.views.IMavenRepositoryNode#getName()
   */
  public String getName() {
    String label = artifactFile.artifact;
    if(artifactFile.classifier != null) {
      label += " : " + artifactFile.classifier;
    }
    if(artifactFile.version != null) {
      label += " : " + artifactFile.version;
    }
    return label;
  }

  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.ui.internal.views.IMavenRepositoryNode#hasChildren()
   */
  public boolean hasChildren() {
    return false;
  }

  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.ui.internal.views.nodes.IMavenRepositoryNode#getImage()
   */
  public Image getImage() {
    if(artifactFile.sourcesExists == IIndex.PRESENT) {
      return MavenImages.IMG_VERSION_SRC;
    }
    return MavenImages.IMG_VERSION;

  }

  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.ui.internal.views.nodes.IArtifactNode#getDocumentKey()
   */
  public String getDocumentKey() {
    return NexusIndexManager.getDocumentKey(artifactFile.getArtifactKey());
  }

  /* (non-Javadoc)
   * @see org.maven.ide.eclipse.ui.internal.views.nodes.IMavenRepositoryNode#isUpdating()
   */
  public boolean isUpdating() {
    return false;
  }

}

/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.editor.composites;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Label provider for <code>String</code> entries
 * 
 * @author Eugene Kuleshov
 */
public class StringLabelProvider extends LabelProvider {
  private final Image img;

  public StringLabelProvider(Image img) {
    this.img = img;
  }

  public String getText(Object element) {
    if(element instanceof String) {
      return (String) element;
    }
    return "";
  }

  public Image getImage(Object element) {
    return img;
  }
}
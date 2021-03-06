/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.editor.xml.search;

/**
 * Packaging representation.
 * 
 * @author Lukas Krecan
 */
public enum Packaging {
  ALL(null), //
  PLUGIN("maven-plugin"), //
  POM("pom");

  private final String text;

  private Packaging(String text) {
    this.text = text;
  }

  /**
   * Text representation of the packaging.
   */
  public String getText() {
    return text;
  }
}

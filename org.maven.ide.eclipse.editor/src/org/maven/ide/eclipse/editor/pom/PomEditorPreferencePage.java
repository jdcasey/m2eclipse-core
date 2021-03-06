/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.editor.pom;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.maven.ide.eclipse.MavenPlugin;
import org.maven.ide.eclipse.core.Messages;

public class PomEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  public static final String P_DEFAULT_POM_EDITOR_PAGE = "eclipse.m2.defaultPomEditorPage";
  public static final String P_SHOW_ADVANCED_TABS = "eclipse.m2.showAdvancedTabs";
  
  final MavenPlugin plugin;
  private Composite parent;
  
  public PomEditorPreferencePage() {
    super(GRID);
    setPreferenceStore(MavenPlugin.getDefault().getPreferenceStore());

    plugin = MavenPlugin.getDefault();
  }

  public void init(IWorkbench workbench) {
  }

  /*
   * Creates the field editors. Field editors are abstractions of the common GUI
   * blocks needed to manipulate various types of preferences. Each field editor
   * knows how to save and restore itself.
   */
  public void createFieldEditors() {
    parent = getFieldEditorParent();
    addField(new BooleanFieldEditor(P_DEFAULT_POM_EDITOR_PAGE, Messages.getString("pomEditor.defaultPage"), parent));
    addField(new BooleanFieldEditor(P_SHOW_ADVANCED_TABS, Messages.getString("pomEditor.showAdvancedTabs"), parent));
  }
}

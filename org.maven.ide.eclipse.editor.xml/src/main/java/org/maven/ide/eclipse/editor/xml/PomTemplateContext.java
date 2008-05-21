/*******************************************************************************
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.maven.ide.eclipse.editor.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.TemplateStore;

import org.maven.ide.eclipse.editor.xml.search.ArtifactInfo;
import org.maven.ide.eclipse.editor.xml.search.Packaging;
import org.maven.ide.eclipse.editor.xml.search.SearchEngine;


/**
 * Context types.
 * 
 * @author Lukas Krecan
 */
public enum PomTemplateContext {
  
  UNKNOWN("unknown"), //
  
  PROJECT("project"), //
  
  PARENT("parent"), //
  
  PROPERTIES("properties"), //
  
  DEPENDENCIES("dependencies"), //
  
  EXCLUSIONS("exclusions"), //
  
  PLUGINS("plugins"), //

  EXECUTIONS("executions"), //
  
  PROFILES("profiles"), //
  
  REPOSITORIES("repositories"), //
  
  GROUP_ID("groupId") {
    @Override
    public void addTemplates(Collection<Template> templates, Node node, String prefix) {
      for(String groupId : getSearchEngine().findGroupIds(prefix, getPackaging(node), getContainingArtifact(node))) {
        templates.add(new Template(groupId, groupId, getContextTypeId(), groupId, false));
      }
    }
  },

  ARTIFACT_ID("artifactId") {
    @Override
    public void addTemplates(Collection<Template> templates, Node node, String prefix) {
      String groupId = getGroupId(node);
      if(groupId != null) {
        for(String artifactId : getSearchEngine().findArtifactIds(groupId, prefix, getPackaging(node),
            getContainingArtifact(node))) {
          templates.add(new Template(artifactId, groupId + ":" + artifactId, getContextTypeId(), artifactId, false));
        }
      }
    }
  },

  VERSION("version") {
    @Override
    public void addTemplates(Collection<Template> templates, Node node, String prefix) {
      String groupId = getGroupId(node);
      String artifactId = getArtifactId(node);
      if(groupId != null && artifactId != null) {
        for(String version : getSearchEngine().findVersions(groupId, artifactId, prefix, getPackaging(node))) {
          templates.add(new Template(version, groupId + ":" + artifactId + ":" + version, //
              getContextTypeId(), version, false));
        }
      }
    }
  },

  CLASSIFIER("classifier") {
    @Override
    public void addTemplates(Collection<Template> templates, Node node, String prefix) {
      String groupId = getGroupId(node);
      String artifactId = getArtifactId(node);
      String version = getVersion(node);
      if(groupId != null && artifactId != null && version != null) {
        for(String classifier : getSearchEngine().findClassifiers(groupId, artifactId, version, prefix,
            getPackaging(node))) {
          templates.add(new Template(classifier, groupId + ":" + artifactId + ":" + version + ":" + classifier,
              getContextTypeId(), classifier, false));
        }
      }
    }
  },

  TYPE("type") {
    @Override
    public void addTemplates(Collection<Template> templates, Node node, String prefix) {
      String groupId = getGroupId(node);
      String artifactId = getArtifactId(node);
      String version = getVersion(node);
      if(groupId != null && artifactId != null && version != null) {
        for(String type : getSearchEngine().findTypes(groupId, artifactId, version, prefix, getPackaging(node))) {
          templates.add(new Template(type, groupId + ":" + artifactId + ":" + version + ":" + type, //
              getContextTypeId(), type, false));
        }
      }
    }
  },
  
  PHASE("phase") {
    @Override
    public void addTemplates(Collection<Template> templates, Node node, String prefix) {
      // TODO the following list should be derived from the packaging handler (the actual lifecycle)
      
      // Clean Lifecycle
      add(templates, "pre-clean", "Executes processes needed prior to the actual project cleaning");
      add(templates, "clean", "Removes all files generated by the previous build");
      add(templates, "post-clean", "Executes processes needed to finalize the project cleaning");
      
      // Default Lifecycle
      add(templates, "validate", "Validate the project is correct and all necessary information is available");
      add(templates, "generate-sources", "Generate any source code for inclusion in compilation");
      add(templates, "process-sources", "Process the source code, for example to filter any values");
      add(templates, "generate-resources", "Generate resources for inclusion in the package");
      add(templates, "process-resources", "Copy and process the resources into the destination directory, ready for packaging");
      add(templates, "compile", "Compile the source code of the project");
      add(templates, "process-classes", "Post-process the generated files from compilation, for example to do bytecode enhancement on Java classes");
      add(templates, "generate-test-sources", "Generate any test source code for inclusion in compilation");
      add(templates, "process-test-sources", "Process the test source code, for example to filter any values");
      add(templates, "generate-test-resources", "Create resources for testing");
      add(templates, "process-test-resources", "Copy and process the resources into the test destination directory");
      add(templates, "test-compile", "Compile the test source code into the test destination directory");
      add(templates, "process-test-classes", "Post-process the generated files from test compilation, for example to do bytecode enhancement on Java classes. For Maven 2.0.5 and above");
      add(templates, "test", "Run tests using a suitable unit testing framework. These tests should not require the code be packaged or deployed");
      add(templates, "prepare-package", "Perform any operations necessary to prepare a package before the actual packaging. This often results in an unpacked, processed version of the package. (Maven 2.1 and above)");
      add(templates, "package", "Take the compiled code and package it in its distributable format, such as a JAR");
      add(templates, "pre-integration-test", "Perform actions required before integration tests are executed. This may involve things such as setting up the required environment");
      add(templates, "integration-test", "Process and deploy the package if necessary into an environment where integration tests can be run");
      add(templates, "post-integration-test", "Perform actions required after integration tests have been executed. This may including cleaning up the environment");
      add(templates, "verify", "Run any checks to verify the package is valid and meets quality criteria");
      add(templates, "install", "Install the package into the local repository, for use as a dependency in other projects locally");
      add(templates, "deploy", "Done in an integration or release environment, copies the final package to the remote repository for sharing with other developers and projects");
      
      // Site Lifecycle
      add(templates, "pre-site", "Executes processes needed prior to the actual project site generation");
      add(templates, "site", "Generates the project's site documentation");
      add(templates, "post-site", "Executes processes needed to finalize the site generation, and to prepare for site deployment");
      add(templates, "site-deploy", "Deploys the generated site documentation to the specified web server");
    }

    private boolean add(Collection<Template> templates, String name, String description) {
      return templates.add(new Template(name, description, getContextTypeId(), name, false));
    }
  };

  private static final String PREFIX = MvnIndexPlugin.PLUGIN_ID + ".templates.contextType.";

  private final String nodeName;

  private PomTemplateContext(String nodeName) {
    this.nodeName = nodeName;
  }

  /**
   * Return templates depending on the context type.
   */
  public Template[] getTemplates(Node node, String prefix) {
    Collection<Template> templates = new ArrayList<Template>();
    addTemplates(templates, node, prefix);
    return templates.toArray(new Template[templates.size()]);
  }
  
  protected void addTemplates(Collection<Template> templates, Node currentNode, String prefix) {
    TemplateStore store = getTemplateStore();
    if(store != null) {
      templates.addAll(Arrays.asList(store.getTemplates(getContextTypeId())));
    }
  }

  protected String getNodeName() {
    return nodeName;
  }
  
  public String getContextTypeId() {
    return PREFIX + nodeName;
  }

  public static PomTemplateContext fromId(String contextTypeId) {
    for(PomTemplateContext context : values()) {
      if(context.getContextTypeId().equals(contextTypeId)) {
        return context;
      }
    }
    return UNKNOWN;
  }

  public static PomTemplateContext fromNodeName(String idSuffix) {
    for(PomTemplateContext context : values()) {
      if(context.getNodeName().equals(idSuffix)) {
        return context;
      }
    }
    return UNKNOWN;
  }

  private static TemplateStore getTemplateStore() {
    return MvnIndexPlugin.getDefault().getTemplateStore();
  }

  private static SearchEngine getSearchEngine() {
    return MvnIndexPlugin.getDefault().getSearchEngine();
  }
  
  
  ///
  
  /**
   * Returns containing artifactInfo for exclusions. Otherwise returns null.
   */
  protected ArtifactInfo getContainingArtifact(Node currentNode) {
    if(isExclusion(currentNode)) {
      Node node = currentNode.getParentNode().getParentNode();
      return getArtifactInfo(node);
    }
    return null;
  }

  /**
   * Returns artifact info from siblings of given node.
   */
  private ArtifactInfo getArtifactInfo(Node node) {
    return new ArtifactInfo(getGroupId(node), getArtifactId(node), getVersion(node), //
        getSiblingTextValue(node, "classifier"), getSiblingTextValue(node, "type"));
  }

  /**
   * Returns required packaging.
   */
  protected Packaging getPackaging(Node currentNode) {
    if(isPlugin(currentNode)) {
      return Packaging.PLUGIN;
    } else if(isParent(currentNode)) {
      return Packaging.POM;
    }
    return Packaging.ALL;
  }

  /**
   * Returns true if user is editing plugin dependency.
   */
  private boolean isPlugin(Node currentNode) {
    return "plugin".equals(currentNode.getParentNode().getNodeName());
  }
  
  /**
   * Returns true if user is editing plugin dependency exclusion.
   */
  private boolean isExclusion(Node currentNode) {
    return "exclusion".equals(currentNode.getParentNode().getNodeName());
  }
  
  /**
   * Returns true if user is editing parent dependency.
   */
  private boolean isParent(Node currentNode) {
    return "parent".equals(currentNode.getParentNode().getNodeName());
  }
  
  protected String getGroupId(Node currentNode) {
    return getSiblingTextValue(currentNode, "groupId");
  }

  protected static String getArtifactId(Node currentNode) {
    return getSiblingTextValue(currentNode, "artifactId");
  }

  protected static String getVersion(Node currentNode) {
    return getSiblingTextValue(currentNode, "version");
  }

  private static String getSiblingTextValue(Node sibling, String name) {
    Node node = getSiblingWithName(sibling, name);
    return getNodeTextValue(node);
  }

  /**
   * Returns sibling with given name.
   */
  private static Node getSiblingWithName(Node node, String name) {
    NodeList nodeList = node.getParentNode().getChildNodes();
    for(int i = 0; i < nodeList.getLength(); i++ ) {
      if(name.equals(nodeList.item(i).getNodeName())) {
        return nodeList.item(i);
      }
    }
    return null;
  }

  /**
   * Returns text value of the node.
   */
  private static String getNodeTextValue(Node node) {
    if(node != null && hasOneNode(node.getChildNodes())) {
      return ((Text) node.getChildNodes().item(0)).getData().trim();
    }
    return null;
  }

  /**
   * Returns true if there is only one node in the nodeList.
   */
  private static boolean hasOneNode(NodeList nodeList) {
    return nodeList != null && nodeList.getLength() == 1;
  }

}
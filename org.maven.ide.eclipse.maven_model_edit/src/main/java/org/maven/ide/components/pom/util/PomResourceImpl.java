/**
 * Copyright (c) 2008 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *
 * $Id$
 */
package org.maven.ide.components.pom.util;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.maven.ide.components.pom.Model;
import org.maven.ide.components.pom.translators.SSESyncResource;

/**
 * <!-- begin-user-doc --> The <b>Resource </b> associated with the package.
 * <!-- end-user-doc -->
 * @see org.maven.ide.components.pom.util.PomResourceFactoryImpl
 * @generated NOT
 */
public class PomResourceImpl extends SSESyncResource {
	/**
	 * Creates an instance of the resource.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @param uri the URI of the new resource.
	 * @generated
	 */
	public PomResourceImpl(URI uri) {
		super(uri);
	}
	
	public void load(Map<?, ?> options) throws IOException {
	  super.load(options);
	}

	public Model getModel() {
		return (Model) getContents().get(0);
	}

} // PomResourceImpl

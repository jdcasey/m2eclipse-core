package org.maven.ide.eclipse.embedder;

import junit.framework.TestCase;

public class ArtifactKeyTest extends TestCase {

  public void testPortableString() {
    ArtifactKey k1 = new ArtifactKey("g", "a", "v", "c");
    assertEquals(k1, ArtifactKey.fromPortableString(k1.toPortableString()));

    ArtifactKey k2 = new ArtifactKey("g", "a", null, null);
    assertEquals(k2, ArtifactKey.fromPortableString(k2.toPortableString()));
  
    ArtifactKey k3 = new ArtifactKey("g", null, null, "c");
    assertEquals(k3, ArtifactKey.fromPortableString(k3.toPortableString()));
  }

}

package de.julianweinelt.datacat;

/**
 * Represents a Maven library dependency to be resolved and bundled into the .yarn.
 *
 * Usage in pom.xml:
 * <pre>
 * &lt;libraries&gt;
 *   &lt;library&gt;
 *     &lt;groupId&gt;com.google.guava&lt;/groupId&gt;
 *     &lt;artifactId&gt;guava&lt;/artifactId&gt;
 *     &lt;version&gt;32.1.2-jre&lt;/version&gt;
 *   &lt;/library&gt;
 * &lt;/libraries&gt;
 * </pre>
 */
public class LibraryDefinition {

    /**
     * Maven groupId of the library (e.g. com.google.guava)
     */
    private String groupId;

    /**
     * Maven artifactId of the library (e.g. guava)
     */
    private String artifactId;

    /**
     * Version of the library (e.g. 32.1.2-jre)
     */
    private String version;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }
}

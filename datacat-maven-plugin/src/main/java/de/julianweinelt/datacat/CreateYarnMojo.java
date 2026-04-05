package de.julianweinelt.datacat;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Creates a .yarn package file from themes, plugins, and libraries.
 *
 * Invoke via: mvn datacat:createyarn
 */
@Mojo(name = "createyarn")
public class CreateYarnMojo extends AbstractMojo {

    // -------------------------------------------------------------------------
    // Injected Maven infrastructure
    // -------------------------------------------------------------------------

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;

    @Component
    private RepositorySystem repositorySystem;

    // -------------------------------------------------------------------------
    // Plugin configuration parameters (set in the consuming project's pom.xml)
    // -------------------------------------------------------------------------

    /**
     * Paths to JSON theme files to include under themes/ in the .yarn.
     *
     * <pre>
     * &lt;themes&gt;
     *   &lt;theme&gt;src/main/datacat/themes/dark.json&lt;/theme&gt;
     *   &lt;theme&gt;src/main/datacat/themes/light.json&lt;/theme&gt;
     * &lt;/themes&gt;
     * </pre>
     */
    @Parameter(property = "datacat.themes")
    private List<String> themes;

    /**
     * Absolute or project-relative paths to JAR plugin files to include under plugins/.
     *
     * <pre>
     * &lt;plugins&gt;
     *   &lt;plugin&gt;/opt/myapp/plugin-auth.jar&lt;/plugin&gt;
     *   &lt;plugin&gt;target/my-plugin.jar&lt;/plugin&gt;
     * &lt;/plugins&gt;
     * </pre>
     */
    @Parameter(property = "datacat.plugins")
    private List<String> plugins;

    /**
     * Maven library dependencies to resolve and bundle under plugins/.
     *
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
    @Parameter(property = "datacat.libraries")
    private List<LibraryDefinition> libraries;

    /**
     * Output file name for the .yarn archive (without extension).
     * Defaults to the project artifactId.
     */
    @Parameter(defaultValue = "${project.artifactId}", property = "datacat.outputName")
    private String outputName;

    /**
     * Output directory for the generated .yarn file.
     * Defaults to ${project.build.directory} (i.e. target/).
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "datacat.outputDirectory")
    private File outputDirectory;

    // -------------------------------------------------------------------------
    // Mojo entry point
    // -------------------------------------------------------------------------

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("=== DataCat: Creating .yarn package ===");

        // Ensure output directory exists
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        File yarnFile = new File(outputDirectory, outputName + ".yarn");
        getLog().info("Output: " + yarnFile.getAbsolutePath());

        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(yarnFile))) {

            // --- meta.json placeholder (user provides the real one) ----------
            addMetaJsonPlaceholder(zip);

            // --- themes/ -----------------------------------------------------
            if (themes != null && !themes.isEmpty()) {
                getLog().info("Packaging " + themes.size() + " theme(s)...");
                for (String themePath : themes) {
                    File themeFile = resolveFile(themePath);
                    addFileToZip(zip, themeFile, "themes/" + themeFile.getName());
                }
            } else {
                getLog().info("No themes configured.");
            }

            // --- plugins/ (direct JAR paths) ---------------------------------
            if (plugins != null && !plugins.isEmpty()) {
                getLog().info("Packaging " + plugins.size() + " plugin JAR(s)...");
                for (String pluginPath : plugins) {
                    File pluginFile = resolveFile(pluginPath);
                    addFileToZip(zip, pluginFile, "plugins/" + pluginFile.getName());
                }
            } else {
                getLog().info("No plugin JARs configured.");
            }

            // --- plugins/ (resolved Maven libraries) -------------------------
            if (libraries != null && !libraries.isEmpty()) {
                getLog().info("Resolving and packaging " + libraries.size() + " librar(y/ies)...");
                for (LibraryDefinition lib : libraries) {
                    File jar = resolveLibrary(lib);
                    addFileToZip(zip, jar, "plugins/" + jar.getName());
                }
            } else {
                getLog().info("No libraries configured.");
            }

        } catch (IOException e) {
            throw new MojoExecutionException("Failed to create .yarn file: " + e.getMessage(), e);
        }

        getLog().info("Successfully created: " + yarnFile.getName());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Writes an empty meta.json stub so the archive always contains it.
     * The user is expected to replace / overwrite this with their own meta.json
     * by placing a meta.json in the project base directory — which takes priority.
     */
    private void addMetaJsonPlaceholder(ZipOutputStream zip) throws IOException {
        // Check if user has a meta.json in the project root
        File userMeta = new File(project.getBasedir(), "meta.json");
        if (userMeta.exists()) {
            getLog().info("Using project meta.json: " + userMeta.getAbsolutePath());
            addFileToZip(zip, userMeta, "meta.json");
        } else {
            getLog().warn("No meta.json found in project root – adding empty placeholder.");
            zip.putNextEntry(new ZipEntry("meta.json"));
            String placeholder = "{\n  \"name\": \"" + outputName + "\",\n  \"version\": \""
                    + project.getVersion() + "\"\n}\n";
            zip.write(placeholder.getBytes("UTF-8"));
            zip.closeEntry();
        }
    }

    /**
     * Resolves a path that is either absolute or relative to the project base dir.
     */
    private File resolveFile(String path) throws MojoExecutionException {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(project.getBasedir(), path);
        }
        if (!file.exists()) {
            throw new MojoExecutionException("File not found: " + file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new MojoExecutionException("Path is not a file: " + file.getAbsolutePath());
        }
        return file;
    }

    /**
     * Resolves a Maven library artifact via the local/remote repositories and
     * returns the JAR file from the local repository cache.
     */
    private File resolveLibrary(LibraryDefinition lib) throws MojoExecutionException {
        getLog().info("  Resolving library: " + lib);

        Artifact artifact = repositorySystem.createArtifact(
                lib.getGroupId(),
                lib.getArtifactId(),
                lib.getVersion(),
                "jar"
        );

        ArtifactResolutionRequest request = new ArtifactResolutionRequest()
                .setArtifact(artifact)
                .setLocalRepository(session.getLocalRepository())
                .setRemoteRepositories(project.getRemoteArtifactRepositories());

        ArtifactResolutionResult result = repositorySystem.resolve(request);

        if (!result.isSuccess()) {
            StringBuilder errors = new StringBuilder();
            result.getExceptions().forEach(e -> errors.append(e.getMessage()).append("; "));
            throw new MojoExecutionException(
                    "Could not resolve library " + lib + ": " + errors
            );
        }

        File jar = artifact.getFile();
        if (jar == null || !jar.exists()) {
            throw new MojoExecutionException("Resolved artifact has no file: " + lib);
        }

        getLog().info("  Resolved: " + jar.getName());
        return jar;
    }

    /**
     * Writes a single file into the ZIP stream at the given entry path.
     */
    private void addFileToZip(ZipOutputStream zip, File file, String entryPath)
            throws IOException {

        getLog().debug("  Adding to yarn: " + entryPath + "  (" + file.getAbsolutePath() + ")");
        zip.putNextEntry(new ZipEntry(entryPath));

        try (InputStream in = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                zip.write(buffer, 0, read);
            }
        }

        zip.closeEntry();
    }
}

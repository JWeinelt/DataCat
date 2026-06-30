package de.julianweinelt.datacat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.julianweinelt.datacat.dbx.api.plugins.yarn.FileManifest;
import de.julianweinelt.datacat.dbx.api.plugins.yarn.YarnManifest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Mojo(name = "createyarn", defaultPhase = LifecyclePhase.PACKAGE, requiresDependencyResolution =  ResolutionScope.RUNTIME)
public class CreateYarnMojo extends AbstractMojo {
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "datacat.includeProjectJar", defaultValue = "false")
    private boolean includeProjectJar;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;
    @Component
    private RepositorySystem repositorySystem;

    @Parameter(property = "datacat.themes")
    private List<String> themes;

    @Parameter(property = "datacat.authors")
    private List<String> authors;

    @Parameter(property = "datacat.description")
    private String description;
    @Parameter(property = "datacat.version")
    private String version;

    @Parameter(property = "datacat.plugins")
    private List<String> plugins;

    @Parameter(property = "datacat.libraries")
    private List<LibraryDefinition> libraries;

    @Parameter(defaultValue = "${project.artifactId}", property = "datacat.outputName")
    private String outputName;

    @Parameter(defaultValue = "${project.build.directory}", property = "datacat.outputDirectory")
    private File outputDirectory;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("=== DataCat: Creating .yarn package ===");

        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        File yarnFile = new File(outputDirectory, outputName + ".yarn");
        getLog().info("Output: " + yarnFile.getAbsolutePath());

        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(yarnFile))) {

            addMetaJsonPlaceholder(zip);
            addPluginIcon(zip);

            if (includeProjectJar) {
                File projectJar = new File(
                        project.getBuild().getDirectory(),
                        project.getBuild().getFinalName() + ".jar"
                );

                if (!projectJar.exists()) {
                    throw new MojoExecutionException(
                            "Project JAR not found: " + projectJar.getAbsolutePath()
                    );
                }

                addFileToZip(zip, projectJar, "datacat/" + projectJar.getName());
                addPluginJson(zip);
                //plugins.add(projectJar.getName());
            }

            addFileManifest(zip);
            addYarnManifest(zip);

            if (themes != null && !themes.isEmpty()) {
                getLog().info("Packaging " + themes.size() + " theme(s)...");
                for (String themePath : themes) {
                    File themeFile = resolveFile(themePath);
                    addFileToZip(zip, themeFile, "themes/" + themeFile.getName());
                }
            } else {
                getLog().info("No themes configured.");
            }

            if (plugins != null && !plugins.isEmpty()) {
                getLog().info("Packaging " + plugins.size() + " plugin JAR(s)...");
                for (String pluginPath : plugins) {
                    File pluginFile = resolveFile(pluginPath);
                    addFileToZip(zip, pluginFile, "plugins/" + pluginFile.getName());
                }
            } else {
                getLog().info("No plugin JARs configured.");
            }

            if (libraries != null && !libraries.isEmpty()) {
                getLog().info("Resolving and packaging " + libraries.size() + " library/ies...");
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

    private void addMetaJsonPlaceholder(ZipOutputStream zip) throws IOException {
        zip.putNextEntry(new ZipEntry("meta.json"));
        JsonObject o = new JsonObject();
        o.addProperty("name", outputName);
        o.addProperty("version", version);
        o.addProperty("description", description);
        o.add("authors", GSON.toJsonTree(authors));
        o.add("libraries", GSON.toJsonTree(libraries));
        o.add("plugins", GSON.toJsonTree(plugins));

        zip.write(o.toString().getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private void addPluginJson(ZipOutputStream zip) throws IOException {
        zip.putNextEntry(new ZipEntry("plugin.json"));
        JsonArray a = new JsonArray();
        JsonObject o = new JsonObject();
        o.addProperty("name", project.getName());
        o.addProperty("version", project.getVersion());
        a.add(o);
        zip.write(a.toString().getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private void addFileManifest(ZipOutputStream zip) throws IOException {
        zip.putNextEntry(new ZipEntry("manifest.json"));
        FileManifest manifest = new FileManifest("1", "", "na");
        zip.write(GSON.toJson(manifest).getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }
    private void addYarnManifest(ZipOutputStream zip) throws IOException {
        zip.putNextEntry(new ZipEntry("yarn.json"));
        YarnManifest manifest = new YarnManifest(outputName, version, version);
        zip.write(GSON.toJson(manifest).getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private void addPluginIcon(ZipOutputStream zip) throws IOException {
        File yarnIcon = new File(project.getBasedir(), "yarn.png");
        if (yarnIcon.exists()) {
            try (InputStream iS = new FileInputStream(yarnIcon)) {
                zip.putNextEntry(new ZipEntry("icon.png"));
                iS.transferTo(zip);
                zip.closeEntry();
            }
            getLog().info("Using project yarn.png: " + yarnIcon.getAbsolutePath());
        } else {
            try (InputStream in = getClass().getResourceAsStream("/plugin-default.png")) {
                if (in == null) {
                    getLog().warn("Default icon not found in plugin resources, skipping icon.");
                    return;
                }
                zip.putNextEntry(new ZipEntry("icon.png"));
                in.transferTo(zip);
                zip.closeEntry();
            }
            getLog().info("No yarn.png found, using default plugin icon.");
        }
    }

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

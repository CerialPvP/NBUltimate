package cc.cerial.nbultimate;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class LibraryLoader implements PluginLoader {
    public static final Dependency SIMPLE_YAML =
            new Dependency(new DefaultArtifact("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4"), null);

    public static final Dependency NOTEBLOCKLIB =
            new Dependency(new DefaultArtifact("net.raphimc:NoteBlockLib:2.1.3-SNAPSHOT"), null);

    public static final Dependency CLASSGRAPH =
            new Dependency(new DefaultArtifact("io.github.classgraph:classgraph:4.8.176"), null);

    public static final Dependency INVUI =
            new Dependency(new DefaultArtifact("xyz.xenondevs.invui:invui:pom:1.37"), null);


    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        classpathBuilder.getContext().getLogger().info("Loading libraries...");
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        // Add repos
        resolver.addRepository(new RemoteRepository.Builder("JitPack", "default", "https://jitpack.io").build());
        resolver.addRepository(new RemoteRepository.Builder("Lenni0451 Snapshots", "default", "https://maven.lenni0451.net/snapshots").build());
        resolver.addRepository(new RemoteRepository.Builder("Maven Central", "default", "https://repo1.maven.org/maven2/").build());
        resolver.addRepository(new RemoteRepository.Builder("xenondevs", "default", "https://repo.xenondevs.xyz/releases/").build());

        resolver.addDependency(SIMPLE_YAML);
        resolver.addDependency(NOTEBLOCKLIB);
        resolver.addDependency(CLASSGRAPH);
        resolver.addDependency(INVUI);

        classpathBuilder.addLibrary(resolver);
    }
}

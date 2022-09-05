package com.exasol.releasedroid.usecases.repository.version;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides information about a repository's version including the current semantic version and a list of one or
 * multiple git tags potentially with different prefixes depending on the repository's modules, see {@link Module}.
 */
public class Revision {

    /**
     * @param version main version of the repository without any prefix
     * @param sources list of source {@link Module}s from repository's configuration file
     * @return new instance of {@link Revision}
     */
    public static Revision from(final String version, final List<Map<String, Object>> sources) {
        return new Revision(version, sources.stream().map(Module::from));
    }

    private final String version;
    private final List<Module> modules;

    Revision(final String version, final Stream<Module> modules) {
        this.version = version;
        this.modules = modules.collect(Collectors.toList());
    }

    /**
     * @return list of git tags required for comprehensive description of the repository's version.
     */
    // [impl->dsn~creating-git-tags~1]
    public List<String> getTags() {
        final List<String> result = new ArrayList<>();
        result.add((containsGolangRootModule() ? "v" : "") + this.version);
        for (final String folder : golangSubFolders()) {
            final String folderVersion = folder.replaceFirst("[^/]+$", "v" + this.version);
            result.add(folderVersion);
        }
        return result;
    }

    /**
     * @return {@code true} if repository contains a golang root module
     */
    boolean containsGolangRootModule() {
        return this.modules.stream() //
                .filter(Module::isGolang) //
                .anyMatch(Predicate.not(Module::isSubfolder));
    }

    /**
     * @return list of folders containing golang modules in subfolders
     */
    List<String> golangSubFolders() {
        return this.modules.stream() //
                .filter(Module::isGolang) //
                .filter(Module::isSubfolder) //
                .map(m -> m.path) //
                .collect(Collectors.toList());
    }

    /**
     * @return version without any prefix
     */
    public String getVersion() {
        return this.version;
    }

    List<Module> modules() {
        return this.modules;
    }
}

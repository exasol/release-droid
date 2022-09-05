package com.exasol.releasedroid.usecases.repository.version;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a module with a project's repository. Different modules may use different programming languages.
 * <p>
 * Each module has a {@link #type}, currently either {@code maven} or {@code golang} and a unique {@link #path} pointing
 * to the subfolder containing the module's files.
 * </p>
 */
class Module {

    static Module from(final Map<String, Object> map) {
        return new Module((String) map.get("type"), (String) map.get("path"));
    }

    final String type;
    final String path;

    Module(final String type, final String path) {
        this.type = type;
        this.path = path;
    }

    boolean isGolang() {
        return "golang".equalsIgnoreCase(this.type);
    }

    boolean isSubfolder() {
        return (this.path != null) && this.path.contains("/");
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.path, this.type);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Module other = (Module) obj;
        return Objects.equals(this.path, other.path) && Objects.equals(this.type, other.type);
    }

}
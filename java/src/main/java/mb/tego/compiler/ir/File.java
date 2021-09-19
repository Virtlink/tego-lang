package mb.tego.compiler.ir;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * A Tego file.
 */
public final class File {

    @Nullable private Project project = null;
    private final List<Module> modules = new Container<Module, File>(this, Module::getFile, Module::setFile);

    /**
     * Initializes a new instance of the {@link File} class.
     */
    public File() {

    }

    /**
     * Gets the project that contains this object.
     *
     * @return the project; or {@code null}
     */
    @Nullable public Project getProject() {
        return project;
    }

    /**
     * Sets the project that contains this object.
     *
     * @param project the project; or {@code null}
     */
    public void setProject(@Nullable Project project) {
        this.project = project;
    }

    /**
     * Gets the modules in the file.
     *
     * @return a list of modules in the file
     */
    public List<Module> getModules() { return this.modules; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File that = (File)o;
        return this.modules.equals(that.modules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                modules
        );
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("File(");
        sb.append(this.modules);
        sb.append(")");
        return sb.toString();
    }
}



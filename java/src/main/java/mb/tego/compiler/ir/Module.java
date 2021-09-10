package mb.tego.compiler.ir;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A Tego file.
 */
public final class Module {

    @Nullable private File file = null;


    /**
     * Initializes a new instance of the {@link Module} class.
     */
    public Module() {

    }

    /**
     * Gets the file that contains this object.
     *
     * @return the file; or {@code null}
     */
    @Nullable public File getFile() {
        return file;
    }

    /**
     * Sets the file that contains this object.
     *
     * @param file the file; or {@code null}
     */
    public void setFile(@Nullable File file) {
        this.file = file;
    }

    /**
     * Gets the modules in the file.
     *
     * @return a list of modules in the file
     */
    public List<java.lang.Module> getModules() { return this.modules; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module that = (Module)o;
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



package mb.tego.compiler.ir;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * A Tego project.
 */
public final class Project {

    private final List<File> files = new Container<File, Project>(this, File::getProject, File::setProject);

    /**
     * Initializes a new instance of the {@link Project} class.
     */
    public Project() {

    }

    /**
     * Gets the files in the project.
     *
     * @return a list of files in the project
     */
    public List<File> getFiles() { return this.files; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project that = (Project)o;
        return this.files.equals(that.files);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                files
        );
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Project(");
        sb.append(this.files);
        sb.append(")");
        return sb.toString();
    }

}



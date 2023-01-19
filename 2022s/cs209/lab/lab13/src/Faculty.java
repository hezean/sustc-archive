import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
@Data
@JsonPropertyOrder({"name", "p", "dep"})
public class Faculty implements Comparable {
    private String name;
    private String p;
    private String dep;

    public String getName() {
        return name;
    }

    public String getP() {
        return p;
    }

    public String getDep() {
        return dep;
    }

    public Faculty(String name, String p, String dep) {
        this.name = name;
        this.p = p;
        this.dep = dep;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s", name, p, dep);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() == this.getClass()) {
            Faculty e = (Faculty) o;
            return (this.name.trim().equals(e.name.trim()))
                    && (this.p.trim().equals(e.p.trim()))
                    && this.dep.trim().equals(e.dep.trim());
        }
        return false;
    }


    @Override
    public int compareTo(@NotNull Object o) {
        if (o.getClass() == this.getClass()) {
            Faculty e = (Faculty) o;
            return this.name.compareTo(e.name);
        }
        return -1;
    }
}

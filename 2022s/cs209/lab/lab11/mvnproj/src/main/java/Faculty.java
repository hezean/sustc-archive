import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonPropertyOrder({"name", "identity", "department"})
@ToString
public class Faculty {
    private String name;
    private String identity;
    private String department;
}

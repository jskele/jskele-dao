package app.data;

import lombok.*;
import org.jskele.libs.dao.EntityRow;
import org.jskele.libs.values.StringValue;

@Getter
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class TestTableRow implements EntityRow<TestTableRowId> {

    private final TestTableRowId id;
    private final String stringColumn;
    private final Integer numericColumn;
    private final JsonColumn jsonColumn;

    public static class JsonColumn extends StringValue {
        public JsonColumn(String value) {
            super(value);
        }
    }
}

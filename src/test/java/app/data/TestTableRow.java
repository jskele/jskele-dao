package app.data;

import lombok.*;
import org.jskele.libs.dao.EntityRow;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class TestTableRow implements EntityRow<TestTableRowId> {

    private final TestTableRowId id;
    private final String stringColumn;
    private final Integer numericColumn;

}

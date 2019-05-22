package app.data;

import lombok.*;
import org.jskele.libs.dao.EntityRow;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class TestTableWithUuidIdRow implements EntityRow<TestTableWithUuidIdRowId> {

    private final TestTableWithUuidIdRowId id;
    private final String stringColumn;
    private final Integer numericColumn;

}

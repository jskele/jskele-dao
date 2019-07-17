package app.data;

import lombok.*;
import lombok.experimental.Wither;
import org.jskele.dao.EntityRow;

@Getter
@Builder
@ToString
@Wither
@EqualsAndHashCode
@RequiredArgsConstructor
public class TestTableWithUuidIdRow implements EntityRow<TestTableWithUuidIdRowId> {

    private final TestTableWithUuidIdRowId id;
    private final String stringColumn;
    private final Integer numericColumn;

}

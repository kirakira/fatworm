package fatworm.dataentity;

import java.math.BigDecimal;

public class Decimal extends DataEntity {
    
    BigDecimal value;
    public Decimal(BigDecimal v) {
        value = v;
    }
    
    public int compareTo(DataEntity t) {
        return 0;
    }
}
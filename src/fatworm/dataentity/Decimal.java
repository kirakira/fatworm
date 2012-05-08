package fatworm.dataentity;

import java.math.BigDecimal;

public class Decimal {
    
    BigDecimal value;
    public Decimal(BigDecimal v) {
        value = v;
    }
    
    public int compareTo(DataEntity t) {
        return 0;
    }
}
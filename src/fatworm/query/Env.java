package fatworm.query;

import fatworm.dataentity.DataEntity;
import fatworm.absyn.ColName;
public interface Env {
    DataEntity getValue(ColName colname);
    DataEntity getValue(String func, ColName colname);
}
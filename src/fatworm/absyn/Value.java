package fatworm.absyn;

import fatworm.dataentity.DataEntity;
import fatworm.query.Env;
abstract public class Value {

    abstract public DataEntity getValue(Env env);
}

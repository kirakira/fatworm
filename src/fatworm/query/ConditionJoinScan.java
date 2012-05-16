package fatworm.query;

import java.util.ArrayList;
import java.util.List;

import fatworm.absyn.AndList;
import fatworm.absyn.BoolExpr;
import fatworm.absyn.ColumnValue;
import fatworm.absyn.CompareExpr;
import fatworm.absyn.ConstValue;
import fatworm.absyn.OrList;
import fatworm.dataentity.DataEntity;
import fatworm.record.Iterator;

public class ConditionJoinScan extends JoinScan {

    Iterator[] indexIteratorList;
    int[] dependent;
    DependentCondition[] dependentCondition;
    List<BoolExpr> conditions;

    public ConditionJoinScan(List<Scan> scanList) {
        super(scanList);
        indexIteratorList = new Iterator[scanList.size()];
        dependent = new int[scanList.size()];
        dependentCondition = new DependentCondition[scanList.size()];
        for (int i = 0; i < dependent.length; i++)
            dependent[i] = -1;
    }

    
    private boolean nextTable(int i) {
        if (dependent[i] >= 0)
            return indexIteratorList[i].next();
        else 
            return scanList.get(i).next();
    }
    
    private void beforeFirstTable(int i) {
        if (dependent[i] >= 0)
            indexIteratorList[i].beforeFirst();
        else 
            scanList.get(i).beforeFirst();
    }
    public void beforeFirst() {
        int i = 0;
        beforeFirstTable(0);
        while (true){
            while(i < scanList.size() && nextTable(i)) { 
                for (int j = i+1; j < scanList.size(); j++) {
                    if (dependent[j] == i) {
                        DependentCondition cond = dependentCondition[j];
                        indexIteratorList[j] = scanList.get(j).getIndex(cond.myleft, getColumn(cond.yourright), cond.cop);
                    }
                    beforeFirstTable(j);
                }
                i++;
            } //while (i < scanList.size() && nextTable(i));
            if (i >= scanList.size())
                break;
            while (i >= 0 && nextTable(i) == false)
                i--;
            if (i < 0)
                break;
            i++;
        }
        beforeFirstTable(scanList.size() - 1);
    }
    
    public boolean next() {
        int i = scanList.size() - 1;
        while(true) {
            if (i >= scanList.size())
                return true;
            while(i >= 0 && nextTable(i) == false) 
                i--;
            if (i < 0)
                break;

            for (int j = i+1; j < scanList.size(); j++) {
                if (dependent[j] == i) {
                    DependentCondition cond = dependentCondition[j];
                    indexIteratorList[j] = scanList.get(j).getIndex(cond.myleft, getColumn(cond.yourright), cond.cop);
                }
                beforeFirstTable(j);                    
            }
            i++;            
            while (i < scanList.size() && nextTable(i)) {
                for (int j = i+1; j < scanList.size(); j++) {
                    if (dependent[j] == i) {
                        DependentCondition cond = dependentCondition[j];
                        indexIteratorList[j] = scanList.get(j).getIndex(cond.myleft, getColumn(cond.yourright), cond.cop);
                    }
                    beforeFirstTable(j);                    
                }
                i++;
            }
        }
        return false;
    }
    
    public DataEntity getColumnByIndex(int index) {
        for (int i = 0; i < columnCount.length; i++) {
            if (index < columnCount[i]) {
                if (dependent[i] < 0)
                    return scanList.get(i).getColumnByIndex(index);
                else 
                    return indexIteratorList[i].getField(index);
            }
            index -= columnCount[i];
        }
        return null;
    }
    
    public DataEntity getField(String fldname) {
        return getColumnByIndex(indexOfField(fldname));
    }
    
    public DataEntity getColumn(String colname) {
        return getColumnByIndex(indexOfColumn(colname));
    }
    
    BoolExpr setCondition(BoolExpr expr) {
        if (expr instanceof OrList) {
            OrList or = (OrList)expr;
            if (or.orList.size() > 1)
                return expr;
            AndList and = (AndList)or.orList.getFirst();
            List<BoolExpr> list = new ArrayList<BoolExpr>();
            for (BoolExpr compare:and.andList) 
                list.add(compare);
            processEqualConst(list);
            processEqualColumn(list);
            processInEqualConst(list);
            processInEqualColumn(list);
            for (int i = 0; i < scanList.size(); i++)
                if(dependent[i] == i)
                    indexIteratorList[i].beforeFirst();
            if (list.size() == 0)
                return null;
            return new AndList(list);
        }
        return null;
    }

    void processEqualConst(List<BoolExpr> list) {
        int i = 0;
        while (i < list.size()) {
            if (list.get(i) instanceof CompareExpr) {
                CompareExpr comp = (CompareExpr) list.get(i);
                if (comp.cop.equals("EQ")) {
                    if (comp.left instanceof ConstValue && comp.right instanceof ColumnValue) {
                        ConstValue left = (ConstValue) comp.left;
                        String right = ((ColumnValue) comp.right).colName.toString();
                        int tableNum = getColumnTable(right);
                        if (tableNum > 0 && dependent[tableNum] < 0 && scanList.get(tableNum).hasIndex(right)) {
                            int type = scanList.get(tableNum).type(right);
                            indexIteratorList[tableNum] = scanList.get(tableNum).getIndex(right,left.getValue(null).toType(type), "EQ");
                            dependent[tableNum] = tableNum;
                            list.remove(i);
                            continue;
                        }
                    }
                    if (comp.right instanceof ConstValue && comp.left instanceof ColumnValue) {
                        String left = ((ColumnValue) comp.left).colName.toString();
                        ConstValue right = (ConstValue) comp.right;
                        int tableNum = getColumnTable(left);
                        if (tableNum > 0 && dependent[tableNum] < 0 && scanList.get(tableNum).hasIndex(left)) {
                            int type = scanList.get(tableNum).type(left);
                            indexIteratorList[tableNum] = scanList.get(tableNum).getIndex(left, right.getValue(null).toType(type), "EQ");
                            dependent[tableNum] = tableNum;
                            list.remove(i);
                            continue;
                        }
                    }
                }
            }
            i++;
        }
    }

    static boolean indexCanUseInEqual(String cop) {
        return !(cop.equals("NE") && cop.equals("EQ"));
    }

    static String inverseCop(String cop) {
        if (cop.equals("LE"))
            return "GE";
        if (cop.equals("LT"))
            return "GT";
        if (cop.equals("GE"))
            return "LE";
        if (cop.equals("GT"))
            return "LT";
        return cop;
    }



    void processInEqualConst(List<BoolExpr> list) {
        int i = 0;
        while (i < list.size()) {
            if (list.get(i) instanceof CompareExpr) {
                CompareExpr comp = (CompareExpr) list.get(i);
                if (indexCanUseInEqual(comp.cop)) {
                    if (comp.left instanceof ConstValue && comp.right instanceof ColumnValue) {
                        ConstValue left = (ConstValue) comp.left;
                        String right = ((ColumnValue) comp.right).colName.toString();
                        int tableNum = getColumnTable(right);
                        if (tableNum > 0 && dependent[tableNum] < 0 && scanList.get(tableNum).hasIndex(right)) {
                            int type = scanList.get(tableNum).type(right);
                            indexIteratorList[tableNum] = scanList.get(tableNum).getIndex(right,left.getValue(null).toType(type), 
                                                                                      inverseCop(comp.cop));
                            dependent[tableNum] = tableNum;
                            list.remove(i);
                            continue;
                        }
                    }
                    if (comp.right instanceof ConstValue && comp.left instanceof ColumnValue) {
                        String left = ((ColumnValue) comp.left).colName.toString();
                        ConstValue right = (ConstValue) comp.right;
                        int tableNum = getColumnTable(left);
                        if (tableNum > 0 && dependent[tableNum] < 0 && scanList.get(tableNum).hasIndex(left)) {
                            int type = scanList.get(tableNum).type(left);
                            indexIteratorList[tableNum] = scanList.get(tableNum).getIndex(left, right.getValue(null).toType(type), comp.cop);
                            dependent[tableNum] = tableNum;
                            list.remove(i);
                            continue;
                        }
                    }
                }
            }
            i++;
        }
    }

    void processEqualColumn(List<BoolExpr> list) {
        for (int i = 0; i < scanList.size(); i++) {
            int j = 0;
            while(j < list.size()) {
                if (list.get(j) instanceof CompareExpr) {
                    CompareExpr comp = (CompareExpr) list.get(j);
                    if (comp.cop.equals("EQ")) {
                        if(comp.left instanceof ColumnValue && comp.right instanceof ColumnValue) {
                            String left = ((ColumnValue)comp.left).colName.toString();
                            String right = ((ColumnValue)comp.right).colName.toString();
                            int lefttable = getColumnTable(left);
                            int righttable = getColumnTable(right);
                            if (lefttable == i && righttable > i && dependent[righttable] < 0) {
                                dependent[righttable] = lefttable;
                                dependentCondition[righttable] = new DependentCondition(right, left, "EQ");
                                list.remove(j);
                                continue;
                            }
                            if (righttable == i && lefttable > i && dependent[lefttable] < 0) {
                                dependent[lefttable] = righttable;
                                dependentCondition[lefttable] = new DependentCondition(left, right,  "EQ");
                                list.remove(j);
                                continue;
                            }
                        }
                    }
                }
                j++;
            }
        }
    }
    void processInEqualColumn(List<BoolExpr> list) {
        for (int i = 0; i < scanList.size(); i++) {
            int j = 0;
            while(j < list.size()) {
                if (list.get(j) instanceof CompareExpr) {
                    CompareExpr comp = (CompareExpr) list.get(j);
                    if (indexCanUseInEqual(comp.cop)) {
                        if(comp.left instanceof ColumnValue && comp.right instanceof ColumnValue) {
                            String left = ((ColumnValue)comp.left).colName.toString();
                            String right = ((ColumnValue)comp.right).colName.toString();
                            int lefttable = getColumnTable(left);
                            int righttable = getColumnTable(right);
                            if (lefttable == i && righttable > i && dependent[righttable] < 0) {
                                dependent[righttable] = lefttable;
                                dependentCondition[righttable] = new DependentCondition(right, left, inverseCop(comp.cop));
                                list.remove(j);
                                continue;
                            }
                            if (righttable == i && lefttable > i && dependent[lefttable] < 0) {
                                dependent[lefttable] = righttable;
                                dependentCondition[lefttable] = new DependentCondition(left, right, comp.cop);
                                list.remove(j);
                                continue;
                            }
                        }
                    }
                }
                j++;
            }
        }
    }



    int getColumnTable(String colname) {
        int i = 0;
        for (Scan scan : scanList) {
            if (scan.hasColumn(colname))
                return i;
            i++;
        }
        return -1;
    }
}

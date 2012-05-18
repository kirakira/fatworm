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
import fatworm.record.RecordIterator;

public class ConditionJoinScan extends JoinScan {

    RecordIterator[] indexIteratorList;
    int[] dependent;
    int[] comparecount;
    int[] tablereorder; //original scanlist.get(i) will be reorderscanlist[tablereorder[i]]
    DependentCondition[] dependentCondition;
    Scan[] reorderscanlist;
    long timeconsume  = 0;
    List<BoolExpr> conditions;
    
    boolean iterTable = true;

    public ConditionJoinScan(List<Scan> scanList) {
        super(scanList);
        indexIteratorList = new RecordIterator[scanList.size()];
        dependent = new int[scanList.size()];
        tablereorder = new int[scanList.size()];
        comparecount = new int[scanList.size()];
        dependentCondition = new DependentCondition[scanList.size()];
        reorderscanlist = new Scan[scanList.size()];
        for (int i = 0; i < dependent.length; i++) {
            dependent[i] = -1;
            tablereorder[i] = i;
            comparecount[i] = 0;
            reorderscanlist[i] = scanList.get(i);
        }
    }

    
    private boolean nextTable(int i) {
        if (dependent[i] >= 0) {
            if (indexIteratorList[i] != null)
                return indexIteratorList[i].next(); 
            else return false;
        }
        else 
            return reorderscanlist[i].next();
    }
    
    private void beforeFirstTable(int i) {
        if (dependent[i] >= 0) {
            if(indexIteratorList[i] != null) 
                indexIteratorList[i].beforeFirst();
        }
        else 
            reorderscanlist[i].beforeFirst();
    }
    
    public void beforeFirst() {
        int i = 0;
        beforeFirstTable(0);
        while (true){
            while(i < scanList.size() && nextTable(i)) { 
                for (int j = i+1; j < scanList.size(); j++) {
                    if (dependent[j] == i) {
                        DependentCondition cond = dependentCondition[j];
                        indexIteratorList[j] = reorderscanlist[j].getIndex(cond.myleft, getColumn(cond.yourright), cond.cop);
                    }
                    if (dependent[j] <= i || dependent[j] == j || dependent[j] < 0)
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
            for (int j = i+1; j < scanList.size(); j++) {
                if (dependent[j] == i) {
                    DependentCondition cond = dependentCondition[j];
                    indexIteratorList[j] = reorderscanlist[j].getIndex(cond.myleft, getColumn(cond.yourright), cond.cop);
                }
                if (dependent[j] <= i || dependent[j] == j || dependent[j] < 0)
                    beforeFirstTable(j);
            }
            i++;
        }
        if (i < 0) {
            iterTable = false;
        }
        beforeFirstTable(scanList.size() - 1);
    }
    
    public boolean next() {
        if (iterTable == false)
            return false;
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
                    indexIteratorList[j] = reorderscanlist[j].getIndex(cond.myleft, getColumn(cond.yourright), cond.cop);
                }
                beforeFirstTable(j);                    
            }
            i++;            
            while (i < scanList.size() && nextTable(i)) {
                for (int j = i+1; j < scanList.size(); j++) {
                    if (dependent[j] == i) {
                        DependentCondition cond = dependentCondition[j];
                        indexIteratorList[j] = reorderscanlist[j].getIndex(cond.myleft, getColumn(cond.yourright), cond.cop);
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
                if (dependent[tablereorder[i]] < 0)
                    return  reorderscanlist[tablereorder[i]].getColumnByIndex(index);
                else 
                    return indexIteratorList[tablereorder[i]].getField(index);
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
            processPriority(list);
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
    
    void processPriority(List<BoolExpr> list) {
        for (int i = 0; i < scanList.size(); i++) {
            for (BoolExpr expr:list) {
                if (expr instanceof CompareExpr) {
                    CompareExpr comp = (CompareExpr) expr;
                    if (comp.cop.equals("NE"))
                        continue;
                    if (comp.left instanceof ColumnValue && ((comp.right instanceof ColumnValue) || comp.right instanceof ConstValue) ) {
                         String left = ((ColumnValue) comp.left).colName.toString();
                         if (scanList.get(i).hasColumn(left))
                             comparecount[i]++;
                    }
                    if (comp.right instanceof ColumnValue && ((comp.left instanceof ColumnValue) || comp.left instanceof ConstValue)) {
                         String right = ((ColumnValue) comp.right).colName.toString();
                         if (scanList.get(i).hasColumn(right))
                             comparecount[i]++;
                    }
                }
            }
        }
        int max = 0;
        int maxi = -1;
        for (int i = 0; i < scanList.size(); i++) {
            if (comparecount[i] > max) { 
                max = comparecount[i];
                maxi = i;
            }
        }
        if (max > 1) {
            tablereorder[maxi] = 0;
            tablereorder[0] = maxi;
        }
        for (int i = 0; i < scanList.size(); i++) 
            reorderscanlist[tablereorder[i]] = scanList.get(i);
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
                        if (tableNum >= 0 && dependent[tableNum] < 0 && reorderscanlist[tableNum].hasIndex(right)) {
                            int type = reorderscanlist[tableNum].type(right);
                            indexIteratorList[tableNum] = reorderscanlist[tableNum].getIndex(right,left.getValue(null).toType(type), "EQ");
                            dependent[tableNum] = tableNum;
                            list.remove(i);
                            continue;
                        }
                    }
                    if (comp.right instanceof ConstValue && comp.left instanceof ColumnValue) {
                        String left = ((ColumnValue) comp.left).colName.toString();
                        ConstValue right = (ConstValue) comp.right;
                        int tableNum = getColumnTable(left);
                        if (tableNum >= 0 && dependent[tableNum] < 0 && reorderscanlist[tableNum].hasIndex(left)) {
                            int type = reorderscanlist[tableNum].type(left);
                            indexIteratorList[tableNum] = reorderscanlist[tableNum].getIndex(left, right.getValue(null).toType(type), "EQ");
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
        return !(cop.equals("NE") || cop.equals("EQ"));
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
                        if (tableNum >= 0 && dependent[tableNum] < 0 && reorderscanlist[tableNum].hasIndex(right)) {
                            int type = reorderscanlist[tableNum].type(right);
                            indexIteratorList[tableNum] = reorderscanlist[tableNum].getIndex(right,left.getValue(null).toType(type), 
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
                        if (tableNum >= 0 && dependent[tableNum] < 0 && reorderscanlist[tableNum].hasIndex(left)) {
                            int type = reorderscanlist[tableNum].type(left);
                            indexIteratorList[tableNum] = reorderscanlist[tableNum].getIndex(left, right.getValue(null).toType(type), comp.cop);
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
                            if (lefttable == i && righttable > i && dependent[righttable] < 0 && reorderscanlist[righttable].hasIndex(right)) {
                                dependent[righttable] = lefttable;
                                dependentCondition[righttable] = new DependentCondition(right, left, "EQ");
                                list.remove(j);
                                continue;
                            }
                            if (righttable == i && lefttable > i && dependent[lefttable] < 0 && reorderscanlist[righttable].hasIndex(left)) {
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
                            if (lefttable == i && righttable > i && dependent[righttable] < 0 && reorderscanlist[righttable].hasIndex(right)) {
                                dependent[righttable] = lefttable;
                                dependentCondition[righttable] = new DependentCondition(right, left, inverseCop(comp.cop));
                                list.remove(j);
                                continue;
                            }
                            if (righttable == i && lefttable > i && dependent[lefttable] < 0 && reorderscanlist[righttable].hasIndex(right)) {
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
        for (Scan scan : reorderscanlist) {
            if (scan.hasColumn(colname))
                return i;
            i++;
        }
        return -1;
    }
}

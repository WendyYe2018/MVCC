public class Record {

    // 当前可见版本
    private Row currentRow;

    // 当前版本由哪个事务产生
    private long trxId;

    // 历史版本链
    private UndoLog undoHead;


     long getTrxId() {
        return this.trxId;
    }


    public void setCurrent(Row current) {
        this.currentRow = current;
    }

    public void setTrxId(long trxId) {
        this.trxId = trxId;
    }

    UndoLog getUndoHead() {
        return this.undoHead;
    }

    void setUndoHead(UndoLog undoHead) {
        this.undoHead = undoHead;
    }


    public Row getCurrentRow() {
        return currentRow;
    }
}

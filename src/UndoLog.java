public class UndoLog {
    Row row;
    long trxId;
    UndoLog prev;

    public void setRow(Row row) {
        this.row = row;
    }

    public void setTrxId(long trxId) {
        this.trxId = trxId;
    }

    public void setPrev(UndoLog previous) {
        this.prev = previous;
    }

    public Row getRow() {return this.row;}

    public long getTrxId() {return this.trxId;}

    public UndoLog getPrev() {return this.prev;}
}

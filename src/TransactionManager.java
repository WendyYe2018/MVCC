import java.util.*;

public class TransactionManager {

    // 下一个事务ID
    private long nextTrxId = 11;

    private final Map<Long, Record> table =
            new HashMap<>();

    // 当前活跃事务
    private final Set<Long> activeTrxIds = new HashSet<>();

    /**
     * 开启事务
     */
    public Transaction begin() {

        long trxId = nextTrxId++;

        activeTrxIds.add(trxId);

        return new Transaction(trxId);
    }

    public void insert(Row row) {

        Record record = new Record();

        record.setCurrent(row);

        record.setTrxId(0);

        table.put(
                row.getId(),
                record
        );
    }


    /**
     * MVCC查询
     */
    public Row select(
            long id,
            ReadView readView) {

        Record record = table.get(id);

        if (record == null) {
            return null;
        }

        // 先检查最新版本；不可见时沿Undo链寻找快照可见的旧版本。
        if (readView.isVisible(record.getTrxId())) {
            return record.getCurrentRow();
        }

        UndoLog undo = record.getUndoHead();

        while (undo != null) {

            if (readView.isVisible(undo.getTrxId())) {
                return undo.getRow();
            }

            undo = undo.getPrev();
        }

        return null;
    }

    /**
     * 提交事务
     */
    public void commit(Transaction trx) {
        activeTrxIds.remove(trx.getTrxId());
    }

    /**
     * 创建ReadView
     */
    public ReadView createReadView(Transaction trx) {

        List<Long> mIds = new ArrayList<>(activeTrxIds);

        long minTrxId =
                mIds.stream()
                        .min(Long::compareTo)
                        .orElse(nextTrxId);

        long maxTrxId = nextTrxId;

        ReadView readView = new ReadView(
                trx.getTrxId(),
                mIds,
                minTrxId,
                maxTrxId
        );
        System.out.println(
                "为事务 T%d 创建 ReadView: 活跃事务=%s, minTrxId=%d, maxTrxId=%d"
                        .formatted(trx.getTrxId(), mIds, minTrxId, maxTrxId)
        );
        return readView;
    }

    /**
     * 更新数据
     */
    public void update(
            long id,
            String newName,
            int newAge,
            Transaction trx) {

        Record record = table.get(id);

        if (record == null) {
            throw new RuntimeException(
                    "record not found : " + id
            );
        }

        // 保存旧版本到UndoLog
        UndoLog undoLog = new UndoLog();

        undoLog.setRow(
                record.getCurrentRow()
        );

        undoLog.setTrxId(
                record.getTrxId()
        );

        undoLog.setPrev(
                record.getUndoHead()
        );

        // 创建新版本
        Row newRow = new Row(
                id,
                newAge,
                newName
        );

        // 更新当前版本
        record.setCurrent(newRow);

        // 当前版本所属事务
        record.setTrxId(
                trx.getTrxId()
        );

        // 更新undo链头
        record.setUndoHead(
                undoLog
        );
    }



}

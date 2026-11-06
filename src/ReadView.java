import java.util.ArrayList;
import java.util.List;

public class ReadView {

    // 创建ReadView的事务ID
    private long creatorTrxId;

    // 活跃事务集合
    private List<Long> mIds;

    // 活跃事务最小ID
    private long minTrxId;

    // 下一个将要分配的事务ID
    private long maxTrxId;
    /**
     * 创建事务的一致性读视图。事务集合和ID边界在创建时固定，
     * 后续事务提交不会改变此ReadView的可见性。
     *
     * @param creatorTrxId 创建该ReadView的事务ID
     * @param activeTrxIds 创建时仍处于活跃状态的事务ID集合
     * @param minTrxId 创建时活跃事务中的最小ID；没有活跃事务时为下一事务ID
     * @param maxTrxId 创建时下一个将分配的事务ID
     */
    public ReadView(
            long creatorTrxId,
            List<Long> activeTrxIds,
            long minTrxId,
            long maxTrxId) {
        this.creatorTrxId = creatorTrxId;
        this.mIds = new ArrayList<>(activeTrxIds);
        this.minTrxId = minTrxId;
        this.maxTrxId = maxTrxId;
    }

    public boolean isVisible(long trxId) {

        // 当前事务自己的修改对自己可见。
        if (trxId == creatorTrxId) {
            return true;
        }

        // 事务ID小于快照中的最小活跃事务ID，说明该事务在快照创建前已提交。
        if (trxId < minTrxId) {
            return true;
        }

        // 事务ID不小于快照创建时的ID上界，说明该事务在快照创建后才启动。
        if (trxId >= maxTrxId) {
            return false;
        }

        // 落在两个边界之间的事务，可能在快照创建时仍活跃。
        // 活跃事务的修改对当前ReadView不可见；其余事务当时已提交，因此可见。
        if (mIds.contains(trxId)) {
            return false;
        }

        return true;
    }
}

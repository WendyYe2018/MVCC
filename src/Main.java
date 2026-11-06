public class Main {

    public static void main(String[] args) {

        /*
        为事务 T11 创建 ReadView: 活跃事务=[11], minTrxId=11, maxTrxId=12
        T11 首次查询: Row{id=1, age=25, name='Wendy'}
        T12 更新后，T11 查询: Row{id=1, age=25, name='Wendy'}
        为事务 T13 创建 ReadView: 活跃事务=[11, 12, 13], minTrxId=11, maxTrxId=14
        T12 未提交时，T13 查询: Row{id=1, age=25, name='Wendy'}
        为事务 T14 创建 ReadView: 活跃事务=[11, 13, 14], minTrxId=11, maxTrxId=15
        T12 已提交后，T14 查询: Row{id=1, age=31, name='Wendy'}
        T12 已提交后，T11 再查询: Row{id=1, age=25, name='Wendy'}
        */
        TransactionManager tm =
                new TransactionManager();

        // 初始化数据
        tm.insert(
                new Row(
                        1L,
                        25,
                        "Wendy"
                )
        );

        // T1
        Transaction t1 = tm.begin();

        ReadView readView =
                tm.createReadView(t1);

        System.out.println(
                "T%d 首次查询: %s".formatted(
                        t1.getTrxId(),
                        tm.select(1L, readView)
                )
        );

        // T2
        Transaction t2 = tm.begin();

        tm.update(
                1L,
                "Wendy",
                31,
                t2
        );

        System.out.println(
                "T%d 更新后，T%d 查询: %s".formatted(
                        t2.getTrxId(),
                        t1.getTrxId(),
                        tm.select(1L, readView)
                )
        );

        // T3
        Transaction t3 = tm.begin();

        ReadView readView3 =
                tm.createReadView(t3);

        System.out.println(
                "T%d 未提交时，T%d 查询: %s".formatted(
                        t2.getTrxId(),
                        t3.getTrxId(),
                        tm.select(1L, readView3)
                )
        );

        tm.commit(t2);


        // T4
        Transaction t4 = tm.begin();

        ReadView readView4 =
                tm.createReadView(t4);

        System.out.println(
                "T%d 已提交后，T%d 查询: %s".formatted(
                        t2.getTrxId(),
                        t4.getTrxId(),
                        tm.select(1L, readView4)
                )
        );

        System.out.println(
                "T%d 已提交后，T%d 再查询: %s".formatted(
                        t2.getTrxId(),
                        t1.getTrxId(),
                        tm.select(1L, readView)
                )
        );
    }
}
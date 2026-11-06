public class Main {

    public static void main(String[] args) {

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

        System.out.println(t1.getTrxId() +
                "首次查询: "
                        + tm.select(1L, readView).toString()
        );

        // T2
        Transaction t2 = tm.begin();

        tm.update(
                1L,
                "Wendy",
                31,
                t2
        );

        System.out.println(t2.getTrxId() +
                "更新," + t1.getTrxId() + "查询: "
                        + tm.select(1L, readView).toString()
        );




        // T3
        Transaction t3 = tm.begin();

        ReadView readView3 =
                tm.createReadView(t3);

        System.out.println(
                (t2.getTrxId()) +
                        "未提交," + t3.getTrxId() + "查询: "
                        + tm.select(1L, readView3).toString()
        );

        tm.commit(t2);


        // T4
        Transaction t4 = tm.begin();

        ReadView readView4 =
                tm.createReadView(t4);

        System.out.println(
                (t2.getTrxId()) +
                        "已经提交," + t4.getTrxId() + "查询: "
                        + tm.select(1L, readView4).toString()
        );

        System.out.println(
                (t2.getTrxId()) +
                        "提交," + t1.getTrxId() + "查询: "
                        + tm.select(1L, readView).toString()
        );



    }
}
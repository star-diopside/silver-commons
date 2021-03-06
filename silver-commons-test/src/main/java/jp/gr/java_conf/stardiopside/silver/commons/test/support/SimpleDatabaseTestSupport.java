package jp.gr.java_conf.stardiopside.silver.commons.test.support;

import javax.sql.DataSource;

import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;

import jp.gr.java_conf.stardiopside.silver.commons.test.exception.TestException;

/**
 * データベースを使用するテストをサポートする機能を実装するクラス
 */
public class SimpleDatabaseTestSupport extends AbstractDatabaseTestSupport {

    /** データベーステスター */
    private IDatabaseTester databaseTester;

    /**
     * コンストラクタ
     * 
     * @param tester テストクラスのインスタンス
     * @param dataSource データソース
     */
    public SimpleDatabaseTestSupport(Object tester, DataSource dataSource) {
        super(tester, dataSource);
    }

    @Override
    public void onSetup() {

        databaseTester = new DefaultDatabaseTester(getConnection());
        databaseTester.setDataSet(getDataSet());

        try {
            databaseTester.onSetup();
        } catch (Exception e) {
            throw new TestException(e);
        }
    }

    @Override
    public void onTearDown() {
        try {
            databaseTester.onTearDown();
        } catch (Exception e) {
            throw new TestException(e);
        }
    }
}

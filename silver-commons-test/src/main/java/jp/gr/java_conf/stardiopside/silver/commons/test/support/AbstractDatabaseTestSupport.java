package jp.gr.java_conf.stardiopside.silver.commons.test.support;

import jp.gr.java_conf.stardiopside.silver.commons.test.exception.TestException;
import jp.gr.java_conf.stardiopside.silver.commons.test.util.DataSetUtils;
import jp.gr.java_conf.stardiopside.silver.commons.test.util.TestUtils;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;

/**
 * データベースを使用するテストをサポートする機能のベースを実装する抽象クラス
 */
public abstract class AbstractDatabaseTestSupport implements DatabaseTestSupport {

    /**
     * テストクラスのインスタンス
     */
    private Object tester;

    /**
     * データソース
     */
    private DataSource dataSource;

    /**
     * テストデータセット
     */
    private IDataSet dataSet;

    /**
     * オブジェクト置換マップ
     */
    private Map<?, ?> replacementObjectMap;

    /**
     * 文字列置換マップ
     */
    private Map<String, String> replacementSubstringMap;

    /**
     * コンストラクタ
     *
     * @param tester     テストクラスのインスタンス
     * @param dataSource データソース
     */
    protected AbstractDatabaseTestSupport(Object tester, DataSource dataSource) {
        this.tester = tester;
        this.dataSource = dataSource;
    }

    /**
     * データソースを取得する。
     *
     * @return データソース
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public IDataSet getDataSet() {
        if (dataSet == null) {
            return null;
        } else if ((replacementObjectMap == null || replacementObjectMap.isEmpty())
                && (replacementSubstringMap == null || replacementSubstringMap.isEmpty())) {
            return dataSet;
        } else {
            return new ReplacementDataSet(dataSet, replacementObjectMap, replacementSubstringMap);
        }
    }

    @Override
    public void setReplacementObjectMap(Map<?, ?> replacementObjectMap) {
        this.replacementObjectMap = replacementObjectMap;
    }

    @Override
    public void setReplacementSubstringMap(Map<String, String> replacementSubstringMap) {
        this.replacementSubstringMap = replacementSubstringMap;
    }

    @Override
    public void setFlatXmlDataSet(String testFile) {
        dataSet = DataSetUtils.createFlatXmlDataSet(TestUtils.findTestDataFile(tester, testFile));
    }

    @Override
    public void setCsvDataSet(String testDirectory) {
        dataSet = DataSetUtils.createCsvDataSet(TestUtils.findTestDataFile(tester, testDirectory));
    }

    @Override
    public IDatabaseConnection getConnection() {
        try {
            return new DatabaseConnection(getDataSource().getConnection());
        } catch (DatabaseUnitException | SQLException e) {
            throw new TestException(e);
        }
    }
}

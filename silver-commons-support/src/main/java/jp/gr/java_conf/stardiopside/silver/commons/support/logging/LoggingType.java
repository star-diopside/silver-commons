package jp.gr.java_conf.stardiopside.silver.commons.support.logging;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

/**
 * {@link Loggable} デフォルト実装でのパラメータのログ出力タイプを指定する列挙体
 * 
 * @see Loggable
 * @see LoggingSetting
 */
public enum LoggingType {

    /**
     * ログ出力パラメータに含める。
     */
    INCLUDE {
        @Override
        Optional<Map.Entry<String, Object>> getLoggingObject(LoggingSetting setting, Field field, Object obj)
                throws IllegalAccessException {
            return Optional.of(Map.entry(getKey(setting, field), field.get(obj)));
        }
    },

    /**
     * ログ出力パラメータに含めない。
     */
    EXCLUDE {
        @Override
        Optional<Map.Entry<String, Object>> getLoggingObject(LoggingSetting setting, Field field, Object obj)
                throws IllegalAccessException {
            return Optional.empty();
        }
    },

    /**
     * ログ出力パラメータに含めるが、値は表示しない。
     */
    PROTECT {
        @Override
        Optional<Map.Entry<String, Object>> getLoggingObject(LoggingSetting setting, Field field, Object obj)
                throws IllegalAccessException {
            return Optional.of(Map.entry(getKey(setting, field), setting.protectValue()));
        }
    },

    /**
     * ログ出力パラメータに含めるが、値が設定されている場合はその値を表示しない。
     */
    PROTECT_IF_PRESENT {
        @Override
        Optional<Map.Entry<String, Object>> getLoggingObject(LoggingSetting setting, Field field, Object obj)
                throws IllegalAccessException {
            Object value = field.get(obj);
            return Optional.of(Map.entry(getKey(setting, field), isPresent(value) ? setting.protectValue() : value));
        }

        private boolean isPresent(Object value) {
            if (value == null) {
                return false;
            } else if (value instanceof String) {
                return ((String) value).length() > 0;
            } else {
                return true;
            }
        }
    };

    /**
     * ログ出力パラメータ値を取得する。
     * 
     * @param setting ログ出力設定情報
     * @param field ログ出力パラメータ情報
     * @param obj ログ出力オブジェクト
     * @return ログ出力パラメータのキー名と値を格納する{@link Map.Entry} (ログ出力を行わない場合はEMPTYを返す。)
     * @throws IllegalAccessException ログ出力フィールドにアクセスできない場合
     */
    abstract Optional<Map.Entry<String, Object>> getLoggingObject(LoggingSetting setting, Field field, Object obj)
            throws IllegalAccessException;

    /**
     * ログ出力キー名を取得する。
     * 
     * @param setting ログ出力設定情報
     * @param field ログ出力パラメータ情報
     * @return ログ出力キー名
     */
    protected String getKey(LoggingSetting setting, Field field) {
        return StringUtils.isEmpty(setting.key()) ? field.getName() : setting.key();
    }
}

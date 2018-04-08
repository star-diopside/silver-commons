package jp.gr.java_conf.stardiopside.silver.commons.support.logging;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import jp.gr.java_conf.stardiopside.silver.commons.support.exception.UncheckedReflectiveOperationException;

/**
 * ログ出力情報取得機能を持つクラスが実装するインタフェース
 */
public interface Loggable {

    /**
     * ログ出力パラメータのストリームを生成する。
     * 
     * @return ログ出力パラメータのストリーム
     */
    default Stream<LoggingParameter> streamLoggingObjects() {
        Stream.Builder<LoggingParameter> builder = Stream.builder();
        Class<?> clazz = getClass();

        try {
            do {
                String className = clazz.getSimpleName();
                Field[] fields = clazz.getDeclaredFields();
                AccessibleObject.setAccessible(fields, true);
                for (Field field : fields) {
                    getLoggingObject(field, this)
                            .ifPresent(entry -> addLog(builder, className + "." + entry.getKey(), entry.getValue()));
                }
            } while ((clazz = clazz.getSuperclass()) != null);
        } catch (IllegalAccessException e) {
            throw new UncheckedReflectiveOperationException(e);
        }

        return builder.build();
    }

    /**
     * オブジェクトをログパラメータに編集し、ログストリームに追加する。
     * 
     * @param builder ログパラメータを設定するストリームビルダー
     * @param itemName ログ出力項目名称
     * @param item ログ出力項目
     */
    private static void addLog(Stream.Builder<LoggingParameter> builder, String itemName, Object item) {
        if (item instanceof Loggable) {
            addLoggableToLog(builder, itemName, (Loggable) item);
        } else if (item instanceof Collection<?>) {
            addListToLog(builder, itemName, (Collection<?>) item);
        } else if (item != null && item.getClass().isArray()) {
            addArrayToLog(builder, itemName, item);
        } else {
            builder.add(new KeyValueLoggingParameter(itemName, String.valueOf(item)));
        }
    }

    /**
     * {@link Loggable}をログパラメータに編集し、ログストリームに追加する。
     * 
     * @param builder ログパラメータを設定するストリームビルダー
     * @param itemName ログ出力項目名称
     * @param item ログ出力項目
     */
    private static void addLoggableToLog(Stream.Builder<LoggingParameter> builder, String itemName, Loggable item) {
        item.streamLoggingObjects().forEach(param -> builder.add(param.createNestedLoggingParameter(itemName)));
    }

    /**
     * リスト項目をログパラメータに編集し、ログストリームに追加する。
     * 
     * @param builder ログパラメータを設定するストリームビルダー
     * @param itemName ログ出力リスト項目名称
     * @param itemList ログ出力リスト項目
     */
    private static void addListToLog(Stream.Builder<LoggingParameter> builder, String itemName,
            Collection<?> itemList) {
        int count = 0;
        for (Object item : itemList) {
            addLog(builder, itemName + "[" + (count++) + "]", item);
        }
    }

    /**
     * 配列項目をログパラメータに編集し、ログストリームに追加する。
     * 
     * @param builder ログパラメータを設定するストリームビルダー
     * @param itemName ログ出力配列項目名称
     * @param itemList ログ出力配列項目
     */
    private static void addArrayToLog(Stream.Builder<LoggingParameter> builder, String itemName, Object itemList) {
        int length = Array.getLength(itemList);
        for (int i = 0; i < length; i++) {
            addLog(builder, itemName + "[" + i + "]", Array.get(itemList, i));
        }
    }

    /**
     * ログ出力フィールド情報を取得する。
     * 
     * @param field ログ出力フィールド
     * @param obj ログ出力オブジェクト
     * @return ログ出力フィールドのキー名と値を格納する{@link Map.Entry} (ログ出力を行わない場合はEMPTYを返す。)
     * @throws IllegalAccessException ログ出力フィールドにアクセスできない場合
     */
    private static Optional<Map.Entry<String, Object>> getLoggingObject(Field field, Object obj)
            throws IllegalAccessException {
        LoggingSetting setting = field.getDeclaredAnnotation(LoggingSetting.class);
        if (setting == null) {
            return Optional.of(Map.entry(field.getName(), field.get(obj)));
        } else {
            return setting.value().getLoggingObject(setting, field, obj);
        }
    }
}

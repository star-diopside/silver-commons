package jp.gr.java_conf.stardiopside.silver.commons.support.interceptor;

import java.util.stream.Stream;

import jp.gr.java_conf.stardiopside.silver.commons.support.logging.Loggable;
import jp.gr.java_conf.stardiopside.silver.commons.support.logging.LoggingParameter;

/**
 * オブジェクトの詳細情報をログに出力するインターセプター
 */
@SuppressWarnings("serial")
public class LoggingObjectDetailsInterceptor extends LoggingInterceptor {

    @Override
    protected Stream<?> streamLoggingObjects(Object obj) {
        if (obj instanceof Loggable) {
            return ((Loggable) obj).streamLoggingObjects().map(LoggingParameter::get);
        } else {
            return super.streamLoggingObjects(obj);
        }
    }
}

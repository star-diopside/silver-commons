package jp.gr.java_conf.stardiopside.silver.commons.batch.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import lombok.Setter;

/**
 * 一時ファイルを生成する{@link JobExecutionListener}
 */
public class TemporaryFileJobListener implements JobExecutionListener, InitializingBean {

    /** 生成した一時ファイル名をExecutionContextに格納するキー */
    @Setter
    private String key;

    /** 一時ファイル名の接頭辞文字列 */
    @Setter
    private String prefix;

    /** 一時ファイル名の接尾辞文字列 */
    @Setter
    private String suffix;

    /** 一時ファイルを生成するディレクトリ */
    @Setter
    private String directory;

    /** 終了時に一時ファイルを削除するかを示すフラグ */
    @Setter
    private boolean deleteOnExit = true;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(key, "Property 'key' is required.");
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        try {
            Path tempFile = (directory == null ? Files.createTempFile(prefix, suffix)
                    : Files.createTempFile(Paths.get(directory), prefix, suffix));
            jobExecution.getExecutionContext().putString(key, tempFile.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (!deleteOnExit) {
            return;
        }
        Path tempFile = Paths.get(jobExecution.getExecutionContext().getString(key));
        try {
            Files.delete(tempFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

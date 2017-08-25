package jp.gr.java_conf.star_diopside.silver.commons.batch.support;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import jp.gr.java_conf.star_diopside.silver.commons.support.file.DeleteDirectoryVisitor;
import lombok.Setter;

/**
 * 一時ディレクトリを生成する{@link JobExecutionListener}
 */
public class TemporaryDirectoryJobListener implements JobExecutionListener, InitializingBean {

    /** 生成した一時ディレクトリ名をExecutionContextに格納するキー */
    @Setter
    private String key;

    /** 一時ディレクトリ名の接頭辞文字列 */
    @Setter
    private String prefix;

    /** 一時ディレクトリを生成するディレクトリ */
    @Setter
    private String directory;

    /** 終了時に一時ディレクトリを削除するかを示すフラグ */
    @Setter
    private boolean deleteOnExit = true;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(key, "Property 'key' is required.");
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        try {
            Path tempDir = (directory == null ? Files.createTempDirectory(prefix)
                    : Files.createTempDirectory(Paths.get(directory), prefix));
            jobExecution.getExecutionContext().putString(key, tempDir.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (!deleteOnExit) {
            return;
        }
        Path tempDir = Paths.get(jobExecution.getExecutionContext().getString(key));
        try {
            Files.walkFileTree(tempDir, new DeleteDirectoryVisitor());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

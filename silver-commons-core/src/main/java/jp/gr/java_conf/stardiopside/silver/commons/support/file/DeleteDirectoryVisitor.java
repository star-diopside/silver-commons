package jp.gr.java_conf.stardiopside.silver.commons.support.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 再帰的にディレクトリの削除を行うFileVisitor実装クラス
 */
public class DeleteDirectoryVisitor extends SimpleFileVisitor<Path> {

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (exc != null) {
            throw exc;
        }
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
    }
}

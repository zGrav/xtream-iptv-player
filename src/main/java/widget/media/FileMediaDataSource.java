package widget.media;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

public class FileMediaDataSource implements IMediaDataSource {
    private RandomAccessFile mFile;
    private long mFileSize = this.mFile.length();

    public FileMediaDataSource(File file) throws IOException {
        this.mFile = new RandomAccessFile(file, "r");
    }

    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        if (this.mFile.getFilePointer() != position) {
            this.mFile.seek(position);
        }
        if (size == 0) {
            return 0;
        }
        return this.mFile.read(buffer, 0, size);
    }

    public long getSize() throws IOException {
        return this.mFileSize;
    }

    public void close() throws IOException {
        this.mFileSize = 0;
        this.mFile.close();
        this.mFile = null;
    }
}

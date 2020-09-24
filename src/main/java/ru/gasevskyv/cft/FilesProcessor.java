package ru.gasevskyv.cft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;


public class FilesProcessor {
    private BufferedWriter writer;
    private BufferedReader[] readers;
    private boolean typeInt;
    private final String REGEX = "\\d+";
    private final String outFile;
    private final String tmpFile = "src\\main\\resources\\tmpFile.txt";
    private static int position = 0;


    private FileChannel read;
    private FileChannel write;
    private ByteBuffer byteBuffer;

    public FilesProcessor(String outFile, String[] inFiles, boolean typeInt) {
        this.outFile = outFile;
        this.writer = this.getWriter(outFile);
        this.readers = this.getReaders(inFiles);
        this.typeInt = typeInt;
        this.read = null;
        this.write = null;
        this.byteBuffer = null;
    }

    void setChannel() {
        try {
            this.close();
            Path source = Path.of(outFile);
            Path dest = Path.of(tmpFile);
            Files.createFile(dest);
            Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(source);
            this.read = FileChannel.open(Path.of(tmpFile),
                    StandardOpenOption.READ
            );
            this.write = FileChannel.open(Path.of(outFile),
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.APPEND
            );
            int bufferSize = 1024;
            if (bufferSize > read.size()) {
                bufferSize = (int) read.size();
            }
            this.byteBuffer = ByteBuffer.allocate(bufferSize);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * writes the buffer content to the file
     *
     * @param lines
     */
    public void writeToFile(List<String> lines) {
        try {
            for (String s : lines) {
                this.writer.append(s).append(System.lineSeparator());
            }
            this.writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод читает из i-го ридера строку и обрататывает возмоные ошибки чтения:
     * если предполагаемый тип данных - int, то будет читать строки, пока не поучит целое число или eof
     * если предполагаемый тип данных - string, то удет читать пока не пулучит eof
     * в обоих случаях пустые строки игнорятся
     * так е в конце делается проверка того, что элементы входящего списка ействительно отсортированны
     * Возвращает строку, содержащую нуные данные или null в случае eof
     *
     * @param index
     * @return
     * @throws IOException
     */
    public String readReader(int index) {
        String result = "";
        while ("".equals(result)) {
            try {
                result = readers[index].readLine();
            } catch (IOException e) {
                result = null;
            }
            if (result == null) {
                break;
            }
            if (typeInt) {
                result = result.trim();
                if (!result.matches(REGEX)) {
                    result = "";
                }
            }
        }
        return result;
    }

    /**
     * Returns an array of Buffered Readers based on the list of incoming files
     *
     * @param inFiles
     * @return array of Buffered Readers
     */
    private BufferedReader[] getReaders(String[] inFiles) {
        BufferedReader[] result = new BufferedReader[inFiles.length];
        try {
            for (int i = 0; i < inFiles.length; i++) {
                result[i] = Files.newBufferedReader(Path.of(inFiles[i]), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Returns the writer based on the file to write
     *
     * @param outFile - file name for writing
     * @return - Buffered Writer
     */
    private BufferedWriter getWriter(String outFile) {
        BufferedWriter result = null;
        try {
            result = Files.newBufferedWriter(Path.of(outFile),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public void close() throws IOException {
        for (BufferedReader reader : this.readers) {
            reader.close();
        }
        this.writer.close();
    }

    public void putToOutFile(Map<String, String> map, boolean isDescending) throws IOException {
        Iterator<String> list = map.keySet().iterator();
        BiFunction<String, String, Boolean> followsBy = (line, newLine) -> {
            int compare = typeInt ?
                    Integer.compare(Integer.parseInt(line), Integer.parseInt(newLine))
                    : line.compareTo(newLine);
            return isDescending ? compare <= 0 : compare >= 0;
        };
        int bufferSize = this.byteBuffer.limit();
        ByteBuffer bufferToWrite = ByteBuffer.allocate(bufferSize);
        int size;
        String key = list.next();
        boolean eof = false;
        int rest = (int) read.size();
        while ((size = read.read(byteBuffer)) > 0 || rest > 0) {
            rest -= size;
            position = (int) read.position();
            byteBuffer.rewind();
            byteBuffer.limit(size);
            eof = size < bufferSize || rest <= 0;
            String line = nextLineFromBuffer(byteBuffer, eof);
            while (line != null) {
                if (key != null && followsBy.apply(line, key)) {
                    write(map.get(key), bufferToWrite, write);
                    key = list.hasNext() ? list.next() : null;
                } else {
                    write(line, bufferToWrite, write);
                    line = nextLineFromBuffer(byteBuffer, eof);
                }
            }
            if (size < bufferSize) break;
            read.position(position);
            byteBuffer.clear();
        }
        flushBuffer(write, bufferToWrite);
        read.close();
        write.close();
        Files.deleteIfExists(Path.of(tmpFile));
    }

    private static void write(String newLine, ByteBuffer buffer, FileChannel channel) {
        int rest = buffer.remaining();
        newLine = newLine + System.lineSeparator();
        if (rest < newLine.length()) {
            flushBuffer(channel, buffer);
        }
        buffer.put(newLine.getBytes(StandardCharsets.UTF_8));
    }

    private static void flushBuffer(FileChannel channel, ByteBuffer buffer) {
        try {
            int pos = buffer.position();
            buffer.limit(pos);
            buffer.rewind();
            channel.write(buffer);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String nextLineFromBuffer(ByteBuffer buffer, boolean eof) {
        StringBuilder result = new StringBuilder();
        char c;
        while (buffer.hasRemaining()) {
            c = (char) buffer.get();
            if (c == '\r' || c == '\n') {
                if (result.length() != 0) {
                    return result.toString();
                }
            } else {
                result.append(c);
            }
        }
        if (result.length() > 0 && eof) {
            return result.toString();
        }
        if (result.length() > 0) {
            position -= result.length();
        }
        return null;
    }
}

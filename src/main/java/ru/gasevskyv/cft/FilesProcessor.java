package ru.gasevskyv.cft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;


public class FilesProcessor {
    private BufferedWriter writer;
    private BufferedReader[] readers;
    private boolean typeInt;
    private final String REGEX = "\\d+";

    public FilesProcessor(String outFile, String[] inFiles, boolean typeInt) {
        this.writer = this.getWriter(outFile);
        this.readers = this.getReaders(inFiles);
        this.typeInt = typeInt;
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
     *
     * Returns the writer based on the file to write
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
}

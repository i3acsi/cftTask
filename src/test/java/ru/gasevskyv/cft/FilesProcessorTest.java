package ru.gasevskyv.cft;


import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FilesProcessorTest {
    private FilesProcessor processor;

    @Test
    public void writeToFileAndThanReadNoMistakes() {
        List<String> list = List.of("1", "2", "3", "4");
        processor = new FilesProcessor("src/main/resources/out.txt",
                new String[]{"src/main/resources/in.txt"}, true);
        processor.writeToFile(list);
        assertThat(processor.readReader(0), is("1"));
        assertThat(processor.readReader(0), is("2"));
        assertThat(processor.readReader(0), is("3"));
        assertThat(processor.readReader(0), is("4"));
        try {
            Files.delete(Paths.get("src/main/resources/out.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void writeToFileAndThanReadWithMistakes() {
        List<String> list = List.of("a1", "b2", "3", "4", "", "", "45");
        processor = new FilesProcessor("src/main/resources/out1.txt",
                new String[]{"src/main/resources/in2.txt"}, true);
        processor.writeToFile(list);
        assertThat(processor.readReader(0), is("3"));
        assertThat(processor.readReader(0), is("4"));
        assertThat(processor.readReader(0), is("45"));
        try {
            Files.delete(Paths.get("src/main/resources/out1.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

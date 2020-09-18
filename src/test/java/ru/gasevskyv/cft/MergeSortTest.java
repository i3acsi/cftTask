package ru.gasevskyv.cft;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class MergeSortTest {
    @Test
    public void integerDescendingTest(){
        String[] args = {"-d", "-i",
                "src/main/resources/testOut.txt",
                "src/main/resources/testIn.txt",
                "src/main/resources/testIn2.txt",
                "src/main/resources/testIn3.txt"};
        ArgsParser param = new ArgsParser(args);
        assert (!param.isFailed());
        MergeSort mergeSort = new MergeSort(param);
        mergeSort.start();
        try (BufferedReader reader1 =
                     Files.newBufferedReader(
                             Path.of("src/main/resources/testOut.txt"));
        BufferedReader reader2 =
                Files.newBufferedReader(
                        Path.of("src/main/resources/expected.txt")))
        {
            String result = reader1.readLine();
            String expected = reader2.readLine();
            while (expected!=null && result!=null){
                assertThat(result, is(expected));
                result = reader1.readLine();
                expected = reader2.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            Files.deleteIfExists(Paths.get("src/main/resources/testOut.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void stringAscendingTest(){
        String[] args = {"-s",
                "src/main/resources/textTestOut.txt",
                "src/main/resources/textTestIn.txt",
                "src/main/resources/textTestIn2.txt",
                "src/main/resources/textTestIn3.txt"};
        ArgsParser param = new ArgsParser(args);
        assert (!param.isFailed());
        MergeSort mergeSort = new MergeSort(param);
        mergeSort.start();
        try (BufferedReader textReader1 =
                     Files.newBufferedReader(
                             Path.of("src/main/resources/textTestOut.txt"));
             BufferedReader textReader2 =
                     Files.newBufferedReader(
                             Path.of("src/main/resources/textExpected.txt")))
        {
            String result = textReader1.readLine();
            String expected = textReader2.readLine();
            while (expected!=null && result!=null){
                assertThat(result, is(expected));
                result = textReader1.readLine();
                expected = textReader2.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            Files.deleteIfExists(Paths.get("src/main/resources/textTestOut.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void integerDescendingTestWithSomeProblemsInFile(){
        String[] args = {"-d", "-i",
                "src/main/resources/testOut2.txt",
                "src/main/resources/testIn.txt",
                "src/main/resources/testIn2.txt",
                "src/main/resources/testIn3Problem.txt"};
        ArgsParser param = new ArgsParser(args);
        assert (!param.isFailed());
        MergeSort mergeSort = new MergeSort(param);
        mergeSort.start();
        try (BufferedReader reader1 =
                     Files.newBufferedReader(
                             Path.of("src/main/resources/testOut2.txt"));
             BufferedReader reader2 =
                     Files.newBufferedReader(
                             Path.of("src/main/resources/expected.txt")))
        {
            String result = reader1.readLine();
            String expected = reader2.readLine();
            while (expected!=null && result!=null){
                assertThat(result, is(expected));
                result = reader1.readLine();
                expected = reader2.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            Files.deleteIfExists(Paths.get("src/main/resources/testOut2.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

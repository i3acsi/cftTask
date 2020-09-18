package ru.gasevskyv.cft;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ArgsParserTest {
    private ArgsParser argsParser;
    private final PrintStream stdout = System.out;
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final String errorMsg = "usage: sort-it.exe -i -a out.txt in.txt (for integers ascending)\r\n" +
            "sort-it.exe -s out.txt in1.txt in2.txt in3.txt (for strings ascending)\r\n" +
            "sort-it.exe -d -s out.txt in1.txt in2.txt (for strings descending)\r\n" +
            "Program parameters are set at startup via command line arguments, in\r\n" +
            "                   order:\r\n" +
            "1. ordering mode (-a or -d), optional, by default sort in ascending order;\r\n" +
            "2. data type (-s or -i), required;\r\n" +
            "3. the name of the output file, required;\r\n" +
            "4. other parameters - names of input files, at least one.\r\n" +
            " -a   for ascending ordering (used by default)\r\n" +
            " -d   for descending ordering\r\n" +
            " -i   for integers\r\n" +
            " -s   for strings\r\n";

    @Test
    public void getArgsNoErrors1() {
        String[] args = {"-d", "-i", "out.txt", "in.txt"};
        argsParser = new ArgsParser(args);
        assertThat(argsParser.getOutFile(), is("out.txt"));
        assertThat(argsParser.getInFiles()[0], is("in.txt"));
        assert (argsParser.isDescending());
        assert (argsParser.isTypeInt());
        assert (!argsParser.isFailed());
    }

    @Test
    public void getArgsNoErrors2() {
        String[] args = {"-a", "-s", "out.txt", "in.txt", "in2.txt"};
        argsParser = new ArgsParser(args);
        assertThat(argsParser.getOutFile(), is("out.txt"));
        assertThat(argsParser.getInFiles()[0], is("in.txt"));
        assertThat(argsParser.getInFiles()[1], is("in2.txt"));
        assert (!argsParser.isDescending());
        assert (!argsParser.isTypeInt());
        assert (!argsParser.isFailed());
    }

    @Test
    public void getArgsNoErrors3() {
        String[] args = {"-s", "out.txt", "in.txt", "in2.txt"};
        argsParser = new ArgsParser(args);
        assertThat(argsParser.getOutFile(), is("out.txt"));
        assertThat(argsParser.getInFiles()[0], is("in.txt"));
        assertThat(argsParser.getInFiles()[1], is("in2.txt"));
        assert (!argsParser.isDescending());
        assert (!argsParser.isTypeInt());
        assert (!argsParser.isFailed());
    }

    @Test
    public void getMessageWhenErrors() {
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String[] args = {"out.txt", "in.txt", "in2.txt"};
        argsParser = new ArgsParser(args);
        assertThat(out.toString(),
                Is.is(
                        "Missing required option: [-i for integers, -s for strings]\r\n" + errorMsg
                ));
        System.setOut(stdout);
        assert (argsParser.isFailed());
    }

    @Test
    public void getMessageWhenErrors2() {
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String[] args = {"-d", "-s", "out.txt"};
        argsParser = new ArgsParser(args);
        assertThat(out.toString(),
                Is.is(
                        "at least two files must be specified: outFile than inFiles\r\n" + errorMsg
                ));
        System.setOut(stdout);
        assert (argsParser.isFailed());
    }
}

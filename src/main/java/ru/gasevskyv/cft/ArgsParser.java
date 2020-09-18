package ru.gasevskyv.cft;


import org.apache.commons.cli.*;

import java.util.Iterator;
import java.util.List;

public class ArgsParser {
    private boolean typeInt = false;
    private boolean isDescending = false;
    private String outFile;
    private String[] inFiles;
    private boolean isFailed = false;

    public ArgsParser(String[] args) {
        Object[] result = parseArgs(args);
        if (!isFailed) {
            this.typeInt = (Boolean) result[0];
            this.isDescending = (Boolean) result[1];
            this.outFile = (String) result[2];
            this.inFiles = (String[]) result[3];
        }
    }

    public boolean isTypeInt() {
        return typeInt;
    }

    public boolean isDescending() {
        return isDescending;
    }

    public String getOutFile() {
        return outFile;
    }

    public String[] getInFiles() {
        return inFiles;
    }

    public boolean isFailed() {
        return isFailed;
    }

    private Object[] parseArgs(String[] args) {
        Object[] result = new Object[4];
        Options options = new Options();

        OptionGroup ordering = new OptionGroup();
        Option aOption = new Option("a", false, "ascending");
        aOption.setDescription("for ascending ordering (used by default)");
        ordering.addOption(aOption);
        Option dOption = new Option("d", false, "descending");
        dOption.setDescription("for descending ordering");
        ordering.addOption(dOption);
        ordering.setRequired(false);
        options.addOptionGroup(ordering);

        OptionGroup type = new OptionGroup();
        Option iOption = new Option("i", false, "integer");
        iOption.setDescription("for integers");
        type.addOption(iOption);
        Option sOption = new Option("s", false, "string");
        sOption.setDescription("for strings");
        type.addOption(sOption);
        type.setRequired(true);
        options.addOptionGroup(type);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLine cmd = parser.parse(options, args);


            boolean typeInt = cmd.hasOption("i") || !cmd.hasOption("s");
            result[0] = typeInt;

            boolean isDescending = !(cmd.hasOption("a") || !cmd.hasOption("d"));
            result[1] = isDescending;


            List<String> files = cmd.getArgList();
            if (files.size() < 2) {
                throw new ParseException("at least two files must be specified: outFile than inFiles");
            }
            Iterator<String> iterator = files.iterator();
            String outFile = iterator.next();
            result[2] = outFile;
            String[] inFiles = new String[files.size() - 1];
            for (int i = 0; i < inFiles.length; i++) {
                inFiles[i] = iterator.next();
            }
            result[3] = inFiles;

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("sort-it.exe -i -a out.txt in.txt (for integers ascending)\r\n" +
                    "sort-it.exe -s out.txt in1.txt in2.txt in3.txt (for strings ascending)\r\n" +
                    "sort-it.exe -d -s out.txt in1.txt in2.txt (for strings descending)\r\n" +
                    "Program parameters are set at startup via command line arguments, in order:\r\n" +
                    "1. ordering mode (-a or -d), optional, by default sort in ascending order;\r\n" +
                    "2. data type (-s or -i), required;\r\n" +
                    "3. the name of the output file, required;\r\n" +
                    "4. other parameters - names of input files, at least one.", options);
            this.isFailed = true;
        }
        return result;
    }
}

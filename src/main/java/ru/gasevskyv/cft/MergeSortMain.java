package ru.gasevskyv.cft;

public class MergeSortMain {
    public static void main(String[] args) {
        ArgsParser param = new ArgsParser(args);
        if (!param.isFailed()) {
            MergeSort mergeSort  = new MergeSort(param);
            mergeSort.start();
        }
    }
}

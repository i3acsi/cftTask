package ru.gasevskyv.cft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 *
 */

public class MergeSort {
    private FilesProcessor processor;
    private boolean typeInt;
    private boolean isDescending;
    private int size;
    private List<String> buffer;
    private final int defaultBufferSize = 1000;

    public MergeSort(ArgsParser param) {
        this.typeInt = param.isTypeInt();
        this.processor = new FilesProcessor(param.getOutFile(), param.getInFiles(), typeInt);
        this.isDescending = param.isDescending();
        this.size = param.getInFiles().length;
        this.buffer = new ArrayList<>(defaultBufferSize);
    }

    public void start() {
        String[] objects = new String[size];
        String[] emptyArray = new String[size];

        for (int i = 0; i < size; i++) {
            objects[i] = processor.readReader(i);
        }

        String tmp = null;
        while (!Arrays.equals(objects, emptyArray)) {
            int index = this.getElement(objects); // получаю элемент из временного списка - кондидат на включение
            tmp = processor.readReader(index); // получаю следующий за ним элеменнт
            while (!this.isRightOrder(tmp, objects[index])) { // делаю проверку на упорядоченность
                this.orderingWriteToBuffer(tmp);// нужно кинуть в буфер в нужное место неправильные данные
                tmp = processor.readReader(index); // затем нежно обновить tmp и сделать новое сравнение
            }
            this.writeToBuffer(objects[index]); // если проверка пройдена, то просто записываю нужный элемент в буфер и обновляю элемент списка сравнения
            objects[index] = tmp;

        }
        if (this.buffer.size() > 0) {
            processor.writeToFile(this.buffer);
            this.buffer.clear();
        }
    }

        // TODO: 17.09.2020 как будет вести себя программа, если сбросился буфер?

    private void writeToBuffer(String newLine) {
        if (buffer.size() >= defaultBufferSize) {
            processor.writeToFile(buffer);
            buffer.clear();
        }
        buffer.add(newLine);
        // todo : в случае если все файлы кончились нужно очистить буфер
    }

    /**
     * If the order is broken than new line should be inserted in the correct position
     * @param newLine
     */
    private void orderingWriteToBuffer(String newLine) {
        ListIterator<String> iterator = buffer.listIterator(buffer.size());
        boolean eol = false;
        while (!eol) {
            int compare;
            if (this.typeInt) {
                compare = Integer.compare(Integer.parseInt(iterator.previous()), Integer.parseInt(newLine));
            } else {
                compare = iterator.previous().compareTo(newLine);
            }
            if (isDescending) {
                if (compare >= 0) {
                    iterator.next();
                    iterator.add(newLine);
                    break;
                }
            } else {
                if (compare <= 0) {
                    iterator.next();
                    iterator.add(newLine);
                    break;
                }
            }
            if (!iterator.hasPrevious()) {
                iterator.add(newLine);
                eol = true;
            }
        }
    }

    /**
     * Getting the an index of item from the list depending on the required order.
     *
     * @param objects - list of objects to compare.
     * @return index of item.
     */
    private int getElement(String[] objects) {
        String tmp = null;
        int index = 0;
        for (; index < objects.length; index++) {
            if (objects[index] != null) {
                tmp = objects[index];
                break;
            }
        }
        int compare = 0;
        int position = index++;
        for (; index < objects.length; index++) {
            if (objects[index] != null && tmp != null) {
                if (typeInt) {
                    compare = Integer.compare(Integer.parseInt(tmp), Integer.parseInt(objects[index]));
                } else {
                    compare = tmp.compareTo(objects[index]);
                }
                if (this.isDescending && compare <= 0 || !this.isDescending && compare >= 0) {
                    tmp = objects[index];
                    position = index;
                }
            }
        }
        return position;

    }


    /**
     * Check that the items in the file are ordered.
     * @param newLine
     * @param prevLine
     */
    private boolean isRightOrder(String newLine, String prevLine) {
        if (newLine != null) {
            int compare = this.typeInt ?
                    Integer.compare(Integer.parseInt(prevLine), Integer.parseInt(newLine))
                    : prevLine.compareTo(newLine);
            return this.isDescending && compare >= 0 || !this.isDescending && compare <= 0;
        }
        return true;
    }

}

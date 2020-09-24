package ru.gasevskyv.cft;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

/**
 *
 */

class MergeSort {
    private FilesProcessor processor;
    private boolean typeInt;
    private boolean isDescending;
    private int size;
    private List<String> buffer;
    private final int defaultBufferSize = 10;

    private Map<String, String> brokenBuffer;
    private String fileTail;

    MergeSort(ArgsParser param) {
        this.typeInt = param.isTypeInt();
        this.processor = new FilesProcessor(param.getOutFile(), param.getInFiles(), typeInt);
        this.isDescending = param.isDescending();
        this.size = param.getInFiles().length;
        this.buffer = new ArrayList<>(defaultBufferSize);
        if (isDescending) {
            this.brokenBuffer = new TreeMap<>(Collections.reverseOrder());
        } else {
            this.brokenBuffer = new TreeMap<>();
        }
    }

    void start() {
        String[] objects = new String[size];
        String[] emptyArray = new String[size];

        for (int i = 0; i < size; i++) {
            objects[i] = processor.readReader(i);
        }

        String tmp;
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
        if (!brokenBuffer.isEmpty()) {
            processor.setChannel();
            try {
                processor.putToOutFile(brokenBuffer, isDescending);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeToBuffer(String newLine) {
        if (buffer.size() >= defaultBufferSize) {
            processor.writeToFile(buffer);
            this.fileTail = buffer.get(buffer.size() - 1);
            buffer.clear();
        }
        buffer.add(newLine);
    }

    /**
     * If the order is broken than new line should be inserted in the correct position
     *
     * @param newLine - new Line that should be add
     */
    private void orderingWriteToBuffer(String newLine) {
        int compare = this.compare(this.fileTail, newLine); // сравниваю с поледней записью в файл
        if (this.isOrdered(compare)) {
            this.brokenBuffer.compute(newLine, (k, v) -> (v == null) ? k :
                    (new StringBuilder(v)
                            .append(System.lineSeparator())
                            .append(k)
                    ).toString());
        } else {
            ListIterator<String> iterator = buffer.listIterator(buffer.size());
            boolean eol = false;
            Consumer<String> addNewLine = x -> {
                iterator.next();
                iterator.add(x);
            };
            while (!eol) {
                compare = this.compare(newLine, iterator.previous());
                if (isOrdered(compare)) {
                    addNewLine.accept(newLine);
                    break;
                }
                if (!iterator.hasPrevious()) {
                    iterator.add(newLine);
                    eol = true;
                }
            }
        }
    }

    private int compare(String prev, String newLine) {
        int result;
        if (this.typeInt) {
            result = Integer.compare(Integer.parseInt(prev), Integer.parseInt(newLine));
        } else {
            result = prev.compareTo(newLine);
        }
        return result;
    }

    private boolean isOrdered(int compareResult) { // есл D и порядок нарушен=> 1
        return isDescending ? compareResult < 0 : compareResult > 0;
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
        int compare;
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
     *
     * Checks if a new line can be added without breaking the order.
     *
     * @param newLine - new line that sould be  add
     * @param prevLine - the last value in the list to add
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

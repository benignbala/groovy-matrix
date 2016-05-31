package org.benignbala.groovy.matrix;

import java.util.ArrayList
import groovyx.gpars.GParsExecutorsPool
import groovyx.gpars.GParsPool
import groovyx.gpars.ParallelEnhancer
/**
 * @author Balachandran Sivakumar
 *
 */

class Matrix {
    int numRows
    int numColumns
    List<ArrayList> elements

    Matrix(int rows, int columns) {
        numRows = rows
        numColumns = columns
        elements = new ArrayList()
    }

    Matrix(int rows, int columns, List<ArrayList> elements) {
        numRows = rows
        numColumns = columns
        this.elements = elements
    }
    
    int rowSize() {
        return this.numRows
    }

    int columnSize() {
        return this.numColumns
    }

    void addRow(ArrayList row) {
        this.elements.add(row)
    }
    
    void setAt(int row, int col, Object element) {
        elements.getAt(row).set(col, element)
    }

    Object getAt(int row, int col) {
        return elements.getAt(row).getAt(col)
    }

    ArrayList getColumns() {
        ArrayList columns = new ArrayList()
        ParallelEnhancer.enhanceInstance(this.elements)
        GParsPool.withPool {
            (0..numColumns-1).parallelStream().each { i ->
                ArrayList column = new ArrayList()
                column = elements.collectParallel { el ->
                    el.get(i)
                }
                columns.add(column)
            }
        }
        return columns
    }
    
    ArrayList getColumn(int idx) throws ArrayIndexOutOfBoundsException {
        ArrayList column = new ArrayList()
        ParallelEnhancer.enhanceInstance(this.elements)
        GParsExecutorsPool.withPool {
            column = elements.collectParallel { el ->
                if (el.size() > idx) {
                    el.get(idx)
                } else {
                    throw new ArrayIndexOutOfBoundsException(idx)
                }
            }
        }
        return column
    }

    Matrix multiply(Matrix other) {
        if (this.columnSize() != other.rowSize()) 
            throw new MatrixMultiplierException(this.columnSize(), this.rowSize());
        ArrayList columns = other.getColumns()
        ArrayList rows = this.getElements()
        Matrix result = new Matrix(this.rowSize(), other.columnSize())
        rows.parallelStream().each { row ->
            ArrayList tmp = new ArrayList()
            columns.parallelStream().each {col ->
                int val = 0
                (0..col.size()-1).parallelStream().each { i ->
                    val += row.get(i) * col.get(i)
                }
                tmp.add(val)
            }
            result.addRow(tmp)
        }
        return result
    }

    Matrix plus(Matrix other) {
        if (this.rowSize() != other.rowSize())  {
            throw new MatrixAdderException("Matrix addition needs two matrices of same dimentsions");
        }
        Matrix result = new Matrix(this.rowSize(), this.columnSize())
        ArrayList rowsSelf = this.getElements()
        ArrayList rowsOther = other.getElements()
        (0..this.rowSize()-1).parallelStream().each { i ->
            ArrayList tmp = new ArrayList()
            (0..this.columnSize()-1).parallelStream().each { c ->
                tmp.add(this.getAt(i, c) + other.getAt(i, c))
            }
            result.addRow(tmp)
        }
        return result
    }
}

class MatrixMultiplierException extends Exception {
    MatrixMultiplierException(int col, int row) {
        super ("Source Matrix should have " + col + "columns and second matrix should have " + row + " rows")
    }
}

    
class MatrixAdderException extends Exception {
    MatrixAdderException(String message) {
        super(message)
    }
}

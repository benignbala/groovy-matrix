package org.benignbala.groovy.matrix;

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
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
        if (this.rowSize() != other.columnSize()) 
            throw new MatrixMultiplierException(this.rowSize(), this.columnSize());
        ArrayList columns = other.getColumns()
        ArrayList rows = this.getElements()
        Matrix result = new Matrix(this.rowSize(), other.columnSize())
        columns.parallelStream().each { col ->
            ArrayList tmp = new ArrayList()
            rows.parallelStream().each {row ->
                int val = 0
                (0..row.size()-1).parallelStream().each { i ->
                    val += row.get(i) * col.get(i)
                }
                tmp.add(val)
            }
            result.addRow(tmp)
        }
        return result
    }            
}

@CompileStatic
@TypeChecked
class MatrixMultiplierException extends Exception {
    MatrixMultiplierException(int row, int col) {
        super ("Source Matrix should have " + row + "rows and second matrix should have " + col + " columns")
    }
}

    

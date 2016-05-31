package org.benignbala.groovy.matrix;

import spock.lang.*

class MatrixTest extends Specification {
    
    def ctrTest() {
        when:
        Matrix m = new Matrix(2, 2)

        then:
        assert m.rowSize() == 2
        assert m.columnSize() == 2

    }

    def ctrWithMatrixTest() {
        when:
        Matrix m = new Matrix(2, 2, [[1, 2], [3, 4]])

        then:
        assert [1, 2] == m.elements.get(0)
    }
    
    def getFirstColumnTest() {
        when:
        Matrix m = new Matrix(3, 3)
        m.addRow([1, 2, 3])
        m.addRow([4, 5, 6])
        m.addRow([7, 8, 9])

        then:
        assert [1, 4, 7] == m.getColumn(0)
    }

    @Ignore(value = "Fails for now")
    def getColumnRowMatrixTest() {
        when:
        Matrix m = new Matrix(1, 3, [[1, 2, 3]])

        then:
        assert [1] == m.getColumn(0)
        
    }
    
    def getColumnsTest() {
        when:
        Matrix m = new Matrix(3, 3)
        m.addRow([1, 2, 3])
        m.addRow([4, 5, 6])
        m.addRow([7, 8, 9])

        then:
        assert [1, 4, 7] == m.getColumns().get(0)
    }

    def multiplyTest() {
        when:
        Matrix m = new Matrix(2, 2)
        Matrix n = new Matrix(2, 2)

        m.addRow([1, 1])
        m.addRow([1, 1])

        n.addRow([1, 1])
        n.addRow([1, 1])

        Matrix r = m * n

        then:
        assert r instanceof Matrix
        assert [2, 2] == r.getColumns().get(0)
    }

    def multipleThreeTest() {
        when:
        Matrix m = new Matrix(2, 2)
        Matrix n = new Matrix(2, 2)
        Matrix o = new Matrix(2, 2)

        m.addRow([1, 1])
        m.addRow([1, 1])

        n.addRow([1, 1])
        n.addRow([1, 1])

        o.addRow([1, 1])
        o.addRow([1, 1])

        Matrix r = m * n * o

        then:
        assert r instanceof Matrix
        assert [4, 4] == r.getColumns().get(0)

    }

    def multiplyUnevenTest() {
        when:
        Matrix m = new Matrix(3, 2, [[1, 2], [3, 4], [5, 6]])
        Matrix n = new Matrix(2, 3, [[1, 2, 3], [4, 5, 6]])

        Matrix r = m * n
        then:
        assert r instanceof Matrix
        assert [9, 12, 15] == r.getElements().get(0)
    }

    def addSimpleTest() {
        when:
        Matrix m = new Matrix(2, 2, [[1, 1], [1, 1]])
        Matrix n = new Matrix(2, 2, [[1, 1], [1, 1]])

        Matrix r = m + n
        
        then:
        assert [2, 2] == r.getColumns().get(0)
    }
}


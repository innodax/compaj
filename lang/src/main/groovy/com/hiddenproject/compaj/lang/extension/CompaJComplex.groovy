package com.hiddenproject.compaj.lang.extension

import org.apache.commons.math3.complex.Complex

class CompaJComplex extends Complex {

    CompaJComplex(Complex num) {
        super(num.getReal(), num.getImaginary())
    }

    CompaJComplex(double real) {
        super(real)
    }

    CompaJComplex(double real, double imaginary) {
        super(real, imaginary)
    }

    String toString() {
        "Complex" + super.toString()
    }
}

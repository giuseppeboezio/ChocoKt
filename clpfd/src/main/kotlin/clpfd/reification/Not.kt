package clpfd.reification

import org.chocosolver.solver.constraints.nary.cnf.ILogical
import org.chocosolver.solver.constraints.nary.cnf.LogOp

object Not: UnaryReificationOperator("#\\") {
    override val operation: (Array<ILogical>) -> LogOp = LogOp::nand
}
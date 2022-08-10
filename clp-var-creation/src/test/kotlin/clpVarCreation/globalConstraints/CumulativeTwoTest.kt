package clpVarCreation.globalConstraints

import clpVarCreation.BaseTest
import clpVarCreation.ClpFdLibrary
import clpVarCreation.assertSolutionAssigns
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

class CumulativeTwoTest: BaseTest() {

    @Test
    fun testCumulativeAllDurationVariables() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                in(H1, '..'(1, 1)), 
                ins([H2, L, D1, D2], '..'(2, 2)),
                cumulative([task(S1,D1,E1,H1, _), task(S2,D2,E2,H2,_)], [limit(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("E1") to intOf(3),
                varOf("E2") to intOf(5)
            )
        }
    }

    @Test
    fun testCumulativeAllDurationIntegers() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                in(H1, '..'(1, 1)), 
                ins([H2, L], '..'(2, 2)),
                cumulative([task(S1,2,E1,H1, _), task(S2,2,E2,H2,_)], [limit(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("E1") to intOf(3),
                varOf("E2") to intOf(5)
            )
        }
    }

    @Test
    fun testCumulativeMixedDuration() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                in(H1, '..'(1, 1)), 
                ins([H2, L, D1], '..'(2, 2)),
                cumulative([task(S1,D1,E1,H1, _), task(S2,2,E2,H2,_)], [limit(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("E1") to intOf(3),
                varOf("E2") to intOf(5)
            )
        }
    }

    @Test
    fun testCumulativeInvalidFunctorTask() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                in(H1, '..'(1, 1)), 
                ins([H2, L, D1], '..'(2, 2)),
                cumulative([invalid(S1,D1,E1,H1, _), task(S2,2,E2,H2,_)], [limit(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = try {
            solver.solveOnce(goal)
        } catch (e: IllegalArgumentException){
            true
        }

        assertTrue(solution as Boolean)
    }

    @Test
    fun testCumulativeInvalidTaskArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                in(H1, '..'(1, 1)), 
                ins([H2, L, D1], '..'(2, 2)),
                cumulative([task(a,D1,E1,H1, _), task(S2,2,E2,H2,_)], [limit(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = try {
            solver.solveOnce(goal)
        } catch (e: IllegalArgumentException){
            true
        }

        assertTrue(solution as Boolean)
    }

    @Test
    fun testCumulativeInvalidOption() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                in(H1, '..'(1, 1)), 
                ins([H2, L, D1], '..'(2, 2)),
                cumulative([task(S1,D1,E1,H1, _), task(S2,2,E2,H2,_)], [limit(L), invalid(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = try {
            solver.solveOnce(goal)
        } catch (e: IllegalArgumentException){
            true
        }

        assertTrue(solution as Boolean)
    }

    @Test
    fun testCumulativeInvalidLimitArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                in(H1, '..'(1, 1)), 
                ins([H2, L, D1], '..'(2, 2)),
                cumulative([task(S1,D1,E1,H1, _), task(S2,2,E2,H2,_)], [limit(a)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = try {
            solver.solveOnce(goal)
        } catch (e: IllegalArgumentException){
            true
        }

        assertTrue(solution as Boolean)
    }
}
/*
 *  Project: Sudoku PDF
 *
 *  Copyright 2008 yaochunlin.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 * ------------------------------------------------------------------------
 * $Header$
 * $Revision$
 * $Date$
 */

package cn.js.nt.yao.sudokupdf;

import diuf.sudoku.Grid;
import diuf.sudoku.solver.Rule;
import java.util.Map;


/**
 *
 * @author yaochunlin
 */
public class PrintRecord {
    private Grid sudoku;
    private Grid answer;
    private double difficulty = 20.0;

    public PrintRecord(Grid sudoku, Grid answer, Map<Rule, Integer> ruleMap) {
        this.sudoku = sudoku;
        this.answer = answer;

        if ((ruleMap != null) && (ruleMap.isEmpty() == false)) {
            for (Rule r : ruleMap.keySet()) {
                this.difficulty = r.getDifficulty();
            }
        }
    }

    public Grid getSudoku() {
        return sudoku;
    }

    public Grid getAnswer() {
        return answer;
    }

    public double getDifficulty() {
        return difficulty;
    }
}

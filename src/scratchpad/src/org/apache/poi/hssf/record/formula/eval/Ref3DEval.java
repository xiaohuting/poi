/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.apache.poi.hssf.record.formula.eval;

import org.apache.poi.hssf.record.formula.Ref3DPtg;

/**
 * @author Amol S. Deshmukh
 *  
 */
public final class Ref3DEval implements RefEval {

    private final ValueEval value;
    private final Ref3DPtg delegate;

    public Ref3DEval(Ref3DPtg ptg, ValueEval ve) {
        value = ve;
        delegate = ptg;
    }
    public ValueEval getInnerValueEval() {
        return value;
    }
    public short getRow() {
        return delegate.getRow();
    }
    public short getColumn() {
        return delegate.getColumn();
    }
    public int getExternSheetIndex() {
        return delegate.getExternSheetIndex();
    }
}
